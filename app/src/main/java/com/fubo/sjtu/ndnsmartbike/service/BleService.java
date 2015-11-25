package com.fubo.sjtu.ndnsmartbike.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.fubo.sjtu.ndnsmartbike.Protocol.layer_one;
import com.fubo.sjtu.ndnsmartbike.Protocol.layer_two;
import com.fubo.sjtu.ndnsmartbike.model.CustomBleDevice;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;
import com.fubo.sjtu.ndnsmartbike.utils.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BleService extends Service {

	private static final String ACTION_CONNECT_COMMAND = GlobalMember.PACKAGE_NAME
			+ ".BLE_ACTION_CONNECT_COMMAND";
	private static final String ACTION_SEARCH_COMMAND = GlobalMember.PACKAGE_NAME
			+ ".BLE_ACTION_SEARCH_COMMAND";
	// 设备不支持蓝牙
	public static final String ACTION_DEVICE_NOT_SUPPORTED = GlobalMember.PACKAGE_NAME
			+ ".DEVICE_NOT_SUPPORTED";

	private BluetoothManager mBluetoothManager = null;
	private BluetoothAdapter mBluetoothAdapter = null;

	private boolean isScanning = false;
	private Handler mHandler = new Handler();
	private static final long MAX_SCANNING_INTERVAL = 1500;
	private LeScanCallback mLeScanCallbackKitKat = null;
	private ScanCallback mScanCallbackLollipop = null;
	// 搜索到的ble设备列表
	private Map<String, CustomBleDevice> customBleDeviceMap = new HashMap<>();

	public final static String ACTION_GATT_CONNECTED = GlobalMember.PACKAGE_NAME
			+ ".ACTION_GATT_CONNECTED";
	public final static String ACTION_GATT_DISCONNECTED = GlobalMember.PACKAGE_NAME
			+ ".ACTION_GATT_DISCONNECTED";
	public final static String ACTION_GATT_SERVICES_DISCOVERED = GlobalMember.PACKAGE_NAME
			+ ".ACTION_GATT_SERVICES_DISCOVERED";

	private BluetoothGatt mBluetoothGatt = null;
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic = null;
	private String serviceUUID = null;
	private String characteristicUUID = null;
	private static final String SERVICE_UUID = GlobalMember.PACKAGE_NAME
			+ ".SERVICE_UUID";
	private static final String CHARACTERISTIC_UUID = GlobalMember.PACKAGE_NAME
			+ ".CHARACTERISTIC_UUID";
	public static final String BLE_GATT_RECEIVE_DATA = GlobalMember.PACKAGE_NAME
			+ ".BLE_GATT_RECEIVE_DATA";
	public static final String ACTION_GATT_RECEIVE_DATA = GlobalMember.PACKAGE_NAME
			+ ".ACTION_GATT_RECEIVE_DATA";
	private static final String BLE_GATT_SEND_DATA = GlobalMember.PACKAGE_NAME
			+ ".BLE_GATT_SEND_DATA";
	private static final String ACTION_GATT_SEND_DATA = GlobalMember.PACKAGE_NAME
			+ ".ACTION_GATT_SEND_DATA";
	private Set<String> incorrectAddressSet = new HashSet<>();
	private static final int MAX_INCORRECT_DEVICE = 3;
	private List<byte[]> dataSendBuffer = new ArrayList<>();
	private static final int MAX_SEND_SIZE = 20;
	private boolean isReadySendData = false;
	private byte[] m_recv_buffer = new byte[0]; // 接收的不完整数据包缓存
	private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
				int newState) {
			// TODO Auto-generated method stub
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				mBluetoothGatt = gatt;
				gatt.discoverServices();
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				sendBleStateBroadCast(ACTION_GATT_DISCONNECTED);
				onClearGattConnection();
			}
			super.onConnectionStateChange(gatt, status, newState);
		}
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// TODO Auto-generated method stub
			List<BluetoothGattService> bleServices = gatt.getServices();
			boolean isConnected = false;
			for (BluetoothGattService service : bleServices) {
				if (serviceUUID.equalsIgnoreCase(service.getUuid().toString())) {
					List<BluetoothGattCharacteristic> bleCharacteristics = service
							.getCharacteristics();
					for (BluetoothGattCharacteristic characteristic : bleCharacteristics) {
						if (characteristicUUID.equalsIgnoreCase(characteristic
								.getUuid().toString())) {
							// 找到指定的charateristic
							mBluetoothGattCharacteristic = characteristic;
							mBluetoothGatt.setCharacteristicNotification(
									mBluetoothGattCharacteristic, true);
							sendBleStateBroadCast(ACTION_GATT_CONNECTED);
							isConnected = true;
							isReadySendData = true;
						}
					}
				}
			}
			// 重新选择设备进行连接操作
			if (!isConnected) {
				incorrectAddressSet.add(gatt.getDevice().getAddress());
				if (incorrectAddressSet.size() > MAX_INCORRECT_DEVICE) {
					bleSearch();
				} else {
					customBleDeviceMap.remove(gatt.getDevice().getAddress());
					bleConnectWithMaxRssi();
				}

			}
			super.onServicesDiscovered(gatt, status);
		}
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			// TODO Auto-generated method stub
			byte[] data = characteristic.getValue();
			on_data_recieved(data);
			//DataAnalyseService.onGetData(data,getApplicationContext());

			//sendBroadCastWithBleData(ACTION_GATT_RECEIVE_DATA, data);
			super.onCharacteristicChanged(gatt, characteristic);
		}
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO Auto-generated method stub
			if (characteristic.getUuid().toString().equalsIgnoreCase(mBluetoothGattCharacteristic
					.getUuid().toString())
					&& BluetoothGatt.GATT_SUCCESS == status) {
				isReadySendData = true;
				sendData();
			}
			super.onCharacteristicWrite(gatt, characteristic, status);
		}
	};

	// 丢弃当前接收缓存
	private void discard_recv_buffer_data() {
		this.m_recv_buffer = new byte[0];
		return;
	}

	// 数据加入接收缓存
	private void add_to_recv_buffer(byte[] data) {
		byte[] result = util.byte_array_append(m_recv_buffer, data);
		this.m_recv_buffer = result;
		return;
	}

	// 当蓝牙数据到达，拼接出一个完整的包
	private void on_data_recieved(byte[] data) {
		if (this.m_recv_buffer.length > 0) {
			// 有缓存时先拼接
			data = util.byte_array_append(m_recv_buffer, data);
		}

		int[] payload = new int[1];
		int rtn = layer_one.read_payload_len(data, payload);
		if (layer_one.TO_BE_CONTINUED == rtn) {
			// 包未收完
			this.add_to_recv_buffer(data);
			return;
		} else if (layer_one.OK != rtn) {
			// 出错，丢弃
			this.discard_recv_buffer_data();
			return;
		}

		int payload_len = payload[0];
		int packet_len = payload_len + layer_one.SIZE;
		if (packet_len > data.length) {
			// 包未收完
			this.m_recv_buffer = data;
			return;
		}

		byte[] packet = Arrays.copyOfRange(data, 0, packet_len);
		if (packet_len == data.length) {
			this.m_recv_buffer = new byte[0];
		} else {
			this.m_recv_buffer = Arrays.copyOfRange(data, packet_len,
					data.length);
		}

		this.on_packect_recieved(packet);
		return;
	}

	// 当收到一个完整的包
	private void on_packect_recieved(byte[] packet) {
		layer_one one = new layer_one();
		int rtn = one.parse(packet);
		if (layer_one.OK != rtn) {
			// 解析出错，以后需要分情况讨论，分别处理，这里先略过
			return;
		}

		layer_two two = new layer_two();
		DataAnalyseService.onGetData(two.parse(packet, layer_one.SIZE),getApplicationContext());
		return;
	}

	private void onClearGattConnection() {
		mBluetoothGattCharacteristic = null;
		mBluetoothGatt = null;
	}
	private void sendBleStateBroadCast(String action) {
		Intent intent = new Intent(action);
		sendBroadcast(intent);
		return;
	}
	private void sendBroadCastWithBleData(String action, byte[] data) {
		Intent intent = new Intent(action);
		intent.putExtra(BLE_GATT_RECEIVE_DATA, data);
		sendBroadcast(intent);
		return;
	}
	/**
	 * 检测设备蓝牙是否正常并且是否支持ble
	 * 
	 * @return
	 */
	private boolean bleInitialize() {
		if (mBluetoothManager == null) {
			mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (mBluetoothManager == null)
				return false;
		}
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		if (mBluetoothAdapter == null)
			return false;
		// 检测是否支持ble
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE))
			return false;
		// 如果蓝牙没有开启，强制开启蓝牙
		if (!mBluetoothAdapter.isEnabled())
			mBluetoothAdapter.enable();
		return true;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@SuppressWarnings("deprecation")
	private void bleSearch() {
		if (isScanning)
			return;
		if (!bleInitialize()) {
			Intent intent = new Intent(ACTION_DEVICE_NOT_SUPPORTED);
			sendBroadcast(intent);
			return;
		}
		// 开始搜索设备
		customBleDeviceMap.clear();
		isScanning = true;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			mBluetoothAdapter.startLeScan(mLeScanCallbackKitKat);
		} else {
			mBluetoothAdapter.getBluetoothLeScanner().startScan(
					mScanCallbackLollipop);
		}
		mHandler.postDelayed(stopScanAndStartConnectTask, MAX_SCANNING_INTERVAL);
	}
	Runnable stopScanAndStartConnectTask = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 如果搜索到合适的ble设备，停止搜索，开始连接？
			if (isScanning) {
				stopBleScan();
				bleConnectWithMaxRssi();
			}
		}
	};
	private void handleSearchDevice(BluetoothDevice device, int rssi,
			byte[] scanRecord) {
		// TODO Auto-generated method stub
		customBleDeviceMap.put(device.getAddress(), new CustomBleDevice(device,
				rssi, scanRecord));
		return;
	}
	private void bleStartConnect() {
		bleSearch();
	}
	/**
	 * 选择信号最强的进行连接，当信号达到哪个程度可以进行连接？
	 */
	private void bleConnectWithMaxRssi() {
		CustomBleDevice customBleDeviceWithMaxRssi = getCustomBleDeviceWithMaxRssi();
		// 如果没有找到最大信号的设备，重新搜索
		if (customBleDeviceWithMaxRssi == null) {
			bleSearch();
			return;
		}
		mBluetoothGatt = customBleDeviceWithMaxRssi.getmBluetoothDevice()
				.connectGatt(this, true, mBluetoothGattCallback);
		return;
	}
	private CustomBleDevice getCustomBleDeviceWithMaxRssi() {
		CustomBleDevice customBleDevice = null;
		Iterator<Map.Entry<String, CustomBleDevice>> iterator = customBleDeviceMap
				.entrySet().iterator();
		if (iterator.hasNext())
			customBleDevice = iterator.next().getValue();
		CustomBleDevice tempCustomBleDevice = null;
		while (iterator.hasNext()) {
			tempCustomBleDevice = iterator.next().getValue();
			if (tempCustomBleDevice.getRssi() > customBleDevice.getRssi())
				customBleDevice = tempCustomBleDevice;
		}
		return customBleDevice;
	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@SuppressWarnings("deprecation")
	private void stopBleScan() {
		// TODO Auto-generated method stub
		if (mBluetoothAdapter != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				mBluetoothAdapter.stopLeScan(mLeScanCallbackKitKat);
			} else {
				mBluetoothAdapter.getBluetoothLeScanner().stopScan(
						mScanCallbackLollipop);
			}
		}
		isScanning = false;
		return;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			mLeScanCallbackKitKat = new LeScanCallback() {
				@Override
				public void onLeScan(BluetoothDevice device, int rssi,
						byte[] scanRecord) {
					// TODO Auto-generated method stub
					handleSearchDevice(device, rssi, scanRecord);
				}
			};
		} else {
			mScanCallbackLollipop = new ScanCallback() {

				@Override
				public void onScanResult(int callbackType, ScanResult result) {
					// TODO Auto-generated method stub
					handleSearchDevice(result.getDevice(), result.getRssi(),
							result.getScanRecord().getBytes());
					super.onScanResult(callbackType, result);
				}
			};
		}
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		String action = null;
		if (intent != null)
			action = intent.getAction();
		if (action!=null&&!"".equals(action)) {
			switch (action) {
				case ACTION_CONNECT_COMMAND :
					this.serviceUUID = intent.getStringExtra(SERVICE_UUID);
					this.characteristicUUID = intent
							.getStringExtra(CHARACTERISTIC_UUID);
					bleStartConnect();
					break;
				case ACTION_SEARCH_COMMAND :
					bleSearch();
					break;
				case ACTION_GATT_SEND_DATA :
					if (intent.getByteArrayExtra(BLE_GATT_SEND_DATA) != null
							&& intent.getByteArrayExtra(BLE_GATT_SEND_DATA).length > 0)
						addSendDataToBuffer(intent
								.getByteArrayExtra(BLE_GATT_SEND_DATA));
					break;
				default :
					break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void addSendDataToBuffer(byte[] data) {
		// TODO Auto-generated method stub
		synchronized (dataSendBuffer) {
			int index = 0;
			while ((index + 1) * MAX_SEND_SIZE < data.length) {
				this.dataSendBuffer.add(Arrays.copyOfRange(data, index
						* MAX_SEND_SIZE, (index + 1) * MAX_SEND_SIZE));
				++index;
			}
			this.dataSendBuffer.add(Arrays.copyOfRange(data, index
					* MAX_SEND_SIZE, data.length));
			this.dataSendBuffer.add(data);
		}
		sendData();
		return;
	}
	private void sendData() {
		if (isReadySendData == true) {
			synchronized (dataSendBuffer) {
				if (this.dataSendBuffer.size() > 0) {
					byte[] data = this.dataSendBuffer.get(0);
					this.dataSendBuffer.remove(0);
					this.mBluetoothGattCharacteristic.setValue(data);
					this.mBluetoothGatt
							.writeCharacteristic(this.mBluetoothGattCharacteristic);
					this.isReadySendData = false;
				} else {
					this.isReadySendData = true;
				}
			}
		}
		else {
			//通知蓝牙已断开
			Intent intent = new Intent();
			intent.setAction(ACTION_GATT_DISCONNECTED);
			sendBroadcast(intent);
		}
		return;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (mBluetoothGatt != null) {
			mBluetoothGatt.close();
		}
		super.onDestroy();
	}

	public static class BlePublicAction {
		public static void bleServiceConnectWithMaxRssi(Context context,
				String serviceUUID, String characteristicUUID) {
			Intent intent = new Intent(context, BleService.class);
			intent.setAction(ACTION_CONNECT_COMMAND);
			intent.putExtra(SERVICE_UUID, serviceUUID);
			intent.putExtra(CHARACTERISTIC_UUID, characteristicUUID);
			context.startService(intent);
			return;
		}

		public static void bleSerivceSearch(Context context) {
			bleServiceAction(context, ACTION_SEARCH_COMMAND);
			return;
		}
		public static void bleSendData(Context context, byte[] data) {
			Intent intent = new Intent(context, BleService.class);
			intent.setAction(ACTION_GATT_SEND_DATA);
			intent.putExtra(BLE_GATT_SEND_DATA, data);
			context.startService(intent);
		}
		/**
		 * 蓝牙命令操作的私有方法
		 * 
		 * @param context
		 * @param action
		 */
		private static void bleServiceAction(Context context, String action) {
			Intent intent = new Intent(context, BleService.class);
			intent.setAction(action);
			context.startService(intent);
			return;
		}
	}
}

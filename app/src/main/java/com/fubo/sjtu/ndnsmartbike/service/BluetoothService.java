package com.fubo.sjtu.ndnsmartbike.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fubo.sjtu.ndnsmartbike.utils.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sjtu on 2015/11/19.
 */
public class BluetoothService extends Service {

    private static final UUID b_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private List<BluetoothDevice> mFindDevice = new ArrayList<>();
    private InputStream inputStream;
    private OutputStream outputStream;
    private BroadcastReceiver mReceiver;
    private Handler handler;
    private static final byte[] header = {0x01, 0x11, 0x22, 0x75};
    private static final byte[] tail = {0x02, 0x12, 0x23, 0x76};
    private boolean isReceiving = false;
    private int bufferLength = 0;
    private byte[] recBuffer;
    private int recLength = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (mBluetoothAdapter == null)
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice
                            .EXTRA_DEVICE);
                    mFindDevice.add(device);
                    Intent intent1 = new Intent();
                    intent1.setAction("findNewDevice");
                    intent1.putExtra("deviceAddress", device.getAddress());
                    intent1.putExtra("deviceName", device.getName());
                    sendBroadcast(intent1);

                }
            }
        };
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        handler = new Handler();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "search":
                    if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.startDiscovery();
                    }
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, filter);
                    break;
                case "connect":
                    String address = intent.getStringExtra("address");
                    connect(address);
                    break;
                case "send":
                    byte[] data = intent.getByteArrayExtra("data");
                    try {
                        outputStream.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void connect(String address) {
        try {
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(b_uuid);
            mBluetoothSocket.connect();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mBluetoothSocket.isConnected()) {
                        try {
                            inputStream = mBluetoothSocket.getInputStream();
                            outputStream = mBluetoothSocket.getOutputStream();
                            startReadThread();
                            Intent intent = new Intent();
                            intent.setAction("connect_complete");
                            sendBroadcast(intent);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 2000);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("连接失败");
        }
    }

    private void startReadThread() {
        if (mBluetoothSocket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        byte[] temp = new byte[1024];
                        try {
                            if (inputStream.read(temp) != -1) {
                                addToBuffer(temp);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    private void addToBuffer(byte[] temp) {
        if (temp.length > 8)
            isStart(temp);
        if (isReceiving) {
            if (recLength + temp.length <= bufferLength) {
                System.arraycopy(temp, 0, recBuffer, recLength, temp.length);
                recLength += temp.length;
            } else {
                System.arraycopy(temp, 0, recBuffer, recLength, bufferLength - recLength);
                recLength = bufferLength;
                isReceiving = false;
                byte[] data = new byte[bufferLength];
                System.arraycopy(recBuffer, 8, data, 0, bufferLength);
                DataAnalyseService.onGetData(data, getApplicationContext());
            }
        }
    }

    private void isEnd(byte[] temp) {

    }

    private void isStart(byte[] temp) {
        if (temp[0] == 0x01 && temp[1] == 0x11 && temp[2] == 0x22 && temp[3] == 0x75) {
            bufferLength = util.bytesToInt(temp, 4);
            recBuffer = new byte[bufferLength + 8];
            isReceiving = true;
            recLength = 0;
        }
    }

    private static byte[] addDataLength(byte[] data) {
        int length = data.length;
        byte[] result = new byte[length + 4];
        System.arraycopy(util.intToBytes(length), 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, length);
        return result;
    }

    private static byte[] addHeader(byte[] data) {
        byte[] result = new byte[data.length + 4];
        System.arraycopy(header, 0, result, 0, 4);
        System.arraycopy(data, 0, result, 4, data.length);
        return result;
    }

    private static byte[] addTail(byte[] data) {
        byte[] result = new byte[data.length + 4];
        System.arraycopy(data, 0, result, 0, data.length);
        System.arraycopy(tail, 0, result, data.length, 4);
        return result;
    }

    private static byte[] packData(byte[] data) {
        return addTail(addHeader(addDataLength(data)));
    }
    public static void sendData(Context context,byte[] data) {
        Intent intent = new Intent();
        intent.setClass(context, BluetoothService.class);
        intent.setAction("send");
        intent.putExtra("data", packData(data));
        context.startService(intent);
    }
}

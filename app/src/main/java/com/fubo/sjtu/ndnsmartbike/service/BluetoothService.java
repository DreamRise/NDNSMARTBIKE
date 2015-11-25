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
    private byte[] recBuffer = new byte[0];

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
                        outputStream.flush();
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
                            mBluetoothAdapter.cancelDiscovery();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReadThread() {
        if (mBluetoothSocket.isConnected()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (mBluetoothSocket.isConnected() && inputStream != null) {
                            byte[] temp = new byte[1024];
                            try {
                                int count = inputStream.read(temp);
                                addRecToBuffer(temp, count);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            break;
                    }
                }
            }).start();
        }
    }

    private synchronized void addRecToBuffer(byte[] temp, int count) {
        addBuffer(temp, count);
        byte[] result=validate(recBuffer);
        recBuffer = result;
    }

    //将蓝牙接收到的数据加入缓存中
    private void addBuffer(byte[] data,int count){
        byte[] temp=new byte[recBuffer.length+count];
        System.arraycopy(recBuffer, 0, temp, 0, recBuffer.length);
        System.arraycopy(data, 0, temp, recBuffer.length, count);
        recBuffer=temp;
    }

    //寻找包头位置
    private int findFirstHeader(byte[] data){
        if (data.length<12)
            return -1;
        else{
            for (int i=0;i<=data.length-4;i++){
                if (data[i]==0x01&&data[i+1]==0x11&&data[i+2]==0x22&&data[i+3]==0x75)
                    return i;
            }
        }
        return -1;
    }
    //寻找包尾的位置
    private int findFirstTail(byte[] data){
        if (data.length<12)
            return -1;
        else{
            for (int i=0;i<=data.length-4;i++){
                if (data[i]==0x02&&data[i+1]==0x12&&data[i+2]==0x23&&data[i+3]==0x76)
                    return i;
            }
        }
        return -1;
    }
    /*
    * 通过包头包尾以及数据包长度进行包验证
    * 若包正确，提取数据包进行分析
    * 一旦存在包尾，验证后更新缓存
    * */
    private  byte[] validate(byte[] data) {
        int indexTail = findFirstTail(data);
        while (indexTail!=-1) {
            int indexHeader = findFirstHeader(data);
            if (indexTail > indexHeader && indexHeader != -1) {
                int dataLength = util.bytesToInt(data, indexHeader + 4);
                //包正确
                if (dataLength == indexTail - indexHeader - 8) {
                    byte[] result = new byte[dataLength];
                    System.arraycopy(data, indexHeader + 8, result, 0, dataLength);
                    DataAnalyseService.onGetData(result, getApplicationContext());
                }
            }
            byte[] temp=new byte[data.length-indexTail-4];
            System.arraycopy(data,indexTail+4,temp,0,data.length-indexTail-4);
            data=temp;
            indexTail = findFirstTail(data);
        }
        return data;
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

    public static void sendData(Context context, byte[] data) {
        Intent intent = new Intent();
        intent.setClass(context, BluetoothService.class);
        intent.setAction("send");
        intent.putExtra("data", packData(data));
        context.startService(intent);
    }
}

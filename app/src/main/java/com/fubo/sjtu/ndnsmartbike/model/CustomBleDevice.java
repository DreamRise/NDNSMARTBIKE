package com.fubo.sjtu.ndnsmartbike.model;

import android.bluetooth.BluetoothDevice;

public class CustomBleDevice {

	private BluetoothDevice mBluetoothDevice;
	private int rssi;
	private byte[] scanRecord;

	public BluetoothDevice getmBluetoothDevice() {
		return mBluetoothDevice;
	}

	public CustomBleDevice(BluetoothDevice mBluetoothDevice, int rssi,
			byte[] scanRecord) {
		super();
		this.mBluetoothDevice = mBluetoothDevice;
		this.rssi = rssi;
		this.scanRecord = scanRecord;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public byte[] getScanRecord() {
		return scanRecord;
	}

	public void setScanRecord(byte[] scanRecord) {
		this.scanRecord = scanRecord;
	}

	public boolean isSameBluetoothDevice(String macAddress) {
		return mBluetoothDevice.getAddress().equalsIgnoreCase(macAddress);
	}

	public boolean isSameBluetoothDevice(BluetoothDevice bluetoothDevice) {
		return isSameBluetoothDevice(bluetoothDevice.getAddress());
	}
}

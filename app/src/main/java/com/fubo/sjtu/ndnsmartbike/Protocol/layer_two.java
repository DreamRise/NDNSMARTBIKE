package com.fubo.sjtu.ndnsmartbike.Protocol;

import java.util.Arrays;

public class layer_two {
	public int command_id = COMMAND_ID_UNDEFINED; // 一级命令码
	public int version = VERSION_UNDEFINED; // L2层版本号
	public byte[] payload = null; // 负载
	public int payload_len = 0; // 负载长度

	public static final int COMMAND_ID_UNDEFINED = 0; // 未定义的一级命令码

	public static final int VERSION_UNDEFINED = 0; // 未定义的L2层版本号

	public static final int SIZE = 2; // 协议规定的L2层长度

	private static final int VERSION_MSK = 0xF0; // L2层版本号掩码

	// 设置数据
	public void set_data(byte[] data, int offset) {
		data[offset + 0] = (byte) (this.command_id & 0xFF);

		data[offset + 1] = (byte) (((((int) data[offset + 1]) & (~VERSION_MSK)) | ((this.version << 4) & VERSION_MSK)) & 0xFF);
		return;
	}

	// 解析数据
	public byte[] parse(byte[] data, int offset) {
		this.command_id = ((int) data[offset + 0]) & 0xFF;

		this.version = ((int) (data[offset + 1] >> 4)) & (~VERSION_MSK);

		this.payload = Arrays.copyOfRange(data, offset + SIZE, data.length);
		this.payload_len = data.length - offset -SIZE;
		return payload;
	}

	// 处理L2数据，工厂模式
	public void handle_data(layer_two_callback cb) {
		switch (this.command_id) {
		case chat.COMMAND_ID:
			chat.handle_by_version(payload, this.version, cb);
			break;
		default:
			// pass
			break;
		}
		return;
	}
}

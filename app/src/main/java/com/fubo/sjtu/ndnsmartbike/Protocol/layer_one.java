package com.fubo.sjtu.ndnsmartbike.Protocol;

import com.fubo.sjtu.ndnsmartbike.utils.crc16_ccitt;
import com.fubo.sjtu.ndnsmartbike.utils.util;

import java.util.concurrent.atomic.AtomicInteger;



//蓝牙协议的L1层
public class layer_one {
	public boolean err = false; // 错误标志
	public boolean ack = false; // ack标志
	public int version = VERSION_UNDEFINED; // 版本号
	public int payload_len = 0; // 负载长度
	public int crc16 = 0; // crc16校验值
	public int seq_id = SEQ_ID_UNDEFINED; // 序号

	public static final int SEQ_ID_UNDEFINED = 0; // 未定义的seq_id
	public static final int SEQ_ID_START = SEQ_ID_UNDEFINED + 1; // seq_id起始值

	private static AtomicInteger s_send_seq_id = new AtomicInteger(SEQ_ID_START); // 发送专用seq_id的专用自增计数器

	// L1层版本
	public static final int VERSION_UNDEFINED = 0; // 版本未定义
	public static final int VERSION_ONE = 1;

	public static final int SIZE = 8; // 协议规定L1层长度

	private static final byte MAGIC = (byte) 0xAB; // L1层魔数

	// 各种掩码
	private static final int ERR_MSK = 0x20; // 错误掩码
	private static final int ACK_MSK = 0x10; // ACK掩码
	private static final int VERSION_MSK = 0xF0; // 版本号掩码

	private static final int CURRENT_VERSION = VERSION_ONE; // 当前版本

	// 各种返回值
	public static int TO_BE_CONTINUED = 1; // 未完待续
	public static int OK = 0; // 正常
	public static int MALFORMED = -1; // 不符合协议数据
	public static int CRC_FAILED = -2; // crc16校验失败

	// 设置L1层数据
	public void set_data(byte[] data) {
		util.mem_zero(data, SIZE);

		data[0] = (byte) MAGIC;

		if (this.err) {
			data[1] |= ERR_MSK;
		}

		if (this.ack) {
			data[1] |= ACK_MSK;
		}

		data[1] = (byte) ((data[1] & VERSION_MSK) | (CURRENT_VERSION & (~VERSION_MSK)));

		int payload_len = data.length - SIZE;
		util.uint16_to_host(payload_len, data, 2);

		int crc16 = crc16_ccitt.calc_crc16_ccitt(data, SIZE, payload_len);
		util.uint16_to_host(crc16, data, 4);

		util.uint16_to_host(get_send_seq_id(), data, 6);
		return;
	}

	// 解析L1层数据
	public int parse(byte[] data) {
		if (MAGIC != data[0]) {
			return MALFORMED;
		}

		this.err = (0 != (data[1] & ERR_MSK));

		this.ack = (0 != (data[1] & ACK_MSK));

		this.version = (data[1] & (~VERSION_MSK));

		int payload_len = data.length - SIZE;
		int payload_len_expect = util.host_to_unit16(data, 2);
		if (payload_len != payload_len_expect) {
			return MALFORMED;
		}
		this.payload_len = payload_len;

		int crc = crc16_ccitt.calc_crc16_ccitt(data, SIZE, payload_len);
		int crc_expect = util.host_to_unit16(data, 4);
		/*if (crc != crc_expect) {
			return CRC_FAILED;
		}*/
		this.crc16 = crc;

		this.seq_id = util.host_to_unit16(data, 6);
		return OK;
	}

	// 读取payload长度
	public static int read_payload_len(byte[] data, int[] payload) {
		if (data.length <= 0 || MAGIC != data[0]) {
			return MALFORMED;
		}

		if (data.length < SIZE) {
			return TO_BE_CONTINUED;
		}

		payload[0] = util.host_to_unit16(data, 2);
		return OK;
	}

	// 获取发送专用的seq_id
	public static int get_send_seq_id() {
		return s_send_seq_id.getAndIncrement();
	}
}

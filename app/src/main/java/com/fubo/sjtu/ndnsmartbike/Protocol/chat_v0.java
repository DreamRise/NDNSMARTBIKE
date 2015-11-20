package com.fubo.sjtu.ndnsmartbike.Protocol;

import com.fubo.sjtu.ndnsmartbike.utils.util;

import java.util.Arrays;


//聊天的版本0，测试专用
public class chat_v0 {
	public final static int VERSION = 0; // 聊天版本

	private final static int HEADER_SIZE = 7; // 头部长度

	private final static byte SIMPLE_TEXT_TYPE = 0; // 简单文字聊天

	// 封包
	public static byte[] simple_text_pack(byte[] chat_content) {
		int len = layer_one.SIZE + layer_two.SIZE
				+ chat_content.length;
		byte[] result = new byte[len];


		// copy聊天内容
		System.arraycopy(chat_content, 0, result, layer_one.SIZE
				+ layer_two.SIZE , chat_content.length);

		// 处理L2层
		layer_two two = new layer_two();
		two.command_id = chat.COMMAND_ID;
		two.version = chat_v0.VERSION;
		two.set_data(result, layer_one.SIZE);

		// 处理L1层
		layer_one one = new layer_one();
		one.set_data(result);
		return result;
	}

	// 处理数据
	public static void handle(byte[] payload, layer_two_callback cb) {
		byte type = payload[0];
		switch (type) {
		case SIMPLE_TEXT_TYPE:
			handle_simple_text(payload, cb);
			break;
		default:
			break;
		}
		return;
	}

	// 处理简单文字信息
	private static void handle_simple_text(byte[] payload, layer_two_callback cb) {
		int recv_id = util.host_to_unit16(payload, 1);
		int send_id = util.host_to_unit16(payload, 3);
		int len = util.host_to_unit16(payload, 5);
		byte[] msg = Arrays
				.copyOfRange(payload, HEADER_SIZE, HEADER_SIZE + len);
		cb.on_simple_text_chat_recieved(recv_id, send_id, msg);
		return;
	}
}

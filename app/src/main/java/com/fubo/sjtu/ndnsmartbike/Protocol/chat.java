package com.fubo.sjtu.ndnsmartbike.Protocol;

public class chat {
	public final static int COMMAND_ID = 0x5; // 命令ID

	// 根据版本选择处理函数
	public static void handle_by_version(byte[] payload, int version, layer_two_callback cb) {
		switch (version) {
		case chat_v0.VERSION:
			chat_v0.handle(payload, cb);
			break;
		default:
			// pass
			break;
		}
		return;
	}
}

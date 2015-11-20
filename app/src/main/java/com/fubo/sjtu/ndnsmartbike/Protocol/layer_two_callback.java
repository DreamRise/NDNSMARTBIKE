package com.fubo.sjtu.ndnsmartbike.Protocol;


public interface layer_two_callback {
	//收到简单文字聊天消息
	void on_simple_text_chat_recieved(int recv_id, int send_id, byte[] msg);
	
	//解析到超声波距离与精度
	void onGetUltrasonicDistance(int distance, int accuracy);
	
	//解析到加速度计数据与精度
	void onGetAccelerometer(int[] acc, int[] ang, int[] att, int accuracy);
}


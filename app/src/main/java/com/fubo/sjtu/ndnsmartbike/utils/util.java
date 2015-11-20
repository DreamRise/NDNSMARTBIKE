package com.fubo.sjtu.ndnsmartbike.utils;

public class util {
	// byte[]按字节置0
	public static void mem_zero(byte[] data, int len) {
		int size = len > data.length ? data.length : len;
		for (int ii = 0; ii < size; ++ii) {
			data[ii] = 0;
		}
		return;
	}

	// 双字转换到网络字节序
	public static void uint16_to_host(int u, byte[] d, int offset) {
		d[offset] = (byte) ((u >> 8) & 0xFF);
		d[offset + 1] = (byte) (u & 0xFF);
		return;
	}

	// 网络字节序转换到双字
	public static int host_to_unit16(final byte[] d, int offset) {
		return ((((int) d[offset]) & 0xFF) << 8)
				| (((int) d[offset + 1]) & 0xFF);
	}

	// byte[]连接
	public static byte[] byte_array_append(byte[] data1, byte[] data2) {
		int len = data1.length + data2.length;
		byte[] result = new byte[len];
		System.arraycopy(data1, 0, result, 0, data1.length);
		System.arraycopy(data2, 0, result, data1.length, data2.length);
		return result;
	}
	/**
	 * 截取字节数组中的bit，高位补0
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public static byte[] arrayBitCut(byte[] data, int offset, int len){
        byte[] result= new byte[len%8==0? len/8:len/8+1];
        if (offset+len>data.length*8) return null;
        byte [] temp=data;
        byte [] tempResult=new byte[(len+offset%8)%8==0? (len+offset%8)/8:(len+offset%8)/8+1];
        System.arraycopy(temp,offset/8,tempResult,0,(len+offset%8)%8==0? (len+offset%8)/8:(len+offset%8)/8+1);
        int j=offset%8;
        tempResult[0]=(byte)(tempResult[0]<<j);
        tempResult[0]=(byte)(tempResult[0]>>j);
        if ((len+offset%8)%8!=0) {
            int k = (len + offset % 8) / 8+1, l = (len + offset % 8) % 8;
            for (int m = k; m > 1; m--)
                tempResult[m - 1] = (byte) ((tempResult[m - 1]&0xff) >> (8 - l) | (tempResult[m - 2]&0xff) << l);
            tempResult[0] = (byte) (tempResult[0] >> (8 - l));
        }
        System.arraycopy(tempResult,tempResult.length-result.length,result,0,result.length);
        return result;
    }

	public static byte[] intToBytes(int value)
	{
		byte[] src = new byte[4];
		src[0] = (byte) ((value>>24) & 0xFF);
		src[1] = (byte) ((value>>16)& 0xFF);
		src[2] = (byte) ((value>>8)&0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ( ((src[offset] & 0xFF)<<24)
				|((src[offset+1] & 0xFF)<<16)
				|((src[offset+2] & 0xFF)<<8)
				|(src[offset+3] & 0xFF));
		return value;
	}
}

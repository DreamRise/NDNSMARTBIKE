package com.fubo.sjtu.ndnsmartbike.service;

import android.content.Context;

import com.fubo.sjtu.ndnsmartbike.Protocol.DataPacketGenerator;
import com.fubo.sjtu.ndnsmartbike.Protocol.DataPacketProcessor;
import com.fubo.sjtu.ndnsmartbike.Protocol.InterestPacketGenerator;
import com.fubo.sjtu.ndnsmartbike.Protocol.InterestPacketProcessor;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;

import net.sf.json.JSONObject;

/**
 * Created by sjtu on 2015/11/17.
 */
public class DataAnalyseService {

    public static void onGetData(byte[] data, Context context) {
        try {
            String dataString = new String(data);
            JSONObject jsonObject = JSONObject.fromObject(dataString);
            switch (jsonObject.getString(GlobalMember.PACKET_TYPE)) {
                //收到data包，进行data包相关操作
                case DataPacketGenerator.DATA_TYPE:
                    DataPacketProcessor.onGetDataPacket(dataString, context);
                    break;
                //收到interest包，进行interest包操作
                case InterestPacketGenerator.INTEREST_TYPE:
                    InterestPacketProcessor.onGetInterestPacket(dataString, context);
                    break;
                default:
                    break;
            }
        }catch(Exception e){

        }
    }

}

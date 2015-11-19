package com.fubo.sjtu.ndnsmartbike.Protocol;


import com.fubo.sjtu.ndnsmartbike.model.DataPacket;
import com.fubo.sjtu.ndnsmartbike.model.InterestPacket;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sjtu on 2015/11/17.
 */
public class DataPacketGenerator {

    public static final String DATA_TYPE= GlobalMember.PACKAGE_NAME+".data_type";
    public static final String ACTION_SEND_ACTIVITY = GlobalMember.PACKAGE_NAME + "" +
            ".action_send_activity";

    public static DataPacket generateBaseDataPacket(InterestPacket interestPacket,String content){
        DataPacket dataPacket=new DataPacket();
        dataPacket.setId(UUIDUtil.getUUID());
        dataPacket.setBuildDate(new Date());
        dataPacket.setName(interestPacket.getName());
        dataPacket.setReceiverId(interestPacket.getPublisherId());
        dataPacket.setAction(interestPacket.getAction());
        dataPacket.setContent(content);
        return dataPacket;
    }
    public static String generateSendDataPacket(DataPacket dataPacket) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalMember.PACKET_TYPE,DATA_TYPE);
            jsonObject.put(GlobalMember.PACKET_NAME, dataPacket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}

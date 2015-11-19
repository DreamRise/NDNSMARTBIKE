package com.fubo.sjtu.ndnsmartbike.Protocol;


import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.model.InterestPacket;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by sjtu on 2015/11/17.
 */
public class InterestPacketGenerator {

    public static final String INTEREST_TYPE= GlobalMember.PACKAGE_NAME+".interest_type";

    public static final String ACTION_REQUEST_ACTIVITY = GlobalMember.PACKAGE_NAME + "" +
            ".action_request_activity";

    public static final int INTEREST_PACKET_NORMAL_FLAG=0;


    public static String generateSendInterestPacket(InterestPacket interestPacket) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalMember.PACKET_TYPE,INTEREST_TYPE);
            jsonObject.put(GlobalMember.PACKET_NAME, interestPacket);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static InterestPacket generateRequestAllInterestPacket() {
        InterestPacket interestPacket = new InterestPacket();
        interestPacket.setId(UUIDUtil.getUUID());
        interestPacket.setBuildDate(new Date());
        interestPacket.setFlag(INTEREST_PACKET_NORMAL_FLAG);
        interestPacket.setPublisherId(MyApplication.getUser().getUserId());
        interestPacket.setName(GlobalMember.NAME_ALL_ACTIVITY);
        interestPacket.setAction(ACTION_REQUEST_ACTIVITY);
        return interestPacket;
    }
    public static InterestPacket generateRequestNewInterestPacket() {
        InterestPacket interestPacket = new InterestPacket();
        interestPacket.setId(UUIDUtil.getUUID());
        interestPacket.setBuildDate(new Date());
        interestPacket.setFlag(INTEREST_PACKET_NORMAL_FLAG);
        interestPacket.setPublisherId(MyApplication.getUser().getUserId());
        interestPacket.setName(GlobalMember.NAME_NEW_ACTIVITY);
        interestPacket.setAction(ACTION_REQUEST_ACTIVITY);
        return interestPacket;
    }
}

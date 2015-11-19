package com.fubo.sjtu.ndnsmartbike.Protocol;


import com.fubo.sjtu.ndnsmartbike.model.DataPacket;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.model.InterestPacket;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;

import java.util.Date;

/**
 * Created by 波 on 2015/11/18 0018.
 */
public class ForwardInfoGenerator {

    public static ForwardInfo generateForwardInfoFromDataPacket(DataPacket dataPacket){
        ForwardInfo forwardInfo=new ForwardInfo();
        forwardInfo.setId(UUIDUtil.getUUID());
        forwardInfo.setBuildDate(new Date());
        forwardInfo.setDataName(dataPacket.getName());
        forwardInfo.setInterestName(dataPacket.getName());
        forwardInfo.setFlag(ForwardInfo.FORWARD_FLAG_NORMAL);
        forwardInfo.setType(ForwardInfo.FORWARD_DATA_TYPE);
        return forwardInfo;
    }
    public static ForwardInfo generateForwardInfoFromInterestPacket(InterestPacket interestPacket){
        ForwardInfo forwardInfo=new ForwardInfo();
        forwardInfo.setId(UUIDUtil.getUUID());
        forwardInfo.setBuildDate(new Date());
        forwardInfo.setInterestName(interestPacket.getName());
        forwardInfo.setFlag(ForwardInfo.FORWARD_FLAG_NORMAL);
        forwardInfo.setType(ForwardInfo.FORWARD_INTEREST_TYPE);
        return forwardInfo;
    }
}

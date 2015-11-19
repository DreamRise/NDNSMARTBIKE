package com.fubo.sjtu.ndnsmartbike.Protocol;

import android.content.Context;
import android.content.Intent;

import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.database.ActivityInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.database.ForwardInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.model.DataPacket;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.service.BleService;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/17.
 */
public class DataPacketProcessor {

    public static void onGetDataPacket(String data, Context context) {
        JSONObject jsonObject = JSONObject.fromObject(data);
        DataPacket dataPacket = (DataPacket) jsonObject.get(GlobalMember.PACKET_NAME);
        switch (dataPacket.getAction()) {
            //处理和活动有关的数据
            case DataPacketGenerator.ACTION_SEND_ACTIVITY:
                processDataPacketWithActivityAction(dataPacket, context);
                break;
            default:
                break;
        }
    }

    //当收到Data packet时，先查询转发表中是否转发过同名的兴趣包
    //如果没有，有两种情况，1是没转发过该兴趣包，2是转发过，但也转发过数据包，完成了转发过程，则不做任何响应
    //如果转发过同名的，更新缓存，如果data packet中的receiverId和自己id匹配，通知界面更新
    //根据转发表中的兴趣包来源更新data packet中的receiverId，更新转发兴趣包，更新本地缓存，记录转发数据包
    private static void processDataPacketWithActivityAction(DataPacket dataPacket, Context
            context) {
        ActivityInfoDataHelper activityInfoDataHelper = ActivityInfoDataHelper.getInstance(context);
        ForwardInfoDataHelper forwardInfoDataHelper = new ForwardInfoDataHelper(context);
        List<ForwardInfo> forwardInfos = forwardInfoDataHelper.selectForwardInfoByInterstName(dataPacket.getName());
        if (forwardInfos.size() == 0) {
            activityInfoDataHelper.closeDatabase();
            forwardInfoDataHelper.closeDatabase();
            return;
        }
        //更新数据库数据
        JSONArray jsonArray = JSONArray.fromObject(dataPacket.getContent());
        boolean hasNewActivity=false;
        List<ActivityInfo> newActivityInfos = new ArrayList<>();
        if (jsonArray!=null && jsonArray.size()>0){
            for (int i=0;i<jsonArray.size();i++) {
                ActivityInfo activityInfo = (ActivityInfo) jsonArray.get(i);
                if (!activityInfoDataHelper.hasSameActivity(activityInfo.getId())) {
                    activityInfoDataHelper.insertActivity(activityInfo);
                    hasNewActivity = true;
                    newActivityInfos.add(activityInfo);
                }
            }
        }
        //更新数据包以及转发表
        boolean hasNewRequest=false;
        for(int i=0;i<forwardInfos.size();i++) {
            if (!StringUtils.contains(dataPacket.getReceiverId(), forwardInfos.get(i)
                    .getInterestFrom())) {
                dataPacket.setReceiverId(dataPacket.getReceiverId()+GlobalMember.NAME_SEPARATOR+forwardInfos.get(i).getInterestFrom());
                hasNewRequest = true;
            }
            forwardInfoDataHelper.invalidForwardInfo(forwardInfos.get(i));
        }

        //本身要这个信息
        if (StringUtils.contains(dataPacket.getReceiverId(), MyApplication.getUser().getUserId())) {
            //将是否含有新数据传递给界面，进行相应的界面刷新
            if (hasNewActivity) {
                Intent intent = new Intent();
                intent.setAction(GlobalMember.ACTION_HAS_NEW_ACTIVITY);
                context.sendBroadcast(intent);
            }
            else{
                Intent intent = new Intent();
                intent.setAction(GlobalMember.ACTION_HAS_NOT_NEW_ACTIVITY);
                context.sendBroadcast(intent);
            }
        }
        //如果有新的请求者才转发，否则不转发
        if (hasNewRequest)
            //打包数据包发送
            BleService.BlePublicAction.bleSendData(context,DataPacketGenerator.generateSendDataPacket(dataPacket).getBytes());
    }
}

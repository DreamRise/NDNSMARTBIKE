package com.fubo.sjtu.ndnsmartbike.Protocol;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.fubo.sjtu.ndnsmartbike.MyApplication;
import com.fubo.sjtu.ndnsmartbike.database.ActivityInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.database.ForwardInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.model.DataPacket;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.service.BluetoothService;
import com.fubo.sjtu.ndnsmartbike.utils.GlobalMember;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/17.
 */
public class DataPacketProcessor {

    public static void onGetDataPacket(String data, Context context) {
        JSONObject jsonObject = null;
        DataPacket dataPacket = null;
        try {
            jsonObject = JSONObject.parseObject(data);
            dataPacket = JSON.parseObject(jsonObject.getString(GlobalMember.PACKET_NAME), DataPacket.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        ForwardInfoDataHelper forwardInfoDataHelper = ForwardInfoDataHelper.getInstance(context);
        List<ForwardInfo> forwardInfos = forwardInfoDataHelper.selectForwardInfoByInterstName(dataPacket.getName());
        if (forwardInfos.size() == 0) {
            /*activityInfoDataHelper.closeDatabase();
            forwardInfoDataHelper.closeDatabase();*/
            return;
        }
        //更新数据库数据
        JSONArray jsonArray = null;
        List<ActivityInfo> activityInfos = new ArrayList<>();
        try {
            activityInfos = JSON.parseArray(dataPacket.getContent(),
                    ActivityInfo.class);
            //jsonArray = JSONArray.parseArray(dataPacket.getContent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        boolean hasNewActivity=false;
        List<ActivityInfo> newActivityInfos = new ArrayList<>();
        if (activityInfos!=null && activityInfos.size()>0){
            for (int i=0;i<activityInfos.size();i++) {
                if (!activityInfoDataHelper.hasSameActivity(activityInfos.get(i).getId())) {
                    activityInfoDataHelper.insertActivity(activityInfos.get(i));
                    hasNewActivity = true;
                    newActivityInfos.add(activityInfos.get(i));
                }
            }
        }
        //更新数据包以及转发表
        boolean hasNewRequest=false;
        for(int i=0;i<forwardInfos.size();i++) {
            if (!dataPacket.getReceiverId().contains(forwardInfos.get(i).getInterestFrom())) {
                dataPacket.setReceiverId(dataPacket.getReceiverId()+GlobalMember.NAME_SEPARATOR+forwardInfos.get(i).getInterestFrom());
                hasNewRequest = true;
            }
            forwardInfoDataHelper.invalidForwardInfo(forwardInfos.get(i));
        }

        //本身要这个信息
        if (dataPacket.getReceiverId().contains(MyApplication.getUser().getUserId())) {
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
        System.out.println(DataPacketGenerator.generateSendDataPacket
                (dataPacket).getBytes());
            BluetoothService.sendData(context, DataPacketGenerator.generateSendDataPacket
                    (dataPacket).getBytes());
    }
}

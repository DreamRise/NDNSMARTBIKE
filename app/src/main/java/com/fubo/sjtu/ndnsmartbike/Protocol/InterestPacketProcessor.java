package com.fubo.sjtu.ndnsmartbike.Protocol;

import android.content.Context;

import com.fubo.sjtu.ndnsmartbike.database.ActivityInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.database.ForwardInfoDataHelper;
import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.model.DataPacket;
import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.model.InterestPacket;
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
public class InterestPacketProcessor {

    public static void onGetInterestPacket(String interest,Context context) {
        JSONObject jsonObject = JSONObject.fromObject(interest);
        InterestPacket interestPacket = (InterestPacket)jsonObject.get(GlobalMember.PACKET_NAME);
        //拿到兴趣包，根据兴趣包的action来分类操作
        switch (interestPacket.getAction()){
            //处理和活动有关的兴趣包
            case InterestPacketGenerator.ACTION_REQUEST_ACTIVITY:
                processInterestPacketWithActivityAction(interestPacket, context);
                break;

            //还可以处理其他相关的兴趣包

            default:
                break;
        }
    }

    //处理总的活动兴趣包
    private static void processInterestPacketWithActivityAction(InterestPacket interestPacket,Context context){
        switch (interestPacket.getName()){
            case GlobalMember.NAME_ALL_ACTIVITY:
                processInterestPacketWithAllActivity(interestPacket, context);
                break;
            case GlobalMember.NAME_NEW_ACTIVITY:
                processInterestPacketWithNewActivity(interestPacket,context);
                break;
        }
    }
    //查询缓存，如果有新的活动，转发新的活动，如果没有，记录兴趣包，转发兴趣包
    private static void processInterestPacketWithNewActivity(InterestPacket interestPacket,Context context){
        ActivityInfoDataHelper activityInfoDataHelper = ActivityInfoDataHelper.getInstance(context);
        ForwardInfoDataHelper forwardInfoDataHelper = new ForwardInfoDataHelper(context);
        List<ActivityInfo> activityInfos=activityInfoDataHelper.selectAllValidActivity();
        if (activityInfos.size()>0) {
            //比较缓存中的活动和Interest包中meta带有的活动id
            List<ActivityInfo> activityInfos1 = new ArrayList<>();
            for (int i=0;i<activityInfos.size();i++){
                if (StringUtils.contains(interestPacket.getMeta(), activityInfos.get(i).getId()))
                    continue;
                else
                    activityInfos1.add(activityInfos.get(i));
            }
            //表示有新活动，转发新的活动，记录转发情况
            if (activityInfos1.size()>0){
                recordAndTransmitDataPacket(context, activityInfos1, interestPacket, forwardInfoDataHelper);
            }
            else
                recordAndTransmitInterestPacket(context, interestPacket, forwardInfoDataHelper);
        }
        else
            recordAndTransmitInterestPacket(context, interestPacket, forwardInfoDataHelper);
        forwardInfoDataHelper.closeDatabase();
        activityInfoDataHelper.closeDatabase();
    }
    private static void processInterestPacketWithAllActivity(InterestPacket interestPacket,Context context) {
        ActivityInfoDataHelper activityInfoDataHelper = ActivityInfoDataHelper.getInstance(context);
        ForwardInfoDataHelper forwardInfoDataHelper = new ForwardInfoDataHelper(context);
        List<ActivityInfo> activityInfos=activityInfoDataHelper.selectAllValidActivity();
        //有数据，直接将数据打包成数据包广播出去，并记录到转发表中
        if (activityInfos.size()>0) {
            recordAndTransmitDataPacket(context, activityInfos, interestPacket, forwardInfoDataHelper);
        }
        //没有数据，检查是否已经转发过该兴趣包，如果没有转发过，记录兴趣包，并且将兴趣包转发出去
        else {
            //进行记录转发
            recordAndTransmitInterestPacket(context, interestPacket, forwardInfoDataHelper);
        }
        forwardInfoDataHelper.closeDatabase();
        activityInfoDataHelper.closeDatabase();
    }
    private static void recordAndTransmitDataPacket(Context context,List<ActivityInfo> activityInfos,InterestPacket interestPacket,ForwardInfoDataHelper forwardInfoDataHelper){
        JSONArray jsonArray = JSONArray.fromObject(activityInfos);
        DataPacket dataPacket = DataPacketGenerator.generateBaseDataPacket(interestPacket, jsonArray.toString());
        String data = DataPacketGenerator.generateSendDataPacket(dataPacket);
        BleService.BlePublicAction.bleSendData(context,data.getBytes());
        //生成转发包，进行记录
        ForwardInfo forwardInfo = ForwardInfoGenerator.generateForwardInfoFromDataPacket(dataPacket);
        forwardInfoDataHelper.insertForwardInfo(forwardInfo);
    }
    private static void recordAndTransmitInterestPacket(Context context,InterestPacket interestPacket,ForwardInfoDataHelper forwardInfoDataHelper){
        if (!forwardInfoDataHelper.hasForwardInfoWithSameInterestFrom(interestPacket.getPublisherId())) {
            ForwardInfo forwardInfo = ForwardInfoGenerator.generateForwardInfoFromInterestPacket(interestPacket);
            forwardInfoDataHelper.insertForwardInfo(forwardInfo);
            BleService.BlePublicAction.bleSendData(context, InterestPacketGenerator.generateSendInterestPacket(interestPacket).getBytes());
        }
    }
}

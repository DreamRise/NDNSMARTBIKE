package com.fubo.sjtu.ndnsmartbike;

import android.app.Application;
import android.content.SharedPreferences;

import com.fubo.sjtu.ndnsmartbike.model.UserInfo;
import com.fubo.sjtu.ndnsmartbike.utils.UUIDUtil;


/**
 * Created by sjtu on 2015/11/15.
 */
public class MyApplication extends Application {

    private static UserInfo userInfo = new UserInfo();
    @Override
    public void onCreate() {
        super.onCreate();
        initUserInfo();
    }

    private void initUserInfo() {
        SharedPreferences sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        if (sharedPreferences.getBoolean("registered",false)==true) {
            userInfo.setBirthYear(sharedPreferences.getInt("birthYear", 0));
            userInfo.setFlag(sharedPreferences.getInt("flag", 0));
            userInfo.setReserve(sharedPreferences.getString("reserve", ""));
            userInfo.setSex(sharedPreferences.getInt("sex", 0));
            userInfo.setUserDes(sharedPreferences.getString("userDes", getResources().getString(R
                    .string.user_des)));
            userInfo.setUserName(sharedPreferences.getString("userName", getResources().getString
                    (R.string.user_name)));
            userInfo.setUserId(sharedPreferences.getString("userId",UUIDUtil.getUUID()));
            userInfo.setUserImage(sharedPreferences.getString("userImage",""));
        }
        else{
            userInfo.setBirthYear(0);
            userInfo.setFlag(0);
            userInfo.setReserve("");
            userInfo.setSex(0);
            userInfo.setUserDes(getResources().getString(R.string.user_des));
            userInfo.setUserName(getResources().getString(R.string.user_name));
            userInfo.setUserId(UUIDUtil.getUUID());
            userInfo.setUserImage("");
        }
    }

    public static UserInfo getUser(){
        return userInfo;
    }
    public static void setUserInfo(UserInfo user) {
        userInfo = user;
    }
}

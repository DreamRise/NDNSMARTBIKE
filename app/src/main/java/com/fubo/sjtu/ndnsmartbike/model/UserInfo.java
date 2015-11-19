package com.fubo.sjtu.ndnsmartbike.model;

import java.io.Serializable;

/**
 * Created by sjtu on 2015/11/15.
 */
public class UserInfo implements Serializable {

    private String userId;
    private String userName;
    private String userDes;
    private String userImage;
    private int sex;
    private int birthYear;
    private String reserve;
    private int flag;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDes() {
        return userDes;
    }

    public void setUserDes(String userDes) {
        this.userDes = userDes;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userDes='" + userDes + '\'' +
                ", userImage='" + userImage + '\'' +
                ", sex=" + sex +
                ", birthYear=" + birthYear +
                ", reserve='" + reserve + '\'' +
                ", flag=" + flag +
                '}';
    }
}

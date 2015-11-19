package com.fubo.sjtu.ndnsmartbike.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sjtu on 2015/11/17.
 */
public class ForwardInfo implements Serializable {

    public static final String ID="id";
    public static final String TYPE="type";
    public static final String BUILD_DATE="build_date";
    public static final String INTEREST_NAME="interest_name";
    public static final String DATA_NAME="data_name";
    public static final String FLAG="flag";
    public static final String INTEREST_FROM = "interest_from";

    public static final int FORWARD_DATA_TYPE=1;
    public static final int FORWARD_INTEREST_TYPE=0;

    public static final int FORWARD_FLAG_NORMAL=0;
    public static final int FORWARD_FLAG_DELETE=1;
    private String id;
    private int type;//0表示转发了兴趣包，1表示转发了数据包
    private String interestFrom;//记录兴趣包的来源，当数据返回时，添加数据包的receiverId。
    private Date buildDate;
    private String interestName;
    private String dataName;
    private int flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getBuildDate() {
        return buildDate;
    }

    public void setBuildDate(Date buildDate) {
        this.buildDate = buildDate;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getInterestFrom() {
        return interestFrom;
    }

    public void setInterestFrom(String interestFrom) {
        this.interestFrom = interestFrom;
    }
}

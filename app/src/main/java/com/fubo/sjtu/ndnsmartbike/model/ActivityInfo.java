package com.fubo.sjtu.ndnsmartbike.model;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by sjtu on 2015/11/17.
 */
public class ActivityInfo implements Serializable {

    public static final String ID = "id";
    public static final String PUBLISHER_ID = "publisher_id";
    public static final String ACTIVITY_TITLE = "activity_title";
    public static final String ACTIVITY_START_PLACE = "activity_start_place";
    public static final String ACTIVITY_END_PLACE = "activity_end_place";
    public static final String ACTIVITY_DATE = "activity_date";
    public static final String ACTIVITY_BUILD_DATE = "activity_build_date";
    public static final String ACTIVITY_DES = "activity_des";
    public static final String RESERVE = "reserve";
    public static final String FLAG = "flag";

    public static final int ACTIVITY_FLAG_NORMAL=0;
    public static final int ACTIVITY_FLAG_DELETE=1;

    private String id;
    private String publisherId;
    private String activityTitle;
    private String activityStartPlace;
    private String activityEndPlace;
    private Date activityDate;
    private Date activityBuildDate;
    private String activityDes;
    private String reserve;
    private int flag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityStartPlace() {
        return activityStartPlace;
    }

    public void setActivityStartPlace(String activityStartPlace) {
        this.activityStartPlace = activityStartPlace;
    }

    public String getActivityEndPlace() {
        return activityEndPlace;
    }

    public void setActivityEndPlace(String activityEndPlace) {
        this.activityEndPlace = activityEndPlace;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public Date getActivityBuildDate() {
        return activityBuildDate;
    }

    public void setActivityBuildDate(Date activityBuildDate) {
        this.activityBuildDate = activityBuildDate;
    }

    public String getActivityDes() {
        return activityDes;
    }

    public void setActivityDes(String activityDes) {
        this.activityDes = activityDes;
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
}

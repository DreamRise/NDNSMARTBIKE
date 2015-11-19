package com.fubo.sjtu.ndnsmartbike.utils;

/**
 * Created by sjtu on 2015/11/17.
 */
public class GlobalMember {

    public static final String PACKAGE_NAME="com.fubo.sjtu.ndnbike";
    public static final String PACKET_TYPE=PACKAGE_NAME+".packet_type";
    public static final String PACKET_NAME=PACKAGE_NAME+".packet_name";
    public static final String NAME_NEW_ACTIVITY = GlobalMember.PACKAGE_NAME + "" +
            ".request_new_activity";
    public static final String NAME_ALL_ACTIVITY = GlobalMember.PACKAGE_NAME + "" +
            ".request_all_activity";
    public static final String NAME_SEPARATOR = "/";

    public static final String ACTION_HAS_NEW_ACTIVITY = PACKAGE_NAME + ".action_has_new_activity";
    public static final String ACTION_HAS_NOT_NEW_ACTIVITY = PACKAGE_NAME + ".action_has_not_new_activity";
}

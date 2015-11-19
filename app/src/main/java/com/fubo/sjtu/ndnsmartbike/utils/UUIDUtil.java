package com.fubo.sjtu.ndnsmartbike.utils;

import java.util.UUID;

/**
 * Created by sjtu on 2015/11/15.
 */
public class UUIDUtil {

    public static String getUUID(){
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;
    }
}

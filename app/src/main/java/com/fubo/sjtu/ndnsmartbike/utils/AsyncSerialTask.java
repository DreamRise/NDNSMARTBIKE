package com.fubo.sjtu.ndnsmartbike.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class AsyncSerialTask {
	
	private static HandlerThread mHandlerThread;
    private static Handler mHandler;

    /**
     * initial in Application.java
     */
    public static void init(){
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("mainHandlerThread");
            mHandlerThread.start();
        }
        if (mHandlerThread != null && mHandler == null){
            mHandler = new Handler(mHandlerThread.getLooper());
        }
    }

    /**
     * execute the task in runnable
     * @param runnable
     */
    public static void executeTask(Runnable runnable){
        mHandler.post(runnable);
    }

}

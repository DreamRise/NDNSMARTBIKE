package com.fubo.sjtu.ndnsmartbike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;


/**
 * Created by sjtu on 2015/11/17.
 */
public class ActivityInfoSQLiteHelper extends SQLiteOpenHelper {
    public static final String TB_NAME = "tb_activity_info";

    public ActivityInfoSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "(" + ActivityInfo.ID + " primary " +
                "key," + ActivityInfo.FLAG + " integer," + ActivityInfo.ACTIVITY_BUILD_DATE + " " +
                "timestamp," +
                ActivityInfo.ACTIVITY_DATE + " timestamp," + ActivityInfo.ACTIVITY_DES + " " +
                " varchar," +
                ActivityInfo.ACTIVITY_END_PLACE + " varchar, " + ActivityInfo.ACTIVITY_START_PLACE
                + " varchar," + ActivityInfo.ACTIVITY_TITLE + " varchar," + ActivityInfo
                .PUBLISHER_ID + " varchar," + ActivityInfo.RESERVE + " varchar" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }
}

package com.fubo.sjtu.ndnsmartbike.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;


/**
 * Created by sjtu on 2015/11/17.
 */
public class ForwardInfoSQLiteHelper extends SQLiteOpenHelper {

    public static final String TB_NAME = "tb_forward_info";

    public ForwardInfoSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "(" + ForwardInfo.ID + " primary key,"
                + ForwardInfo.TYPE + " integer," + ForwardInfo.INTEREST_NAME + " varchar," +
                ForwardInfo.DATA_NAME + " varchar," + ForwardInfo.BUILD_DATE + " timestamp," +
                ForwardInfo.FLAG + " integer," +ForwardInfo.INTEREST_FROM +" varchar"+ ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
            onCreate(db);
        }
    }
}

package com.fubo.sjtu.ndnsmartbike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fubo.sjtu.ndnsmartbike.model.ActivityInfo;
import com.fubo.sjtu.ndnsmartbike.utils.SimpleDateFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/17.
 */
public class ActivityInfoDataHelper {

    private static final String DB_NAME = ActivityInfoSQLiteHelper.TB_NAME + ".db";
    private static final int DB_VERSION = 1;
    private static  SQLiteDatabase mSQLiteDatabase;
    private static ActivityInfoSQLiteHelper mActivityInfoSQLiteHelper;
    private static ActivityInfoDataHelper activityInfoDataHelper;

    private ActivityInfoDataHelper(Context context) {
        mActivityInfoSQLiteHelper = new ActivityInfoSQLiteHelper(context, DB_NAME, null,
                DB_VERSION);
        mSQLiteDatabase = mActivityInfoSQLiteHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        if (mSQLiteDatabase != null)
            mSQLiteDatabase.close();
        if (mActivityInfoSQLiteHelper != null)
            mActivityInfoSQLiteHelper.close();
        if (mActivityInfoSQLiteHelper!=null)
            mActivityInfoSQLiteHelper.close();
    }

    public List<ActivityInfo> selectAllValidActivity() {
        Cursor cursor = mSQLiteDatabase.query(ActivityInfoSQLiteHelper.TB_NAME, null,
                ActivityInfo.FLAG + "=" + ActivityInfo.ACTIVITY_FLAG_NORMAL + " and " +
                        ActivityInfo.ACTIVITY_DATE + "> datetime(\"now\",\"localtime\")", null,
                null, null, ActivityInfo.ACTIVITY_DATE + " desc");
        List<ActivityInfo> activityInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            activityInfos.add(getActivityFromCursor(cursor));
        }
        return activityInfos;
    }

    public void insertActivity(ActivityInfo activityInfo) {
        ContentValues values = new ContentValues();
        values.put(ActivityInfo.ACTIVITY_BUILD_DATE, SimpleDateFormatUtil.formatUtilDate(activityInfo.getActivityBuildDate(), SimpleDateFormatUtil.NORMAL_UTIL_DATE_FORMAT));
        values.put(ActivityInfo.ACTIVITY_DATE, SimpleDateFormatUtil.formatUtilDate(activityInfo
                .getActivityDate(), SimpleDateFormatUtil.NORMAL_UTIL_DATE_FORMAT));
        values.put(ActivityInfo.ACTIVITY_DES, activityInfo.getActivityDes());
        values.put(ActivityInfo.ACTIVITY_END_PLACE, activityInfo.getActivityEndPlace());
        values.put(ActivityInfo.ACTIVITY_START_PLACE, activityInfo.getActivityStartPlace());
        values.put(ActivityInfo.ACTIVITY_TITLE, activityInfo.getActivityTitle());
        values.put(ActivityInfo.FLAG, activityInfo.getFlag());
        values.put(ActivityInfo.ID, activityInfo.getId());
        values.put(ActivityInfo.PUBLISHER_ID, activityInfo.getPublisherId());
        values.put(ActivityInfo.RESERVE, activityInfo.getReserve());
        mSQLiteDatabase.insert(mActivityInfoSQLiteHelper.TB_NAME, null, values);
    }

    private ActivityInfo getActivityFromCursor(Cursor cursor) {
        ActivityInfo activityInfo = new ActivityInfo();
        if (cursor != null) {
            activityInfo.setActivityBuildDate(SimpleDateFormatUtil.getUtilDateFromString(cursor
                    .getString(cursor.getColumnIndex(ActivityInfo.ACTIVITY_BUILD_DATE))));
            activityInfo.setActivityDate(SimpleDateFormatUtil.getUtilDateFromString(cursor
                    .getString(cursor.getColumnIndex(ActivityInfo.ACTIVITY_DATE))));
            activityInfo.setActivityDes(cursor.getString(cursor.getColumnIndex(ActivityInfo
                    .ACTIVITY_DES)));
            activityInfo.setActivityEndPlace(cursor.getString(cursor.getColumnIndex(ActivityInfo
                    .ACTIVITY_END_PLACE)));
            activityInfo.setActivityStartPlace(cursor.getString(cursor.getColumnIndex
                    (ActivityInfo.ACTIVITY_START_PLACE)));
            activityInfo.setActivityTitle(cursor.getString(cursor.getColumnIndex(ActivityInfo
                    .ACTIVITY_TITLE)));
            activityInfo.setFlag(cursor.getInt(cursor.getColumnIndex(ActivityInfo.FLAG)));
            activityInfo.setId(cursor.getString(cursor.getColumnIndex(ActivityInfo.ID)));
            activityInfo.setPublisherId(cursor.getString(cursor.getColumnIndex(ActivityInfo
                    .PUBLISHER_ID)));
            activityInfo.setReserve(cursor.getString(cursor.getColumnIndex(ActivityInfo.RESERVE)));
        }
        return activityInfo;
    }
    public boolean hasSameActivity(String id) {
        Cursor cursor = mSQLiteDatabase.query(mActivityInfoSQLiteHelper.TB_NAME, null,
                ActivityInfo.ID + "=?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst())
            return true;
        else
            return false;
    }

    public static ActivityInfoDataHelper getInstance(Context context){
        if (activityInfoDataHelper==null){
            activityInfoDataHelper= new ActivityInfoDataHelper(context);
        }
        return activityInfoDataHelper;
    }
}

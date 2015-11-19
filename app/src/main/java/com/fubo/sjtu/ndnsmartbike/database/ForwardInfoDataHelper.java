package com.fubo.sjtu.ndnsmartbike.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fubo.sjtu.ndnsmartbike.model.ForwardInfo;
import com.fubo.sjtu.ndnsmartbike.utils.SimpleDateFormatUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sjtu on 2015/11/17.
 */
public class ForwardInfoDataHelper {
    private static final String DB_NAME = ForwardInfoSQLiteHelper.TB_NAME + ".db";
    private static final int DB_VERSION = 1;
    private SQLiteDatabase mSQLiteDatabase;
    private ForwardInfoSQLiteHelper mForwardInfoSQLiteHelper;

    public ForwardInfoDataHelper(Context context) {
        mForwardInfoSQLiteHelper = new ForwardInfoSQLiteHelper(context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mForwardInfoSQLiteHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        if (mSQLiteDatabase!=null)
            mSQLiteDatabase.close();
        if (mForwardInfoSQLiteHelper != null)
            mForwardInfoSQLiteHelper.close();
    }

    public List<ForwardInfo> selectForwardInfoByInterstName(String interestName) {
        Cursor cursor = mSQLiteDatabase.query(ForwardInfoSQLiteHelper.TB_NAME, null, ForwardInfo
                .INTEREST_NAME + "=? and" + ForwardInfo.FLAG + "="+ForwardInfo.FORWARD_FLAG_NORMAL+" and"+ForwardInfo.TYPE+"="+ForwardInfo.FORWARD_INTEREST_TYPE, new String[]{interestName},
                null, null, null);
        List<ForwardInfo> forwardInfos = new ArrayList<>();
        while(cursor.moveToNext()){
            ForwardInfo forwardInfo = getForwardInfoFromCursor(cursor);
            forwardInfos.add(forwardInfo);
        }
    return forwardInfos;
    }
    private ForwardInfo getForwardInfoFromCursor(Cursor cursor){
        ForwardInfo forwardInfo = new ForwardInfo();
        if (cursor!=null){
            forwardInfo.setFlag(cursor.getInt(cursor.getColumnIndex(ForwardInfo.FLAG)));
            forwardInfo.setBuildDate(SimpleDateFormatUtil.getUtilDateFromString(cursor.getString
                    (cursor.getColumnIndex(ForwardInfo.BUILD_DATE))));
            forwardInfo.setDataName(cursor.getString(cursor.getColumnIndex(ForwardInfo.DATA_NAME)));
            forwardInfo.setId(cursor.getString(cursor.getColumnIndex(ForwardInfo.ID)));
            forwardInfo.setInterestName(cursor.getString(cursor.getColumnIndex(ForwardInfo.INTEREST_NAME)));
            forwardInfo.setType(cursor.getInt(cursor.getColumnIndex(ForwardInfo.TYPE)));
            forwardInfo.setInterestFrom(cursor.getString(cursor.getColumnIndex(ForwardInfo.INTEREST_FROM)));
        }
        return forwardInfo;
    }
    public Boolean hasForwardInfoWithSameInterestFrom(String interestFrom){
        Cursor cursor = mSQLiteDatabase.query(ForwardInfoSQLiteHelper.TB_NAME, null, ForwardInfo
                        .INTEREST_FROM + "=? and" + ForwardInfo.FLAG + "="+ForwardInfo.FORWARD_FLAG_NORMAL+" and "+ForwardInfo.TYPE+"="+ForwardInfo.FORWARD_INTEREST_TYPE, new String[]{interestFrom},
                null, null, null);
        if (!cursor.moveToFirst())
            return false;
        else
            return true;
    }
    public ForwardInfo selectForwardInfoByDataName(String dataName) {
        Cursor cursor = mSQLiteDatabase.query(ForwardInfoSQLiteHelper.TB_NAME, null, ForwardInfo
                        .DATA_NAME + "=? and " + ForwardInfo.FLAG + "="+ForwardInfo.FORWARD_FLAG_NORMAL+" and "+ForwardInfo.TYPE+"="+ForwardInfo.FORWARD_DATA_TYPE, new String[]{dataName},
                null, null, null);
        if (!cursor.moveToFirst())
            return null;
        else {
            ForwardInfo forwardInfo = getForwardInfoFromCursor(cursor);
            return forwardInfo;
        }
    }
    public Boolean hasForwardInfoWithSameDataName(String dataName){
        Cursor cursor = mSQLiteDatabase.query(ForwardInfoSQLiteHelper.TB_NAME, null, ForwardInfo
                        .DATA_NAME + "=? and " + ForwardInfo.FLAG + "="+ForwardInfo.FORWARD_FLAG_NORMAL+" and "+ForwardInfo.TYPE+" = "+ForwardInfo.FORWARD_DATA_TYPE, new String[]{dataName},
                null, null, null);
        if (!cursor.moveToFirst())
            return false;
        else
            return true;
    }
    public void insertForwardInfo(ForwardInfo forwardInfo){
        ContentValues values = new ContentValues();
        values.put(ForwardInfo.ID, forwardInfo.getId());
        values.put(ForwardInfo.BUILD_DATE, SimpleDateFormatUtil.formatUtilDate(forwardInfo.getBuildDate(), SimpleDateFormatUtil.NORMAL_UTIL_DATE_FORMAT));
        values.put(ForwardInfo.DATA_NAME, forwardInfo.getDataName());
        values.put(ForwardInfo.FLAG, forwardInfo.getFlag());
        values.put(ForwardInfo.INTEREST_NAME, forwardInfo.getInterestName());
        values.put(ForwardInfo.TYPE, forwardInfo.getType());
        values.put(ForwardInfo.INTEREST_FROM,forwardInfo.getInterestFrom());
        mSQLiteDatabase.insert(mForwardInfoSQLiteHelper.TB_NAME, null, values);
    }

    public void invalidForwardInfo(ForwardInfo forwardInfo) {
        ContentValues values = new ContentValues();
        values.put(ForwardInfo.FLAG, ForwardInfo.FORWARD_FLAG_DELETE);
        mSQLiteDatabase.update(mForwardInfoSQLiteHelper.TB_NAME, values, ForwardInfo.ID + "=?",
                new String[]{forwardInfo.getId()});
    }
}

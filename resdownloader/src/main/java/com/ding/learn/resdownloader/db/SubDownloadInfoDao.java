package com.ding.learn.resdownloader.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.ding.learn.resdownloader.SubDownloadInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by jindingwei on 2019/7/7.
 */

public class SubDownloadInfoDao {
    private static final String TABLE_NAME = SubDownloadInfoDao.class.getSimpleName();

    private Context mContext;
    private DBOpenHelper mDBHelper;

    public SubDownloadInfoDao(Context context) {
        mContext = context;
        mDBHelper = new DBOpenHelper(context);
    }

    public static void createTable(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(id integer primary key autoincrement, " +
                "tag text, taskId Integer, start long, end long, finished long)");
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }

    public void insert(SubDownloadInfo downloadInfo) {
        if (downloadInfo == null) {
            return;
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("insert into " + TABLE_NAME + "(tag, taskId, start, end, finished) values(?, ?, ?, ?, ?)",
                new Object[]{downloadInfo.getTag(), downloadInfo.getTaskId(), downloadInfo.getStart(),
                        downloadInfo.getEnd(), downloadInfo.getFinished()});
    }

    public List<SubDownloadInfo> getSubDownloadInfos(String tag) {
        LinkedList<SubDownloadInfo> list = new LinkedList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        + " where tag = ?",
                new String[]{tag});
        while (cursor.moveToNext()) {
            SubDownloadInfo downloadInfo = new SubDownloadInfo();
            downloadInfo.setTag(tag);
            downloadInfo.setTaskId(cursor.getInt(cursor.getColumnIndex("taskId")));
            downloadInfo.setStart(cursor.getLong(cursor.getColumnIndex("start")));
            downloadInfo.setEnd(cursor.getLong(cursor.getColumnIndex("end")));
            downloadInfo.setFinished(cursor.getLong(cursor.getColumnIndex("finished")));
            list.add(downloadInfo);
        }
        cursor.close();
        return list;
    }

    public void updateFinished(String tag, int taskId, long finished) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("update "
                        + TABLE_NAME
                        + " set finished = ?"
                        + " where tag = ? and taskId = ?",
                new Object[]{finished, tag, taskId});
    }

    public void delete(String tag) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("delete from "
                        + TABLE_NAME
                        + " where tag = ?",
                new Object[]{tag});
    }

    public boolean exists(String tag, int taskId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        + " where tag = ? and taskId = ?",
                new String[]{tag, taskId + ""});
        boolean isExists = cursor.moveToNext();
        cursor.close();
        return isExists;
    }
}

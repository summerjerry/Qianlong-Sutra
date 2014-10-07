/**
 *
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 *
 * @Description :
 *
 */

package com.buddhism.sutra.data;

import com.buddhism.base.ContextProvider;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author summerxiaqing
 */
public class HistoryDbHelper extends SQLiteOpenHelper {
  private final static String DB_NAME = "history.db";
  private static final int DATABASE_VERSION = 1;
  private final static int DB_DEFAULT_HISTORY_SIZE = 20;
  protected Context mContext;
  private static HistoryDbHelper sDbHelper = null;
  private static String CREATE_SUTRA_HISTORY_TABLE = "sutraHistoryTable";
  public static int SUTRA_HISTORY_PERCENT = 10000;

  public static class SutraHistoryTableItem {
    static final String SINDEX = "s_id";
    static final String SNAME = "s_name";
    static final String READ_PERCENT = "percent";
    static final String CREATED_TIME = "c_time";

    public String sIndex;
    public String sName;
    public int readPercent; // ?/SUTRA_HISTORY_PERCENT
    public String createdTime;

    public SutraHistoryTableItem(String index, String name, int readPercent, String createdTime) {
      this.sIndex = index;
      this.sName = name;
      this.readPercent = readPercent;
      this.createdTime = createdTime;
    }
  }

  /**
   * Constructor
   * @throws IOException
   */
  private HistoryDbHelper(Context context) throws IOException {
    super(context, DB_NAME, null, DATABASE_VERSION);
    this.mContext = context;
  }

  public synchronized static HistoryDbHelper getInstance() {
    if (sDbHelper == null) {
      try {
        sDbHelper = new HistoryDbHelper(ContextProvider.getApplicationContext());
      } catch (IOException e) {
        throw new NullPointerException("History Database can not be null");
      }
    }

    return sDbHelper;
  }

  /*
   * (non-Javadoc)
   * @see
   * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase
   * )
   */
  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE IF NOT EXISTS " + CREATE_SUTRA_HISTORY_TABLE + " (" +
        SutraHistoryTableItem.SINDEX + " TEXT PRIMARY KEY," +
        SutraHistoryTableItem.SNAME + " TEXT," +
        SutraHistoryTableItem.READ_PERCENT + " INTEGER," +
        SutraHistoryTableItem.CREATED_TIME + " TEXT)" +
        ";");
  }

  /**
   * Insert one item to SutraHistoryTable
   * @param item
   * @return
   */
  public boolean replace(SutraHistoryTableItem item) {
    Logger.log("HistoryDbHelper replace");
    if (Utils.isStringEmpty(item.sIndex)) {
      return false;
    }

    SQLiteDatabase db = this.getWritableDatabase();
    // try {
    // String cmd = "INSERT OR REPLACE INTO " + CREATE_SUTRA_HISTORY_TABLE + " VALUES "
    // + "('" + item.sIndex
    // + "', '" + (item.sName == null ? "" : item.sName)
    // + "', " + (item.readPercent > 0 ? item.readPercent : 0)
    // + ", '" + System.currentTimeMillis()
    // + "');";
    // Logger.log(cmd);
    // db.execSQL("");
    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }

    ContentValues values = new ContentValues();
    values.put(SutraHistoryTableItem.SINDEX, item.sIndex);
    values.put(SutraHistoryTableItem.SNAME, item.sName);
    values.put(SutraHistoryTableItem.READ_PERCENT, item.readPercent);
    values.put(SutraHistoryTableItem.CREATED_TIME, System.currentTimeMillis());
    long ret = db.insertWithOnConflict(CREATE_SUTRA_HISTORY_TABLE, null,
        values,
        SQLiteDatabase.CONFLICT_REPLACE);

    if (ret == -1) {
      return false;
    }
    return true;
  }

  /**
   * Get SutraHistoryTableItem by sIndex
   * @param sIndex
   * @return SutraHistoryTableItem or null
   */
  public SutraHistoryTableItem queryBySIndex(String sIndex) {
    Logger.log("getDetailBySIndex sIndex = " + sIndex);
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cur = db.rawQuery(
        "select * from " + CREATE_SUTRA_HISTORY_TABLE + " where s_id=?", new String[] { sIndex });
    if (cur == null) {
      return null;
    }

    if (cur.isAfterLast() || cur.getCount() == 0) {
      Logger.log("queryBySIndex select with no data");
      cur.close();
      return null;
    }

    if (cur.moveToFirst()) {
      String index = cur.getString(0);
      String name = cur.getString(1);
      int percent = cur.getInt(2);
      String createdTime = cur.getString(3);
      Logger.log("index = " + index
          + " name = " + name
          + " percent = " + percent
          + " createdTime = " + createdTime);
      cur.close();
      return new SutraHistoryTableItem(index, name, percent, createdTime);
    }

    cur.close();

    return null;

  }

  /*
   * (non-Javadoc)
   * @see
   * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase
   * , int, int)
   */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

  }

  /**
   * Get the given number's record.
   * @param number
   * @return
   */
  public List<SutraHistoryTableItem> getHistoryListOrderedByCreatedTime(int number) {
    List<SutraHistoryTableItem> list = new ArrayList<SutraHistoryTableItem>();
    SQLiteDatabase db = this.getReadableDatabase();
    number = number > 0 ? number : DB_DEFAULT_HISTORY_SIZE;
    Cursor cur = db.rawQuery(
        "select * from " + CREATE_SUTRA_HISTORY_TABLE + " order by "
            + SutraHistoryTableItem.CREATED_TIME + " desc limit " + Integer.toString(number), null);
    if (cur == null) {
      return list;
    }

    if (cur.moveToFirst()) {
      while (!cur.isAfterLast()) {
        String index = cur.getString(0);
        String name = cur.getString(1);
        int percent = cur.getInt(2);
        String createdTime = cur.getString(3);
        Logger.log("index = " + index
            + " name = " + name
            + " percent = " + percent
            + " createdTime = " + createdTime);
        list.add(new SutraHistoryTableItem(index, name, percent, createdTime));
        cur.moveToNext();
      }
    }

    cur.close();

    return list;

  }
}

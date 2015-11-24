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

import com.buddhism.sutra.data.HistoryDbHelper.SutraHistoryTableItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraDetailItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraPrimaryIndexItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraSecondaryIndexItem;
import com.buddhism.util.Logger;

import android.os.Handler;
import android.os.Looper;

import java.util.List;

/**
 * @author summerxiaqing
 */
final public class DbRequest {
  private static final Handler sHandler = new Handler(Looper.getMainLooper());

  private static void callback(final DbListener listener, final DbResponseData<?> data) {
    sHandler.post(new Runnable() {
      @Override
      public void run() {
        listener.onDbResponse(data);
      }
    });
  }

  private static void callbackError(final DbListener listener, final int errorCode) {
    sHandler.post(new Runnable() {
      @Override
      public void run() {
        listener.onDbError(errorCode);
      }
    });
  }

  /**
   * Get primary index data from sutra db.
   * @param listener
   */
  public static void getPrimaryIndex(final DbListener listener) {
    if (listener == null) {
      return;
    }
    Logger.log("getPrimaryIndex");

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          List<SutraPrimaryIndexItem> list = SutraDbHelper.getInstance().getPrimaryIndex();
          callback(listener, new DbPrimaryIndexData(DbResponseData.ERROR_CODE_SUC, list));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }

  /**
   * Get secondary index data from sutra db by primary index.
   * @param listener
   * @param pIndex
   */
  public static void getSecondaryIndexByPIndex(final DbListener listener, final String pIndex) {
    if (listener == null) {
      return;
    }

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          List<SutraSecondaryIndexItem> list =
              SutraDbHelper.getInstance().getSecondaryIndexByPIndex(pIndex);
          callback(listener, new DbSecondaryIndexData(DbResponseData.ERROR_CODE_SUC, list));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }

  /**
   * Search secondary index data from sutra db by secondary name.
   * @param listener
   * @param sName
   */
  public static void searchSecondaryIndexBySName(final DbListener listener, final String sName) {
    if (listener == null) {
      return;
    }

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          List<SutraSecondaryIndexItem> list =
              SutraDbHelper.getInstance().searchSecondaryIndexBySName(sName);
          callback(listener, new DbSearchListData(DbResponseData.ERROR_CODE_SUC, list));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }

  /**
   * Get sutra detail by given secondary index.
   * @param listener
   * @param sIndex
   */
  public static void getDetailBySIndex(final DbListener listener, final String sIndex) {
    if (listener == null) {
      return;
    }

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          SutraDetailItem item =
              SutraDbHelper.getInstance().getDetailBySIndex(sIndex);
          callback(listener, new DbDetailData(DbResponseData.ERROR_CODE_SUC, item));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }

  /**
   * Copy sutra database to SD card.
   * @param listener
   */
  public static void createSutraDatabase(final DbListener listener) {
    if (listener == null) {
      return;
    }

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          SutraDbHelper.getInstance().createDataBase();
          callback(listener, new DbResponseData<Object>(DbResponseData.ERROR_CODE_SUC));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }

  public static void getReadHistoryByLimitSize(final DbListener listener, final int limitSize) {
    if (listener == null) {
      return;
    }

    new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          List<SutraHistoryTableItem> list =
              HistoryDbHelper.getInstance().getHistoryListOrderedByCreatedTime(limitSize);
          callback(listener, new DbHistoryListData(DbResponseData.ERROR_CODE_SUC, list));
        } catch (Exception e) {
          e.printStackTrace();
          callbackError(listener, DbResponseData.ERROR_DB_ERROR);
        }
      }

    }).start();
  }
}

/**
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 * @Description :
 */

package com.buddhism.sutra.data;

import android.os.Handler;
import android.os.Looper;

import com.buddhism.sutra.data.HistoryDbHelper.SutraHistoryTableItem;
import com.buddhism.sutra.data.SutraDbHelper.DictionaryItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraDetailItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraPrimaryIndexItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraSecondaryIndexItem;
import com.buddhism.util.Logger;

import java.util.List;

/**
 * @author summerxiaqing
 */
final public class DbRequest {
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    private static void callback (final DbListener listener, final DbResponseData<?> data) {
        sHandler.post(new Runnable() {
            @Override
            public void run () {
                listener.onDbResponse(data);
            }
        });
    }

    private static void callbackError (final DbListener listener, final int errorCode) {
        sHandler.post(new Runnable() {
            @Override
            public void run () {
                listener.onDbError(errorCode);
            }
        });
    }

    /**
     * Get primary index data from sutra db.
     * @param listener
     */
    public static void getPrimaryIndex (final DbListener listener) {
        if (listener == null) {
            return;
        }
        Logger.log("getPrimaryIndex");

        new Thread(new Runnable() {

            @Override
            public void run () {
                try {
                    List<SutraPrimaryIndexItem> list =
                        SutraDbHelper.getInstance().getPrimaryIndex();
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
    public static void getSecondaryIndexByPIndex (final DbListener listener, final String pIndex) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
                try {
                    List<SutraSecondaryIndexItem> list =
                        SutraDbHelper.getInstance().getSecondaryIndexByPIndex(pIndex);
                    callback(listener,
                        new DbSecondaryIndexData(DbResponseData.ERROR_CODE_SUC, list));
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
    public static void searchSecondaryIndexBySName (final DbListener listener, final String sName) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
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
     *
     * @param listener
     * @param query
     * @param count
     */
    public static void searchDictionary (
        final DbListener listener, final String query, final int count) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
                try {
                    List<DictionaryItem> list =
                        SutraDbHelper.getInstance().searchDictionary(query, count);
                    callback(listener,
                        new DbDicSearchListData(DbResponseData.ERROR_CODE_SUC, list));
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO(qingxia): Maybe we should return other error.
                    // callbackError(listener, DbResponseData.ERROR_DB_ERROR);
                    callback(listener,
                        new DbDicSearchListData(DbResponseData.ERROR_CODE_SUC, null));
                }
            }

        }).start();
    }

    /**
     * Search data in dictionary.
     * @param listener
     * @param query
     */
    public static void searchDictionary (final DbListener listener, final String query) {
        searchDictionary(listener, query, 0);
    }

    /**
     * Get sutra detail by given secondary index.
     * @param listener
     * @param sIndex
     */
    public static void getDetailBySIndex (final DbListener listener, final String sIndex) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
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
    public static void createSutraDatabase (final DbListener listener) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
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

    public static void getReadHistoryByLimitSize (final DbListener listener, final int limitSize) {
        if (listener == null) {
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run () {
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

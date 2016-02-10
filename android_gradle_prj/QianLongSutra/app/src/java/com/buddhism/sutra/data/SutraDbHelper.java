/**
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 * @Description :
 */

package com.buddhism.sutra.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.buddhism.sutra.MyApplication;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SutraDbHelper extends SQLiteOpenHelper {
    // Define database table name
    private final static String TABLE_PRIMARY_INDEX = "tablePrimaryIndex";
    private final static String PRIMARY_INDEX_ID = "p_id";
    private final static String PRIMARY_INDEX_NAME = "p_name";

    public class SutraPrimaryIndexItem {
        public String pIndex;
        public String pName;

        public SutraPrimaryIndexItem (String index, String name) {
            this.pIndex = index;
            this.pName = name;
        }

        @Override
        public boolean equals (Object object) {
            if (object == null || !(object instanceof SutraPrimaryIndexItem)) {
                return false;
            }

            SutraPrimaryIndexItem itemData = (SutraPrimaryIndexItem) object;
            if (itemData.pIndex == null && this.pIndex != null ||
                !itemData.pIndex.equals(this.pIndex)) {
                return false;
            }

            if (itemData.pName == null && this.pName != null ||
                !itemData.pName.equals(this.pName)) {
                return false;
            }

            return true;
        }
    }

    private final static String TABLE_SECONDARY_INDEX = "tableSecondaryIndex";
    private final static String SECONDARY_INDEX_ID = "s_id";
    private final static String SECONDARY_INDEX_NAME = "s_name";

    public class SutraSecondaryIndexItem {
        public String pIndex;
        public String sIndex;
        public String sName;

        public SutraSecondaryIndexItem (String index, String sIndex, String name) {
            this.pIndex = index;
            this.sIndex = sIndex;
            this.sName = name;
        }

        @Override
        public boolean equals (Object object) {
            if (object == null || !(object instanceof SutraSecondaryIndexItem)) {
                return false;
            }
            SutraSecondaryIndexItem itemData = (SutraSecondaryIndexItem) object;
            if (itemData.pIndex == null && this.pIndex != null ||
                !itemData.pIndex.equals(this.pIndex)) {
                return false;
            }

            if (itemData.sIndex == null && this.sIndex != null ||
                !itemData.sIndex.equals(this.sIndex)) {
                return false;
            }

            if (itemData.sName == null && this.sName != null ||
                !itemData.sName.equals(this.sName)) {
                return false;
            }

            return true;
        }
    }

    private final static String TABLE_BUDDHISM_DETAIL = "tableBuddhismDetail";
    private final static String BUDDHISM_DETAIL = "content";

    public class SutraDetailItem {
        public String sIndex;
        public String content;

        public SutraDetailItem (String sIndex, String content) {
            this.sIndex = sIndex;
            this.content = content;
        }
    }

    private final static String TABLE_BUDDHISM_DIC = "tableBuddhismDic";
    private final static String DIC_ID = "d_id";
    private final static String DIC_TYPE = "d_type";
    private final static String DIC_KEYWORD = "d_keyword";
    private final static String DIC_DESCRIPTION = "d_description";

    public class DictionaryItem {
        public String id;
        public String type;
        public String keyword;
        public String description;

        public DictionaryItem (String id, String type, String keyword, String description) {
            this.id = id;
            this.type = type;
            this.keyword = keyword;
            this.description = description;
        }
    }

    // The address of our application, change the word package by package name of our app
    private final static String DB_PATH =
        Environment.getExternalStorageDirectory().getPath() + "/sutradb_new/";

    private final static String OLD_DB_PATH =
        Environment.getExternalStorageDirectory().getPath() + "/sutradb/";

    private final static String DB_PATH_1 =
        Environment.getExternalStorageDirectory().getPath() + "/sutradb_1/";

    // The name of our data base file. It must be in our folder assets.
    private final static String DB_NAME = "qldz.db";
    private final static String DB_ZIP_NAME = DB_NAME;// "qldzdb.zip";
    private final static int DB_VERSION = 2;

    private final static String DB_FINAL_PATH_1 = DB_PATH_1 + DB_NAME;
    private final static String DB_FINAL_PATH = DB_PATH + DB_NAME;
    private final static String OLD_DB_FINAL_PATH = OLD_DB_PATH + DB_NAME;
    private final static String DB_ZIP_FINAL_PATH = DB_PATH_1 + DB_ZIP_NAME;

    private SQLiteDatabase mDataBase;

    private final Context mContext;

    private static SutraDbHelper sDbHelper;

    /**
     * Constructor
     *
     * @throws IOException
     */
    private SutraDbHelper (Context context) throws IOException {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    public synchronized static SutraDbHelper getInstance () {
        if (sDbHelper == null) {
            try {
                sDbHelper = new SutraDbHelper(MyApplication.getApplication());
            } catch (IOException e) {
                throw new NullPointerException("Sutra Database can not be null");
            }
        }

        return sDbHelper;
    }

    /**
     * Creates a empty sqlite database on the system and rewrites it with your own database.
     */
    public void createDataBase () throws IOException {
        // Check if the sqlite database already exists
        boolean dbExist = this.checkDataBase();

        if (dbExist) {
            // If our database exists, do nothing
        } else {
            // If doesnt exists this method creates an empty database into the default system
            // path
            // this.getReadableDatabase();

            try {
                // Copy our database.sqlite into the new database
                this.copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.openDataBase();
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the
     * application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase () {
        SQLiteDatabase checkDB = null;

        try {
            checkDB = SQLiteDatabase.openDatabase(
                DB_FINAL_PATH_1,
                null,
                SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            // database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database
     */
    private void copyDataBase () throws IOException {
        // Delete old file
        File delFile = new File(OLD_DB_FINAL_PATH);
        if (delFile.exists()) {
            delFile.delete();
        }

        // Delete old file
        File delFile2 = new File(DB_FINAL_PATH);
        if (delFile2.exists()) {
            delFile2.delete();
        }

        // Open your local db as the input stream
        InputStream myInput = this.mContext.getAssets().open(DB_ZIP_NAME);

        File file = new File(DB_PATH_1);
        if (!file.exists()) {
            file.mkdir();
        }

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(DB_ZIP_FINAL_PATH);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
        // ZipUtils.unZip(DB_ZIP_FINAL_PATH, DB_PATH_1);
        // File fileDelete = new File(DB_ZIP_FINAL_PATH);
        // if (fileDelete.isFile()) {
        // fileDelete.delete();
        // }
    }

    /**
     * Open the database
     *
     * @throws SQLException
     */
    public void openDataBase () throws SQLException {
        this.mDataBase = SQLiteDatabase.openDatabase(
            DB_FINAL_PATH_1,
            null,
            SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close () {
        if (this.mDataBase != null)
            this.mDataBase.close();

        super.close();
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        // NOTE(qingxia): Do copy database instead
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion >= DB_VERSION) {
            Logger.log("New Version Updated. New version is : " + newVersion);
        }
    }

    /**
     * Get primary index
     *
     * @return
     */
    public List<SutraPrimaryIndexItem> getPrimaryIndex () {
        Logger.log("SutraDbHelper getPrimaryIndex");
        if (this.mDataBase == null) {
            this.openDataBase();
        }
        List<SutraPrimaryIndexItem> list = new ArrayList<SutraPrimaryIndexItem>();
        Cursor cur = this.mDataBase.query(
            TABLE_PRIMARY_INDEX,
            null,
            null,
            null,
            null,
            null,
            null);
        if (cur == null) {
            return list;
        }

        int primaryIndex = cur.getColumnIndex(PRIMARY_INDEX_ID);
        int primaryName = cur.getColumnIndex(PRIMARY_INDEX_NAME);
        if (primaryName == -1 || primaryIndex == -1) {
            cur.close();
            return list;
        }

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                Logger.log(cur.getString(primaryIndex) + "|" + cur.getString(primaryName));
                list.add(
                    new SutraPrimaryIndexItem(cur.getString(primaryIndex),
                        cur.getString(primaryName)));
                cur.moveToNext();
            }
        }

        cur.close();

        return list;
    }

    /**
     * Get secondary index by p index
     *
     * @param pIndex
     *
     * @return
     */
    public List<SutraSecondaryIndexItem> getSecondaryIndexByPIndex (String pIndex) {
        if (this.mDataBase == null) {
            this.openDataBase();
        }
        List<SutraSecondaryIndexItem> list = new ArrayList<SutraSecondaryIndexItem>();
        Cursor cur = this.mDataBase.query(
            TABLE_SECONDARY_INDEX,
            new String[]{SECONDARY_INDEX_ID, SECONDARY_INDEX_NAME},
            PRIMARY_INDEX_ID + "=" + pIndex,
            null,
            null,
            null,
            null);
        if (cur == null) {
            return list;
        }

        int secondaryIndex = cur.getColumnIndex(SECONDARY_INDEX_ID);
        int secondaryName = cur.getColumnIndex(SECONDARY_INDEX_NAME);
        if (secondaryName == -1 || secondaryIndex == -1) {
            cur.close();
            return list;
        }

        if (cur.moveToFirst()) {
            while (!cur.isAfterLast()) {
                Logger.log(cur.getString(secondaryIndex) + "|" + cur.getString(secondaryName));
                list.add(
                    new SutraSecondaryIndexItem(pIndex, cur.getString(secondaryIndex), cur
                        .getString(secondaryName)));
                cur.moveToNext();
            }
        }

        cur.close();

        return list;
    }

    private String createSearchString (String name) {
        final String WILDCARD_PERCENT = "%";
        final String COLON = "'";
        if (Utils.isStringEmpty(name)) {
            return null;
        }

        String querys[] = name.split(" ");
        if (querys.length < 0) {
            return null;
        }

        String finalQuery = COLON + WILDCARD_PERCENT;
        for (String query : querys) {
            finalQuery += query + WILDCARD_PERCENT;
        }
        finalQuery += COLON;
        Logger.log("new query = " + finalQuery);

        return finalQuery;
    }

    public List<DictionaryItem> searchDictionary (String queryStr) {
        return this.searchDictionary(queryStr, 0);
    }

    /**
     * Search dictionary by query
     *
     * @param queryStr
     *
     * @return
     */
    public List<DictionaryItem> searchDictionary (String queryStr, final int countNum) {
        final int RESULT_NUM = 5;
        final int MAX_RESULT_NUM = 100;
        final String COLON = "'";

        int retNumber = countNum > RESULT_NUM ? countNum : RESULT_NUM;

        if (retNumber > MAX_RESULT_NUM) {
            retNumber = MAX_RESULT_NUM;
        }

        if (this.mDataBase == null) {
            this.openDataBase();
        }

        List<DictionaryItem> list = new ArrayList<DictionaryItem>();

        // Is search
        String sameQuery = COLON + queryStr + COLON;
        if (!Utils.isStringEmpty(sameQuery)) {
            Cursor cur = this.mDataBase.query(
                TABLE_BUDDHISM_DIC,
                new String[]{DIC_ID, DIC_TYPE, DIC_KEYWORD, DIC_DESCRIPTION},
                DIC_KEYWORD + " = " + sameQuery,
                null,
                null,
                null,
                null);

            if (cur == null) {
                return list;
            }

            int idIndex = cur.getColumnIndex(DIC_ID);
            int typeIndex = cur.getColumnIndex(DIC_TYPE);
            int keywordIndex = cur.getColumnIndex(DIC_KEYWORD);
            int descriptionIndex = cur.getColumnIndex(DIC_DESCRIPTION);
            if (idIndex == -1 || typeIndex == -1 || keywordIndex == -1 || descriptionIndex == -1) {
                cur.close();
                return list;
            }

            int count = 0;
            if (cur.moveToFirst()) {
                while (!cur.isAfterLast() && count < retNumber) {
                    count++;
                    Logger.log(cur.getString(idIndex) + "|" + cur.getString(keywordIndex));

                    list.add(
                        new DictionaryItem(
                            cur.getString(idIndex),
                            cur.getString(typeIndex),
                            cur.getString(keywordIndex),
                            cur.getString(descriptionIndex)
                        ));

                    cur.moveToNext();
                }
            }

            cur.close();
        }

        // Like search.
        String query = this.createSearchString(queryStr);
        if (Utils.isStringEmpty(query)) {
            return list;
        }

        Cursor cur = this.mDataBase.query(
            TABLE_BUDDHISM_DIC,
            new String[]{DIC_ID, DIC_TYPE, DIC_KEYWORD, DIC_DESCRIPTION},
            DIC_KEYWORD + " like " + query,
            null,
            null,
            null,
            null);

        if (cur == null) {
            return list;
        }

        int idIndex = cur.getColumnIndex(DIC_ID);
        int typeIndex = cur.getColumnIndex(DIC_TYPE);
        int keywordIndex = cur.getColumnIndex(DIC_KEYWORD);
        int descriptionIndex = cur.getColumnIndex(DIC_DESCRIPTION);
        if (idIndex == -1 || typeIndex == -1 || keywordIndex == -1 || descriptionIndex == -1) {
            cur.close();
            return list;
        }

        int count = 0;
        int alreadyInListNum = list.size();
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast() && count < retNumber - alreadyInListNum) {
                count++;
                Logger.log(cur.getString(idIndex) + "|" + cur.getString(keywordIndex));

                // Like search will not return the correct item.
                if (cur.getString(keywordIndex).equals(queryStr)) {
                    continue;
                }

                list.add(
                    new DictionaryItem(
                        cur.getString(idIndex),
                        cur.getString(typeIndex),
                        cur.getString(keywordIndex),
                        cur.getString(descriptionIndex)
                    ));

                cur.moveToNext();
            }
        }

        cur.close();

        return list;
    }

    /**
     * Search secondary index by p index
     *
     * @param sName
     *
     * @return
     */
    public List<SutraSecondaryIndexItem> searchSecondaryIndexBySName (String sName) {
        final int MAX_RESULT_NUM = 50;
        if (this.mDataBase == null) {
            this.openDataBase();
        }

        List<SutraSecondaryIndexItem> list = new ArrayList<SutraSecondaryIndexItem>();

        String query = this.createSearchString(sName);
        if (Utils.isStringEmpty(query)) {
            return list;
        }

        Cursor cur = this.mDataBase.query(
            TABLE_SECONDARY_INDEX,
            new String[]{PRIMARY_INDEX_ID, SECONDARY_INDEX_ID, SECONDARY_INDEX_NAME},
            SECONDARY_INDEX_NAME + " like " + query,
            null,
            null,
            null,
            null);
        if (cur == null) {
            return list;
        }

        int primaryIndex = cur.getColumnIndex(PRIMARY_INDEX_ID);
        int secondaryIndex = cur.getColumnIndex(SECONDARY_INDEX_ID);
        int secondaryName = cur.getColumnIndex(SECONDARY_INDEX_NAME);
        if (primaryIndex == -1 || secondaryName == -1 || secondaryIndex == -1) {
            cur.close();
            return list;
        }

        int count = 0;
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast() && count < MAX_RESULT_NUM) {
                count++;
                Logger.log(cur.getString(secondaryIndex) + "|" + cur.getString(secondaryName));
                list.add(
                    new SutraSecondaryIndexItem(
                        cur.getString(primaryIndex),
                        cur.getString(secondaryIndex),
                        cur.getString(secondaryName)));

                cur.moveToNext();
            }
        }

        cur.close();

        return list;
    }

    /**
     * Get sutra detail by secondary index
     *
     * @param sIndex
     *
     * @return
     */
    public SutraDetailItem getDetailBySIndex (String sIndex) {
        Logger.log("getDetailBySIndex sIndex = " + sIndex);
        if (this.mDataBase == null) {
            this.openDataBase();
        }
        Cursor cur = this.mDataBase.rawQuery(
            "select * from " + TABLE_BUDDHISM_DETAIL + " where s_id=?", new String[]{sIndex});
        if (cur == null) {
            return null;
        }

        if (cur.isAfterLast() || cur.getCount() == 0) {
            Logger.log("getDetailBySIndex select with no data");
            cur.close();
            return null;
        }

        int detailIndex = cur.getColumnIndex(BUDDHISM_DETAIL);
        if (detailIndex == -1) {
            cur.close();
            return null;
        }

        if (cur.moveToFirst()) {
            String detail = "";
            detail = cur.getString(detailIndex);
            // Logger.log("Sutra detail = " + detail);
            cur.close();
            return new SutraDetailItem(sIndex, detail);
        }

        cur.close();

        return null;
    }
}

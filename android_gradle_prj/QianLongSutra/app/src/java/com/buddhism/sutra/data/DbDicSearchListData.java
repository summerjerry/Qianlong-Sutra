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

import com.buddhism.sutra.data.SutraDbHelper.DictionaryItem;

import java.util.List;

/**
 * @author summerxiaqing
 */
public class DbDicSearchListData extends DbResponseData<List<DictionaryItem>> {
  /**
   * @param errorCode
   */
  public DbDicSearchListData (int errorCode, List<DictionaryItem> list) {
    super(errorCode);
    this.mData = list;
  }
}

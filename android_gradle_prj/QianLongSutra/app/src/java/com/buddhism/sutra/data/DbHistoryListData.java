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

import java.util.List;

/**
 * @author summerxiaqing
 */
public class DbHistoryListData extends DbResponseData<List<SutraHistoryTableItem>> {
  /**
   * @param errorCode
   */
  public DbHistoryListData(int errorCode, List<SutraHistoryTableItem> list) {
    super(errorCode);
    this.mData = list;
  }
}

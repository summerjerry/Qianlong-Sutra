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

import com.buddhism.sutra.data.SutraDbHelper.SutraSecondaryIndexItem;

import java.util.List;

/**
 * @author summerxiaqing
 */
public class DbSearchListData extends DbResponseData<List<SutraSecondaryIndexItem>> {
  /**
   * @param errorCode
   */
  public DbSearchListData(int errorCode, List<SutraSecondaryIndexItem> list) {
    super(errorCode);
    this.mData = list;
  }
}

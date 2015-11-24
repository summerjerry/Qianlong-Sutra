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

import com.buddhism.sutra.data.SutraDbHelper.SutraPrimaryIndexItem;

import java.util.List;

/**
 * @author summerxiaqing
 */
public class DbPrimaryIndexData extends DbResponseData<List<SutraPrimaryIndexItem>> {

  /**
   * @param errorCode
   */
  public DbPrimaryIndexData(int errorCode, List<SutraPrimaryIndexItem> list) {
    super(errorCode);
    this.mData = list;
  }

}

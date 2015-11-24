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

import com.buddhism.sutra.data.SutraDbHelper.SutraDetailItem;

/**
 * @author summerxiaqing
 */
public class DbDetailData extends DbResponseData<SutraDetailItem> {

  /**
   * @param errorCode
   */
  public DbDetailData(int errorCode, SutraDetailItem item) {
    super(errorCode);
    this.mData = item;
  }

}

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

/**
 * @author summerxiaqing
 */
public interface DbListener {
  public void onDbResponse(DbResponseData<?> data);

  public void onDbError(int errorCode);
}

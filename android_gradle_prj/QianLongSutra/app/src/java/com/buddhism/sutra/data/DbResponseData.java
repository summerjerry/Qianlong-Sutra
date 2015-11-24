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
public class DbResponseData<T> {
  public static final int ERROR_CODE_SUC = 0;
  public static final int ERROR_DB_ERROR = 1;
  private int mErrorCode;
  protected T mData;

  public DbResponseData(int errorCode) {
    this.setErrorCode(errorCode);
  }

  /**
   * @return the mErrorCode
   */
  public int getErrorCode() {
    return this.mErrorCode;
  }

  /**
   * @param mErrorCode
   *          the mErrorCode to set
   */
  public void setErrorCode(int mErrorCode) {
    this.mErrorCode = mErrorCode;
  }

  /**
   * @return the mData
   */
  public T getData() {
    return this.mData;
  }

  /**
   * @param mData
   *          the mData to set
   */
  public void setData(T mData) {
    this.mData = mData;
  }
}

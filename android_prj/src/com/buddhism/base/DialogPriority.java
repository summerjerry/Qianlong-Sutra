/**
 *
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 *
 * @Description :
 *
 */

package com.buddhism.base;

/**
 * @author qizhang
 */
public enum DialogPriority {
  HIGHEST(0),
  MEDIUM(1),
  LOW(2);

  private final int mPriority;

  public boolean isLowerEqualThan(DialogPriority priority) {
    return this.mPriority >= priority.getPriority();
  }

  private int getPriority() {
    return this.mPriority;
  }

  private DialogPriority(int priority) {
    this.mPriority = priority;
  }

}

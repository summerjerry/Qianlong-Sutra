/**
 *
 * Copyright 2013 YunRang Technology Co. Ltd., Inc. All rights reserved.
 *
 * @Author : qingxia
 *
 * @Description :
 *
 */

package com.buddhism.skin;

/**
 * @Description: This class support a enum to map different android resource string.
 * @author qingxia
 */
public enum SkinResType {
  LAYOUT("layout"),
  DIMEN("dimen"),
  COLOR("color"),
  DRAWABLE("drawable"),
  ANIM("anim");

  private String mTypeString;

  SkinResType(String typeString) {
    this.mTypeString = typeString;
  }

  public String getTypeString() {
    return this.mTypeString;
  }
}

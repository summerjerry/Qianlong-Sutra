/**
 *
 * No Copyright
 *
 * @Author : qingxia
 *
 * @Description :
 *
 */

package com.buddhism.skin;

import com.buddhism.util.Utils;

/**
 * TODO(qingxia): Small font size is default is a hack, will refactor later
 * @author qingxia
 */
public enum SkinFontSize {
  FONT_SIZE_SMALL(""),
  FONT_SIZE_MIDDLE("middle"),
  FONT_SIZE_BIG("big");

  private String mTag = "";

  SkinFontSize(String tag) {
    this.mTag = tag;
  }

  /**
   * @return the mTag
   */
  public String getTag() {
    return this.mTag;
  }

  /**
   * Return corresponding font size by a given string
   * @param str
   * @return
   */
  public static SkinFontSize getFontSizeByString(String str) {
    if (Utils.isStringEmpty(str)) {
      return FONT_SIZE_SMALL;
    }

    if (str.trim().equals(FONT_SIZE_BIG.getTag())) {
      return FONT_SIZE_BIG;
    } else if (str.trim().equals(FONT_SIZE_MIDDLE.getTag())) {
      return FONT_SIZE_MIDDLE;
    }

    return FONT_SIZE_SMALL;
  }
}

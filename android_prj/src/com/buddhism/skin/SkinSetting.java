/**
 *
 * Copyright No Copyright
 *
 * @Author : qingxia
 *
 * @Description :
 *
 */

package com.buddhism.skin;

import com.buddhism.base.SharedPreferencesManager;

/**
 * TODO(qingxia): Refactor later.
 * @description： This class provide a common setting of skin.
 * @author qingxia
 */
class SkinSetting {
  public static SkinSettingInfo sCurrentSkinSettingInfo = new SkinSettingInfo();
  private static final String SKIN_SHARE_PRE_NAME = "skin_share_pre_name";
  private static final String SKIN_SHARE_PRE_INDEX = "skin_share_pre_index";
  private static final String SKIN_SHARE_PRE_FONT_SIZE = "skin_share_pre_size";
  private static final String SKIN_SHARE_PRE_SHOW_IMAGE = "skin_share_pre_show_image";
  private static final String SKIN_SHARE_PRE_SHOW_IMAGE_IN_3G = "skin_share_pre_show_image_in_3g";

  // Initialize skin setting info.
  static {
    sCurrentSkinSettingInfo.mFontSize = SkinFontSize.getFontSizeByString(SharedPreferencesManager
        .getString(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_FONT_SIZE, ""));
    sCurrentSkinSettingInfo.mSkinIndex = SharedPreferencesManager
        .getInt(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_INDEX, 0);
    sCurrentSkinSettingInfo.isShowImage = SharedPreferencesManager
        .getBoolean(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_SHOW_IMAGE, true);
    sCurrentSkinSettingInfo.isShowImageIn3G = SharedPreferencesManager
        .getBoolean(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_SHOW_IMAGE_IN_3G, true);
  }

  public static SkinSettingInfo getCurrentSkinSettingInfo() {
    return sCurrentSkinSettingInfo.clone();
  }

  public static int getCurrentSkinIndex() {
    return sCurrentSkinSettingInfo.mSkinIndex;
  }

  public static SkinFontSize getCurrentFontSize() {
    return sCurrentSkinSettingInfo.mFontSize;
  }

  /**
   * Get if we need to show image in WIFI
   * @return
   */
  public static boolean isShowImage() {
    return sCurrentSkinSettingInfo.isShowImage;
  }

  public static boolean isShowImageIn3G() {
    return sCurrentSkinSettingInfo.isShowImageIn3G;
  }

  public static void setCurrentSkinIndex(int index) {
    // TODO(qingxia): Check index validate
    if (sCurrentSkinSettingInfo.mSkinIndex == index) {
      return;
    }
    sCurrentSkinSettingInfo.mSkinIndex = index;
    SharedPreferencesManager.putInt(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_INDEX, index);
  }

  public static void setCurrentSkinIsShowImage(boolean isShow) {
    if (sCurrentSkinSettingInfo.isShowImage == isShow) {
      return;
    }
    sCurrentSkinSettingInfo.isShowImage = isShow;
    SharedPreferencesManager.putBoolean(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_SHOW_IMAGE, isShow);
  }

  public static void setCurrentSkinIsShowImageIn3G(boolean isShow) {
    if (sCurrentSkinSettingInfo.isShowImageIn3G == isShow) {
      return;
    }
    sCurrentSkinSettingInfo.isShowImageIn3G = isShow;
    SharedPreferencesManager.putBoolean(
        SKIN_SHARE_PRE_NAME,
        SKIN_SHARE_PRE_SHOW_IMAGE_IN_3G,
        isShow);
  }

  public static void setCurrentFontSize(SkinFontSize fontSize) {
    if (fontSize == null || sCurrentSkinSettingInfo.mFontSize == fontSize) {
      return;
    }
    sCurrentSkinSettingInfo.mFontSize = fontSize;
    SharedPreferencesManager.putString(SKIN_SHARE_PRE_NAME, SKIN_SHARE_PRE_FONT_SIZE, fontSize
        .getTag());
  }
}

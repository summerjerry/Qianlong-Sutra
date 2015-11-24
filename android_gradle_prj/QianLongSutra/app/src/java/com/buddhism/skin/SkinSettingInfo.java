/**
 *
 * No Copyright.
 *
 * @Author : qingxia
 *
 * @Description :
 *
 */

package com.buddhism.skin;

/**
 * @author qingxia
 */
public class SkinSettingInfo implements Cloneable {
  int mSkinIndex;
  SkinFontSize mFontSize;
  boolean isShowImage;
  boolean isShowImageIn3G;

  public int getSkinIndex() {
    return this.mSkinIndex;
  }

  public SkinFontSize getFontSize() {
    return this.mFontSize;
  }

  /**
   * Get if we need to show image in WIFI
   * @return
   */
  public boolean getIsShowImage() {
    return this.isShowImage;
  }

  public boolean getIsShowImageIn3G() {
    return this.isShowImageIn3G;
  }

  @Override
  public SkinSettingInfo clone() {
    SkinSettingInfo settingInfo = null;
    try {
      settingInfo = (SkinSettingInfo) super.clone();
      settingInfo.mSkinIndex = this.mSkinIndex;
      settingInfo.mFontSize = this.mFontSize;
      settingInfo.isShowImage = this.isShowImage;
      settingInfo.isShowImageIn3G = this.isShowImageIn3G;
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return settingInfo;
  }

  @Override
  public boolean equals(Object info) {
    if (this == info)
      return true;

    if (!(info instanceof SkinSettingInfo))
      return false;

    final SkinSettingInfo other = (SkinSettingInfo) info;

    if (this.mSkinIndex == other.mSkinIndex &&
        this.mFontSize == other.mFontSize &&
        this.isShowImage == other.isShowImage &&
        this.isShowImageIn3G == other.isShowImageIn3G)
      return true;

    return false;
  }

  /**
   * This function used to judge if the setting has been changed.
   * @param info
   * @return
   */
  public boolean skinSettingEquals(Object info) {
    if (this == info)
      return true;

    if (!(info instanceof SkinSettingInfo))
      return false;

    final SkinSettingInfo other = (SkinSettingInfo) info;

    if (this.mSkinIndex == other.mSkinIndex &&
        this.mFontSize == other.mFontSize)
      return true;

    return false;
  }
}

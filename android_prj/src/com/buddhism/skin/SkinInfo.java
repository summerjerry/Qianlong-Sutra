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
 * @Description: This class support one skin information.
 * @author qingxia
 */
public class SkinInfo {
  private String mSkinID;
  private String mSkinName;
  private int mSkinIndex;
  private int mSkinDefaultColor;

  public SkinInfo(int skinIndex, String skinID, String skinName, int skinDefaultColor) {
    if (skinID == null || Utils.isStringEmpty(skinName)) {
      throw new NullPointerException("Skin info constructor parameter can not be null");
    }

    this.mSkinIndex = skinIndex;
    this.mSkinID = skinID;
    this.mSkinName = skinName;
    this.mSkinDefaultColor = skinDefaultColor;
  }

  /**
   * @return the mSkinID
   */
  public String getSkinID() {
    return this.mSkinID;
  }

  /**
   * @param mSkinID
   *          the mSkinID to set
   */
  public void setSkinID(String skinID) {
    this.mSkinID = skinID;
  }

  /**
   * @return the mSkinName
   */
  public String getSkinName() {
    return this.mSkinName;
  }

  /**
   * @param mSkinName
   *          the mSkinName to set
   */
  public void setSkinName(String skinName) {
    this.mSkinName = skinName;
  }

  /**
   * @return the mSkinIndex
   */
  public int getSkinIndex() {
    return this.mSkinIndex;
  }

  /**
   * @param mSkinIndex
   *          the mSkinIndex to set
   */
  public void setSkinIndex(int skinIndex) {
    this.mSkinIndex = skinIndex;
  }

  /**
   * @return the mSkinDefaultColor
   */
  public int getSkinDefaultColor() {
    return this.mSkinDefaultColor;
  }

  /**
   * @param mSkinDefaultColor
   *          the mSkinDefaultColor to set
   */
  public void setSkinDefaultColor(int skinDefaultColor) {
    this.mSkinDefaultColor = skinDefaultColor;
  }
}

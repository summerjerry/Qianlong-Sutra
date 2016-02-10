/**
 * No Copyright
 *
 * @Author : qingxia
 * @Description :
 */

package com.buddhism.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

import com.buddhism.base.ContextProvider;
import com.buddhism.sutra.R;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;

import java.util.HashMap;

/**
 * @Description: This class provide a common interface to manage skin information.
 * @author qingxia
 */
final public class SkinManager {
  private static int sSkinCount = 0;
  // HACK(qingxia): We should initialize package information by construct.
  private static final String sWeiboReaderPackage = "com.buddhism.sutra";
  private static String sCurrentPackage = sWeiboReaderPackage;
  private static HashMap<Integer, SkinInfo> sSkinMap = new HashMap<Integer, SkinInfo>();

  static {
    sCurrentPackage = ContextProvider.getApplicationContext().getString(R.string.package_name);
    Logger.log("sCurrentPackage = " + sCurrentPackage);
  }

  public static void addSkin(String skinID, String skinName, int skinDefaultColor) {
    SkinInfo skinInfo = new SkinInfo(sSkinCount++, skinID, skinName, skinDefaultColor);
    sSkinMap.put(skinInfo.getSkinIndex(), skinInfo);
  }

  private SkinManager() {
    if (sSkinCount == 0) {
      throw new NullPointerException("SkinManager null pointer exception.");
    }
    // TODO(qingxia): Load skin info from other path in future version.
  }

  public static final HashMap<Integer, SkinInfo> getSkinMap() {
    return new HashMap<Integer, SkinInfo>(sSkinMap);
  }

  /**
   * Get current skin numbers.
   * @return
   */
  public static int getSkinCount() {
    return sSkinCount;
  }

  /**
   * Get skin info by skin index.
   * @param skinIndex
   * @return
   */
  public static SkinInfo getSkinInfoByIndex(int skinIndex) {
    return sSkinMap.get(skinIndex);
  }

  /**
   * @return the sCurrentSkinIndex
   */
  public static int getCurrentSkinIndex() {
    return SkinSetting.getCurrentSkinIndex();
  }

  /**
   * @return current font size
   */
  public static SkinFontSize getCurrentFontSize() {
    return SkinSetting.getCurrentFontSize();
  }

  /**
   * @return current font size
   */
  public static Drawable colorMixedDrawable(int color, Drawable drawable) {
    if (drawable != null) {
      drawable.setColorFilter(new LightingColorFilter(0, color));
    }
    return drawable;
  }

  /**
   * @return current font size
   */
  public static Drawable colorMixedDrawableByID(int color, int drawableID) {
    Drawable drawable = null;
    if (drawableID != 0) {
      drawable = getDrawableById(drawableID);
      if (drawable != null) {
        return colorMixedDrawable(color, drawable);
      }
    }
    return drawable;
  }

  /**
   * @return current show image setting in wifi.
   */
  public static boolean isShowImage() {
    return SkinSetting.isShowImage();
  }

  public static boolean isShowImageIn3G() {
    return SkinSetting.isShowImageIn3G();
  }

  /**
   * @param currentSkinIndex
   *          the sCurrentSkinIndex to set
   */
  public static void setCurrentSkinIndex(int currentSkinIndex) {
    SkinSetting.setCurrentSkinIndex(currentSkinIndex);
  }

  /**
   * Return a drawable object associated with a particular resource name. Various types of
   * objects will be returned depending on the underlying resource -- for example, a solid
   * color, PNG image, scalable image, etc. The Drawable API hides these implementation
   * details. <p class="note"> <strong>Note:</strong> Prior to
   * {@link android.os.Build.VERSION_CODES#JELLY_BEAN}, this function would not correctly
   * retrieve the final configuration density when the resource ID passed here is an alias
   * to another Drawable resource. This means that if the density configuration of the
   * alias resource is different than the actual resource, the density of the returned
   * Drawable would be incorrect, resulting in bad scaling. To work around this, you can
   * instead retrieve the Drawable through {@link TypedArray#getDrawable
   * TypedArray.getDrawable}. Use
   * {@link android.content.Context#obtainStyledAttributes(int[])
   * Context.obtainStyledAttributes} with an array containing the resource ID of interest
   * to create the TypedArray. </p>
   * @param resId
   *          The desired resource name.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * @return Drawable An object that can be used to draw this resource.
   */
  public static Drawable getDrawableById(int resId) {
    return getDrawableById(resId, SkinResType.DRAWABLE);
  }

  public static Drawable getDrawableById(int resId, SkinResType type) {
    try {
      return getResources().getDrawable(getCurrentResId(resId, type));
    } catch (NotFoundException ex) {
      throw ex;
    }
  }

  /**
   * Retrieve a dimensional for a particular resource name for use as a size in raw
   * pixels. This is the same as {@link #getDimension}, except the returned value is
   * converted to integer pixels for use as a size. A size conversion involves rounding
   * the base value, and ensuring that a non-zero base value is at least one pixel in
   * size.
   * @param resId
   *          The desired resource name.
   * @return Resource dimension value multiplied by the appropriate metric and truncated
   *         to integer pixels.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * @see android.content.res.Resource#getDimension
   * @see android.content.res.Resource#getDimensionPixelOffset
   */
  public static int getDimensionPixelSizeById(int resId) {
    try {
      return getResources().getDimensionPixelSize(getCurrentResId(resId, SkinResType.DIMEN));
    } catch (NotFoundException ex) {
      throw ex;
    }
  }

  /**
   * Return a color integer associated with a particular resource name. If the resource
   * holds a complex {@link android.content.res.ColorStateList}, then the default color
   * from the set is returned.
   * @param resId
   *          The desired resource name.
   * @throws NotFoundException
   *           Throws NotFoundException if the given ID does not exist.
   * @return Returns a single color value in the form 0xAARRGGBB.
   */
  public static int getColorById(int resId) {
    try {
      return getResources().getColor(getCurrentResId(resId, SkinResType.COLOR));
    } catch (NotFoundException ex) {
      throw ex;
    }
  }

  /**
   * @Description: If we use a selector as one ui widget's background, we can
   *               setBackgroudDrawable to a StateListDrawable, it is also a background.
   * @param normalDrawableId
   * @param pressedDrawableId
   * @return
   */
  public static StateListDrawable getPressedListDrawable(int normalDrawableId, int pressedDrawableId) {
    return getListDrawableByNames(new int[] { normalDrawableId, pressedDrawableId });
  }

  public static StateListDrawable getPressedListDrawableByColorID(int normalColorId,
      int pressedColorId) {
    return getListDrawableByColorID(new int[] { normalColorId, pressedColorId });
  }

  public static StateListDrawable getPressedListDrawableByColor(int normalColor,
      int pressedColor) {
    return getListDrawableByColor(new int[] { normalColor, pressedColor });
  }

  public static SkinSettingInfo getCurrentSkinSettingInfo() {
    return SkinSetting.getCurrentSkinSettingInfo();
  }

  /**
   * Set show image setting in wifi mode
   * @param isShow
   */
  public static void setCurrentSkinIsShowImage(boolean isShow) {
    SkinSetting.setCurrentSkinIsShowImage(isShow);
  }

  public static void setCurrentSkinIsShowImageIn3G(boolean isShow) {
    SkinSetting.setCurrentSkinIsShowImageIn3G(isShow);
  }

  public static void setCurrentFontSize(SkinFontSize fontSize) {
    SkinSetting.setCurrentFontSize(fontSize);
  }

  /**
   * @Description: NOTE(chuanbeiqiao):这个类存在的原因是为了用View.PRESSED_ENABLED_STATE_SET等几个set，
   *               因为这几个set是View的protect的，非view子类 不能使用。
   * @param names
   *          0是normal状态，1是pressed状态。先支持两个看。做的过程中如果有3个以上需求，修改下MyView.getbg即可。
   * @return StateListDrawable。是用来设置点击效果的drawable。
   */
  private static StateListDrawable getListDrawableByNames(int[] names) {
    return MyView.getBackgroundByDrawableID(names);
  }

  private static StateListDrawable getListDrawableByColorID(int[] names) {
    return MyView.getBackgroundByColorID(names);
  }

  private static StateListDrawable getListDrawableByColor(int[] value) {
    return MyView.getBackgroundByColor(value);
  }

  private static Resources getResources() {
    PackageManager pm = ContextProvider.getApplicationContext().getPackageManager();
    try {
      return pm.getResourcesForApplication(sCurrentPackage);
    } catch (NameNotFoundException ex) {
      ex.printStackTrace();
      return ContextProvider.getApplicationContext().getResources();
    }
  }

  /**
   * HACK(qingxia): This is a hack if we want to get a series of drawables
   * @author qingxia
   */
  private static class MyView extends View {
    public MyView(Context context) {
      super(context);
    }

    private static StateListDrawable getBackground(Drawable normal, Drawable selected) {
      StateListDrawable bg = new StateListDrawable();
      bg.addState(View.PRESSED_ENABLED_STATE_SET, selected);
      bg.addState(View.ENABLED_STATE_SET, normal);
      bg.addState(View.FOCUSED_STATE_SET, selected);
      bg.addState(View.EMPTY_STATE_SET, normal);
      return bg;
    }

    public static StateListDrawable getBackgroundByDrawableID(int[] names) {
      Drawable normal = getDrawableById(names[0]);
      Drawable selected = getDrawableById(names[1]);
      return getBackground(normal, selected);
    }

    public static StateListDrawable getBackgroundByColorID(int[] names) {
      names[0] = getColorById(names[0]);
      names[1] = getColorById(names[1]);
      return getBackgroundByColor(names);
    }

    public static StateListDrawable getBackgroundByColor(int[] names) {
      Drawable normal = new ColorDrawable(names[0]);
      Drawable selected = new ColorDrawable(names[1]);
      return getBackground(normal, selected);
    }
  }

  /**
   * This function return resource id to corresponding a resource name.
   * @param resId
   * @param type
   * @return success: id (like R.id.xxx) failed: 0
   */
  public static int getCurrentResId(int resId, SkinResType type) {
    if (type == null) {
      return 0;
    }

    SkinInfo currentSkinInfo = getSkinInfoByIndex(getCurrentSkinIndex());

    if (currentSkinInfo == null) {
      return 0;
    }

    String resName = "";
    try {
      resName = getResources().getResourceEntryName(resId);
    } catch (NotFoundException ex) {
      // Can not find this id.
      ex.printStackTrace();
      return 0;
    }

    String skinTag = Utils.isStringEmpty(currentSkinInfo.getSkinID()) ? "" : currentSkinInfo
        .getSkinID() + "_";
    String skinName = skinTag + resName;
    // TODO(qingxia): Enable when needed
    // Logger.log("Get skin resource id by name = " + skinName + " type = " + type);

    // NOTE(junfengli) : if don't find resources, use resId
    int retId = getResources().getIdentifier(skinName, type.getTypeString(), sCurrentPackage);
    return (retId != 0 ? retId : resId);
  }

  /**
   * Get font size by a given id. This function will return the font size by current font
   * setting.
   * @param resId
   * @return
   */
  public static int getFontSizeById(int resId) {
    String resName = "";
    try {
      resName = getResources().getResourceEntryName(resId);
    } catch (NotFoundException ex) {
      // Can not find this id.
      ex.printStackTrace();
      return 0;
    }

    String fontTag = getCurrentFontSize().getTag();
    fontTag = Utils.isStringEmpty(fontTag) ? "" : fontTag + "_";
    String fontSizeName = fontTag + resName;

    int fontSizeId = getResources().getIdentifier(fontSizeName, SkinResType.DIMEN.getTypeString(),
        sCurrentPackage);

    return getResources().getDimensionPixelSize(fontSizeId);
  }

  public static void setCurrentResourcePackage(String packageName) {
    sCurrentPackage = packageName;
  }
}
/**
 *
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 *
 * @Description :
 *
 */

package com.buddhism.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.buddhism.base.ContextProvider;
import com.buddhism.sutra.R;

import java.util.List;
import java.util.Locale;

/**
 * @author summerxiaqing
 */
final public class Utils {
  /**
   * Check a string is empty or not.
   * @param str
   * @return boolean
   */
  public static boolean isStringEmpty(String str) {
    return str == null || str.length() == 0 || str.trim().length() == 0;
  }

  public static boolean isListDataEquals(
      List<? extends Object> data1, List<? extends Object> data2) {
    if (data1 == null && data2 == null) {
      return true;
    }

    if (data1 == null || data2 == null) {
      return false;
    }

    if (data1.size() != data2.size()) {
      return false;
    }

    int length = data1.size();
    for (int i = 0; i < length; i++) {
      if (!data1.get(i).equals(data2.get(i))) {
        return false;
      }
    }

    return true;
  }

  /**
   * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
   * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
   */
  public static int getScreenMode() {
    int screenMode = 0;
    try {
      screenMode = Settings.System.getInt(
          ContextProvider.getApplicationContext().getContentResolver(),
          Settings.System.SCREEN_BRIGHTNESS_MODE);
    } catch (Exception localException) {

    }
    return screenMode;
  }

  /**
   * 获得当前屏幕亮度值 0--255
   */
  public static int getScreenBrightness() {
    int screenBrightness = 255;
    try {
      screenBrightness = Settings.System.getInt(
          ContextProvider.getApplicationContext().getContentResolver(),
          Settings.System.SCREEN_BRIGHTNESS);
    } catch (Exception localException) {

    }
    return screenBrightness;
  }

  /**
   * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
   * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
   */
  public static void setScreenMode(int paramInt) {
    try {
      Settings.System
          .putInt(
              ContextProvider.getApplicationContext().getContentResolver(),
              Settings.System.SCREEN_BRIGHTNESS_MODE,
              paramInt);
    } catch (Exception localException) {
      localException.printStackTrace();
    }
  }

  /**
   * 设置当前屏幕亮度值 0--255
   */
  public static void saveScreenBrightness(int paramInt) {
    try {
      Settings.System.putInt(
          ContextProvider.getApplicationContext().getContentResolver(),
          Settings.System.SCREEN_BRIGHTNESS,
          paramInt);
    } catch (Exception localException) {
      localException.printStackTrace();
    }
  }

  /**
   * 保存当前的屏幕亮度值，并使之生效
   */
  public static void setScreenBrightness(Activity activity, int paramInt) {
    if (activity == null || paramInt > 255 || paramInt < 0) {
      return;
    }
    Window localWindow = activity.getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    float f = paramInt / 255.0F;
    localLayoutParams.screenBrightness = f;
    localWindow.setAttributes(localLayoutParams);
  }

  /**
   * Return if current mode is in simple Chinese
   * @return
   */
  public static boolean isInSimpleChineseMode() {
    final String CHINESE_CN = "cn";
    Locale locale = Locale.getDefault();
    if (locale.getCountry().equalsIgnoreCase(CHINESE_CN)) {
      return true;
    }

    return false;
  }

  // TODO(qingxia): This function will not return the correct result when the string have
  // both big5 and simple Chinese char
  /**
   * Check current string is simple Chinese or not
   * @return true simple chinese
   */
  public static boolean isCS(String str) {
    if (Utils.isStringEmpty(str)) {
      return false;
    }

    byte[] bytes = str.getBytes();
    if (bytes.length < 2)
      return false;

    byte aa = (byte) 0xB0;
    byte bb = (byte) 0xF7;
    byte cc = (byte) 0xA1;
    byte dd = (byte) 0xFE;

    // Judge the codec scope
    if (bytes[0] >= aa && bytes[0] <= bb) {
      if (bytes[1] < cc || bytes[1] > dd) {
        return false;
      }
      return true;
    }

    return false;
  }

  // TODO(qingxia): This function will not return the correct result when the string have
  // both big5 and simple Chinese char
  /**
   * Check current string is Big5 Chinese or not
   * @return
   */
  public static boolean isBig5(String str) {
    if (Utils.isStringEmpty(str)) {
      return false;
    }

    byte[] bytes = str.getBytes();
    if (bytes.length < 2)
      return false;

    final byte aa = (byte) 0xB0;
    final byte bb = (byte) 0xF7;
    final byte cc = (byte) 0xA1;
    final byte dd = (byte) 0xFE;

    // Judge the codec scope
    if (bytes[0] >= aa && bytes[0] <= bb) {
      if (bytes[1] < cc || bytes[1] > dd) {
        return true;
      }
      return false;
    }
    return false;
  }

  public static void openBrower(Activity activity, String url) {
    if (activity == null || isStringEmpty(url)) {
      return;
    }

    try {
      Uri uri = Uri.parse(url);
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      activity.startActivity(intent);
    } catch (Exception ex) {
      Toast
          .makeText(activity, activity.getString(R.string.error_no_browser), Toast.LENGTH_LONG)
          .show();
    }

  }

  public static void setBackgroundDrawableToView (View view, Drawable background) {
    if (view == null || background == null) {
      return;
    }

    if (Build.VERSION.SDK_INT < 16) {
      view.setBackgroundDrawable(background);
    } else {
      view.setBackground(background);
    }
  }
}

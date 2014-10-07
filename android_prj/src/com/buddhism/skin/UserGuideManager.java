/**
 * No Copyright.
 *
 * @Author : junfengli
 * @Description :
 */

package com.buddhism.skin;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

/**
 * @Description: This class provide a common interface to manage user guide windows.
 * @author junfengli
 */
final public class UserGuideManager {
  private static PopupWindow sPopupWindow = null;

  public static void showUserGuideWindow(Context context, View parent, int layout,
      int animationStyle, int backgroundColor, int width, int height) {
    if (parent != null) {
      int[] location = new int[2];
      parent.getLocationOnScreen(location);
      showUserGuideWindow(
          context,
          parent,
          layout,
          animationStyle,
          backgroundColor,
          location[0],
          location[1],
          width,
          height);
    }
  }

  public static void showUserGuideWindow(Context context, View parent, int layout,
      int animationStyle, int backgroundColor, int x, int y, int width, int height) {
    destoryGuideWindow();
    if (context == null || parent == null) {
      return;
    }
    View popupView = LayoutInflater.from(context).inflate(layout, null);
    sPopupWindow = new PopupWindow(popupView, width, height);
    sPopupWindow.setAnimationStyle(animationStyle);
    sPopupWindow.setBackgroundDrawable(new ColorDrawable(backgroundColor));
    sPopupWindow.setOutsideTouchable(true);
    popupView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        destoryGuideWindow();
      }
    });
    sPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, x, y);
  }

  public static void destoryGuideWindow() {
    if (sPopupWindow != null && sPopupWindow.isShowing()) {
      sPopupWindow.dismiss();
    }
    sPopupWindow = null;
  }
}
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有Dialog的基类，包含了一个显示优先级的属性mPriority。 只有优先级更高的Dialog才能覆盖正在显示的Dialog。
 * @author qizhang
 */

public class BaseDialog extends AlertDialog {
  private DialogPriority mPriority;
  private static BaseDialog sTopDialog;
  private static List<BaseDialog> sDialogs = new ArrayList<BaseDialog>();

  public DialogPriority getPriority() {
    return this.mPriority;
  }

  /**
   * @param context
   */
  protected BaseDialog(Context context, DialogPriority priority) {
    super(context);
    this.mPriority = priority;
    super.setOwnerActivity((Activity) context);
  }

  @Override
  public void show() {
    if (sTopDialog != null) {
      // 如果优先级比当前正在显示的dialog优先级低或者相等，则不显示。
      if (this.getPriority().isLowerEqualThan(sTopDialog.getPriority())) {
        return;
      }
    }
    sDialogs.add(this);
    sTopDialog = this;
    try {
      super.show();
    } catch (WindowManager.BadTokenException e) {
      // 如果创建这个Dialog使用的Activity已经finish了，再显示这个Dialog就会导致本异常的出现。
      // 没有很好的办法解决这个问题，Activity自带的showDialog方法也没解决这个问题。
      // YrLogger.e("BaseDailog", e.toString());
    }
  }

  public static void dismissAllDialogs() {
    for (BaseDialog dialog : sDialogs) {
      dialog.dismiss();
    }
  }

  @Override
  public void dismiss() {
    // 这里之所以加上一个判断，是因为在有些情况下一个dialog所在的activity已经为null，但是不会拋出leak a
    // window的异常，而在这里dismiss的时候才会拋异常，因此加上了这个判断。
    if (this.getOwnerActivity() != null && !this.getOwnerActivity().isFinishing()) {
      super.dismiss();
    }
    sDialogs.remove(this);
    sTopDialog = null;
  }
}
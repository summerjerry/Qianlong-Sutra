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

import android.util.Log;

/**
 * @author summerxiaqing
 */
final public class Logger {
  private static boolean isDebug = true;
  private static final String TAG = "BUDDHISM";

  public static void log(String text) {
    if (Utils.isStringEmpty(text) || !isDebug) {
      return;
    }

    Log.i(TAG, text);
  }
}

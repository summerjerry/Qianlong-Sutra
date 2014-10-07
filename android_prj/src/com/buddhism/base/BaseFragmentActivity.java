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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.List;

/**
 * @author summerxiaqing
 */
public class BaseFragmentActivity extends FragmentActivity {
  private static ActivityManager sManager = ActivityManager.getInstance();

  public static Activity getTopActivity() {
    return sManager.getTopActivity();
  }

  public static List<Activity> getActivities() {
    return sManager.getActivities();
  }

  public static void finishAllActivities() {
    sManager.finishAllActivities();
  }

  public static String getAplicationInBackgroundFlag() {
    return sManager.getAplicationInBackgroundFlag();
  }

  @Override
  protected void onPause() {
    super.onPause();
    sManager.onPause(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sManager.onCreate(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    sManager.onResume(this);
    this.onRefreshSkin();
  }

  @Override
  final public void finish() {
    super.finish();
    sManager.onDestroy(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    sManager.onDestroy(this);
  }

  protected void onRefreshSkin() {
  }

}

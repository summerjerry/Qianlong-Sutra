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

import com.buddhism.skin.SkinManager;
import com.buddhism.skin.SkinSettingInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;

import java.util.List;

/**
 * 这个类是所有Activity的基类，其中主要维护了两个变量：sTopActivity和sActivities。
 * 前者是这个APP正在显示的Activity，后者保存程序中所有的Activity，用于对程序的退出。
 * 在APP运行的过程中，可能会需要强制更新。可以通过showUpdateRequiredDialog(String, String)方法进行提示。
 * @author qizhang
 */
public class BaseActivity extends Activity {
  private static ActivityManager sManager = ActivityManager.getInstance();
  protected SkinSettingInfo mSkinSettingInfo = null;

  public interface ActivityResultListener {
    public void onActivityResult(int resultCode, Intent data);
  }

  private SparseArray<ActivityResultListener> listenerMap = new SparseArray<ActivityResultListener>();
  private int count = 0;

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

  protected void onRefreshSkin() {
  }

  /**
   * Start one activity, the requestCode will be handled automatically.
   * @param intent
   *          This intent contains the parameters to be passed to new activity.
   * @param listener
   *          The listener to call when the target activity returned. null if no return
   *          required.
   */
  public void transferActivity(Intent intent, ActivityResultListener listener) {
    if (listener == null) {
      this.startActivity(intent);
    } else {
      int requestCode = this.generateRequestCode();
      this.listenerMap.append(requestCode, listener);
      this.startActivityForResult(intent, requestCode);
    }
  }

  /**
   * Start one activity, the requestCode will be handled automatically.
   * @param cls
   *          This class need to be explicit sent.
   * @param listener
   *          The listener to call when the target activity returned. null if no return
   *          required.
   */
  public void transferActivity(Class<?> cls, ActivityResultListener listener) {
    this.transferActivity(new Intent(this.getApplicationContext(), cls), listener);
  }

  /**
   * Start activity with no parameter.
   * @param dest
   *          The destination activity to be transferred.
   * @param listener
   */
  public void transferActivity(Activity dest, ActivityResultListener listener) {
    this.transferActivity(new Intent(this.getApplicationContext(), dest.getClass()), listener);
  }

  /**
   * Start activity without return expected.
   */
  public void transferActivity(Intent intent) {
    this.transferActivity(intent, null);
  }

  /**
   * Start activity without return expected with no parameter.
   */
  public void transferActivity(Activity dest) {
    this.transferActivity(dest, null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    ActivityResultListener listener = this.listenerMap.get(requestCode);
    if (listener != null) {
      listener.onActivityResult(resultCode, data);
      this.listenerMap.remove(requestCode);
    }
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

  /**
   * Check if the skin setting has been changed. If changed will set current setting to
   * new setting infomation.
   * @return true: If the skin setting has been changed; false: If the skin setting has
   *         not been changed
   */
  protected boolean isSkinSettingChangedAndSetNewSetting() {
    SkinSettingInfo settingInfo = SkinManager.getCurrentSkinSettingInfo();

    if (this.mSkinSettingInfo == null && settingInfo == null) {
      return false;
    }

    if (this.mSkinSettingInfo == null || !this.mSkinSettingInfo.skinSettingEquals(settingInfo)) {
      this.mSkinSettingInfo = settingInfo;
      return true;
    }

    return false;
  }

  @Override
  protected void onResume() {
    super.onResume();
    sManager.onResume(this);
    if (this.isSkinSettingChangedAndSetNewSetting()) {
      this.onRefreshSkin();
    }
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

  /**
   * One activity may handle the back pressed event itself, otherwise, leave to normal
   * process. Sub classes can override this function to handle their own back pressed
   * event.
   * @return false This activity will handle back pressed event. true This activity will
   *         not handle this event.
   */
  protected boolean onHandleBackPressed() {
    return false;
  }

  @Override
  public void onBackPressed() {
    if (this.onHandleBackPressed()) {
      return;
    }
    super.onBackPressed();
  }

  private int generateRequestCode() {
    return this.count++;
  }
}

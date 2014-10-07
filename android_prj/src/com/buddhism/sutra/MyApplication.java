/**
 *
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 *
 * @Description :
 *
 */

package com.buddhism.sutra;

import com.buddhism.base.ContextProvider;
import com.buddhism.base.SharedPreferencesManager;
import com.buddhism.skin.SkinManager;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

/**
 * This class used to support some global infomation for the whole application
 * @author summerxiaqing
 */
public class MyApplication extends Application {
  private static MyApplication sApp = null;

  /**
   * We should initialize the application global environment here.
   */
  @Override
  public void onCreate() {
    super.onCreate();
    // We can initialize the application reference here.
    sApp = this;
    this.init();
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
  }

  /**
   * Get the application reference.
   * @return MyApplication the reference of MyApplication
   */
  synchronized public static MyApplication getApplication() {
    return sApp;
  }

  private void initSkin() {
    Resources resources = sApp.getResources();
    SkinManager.addSkin(resources.getString(R.string.skin_color_day),
        resources.getString(R.string.skin_color_day_name), -1);
    SkinManager.addSkin(resources.getString(R.string.skin_color_night),
        resources.getString(R.string.skin_color_night_name), -1);
  }

  /**
   * Do the global initialization in this function.
   */
  private void init() {
    ContextProvider.init(this);
    // Create global share preferences manager
    SharedPreferencesManager.createInstance(this);
    this.initSkin();
  }

}

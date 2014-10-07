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

import com.buddhism.base.BaseActivity;
import com.buddhism.sutra.data.DbListener;
import com.buddhism.sutra.data.DbRequest;
import com.buddhism.sutra.data.DbResponseData;
import com.buddhism.util.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class WelcomeActivity extends BaseActivity implements DbListener {
  private static final int PAGE_TIMER = 3000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_welcome);

    this.createDatabase();
  }

  private void createDatabase() {
    Toast.makeText(this, R.string.copying_db, Toast.LENGTH_LONG).show();
    DbRequest.createSutraDatabase(WelcomeActivity.this);
  }

  /**
   * 
   */
  private void prepareForNextStep(int timer) {
    timer = timer > 0 ? timer : PAGE_TIMER;
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.startActivity(intent);
        WelcomeActivity.this.finish();
      }
    }, timer);
  }

  /*
   * (non-Javadoc)
   * @see
   * com.buddhism.sutra.data.DbListener#onDbResponse(com.buddhism.sutra.data.DbResponseData
   * )
   */
  @Override
  public void onDbResponse(DbResponseData<?> data) {
    this.prepareForNextStep(PAGE_TIMER);
  }

  /*
   * (non-Javadoc)
   * @see com.buddhism.sutra.data.DbListener#onDbError(int)
   */
  @Override
  public void onDbError(int errorCode) {
    Logger.log("Create db failed");
    Toast.makeText(this, R.string.sd_error, Toast.LENGTH_SHORT).show();
    this.finish();
  }
}

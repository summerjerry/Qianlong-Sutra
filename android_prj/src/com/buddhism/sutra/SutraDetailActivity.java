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
import com.buddhism.base.SharedPreferencesManager;
import com.buddhism.skin.SkinManager;
import com.buddhism.sutra.data.DbDetailData;
import com.buddhism.sutra.data.DbListener;
import com.buddhism.sutra.data.DbRequest;
import com.buddhism.sutra.data.DbResponseData;
import com.buddhism.sutra.data.HistoryDbHelper;
import com.buddhism.sutra.data.HistoryDbHelper.SutraHistoryTableItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraDetailItem;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;
import com.buddhism.view.ObservableScrollView;
import com.buddhism.view.OnScrollChangedListener;
import com.buddhism.view.SelectableTextView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import taobe.tec.jcc.JChineseConvertor;

import java.io.IOException;

/**
 * @author summerxiaqing
 */
public class SutraDetailActivity extends BaseActivity implements DbListener, OnClickListener {
  final private static int MAX_SEEK_PROGRESS = 255;
  final private static int MAX_TEXT_SIZE = 100;
  final private static int MIN_TEXT_SIZE = 28;
  final private static int MIN_BRIGHTNESS = 5;
  private final static int DEFAULT_SELECTION_LEN = 5;

  private static final String SETTING_NAME = "setting_name";
  private static final String BRIGHTNESS_INDEX = "brightness_index";
  private static final String FONT_SIZE = "font_size";
  private SelectableTextView mTextView = null;
  private TextView mTitleView = null;
  private View mSutraView = null;
  private ObservableScrollView mScrollView = null;
  private ProgressBar mProgressBar;
  private View mSettingView = null;
  private String mSIndex = "";
  private String mSName = "";
  private boolean mIsLoading = false;
  private boolean mIsHideSettingView = true;
  private boolean mIsHideUtilsView = true;
  private String mContent = "";
  private SeekBar mSeekBar = null;
  private Button mFontAddBtn = null;
  private Button mFontSubBtn = null;
  private Button mSkinDayBtn = null;
  private Button mSkinNightBtn = null;
  private View mUtilView = null;
  private Button mUtilCopy = null;
  private Button mUtilBaidu = null;
  private Button mUtilGoogle = null;
  private int mTouchX;
  private int mTouchY;
  private int mSelectionColor = 0x40FF33FF;
  // Content control bar
  private TextView mNextPageBtn = null;
  private SeekBar mContentSeekBar = null;

  private OnClickListener mFontBtnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      if (v == SutraDetailActivity.this.mFontAddBtn) {
        Logger
            .log("mFontBtnClickListener add fontsize = " + SutraDetailActivity.this.getFontSize());
        SutraDetailActivity.this.setFontSize(SutraDetailActivity.this.getFontSize() + 1);
        SutraDetailActivity.this.onResetFontsize();
      }

      if (v == SutraDetailActivity.this.mFontSubBtn) {
        Logger
            .log("mFontBtnClickListener sub fontsize = " + SutraDetailActivity.this.getFontSize());
        SutraDetailActivity.this.setFontSize(SutraDetailActivity.this.getFontSize() - 1);
        SutraDetailActivity.this.onResetFontsize();
      }
    }

  };

  private OnClickListener mTextUtilListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      String selectStr = SutraDetailActivity.this.mTextView
          .getSelection()
          .getSelectedText()
          .toString();

      Logger.log("Select string = " + selectStr);

      if (v == SutraDetailActivity.this.mUtilCopy) {
        SutraDetailActivity.this.switchUtilsView(true);
        ClipboardManager cmb = (ClipboardManager) SutraDetailActivity.this
            .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(selectStr);
        Toast.makeText(
            SutraDetailActivity.this,
            SutraDetailActivity.this.getString(R.string.copy_to_clipboard),
            Toast.LENGTH_LONG).show();
      } else if (v == SutraDetailActivity.this.mUtilBaidu) {
        if (Utils.isStringEmpty(selectStr) || selectStr.length() > 50) {
          SutraDetailActivity.this.switchUtilsView(true);
          return;
        }

        final String url = "http://www.baidu.com/s?wd=" + selectStr;
        Utils.openBrower(SutraDetailActivity.this, url);
      } else if (v == SutraDetailActivity.this.mUtilGoogle) {
        if (Utils.isStringEmpty(selectStr) || selectStr.length() > 50) {
          SutraDetailActivity.this.switchUtilsView(true);
          return;
        }

        final String url = "https://www.google.com.hk/#q=" + selectStr;
        Utils.openBrower(SutraDetailActivity.this, url);
      }
    }

  };

  private void onResetFontsize() {
    long percent = this.getCurrentScrollPercent();
    this.onRefreshSkin();
    this.setScrollTo(percent);
  }

  private OnClickListener mSkinBtnClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {
      Logger.log("mSkinBtnClickListener current skin index = " + SkinManager.getCurrentSkinIndex());
      if (SutraDetailActivity.this.mSkinDayBtn == v) {
        // TODO(qingxia): Define 0 as a const
        if (SkinManager.getCurrentSkinIndex() != 0) {
          Logger.log("mSkinBtnClickListener change skin to day");
          SkinManager.setCurrentSkinIndex(0);
          SutraDetailActivity.this.onRefreshSkin();
        }
      }

      if (SutraDetailActivity.this.mSkinNightBtn == v) {
        // TODO(qingxia): Define 1 as a const
        if (SkinManager.getCurrentSkinIndex() != 1) {
          Logger.log("mSkinBtnClickListener change skin to night");
          SkinManager.setCurrentSkinIndex(1);
          SutraDetailActivity.this.onRefreshSkin();
        }
      }
    }

  };

  private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      int progress = seekBar.getProgress();
      Logger.log("onStopTrackingTouch progress = " + progress);
      if (progress < MIN_BRIGHTNESS) {
        progress = MIN_BRIGHTNESS;
      }
      SutraDetailActivity.this.setBrightness(progress);
    }

  };

  private void setSkinBtnStyle() {
    if (SkinManager.getCurrentSkinIndex() == 0) {
      this.mSkinDayBtn.setBackgroundColor(SkinManager
          .getColorById(R.color.main_page_setting_item_selected));
      this.mSkinDayBtn.setTextColor(SkinManager
          .getColorById(R.color.main_page_setting_btn_color_selected));
      this.mSkinNightBtn.setBackgroundColor(SkinManager
          .getColorById(R.color.main_page_setting_item_unselected));
      this.mSkinNightBtn
          .setTextColor(SkinManager.getColorById(R.color.main_page_setting_btn_color));
    }

    if (SkinManager.getCurrentSkinIndex() == 1) {
      this.mSkinDayBtn.setBackgroundColor(SkinManager
          .getColorById(R.color.main_page_setting_item_unselected));
      this.mSkinDayBtn.setTextColor(SkinManager
          .getColorById(R.color.main_page_setting_btn_color));
      this.mSkinNightBtn.setBackgroundColor(SkinManager
          .getColorById(R.color.main_page_setting_item_selected));
      this.mSkinNightBtn
          .setTextColor(SkinManager.getColorById(R.color.main_page_setting_btn_color_selected));
    }
  }

  private void setFontBtnStyle() {
    this.mFontAddBtn.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.setting_item_bg));
    this.mFontAddBtn.setTextColor(SkinManager
        .getColorById(R.color.main_page_setting_btn_color));
    this.mFontSubBtn.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.setting_item_bg));
    this.mFontSubBtn
        .setTextColor(SkinManager.getColorById(R.color.main_page_setting_btn_color));
  }

  private void setUtilsBtnStyle() {
    this.mUtilCopy.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.setting_item_bg));
    this.mUtilCopy.setTextColor(SkinManager
        .getColorById(R.color.main_page_setting_btn_color));
    this.mUtilBaidu.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.setting_item_bg));
    this.mUtilBaidu.setTextColor(SkinManager
        .getColorById(R.color.main_page_setting_btn_color));
    this.mUtilGoogle.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.setting_item_bg));
    this.mUtilGoogle.setTextColor(SkinManager
        .getColorById(R.color.main_page_setting_btn_color));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.initView();
    this.initData();
  }

  private void showSelectionCursors(int x, int y) {
    int start = this.mTextView.getPreciseOffset(x, y);

    if (start > -1) {
      int end = start + DEFAULT_SELECTION_LEN;
      if (end >= this.mTextView.getText().length()) {
        end = this.mTextView.getText().length() - 1;
      }
      this.mTextView.showSelectionControls(start, end);
    }
  }

  private void initView() {
    this.setContentView(R.layout.sutra_detail);
    // Initialize sutra content textview.
    this.mTextView = (SelectableTextView) this.findViewById(R.id.txt_content);
    this.mTextView.setDefaultSelectionColor(this.mSelectionColor);

    this.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        // Show selection while the setting view is hide
        if (SutraDetailActivity.this.mIsHideSettingView) {
          SutraDetailActivity.this.switchUtilsView(false);
        }

        return true;
      }
    });

    this.mTextView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!SutraDetailActivity.this.mIsHideUtilsView) {
          SutraDetailActivity.this.switchUtilsView(true);

          if (!SutraDetailActivity.this.mIsHideSettingView) {
            SutraDetailActivity.this.switchSettingView(true);
          }
          return;
        }

        SutraDetailActivity.this.switchSettingView(!SutraDetailActivity.this.mIsHideSettingView);
      }
    });

    this.mTextView.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        SutraDetailActivity.this.mTouchX = (int) event.getX();
        SutraDetailActivity.this.mTouchY = (int) event.getY();
        return false;
      }
    });

    // Initialize utils view
    this.mUtilView = this.findViewById(R.id.text_utils);
    this.mUtilCopy = (Button) this.findViewById(R.id.text_utils_copy);
    this.mUtilCopy.setOnClickListener(this.mTextUtilListener);
    this.mUtilBaidu = (Button) this.findViewById(R.id.text_utils_baidu);
    this.mUtilBaidu.setOnClickListener(this.mTextUtilListener);
    this.mUtilGoogle = (Button) this.findViewById(R.id.text_utils_google);
    this.mUtilGoogle.setOnClickListener(this.mTextUtilListener);

    this.mTitleView = (TextView) this.findViewById(R.id.txt_title);
    this.mSutraView = this.findViewById(R.id.sutra_detail_view);
    this.mScrollView = (ObservableScrollView) this.findViewById(R.id.scroll_view);
    this.mScrollView.addOnScrollChangedListener(new OnScrollChangedListener() {

      @Override
      public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        SutraDetailActivity.this.resetContentSeekBar();
      }

    });
    this.mProgressBar = (ProgressBar) this.findViewById(R.id.progress_loading);
    this.mSettingView = this.findViewById(R.id.detail_setting_view);
    this.mSeekBar = (SeekBar) this.findViewById(R.id.detail_setting_seekbar);
    this.mSeekBar.setMax(MAX_SEEK_PROGRESS);
    this.mSeekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
    this.mFontAddBtn = (Button) this.findViewById(R.id.detail_setting_btn_add);
    this.mFontAddBtn.setOnClickListener(this.mFontBtnClickListener);
    this.mFontSubBtn = (Button) this.findViewById(R.id.detail_setting_btn_sub);
    this.mFontSubBtn.setOnClickListener(this.mFontBtnClickListener);
    this.mSkinDayBtn = (Button) this.findViewById(R.id.detail_setting_btn_day);
    this.mSkinDayBtn.setOnClickListener(this.mSkinBtnClickListener);
    this.mSkinNightBtn = (Button) this.findViewById(R.id.detail_setting_btn_night);
    this.mSkinNightBtn.setOnClickListener(this.mSkinBtnClickListener);

    this.mContentSeekBar = (SeekBar) this.findViewById(R.id.content_seekbar);
    this.mContentSeekBar.setMax(HistoryDbHelper.SUTRA_HISTORY_PERCENT);
    this.mContentSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        Logger.log("ContentSeekBar onStopTrackingTouch progress = " + progress);

        // Seek the current text size to progress.
        SutraDetailActivity.this.setScrollTo(progress);
      }

    });
    this.mNextPageBtn = (TextView) this.findViewById(R.id.content_next_page_button);
    this.mNextPageBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        SutraDetailActivity.this.gotoNextPage();
      }

    });
  }

  private int getPageLines() {
    int lineHeight = this.mTextView.getLineHeight();
    int textViewHeight = 0;
    Rect rect = new Rect();
    if (this.mTextView.getGlobalVisibleRect(rect)) {
      textViewHeight = rect.height();
    }

    if (textViewHeight < 1) {
      textViewHeight = 1;
    }

    Logger.log("getPageLines lines number = " + textViewHeight / lineHeight);
    return textViewHeight / lineHeight;
  }

  /**
   * Scroll to next page.
   */
  private void gotoNextPage() {
    final int REMAIN_LINES_WHILE_PAGING = 5;
    int linesInAPage = this.getPageLines() - REMAIN_LINES_WHILE_PAGING;
    if (linesInAPage < 1) {
      return;
    }

    long percent = this.getCurrentScrollPercent() + linesInAPage
        * HistoryDbHelper.SUTRA_HISTORY_PERCENT / this.mTextView.getLineCount();
    this.setScrollTo(
        percent > HistoryDbHelper.SUTRA_HISTORY_PERCENT ? HistoryDbHelper.SUTRA_HISTORY_PERCENT
            : percent);
  }

  private void initData() {
    Intent intent = this.getIntent();
    if (intent == null) {
      return;
    }

    this.mSIndex = intent.getStringExtra(Intent.EXTRA_TEXT);
    this.mSName = intent.getStringExtra(Intent.EXTRA_TITLE);
    if (!Utils.isInSimpleChineseMode()) {
      try {
        this.mTitleView.setText(JChineseConvertor.getInstance().s2t(this.mSName));
      } catch (IOException e) {
        e.printStackTrace();
        this.mTitleView.setText(this.mSName);
      }
    } else {
      this.mTitleView.setText(this.mSName);
    }

    Logger.log("SutraDetailActivity sIndex = " + this.mSIndex);
    DbRequest.getDetailBySIndex(this, this.mSIndex);
    this.setLoadingState(true);
  }

  private void switchSettingView(boolean isHide) {
    this.mIsHideSettingView = isHide;
    if (isHide) {
      this.mSettingView.setVisibility(View.GONE);
    } else {
      this.mSeekBar.setProgress(this.getBrightness());
      this.mSettingView.setVisibility(View.VISIBLE);
    }
  }

  private void switchUtilsView(boolean isHide) {
    this.mIsHideUtilsView = isHide;
    this.mTextView.removeSelection();
    if (isHide) {
      this.mUtilView.setVisibility(View.GONE);
      this.mTextView.hideCursor();
    } else {
      this.mUtilView.setVisibility(View.VISIBLE);
      this.showSelectionCursors(this.mTouchX, this.mTouchY);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * com.buddhism.sutra.data.DbListener#onDbResponse(com.buddhism.sutra.data.DbResponseData
   * )
   */
  @Override
  public void onDbResponse(DbResponseData<?> data) {
    if (data instanceof DbDetailData) {
      SutraDetailItem itemData = ((DbDetailData) data).getData();
      this.setLoadingState(false);
      if (itemData == null || Utils.isStringEmpty(itemData.content)) {
        this.exitWithError();
        return;
      }

      if (!Utils.isInSimpleChineseMode()) {
        try {
          SpannableString str = new SpannableString(JChineseConvertor.getInstance().s2t(
              itemData.content));
          this.mTextView.setText(str);
        } catch (IOException e) {
          e.printStackTrace();
          SpannableString str = new SpannableString(itemData.content);
          this.mTextView.setText(str);
        }
      } else {
        SpannableString str = new SpannableString(itemData.content);
        this.mTextView.setText(str);
      }

      this.mContent = itemData.content;
      this.initFromHistory();
      return;
    }
  }

  /*
   * (non-Javadoc)
   * @see com.buddhism.sutra.data.DbListener#onDbError(int)
   */
  @Override
  public void onDbError(int errorCode) {
    this.setLoadingState(false);
    this.exitWithError();
  }

  private void initFromHistory() {
    SutraHistoryTableItem item = HistoryDbHelper.getInstance().queryBySIndex(this.mSIndex);
    if (item != null) {
      final int percent = item.readPercent;
      this.setScrollTo(percent);
    }
  }

  private void setScrollTo(final long percent) {
    if (percent > HistoryDbHelper.SUTRA_HISTORY_PERCENT || percent < 0) {
      return;
    }

    new Handler().post(new Runnable() {
      @Override
      public void run() {
        long line = SutraDetailActivity.this.mTextView.getLineCount() * percent
            / HistoryDbHelper.SUTRA_HISTORY_PERCENT;
        SutraDetailActivity.this.scrollSutraToLine((int) line);
        SutraDetailActivity.this.mContentSeekBar.setProgress((int) percent);
      }
    });
  }

  private void resetContentSeekBar() {
    final long percent = this.getCurrentScrollPercent();
    new Handler().post(new Runnable() {
      @Override
      public void run() {
        SutraDetailActivity.this.mContentSeekBar.setProgress((int) percent);
      }
    });
  }

  /**
   * Scroll current sutra to the line by parameter line.
   * @param line
   */
  private void scrollSutraToLine(final int line) {
    final int realLine = line > 0 ? line : 0;

    new Handler().post(new Runnable() {
      @Override
      public void run() {
        SutraDetailActivity.this.mScrollView.scrollTo(0, realLine
            * SutraDetailActivity.this.mTextView.getLineHeight()
            + SutraDetailActivity.this.mTextView.getPaddingTop());
      }
    });
  }

  private void exitWithError() {
    Toast.makeText(this, R.string.db_error, Toast.LENGTH_LONG).show();
    this.finish();
  }

  private void setLoadingState(boolean isLoading) {
    if (isLoading == this.mIsLoading) {
      return;
    }

    this.mIsLoading = isLoading;

    this.mProgressBar.setVisibility(this.mIsLoading ? View.VISIBLE : View.GONE);
  }

  @Override
  protected void onRefreshSkin() {
    Logger.log("SutraDetailActivity onRefreshSkin");
    this.mTextView.setDefaultSelectionColor(SkinManager.getColorById(R.color.select_bg_color));
    this.mTextView.setBackgroundColor(SkinManager.getColorById(R.color.main_page_bg));
    this.mTextView.setTextColor(SkinManager.getColorById(R.color.main_page_text_color));
    this.mNextPageBtn.setTextColor(SkinManager.getColorById(R.color.night_search_text_color));
    this.mTextView.setSelectionDrawable(SkinManager.getDrawableById(R.drawable.cursor));
    this.mSutraView.setBackgroundColor(SkinManager.getColorById(R.color.main_page_bg));
    this.mSettingView.setBackgroundColor(SkinManager
        .getColorById(R.color.main_page_setting_bg));
    this.mTitleView.setTextColor(SkinManager
        .getColorById(R.color.main_page_tab_select_text_color));
    this.mTitleView.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.tab_selected));
    this.mUtilView.setBackgroundColor(SkinManager.getColorById(R.color.main_page_setting_bg));
    this.setSkinBtnStyle();
    this.setFontBtnStyle();
    this.setUtilsBtnStyle();
    this.mSeekBar.setThumb(SkinManager.getDrawableById(R.drawable.seek_bt));
    this.mContentSeekBar.setThumb(SkinManager.getDrawableById(R.drawable.seek_bt));
    this.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.getFontSize());
  }

  private int getFontSize() {
    int fontSize = SharedPreferencesManager.getInt(SETTING_NAME, FONT_SIZE, this
        .getResources()
        .getDimensionPixelSize(R.dimen.sutra_content_size));

    return fontSize;
  }

  private void setFontSize(int fontSize) {
    int newFontSize = fontSize;
    if (fontSize > MAX_TEXT_SIZE) {
      newFontSize = MAX_TEXT_SIZE;
    }

    if (fontSize < MIN_TEXT_SIZE) {
      newFontSize = MIN_TEXT_SIZE;
    }

    SharedPreferencesManager.putInt(SETTING_NAME, FONT_SIZE, newFontSize);
  }

  /*
   * (non-Javadoc)
   * @see android.view.View.OnClickListener#onClick(android.view.View)
   */
  @Override
  public void onClick(View v) {
    if (v == this.mTextView) {
      this.switchSettingView(!this.mIsHideSettingView);
    }
  }

  private int getBrightness() {
    int progress = SharedPreferencesManager.getInt(SETTING_NAME, BRIGHTNESS_INDEX, Utils
        .getScreenBrightness());

    return progress;
  }

  @SuppressWarnings("unused")
  private void loadBrightness() {
    int brightness = SharedPreferencesManager.getInt(SETTING_NAME, BRIGHTNESS_INDEX, -1);
    if (brightness == -1) {
      SharedPreferencesManager.putInt(SETTING_NAME, BRIGHTNESS_INDEX, Utils
          .getScreenBrightness());
    }
    Utils.setScreenBrightness(this, brightness);
  }

  private void setBrightness(int brightness) {
    int newBrightness = brightness;
    if (brightness < 0) {
      newBrightness = 0;
    }

    if (brightness > MAX_SEEK_PROGRESS) {
      newBrightness = MAX_SEEK_PROGRESS;
    }

    Utils.setScreenBrightness(this, newBrightness);
    SharedPreferencesManager.putInt(SETTING_NAME, BRIGHTNESS_INDEX, newBrightness);
  }

  @Override
  protected void onResume() {
    super.onResume();
    // TODO(qingxia): Maybe add later.
    // this.loadBrightness();
  }

  private long getCurrentScrollPercent() {
    long lineHeight = this.mTextView.getLineHeight();
    long lineCount = this.mTextView.getLineCount();
    if (lineHeight * lineCount == 0) {
      return 0;
    }

    long percent = (long) this.mScrollView.getScrollY()
        * HistoryDbHelper.SUTRA_HISTORY_PERCENT / (lineCount * lineHeight);
    return percent;
  }

  @Override
  protected void onPause() {
    super.onPause();

    // There is no content data, so we will not store history.
    if (Utils.isStringEmpty(this.mContent)) {
      return;
    }

    long percent = this.getCurrentScrollPercent();
    Logger.log("onPause content view position : " +
        " getTop:" + this.mTextView.getTop() +
        " getHeight:" + this.mTextView.getHeight() +
        " getLineHeight:" + this.mTextView.getLineHeight() +
        " getLineCount:" + this.mTextView.getLineCount()
        + " scrollY:" + this.mScrollView.getScrollY()
        + " currentLine:" + percent);
    HistoryDbHelper.getInstance().replace(
        new SutraHistoryTableItem(this.mSIndex, this.mSName, (int) percent, null));
  }

  @Override
  protected boolean onHandleBackPressed() {
    if (!this.mIsHideSettingView) {
      this.switchSettingView(true);
      return true;
    }

    if (!this.mIsHideUtilsView) {
      this.switchUtilsView(true);
      return true;
    }

    return false;
  }
}

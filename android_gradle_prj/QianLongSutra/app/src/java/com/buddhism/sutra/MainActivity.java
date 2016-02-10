/**
 * Copyright 2013
 *
 * @Author : summerxiaqing
 * @Description :
 */

package com.buddhism.sutra;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.buddhism.base.BaseActivity;
import com.buddhism.skin.SkinManager;
import com.buddhism.sutra.HistoryListAdapter.OnDeleteBtnClickListener;
import com.buddhism.sutra.data.DbDicSearchListData;
import com.buddhism.sutra.data.DbHistoryListData;
import com.buddhism.sutra.data.DbListener;
import com.buddhism.sutra.data.DbPrimaryIndexData;
import com.buddhism.sutra.data.DbRequest;
import com.buddhism.sutra.data.DbResponseData;
import com.buddhism.sutra.data.DbSearchListData;
import com.buddhism.sutra.data.DbSecondaryIndexData;
import com.buddhism.sutra.data.HistoryDbHelper.SutraHistoryTableItem;
import com.buddhism.sutra.data.SutraDbHelper;
import com.buddhism.sutra.data.SutraDbHelper.SutraPrimaryIndexItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraSecondaryIndexItem;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;

/**
 * @author summerxiaqing
 */
public class MainActivity extends BaseActivity implements DbListener {
    private enum Tab {
        TAB_SUTRA(0),
        TAB_HISTORY(1),
        TAB_SEARCH(2);

        int index;

        Tab (int index) {
            this.index = index;
        }
    }

    private final static int HISTORY_PAGE_SIZE = 20;
    private ProgressBar mProgressBar;
    private boolean mIsLoading = false;
    private View mMainView = null;
    private View mTabSutraView = null;
    private TextView mSutraTextView = null;
    private TextView mHistoryTextView = null;
    private TextView mSearchTextView = null;
    private ViewPager mViewPager = null;
    private View mSutraIndexPage = null;
    private ListView mPrimaryIndexListView = null;
    private PrimaryListAdapter mPrimaryListAdapter = null;
    private ListView mSecondaryIndexListView = null;
    private SecondaryListAdapter mSecondaryListAdapter = null;
    private ListView mHistoryListView = null;
    private HistoryListAdapter mHistoryAdapter = null;
    private ListView mSearchListView = null;
    private SearchListAdapter mSearchAdapter = null;
    private EditText mSearchEdit = null;
    private TextView mSearchBtn = null;
    private View mSearchHeader = null;
    private Tab mCurrentTab = null;
    private String mQuery = null;
    private OnItemClickListener mPrimaryListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            if (MainActivity.this.mPrimaryListAdapter != null
                && position < MainActivity.this.mPrimaryListAdapter.getCount()) {
                String pIndex =
                    MainActivity.this.mPrimaryListAdapter.getData().get(position).pIndex;
                DbRequest.getSecondaryIndexByPIndex(MainActivity.this, pIndex);
                MainActivity.this.setLoadingState(true);
                MainActivity.this.switchIndexView(false);
            }
        }

    };

    private OnItemClickListener mSecondaryListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            if (MainActivity.this.mSecondaryListAdapter != null
                && position < MainActivity.this.mSecondaryListAdapter.getCount()) {
                String sIndex =
                    MainActivity.this.mSecondaryListAdapter.getData().get(position).sIndex;
                String sName =
                    MainActivity.this.mSecondaryListAdapter.getData().get(position).sName;
                Intent intent = new Intent(MainActivity.this, SutraDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, sIndex);
                intent.putExtra(Intent.EXTRA_TITLE, sName);
                MainActivity.this.startActivity(intent);
            }
        }

    };

    private OnItemClickListener mHistoryListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            if (MainActivity.this.mHistoryAdapter != null
                && position < MainActivity.this.mHistoryAdapter.getCount()) {
                String sIndex = MainActivity.this.mHistoryAdapter.getData().get(position).sIndex;
                String sName = MainActivity.this.mHistoryAdapter.getData().get(position).sName;
                Intent intent = new Intent(MainActivity.this, SutraDetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, sIndex);
                intent.putExtra(Intent.EXTRA_TITLE, sName);
                MainActivity.this.startActivity(intent);
            }
        }

    };

    private OnItemClickListener mSearchListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            if (MainActivity.this.mSearchAdapter != null
                && position < MainActivity.this.mSearchAdapter.getCount()) {
                if (MainActivity.this.mSearchAdapter.getData().get(
                    position) instanceof SutraSecondaryIndexItem) {
                    SutraSecondaryIndexItem itemData =
                        (SutraSecondaryIndexItem) MainActivity.this.mSearchAdapter.getData()
                                                                                  .get(position);
                    String sIndex = itemData.sIndex;
                    String sName = itemData.sName;
                    Intent intent = new Intent(MainActivity.this, SutraDetailActivity.class);
                    intent.putExtra(Intent.EXTRA_TEXT, sIndex);
                    intent.putExtra(Intent.EXTRA_TITLE, sName);
                    MainActivity.this.startActivity(intent);
                }
            }
        }

    };

    private OnKeyListener mKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey (View v, int keyCode, KeyEvent event) {
            if (KeyEvent.KEYCODE_ENTER == keyCode &&
                KeyEvent.ACTION_UP == (event.getAction() & MotionEvent.ACTION_MASK)) {
                MainActivity.this.doSearch();
                return true;
            }
            return false;
        }
    };

    public class MainPagerAdapter extends PagerAdapter implements OnPageChangeListener {

        Context context;
        List<View> viewList;

        @SuppressLint ("InflateParams")
        public MainPagerAdapter (Context context) {
            this.context = context;
            this.viewList = new LinkedList<View>();
            LayoutInflater inflater = LayoutInflater.from(context);
            // Init sutra tab
            MainActivity.this.mSutraIndexPage = inflater.inflate(R.layout.main_list, null);
            MainActivity.this.mPrimaryIndexListView = (ListView) MainActivity.this.mSutraIndexPage
                .findViewById(R.id.list_primary);
            MainActivity.this.mPrimaryIndexListView
                .setOnItemClickListener(MainActivity.this.mPrimaryListItemClickListener);
            MainActivity.this.mPrimaryListAdapter = new PrimaryListAdapter(MainActivity.this);
            MainActivity.this.mPrimaryIndexListView
                .setAdapter(MainActivity.this.mPrimaryListAdapter);
            MainActivity.this.mSecondaryIndexListView = (ListView) MainActivity.this.mSutraIndexPage
                .findViewById(R.id.list_secondary);
            MainActivity.this.mSecondaryListAdapter = new SecondaryListAdapter(MainActivity.this);
            MainActivity.this.mSecondaryIndexListView
                .setAdapter(MainActivity.this.mSecondaryListAdapter);
            MainActivity.this.mSecondaryIndexListView
                .setOnItemClickListener(MainActivity.this.mSecondaryListItemClickListener);
            MainActivity.this.mTabSutraView = MainActivity.this.mSutraIndexPage
                .findViewById(R.id.tab_sutra_view);
            this.viewList.add(MainActivity.this.mSutraIndexPage);
            // Init history tab
            View historyPage = inflater.inflate(R.layout.history_list, null);
            MainActivity.this.mHistoryListView =
                (ListView) historyPage.findViewById(R.id.list_history);
            MainActivity.this.mHistoryAdapter = new HistoryListAdapter(MainActivity.this);
            MainActivity.this.mHistoryListView.setAdapter(MainActivity.this.mHistoryAdapter);
            MainActivity.this.mHistoryListView
                .setOnItemClickListener(MainActivity.this.mHistoryListItemClickListener);
            MainActivity.this.mHistoryAdapter.setOnBtnClickListener(new OnDeleteBtnClickListener() {

                @Override
                public void onDeleteBtnClicked (final String id) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                    dialog.setTitle(MainActivity.this.getString(R.string.delete_record));
                    dialog.setCancelable(true);

                    dialog.setButton(DialogInterface.BUTTON_POSITIVE, MainActivity.this
                        .getString(R.string.ok), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            // Delete history db.
                            if (MainActivity.this.mHistoryAdapter.deleteItemBySId(id)) {
                                Toast.makeText(
                                    MainActivity.this,
                                    MainActivity.this.getString(R.string.delete_success),
                                    Toast.LENGTH_SHORT).show();

                                return;
                            }

                            Toast.makeText(
                                MainActivity.this,
                                MainActivity.this.getString(R.string.delete_failed),
                                Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.setButton2(
                        MainActivity.this.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick (DialogInterface dialog, int which) {
                                // Do nothing
                            }

                        });

                    dialog.show();
                }

            });
            this.viewList.add(historyPage);

            // Init search tab
            View searchPage = inflater.inflate(R.layout.search_list, null);
            MainActivity.this.mSearchListView =
                (ListView) searchPage.findViewById(R.id.list_history);
            MainActivity.this.mSearchAdapter = new SearchListAdapter(MainActivity.this);
            MainActivity.this.mSearchListView.setAdapter(MainActivity.this.mSearchAdapter);
            MainActivity.this.mSearchListView
                .setOnItemClickListener(MainActivity.this.mSearchListItemClickListener);
            MainActivity.this.mSearchEdit = (EditText) searchPage.findViewById(R.id.search_edit);
            MainActivity.this.mSearchEdit.setOnKeyListener(MainActivity.this.mKeyListener);
            MainActivity.this.mSearchBtn = (TextView) searchPage.findViewById(R.id.search_btn);
            MainActivity.this.mSearchBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick (View v) {
                    MainActivity.this.doSearch();
                }

            });
            MainActivity.this.mSearchHeader = searchPage.findViewById(R.id.search_header);
            this.viewList.add(searchPage);
        }

        @Override
        public int getCount () {
            return this.viewList.size();
        }

        @Override
        public boolean isViewFromObject (View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem (ViewGroup container, int position) {
            Logger.log("instantiateItem position = " + position);
            if (this.viewList == null) {
                Logger.log("instantiateItem viewlist == null");
            }

            Logger.log("instantiateItem viewlist size = " + this.viewList.size());
            Logger.log("viewlist position " + position + " view is "
                + this.viewList.get(position).getClass());
            ((ViewPager) container).addView(this.viewList.get(position));
            Logger.log("Adapter size = " + MainActivity.this.mPrimaryListAdapter.getCount());
            Logger.log("Adapter size = " + MainActivity.this.mSecondaryListAdapter.getCount());
            return this.viewList.get(position);
        }

        /**
         * Attention here! If you got a android ANR problem here,comment the super() method of it.
         */
        @Override
        public void destroyItem (ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(this.viewList.get(position));
        }

        /*
         * (non-Javadoc)
         * @see
         * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged
         * (int)
         */
        @Override
        public void onPageScrollStateChanged (int arg0) {
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int,
         * float, int)
         */
        @Override
        public void onPageScrolled (int arg0, float arg1, int arg2) {
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
         */
        @Override
        public void onPageSelected (int index) {
            Logger.log("Switch tab to index = " + index);
            if (index == Tab.TAB_SUTRA.index) {
                MainActivity.this.switchToTab(Tab.TAB_SUTRA);
            } else if (index == Tab.TAB_HISTORY.index) {
                MainActivity.this.switchToTab(Tab.TAB_HISTORY);
            } else if (index == Tab.TAB_SEARCH.index) {
                MainActivity.this.switchToTab(Tab.TAB_SEARCH);
            }
        }
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initView();
        this.initData();
    }

    private void initData () {
        this.switchToTab(Tab.TAB_SUTRA);
    }

    private void initView () {
        this.setContentView(R.layout.main);
        this.mProgressBar = (ProgressBar) this.findViewById(R.id.progress_loading);
        this.mViewPager = (ViewPager) this.findViewById(R.id.vp_main_page);
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        this.mViewPager.setAdapter(pagerAdapter);
        this.mViewPager.setOnPageChangeListener(pagerAdapter);
        this.mHistoryTextView = (TextView) this.findViewById(R.id.bt_history);
        this.mHistoryTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                MainActivity.this.switchToTab(Tab.TAB_HISTORY);
                MainActivity.this.mViewPager.setCurrentItem(Tab.TAB_HISTORY.index);
            }
        });
        this.mSutraTextView = (TextView) this.findViewById(R.id.bt_sutra_entrance);
        this.mSutraTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                MainActivity.this.switchToTab(Tab.TAB_SUTRA);
                MainActivity.this.mViewPager.setCurrentItem(Tab.TAB_SUTRA.index);
            }
        });
        this.mSearchTextView = (TextView) this.findViewById(R.id.bt_search);
        this.mSearchTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View v) {
                MainActivity.this.switchToTab(Tab.TAB_SEARCH);
                MainActivity.this.mViewPager.setCurrentItem(Tab.TAB_SEARCH.index);
            }
        });

        this.mMainView = this.findViewById(R.id.main_view);
    }

    private void doSearch (String oldQuery) {
        String query = oldQuery;
        Logger.log("doSearch query = " + query);

        ((InputMethodManager) this.mSearchEdit.getContext().getSystemService(
            Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(this.mSearchEdit.getWindowToken(), 0);

        if (Utils.isStringEmpty(query)) {
            Toast.makeText(this, this.getString(R.string.input_can_not_null), Toast.LENGTH_SHORT)
                 .show();
            return;
        }

        try {
            query = JChineseConvertor.getInstance().t2s(query);
        } catch (IOException e) {
            Toast.makeText(this, this.getString(R.string.input_can_not_known), Toast.LENGTH_SHORT)
                 .show();
            e.printStackTrace();
        }

        DbRequest.searchDictionary(this, query);
    }

    private void doSearch () {
        this.mQuery = this.mSearchEdit.getText().toString();
        this.doSearch(this.mQuery);
    }

    private void switchToTab (Tab tab) {
        if (tab == null || this.mCurrentTab == tab) {
            return;
        }

        this.mCurrentTab = tab;
        Logger.log("switchToTab index = " + tab.index);

        switch (this.mCurrentTab) {
            case TAB_SUTRA:
                // The sutra index data are always
                if (this.mPrimaryListAdapter != null && this.mPrimaryListAdapter.getCount() > 0) {
                    break;
                }
                Logger.log(
                    "Primary list is not null, count = " + this.mPrimaryListAdapter.getCount());

                if (this.isSutraTabInPrimaryIndex()) {
                    this.switchIndexView(true);
                } else {
                    this.switchIndexView(false);
                }

                this.prepareSutraData();
                break;
            case TAB_HISTORY:
                this.prepareHistoryData();
                break;
            case TAB_SEARCH:
                // TODO(qingxia): Add later.
                break;
        }

        this.setTabStyle();
    }

    private void prepareSutraData () {
        Logger.log("prepareSutraData");
        DbRequest.getPrimaryIndex(this);
        this.setLoadingState(true);
    }

    private boolean isSutraTabInPrimaryIndex () {
        return this.mPrimaryIndexListView.getVisibility() == View.VISIBLE;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.buddhism.sutra.data.DbListener#onDbResponse(com.buddhism.sutra.data.DbResponseData
     * )
     */
    @Override
    public void onDbResponse (DbResponseData<?> data) {
        Logger.log("onDbResponse data is" + data.getClass());
        if (data instanceof DbPrimaryIndexData) {
            List<SutraPrimaryIndexItem> listData = ((DbPrimaryIndexData) data).getData();
            this.mPrimaryListAdapter.setData(listData);
            this.setLoadingState(false);
            return;
        }

        if (data instanceof DbSecondaryIndexData) {
            this.setLoadingState(false);
            List<SutraSecondaryIndexItem> listData = ((DbSecondaryIndexData) data).getData();
            this.mSecondaryListAdapter.setData(listData);
            return;
        }

        if (data instanceof DbHistoryListData) {
            this.setLoadingState(false);
            List<SutraHistoryTableItem> listData = ((DbHistoryListData) data).getData();
            this.mHistoryAdapter.setData(listData);
            return;
        }

        if (data instanceof DbSearchListData) {
            this.setLoadingState(false);
            List<SutraSecondaryIndexItem> listData = ((DbSearchListData) data).getData();
            // If there is no result, we just nofity user.
            if (listData.isEmpty() && (this.mSearchAdapter.getData() == null || this
                .mSearchAdapter.getData().isEmpty())) {
                Toast.makeText(this, this.getString(R.string.search_no_result), Toast.LENGTH_SHORT)
                     .show();
            } else {
                this.mSearchAdapter.addData(listData);
                // Scroll search list view to top
                if (!this.mSearchListView.isStackFromBottom()) {
                    this.mSearchListView.setStackFromBottom(true);
                }
                this.mSearchListView.setStackFromBottom(false);
            }
            return;
        }

        if (data instanceof DbDicSearchListData) {
            List<SutraDbHelper.DictionaryItem> listData = ((DbDicSearchListData) data).getData();

            if (listData != null && !listData.isEmpty()) {
                this.mSearchAdapter.setData(listData, false);
            }

            DbRequest.searchSecondaryIndexBySName(this, this.mQuery);
            return;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.buddhism.sutra.data.DbListener#onDbError(int)
     */
    @Override
    public void onDbError (int errorCode) {
        Logger.log("onDbError errorCode = " + errorCode);
        this.setLoadingState(false);
    }

    private void prepareHistoryData () {
        DbRequest.getReadHistoryByLimitSize(this, HISTORY_PAGE_SIZE);
        this.setLoadingState(true);
    }

    private void switchIndexView (boolean isPrimaryIndex) {
        if (isPrimaryIndex) {
            this.mPrimaryIndexListView.setVisibility(View.VISIBLE);
            this.mSecondaryIndexListView.setVisibility(View.GONE);
        } else {
            this.mPrimaryIndexListView.setVisibility(View.GONE);
            this.mSecondaryIndexListView.setVisibility(View.VISIBLE);
            this.mSecondaryIndexListView.setSelection(0);
        }
    }

    private void setLoadingState (boolean isLoading) {
        if (isLoading == this.mIsLoading) {
            return;
        }

        this.mIsLoading = isLoading;

        this.mProgressBar.setVisibility(this.mIsLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed () {
        if (!this.isSutraTabInPrimaryIndex()) {
            this.switchIndexView(true);
            return;
        }

        super.onBackPressed();
    }

    private void setTabStyle () {
        switch (this.mCurrentTab) {
            case TAB_SUTRA:
                Utils.setBackgroundDrawableToView(this.mSutraTextView, SkinManager
                    .getDrawableById(R.drawable.tab_selected));
                this.mSutraTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_select_text_color));
                Utils.setBackgroundDrawableToView(this.mHistoryTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mHistoryTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                Utils.setBackgroundDrawableToView(this.mSearchTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mSearchTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                break;
            case TAB_HISTORY:
                Utils.setBackgroundDrawableToView(this.mHistoryTextView, SkinManager
                    .getDrawableById(R.drawable.tab_selected));
                this.mHistoryTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_select_text_color));
                Utils.setBackgroundDrawableToView(this.mSutraTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mSutraTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                Utils.setBackgroundDrawableToView(this.mSearchTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mSearchTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                break;
            case TAB_SEARCH:
                Utils.setBackgroundDrawableToView(this.mSearchTextView, SkinManager
                    .getDrawableById(R.drawable.tab_selected));
                this.mSearchTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_select_text_color));
                Utils.setBackgroundDrawableToView(this.mSutraTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mSutraTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                Utils.setBackgroundDrawableToView(this.mHistoryTextView, SkinManager
                    .getDrawableById(R.drawable.tab_unselected));
                this.mHistoryTextView.setTextColor(SkinManager
                    .getColorById(R.color.main_page_tab_text_color));
                break;
        }
    }

    @Override
    protected void onRefreshSkin () {
        Logger.log("MainActivity onRefreshSkin");
        this.mSutraTextView
            .setTextColor(SkinManager.getColorById(R.color.main_page_tab_text_color));
        this.mHistoryTextView
            .setTextColor(SkinManager.getColorById(R.color.main_page_tab_text_color));
        this.mSearchTextView
            .setTextColor(SkinManager.getColorById(R.color.main_page_tab_text_color));
        this.setTabStyle();
        this.mMainView.setBackgroundColor(SkinManager.getColorById(R.color.main_page_bg));
        this.mPrimaryIndexListView.setDivider(new ColorDrawable(SkinManager
            .getColorById(R.color.main_list_divider)));
        this.mSecondaryIndexListView.setDivider(new ColorDrawable(SkinManager
            .getColorById(R.color.main_list_divider)));
        this.mHistoryListView.setDivider(new ColorDrawable(SkinManager
            .getColorById(R.color.main_list_divider)));
        this.mHistoryListView
            .setBackgroundColor(SkinManager.getColorById(R.color.main_list_divider));
        this.mSearchListView.setDivider(new ColorDrawable(SkinManager
            .getColorById(R.color.main_list_divider)));
        this.mSearchListView
            .setBackgroundColor(SkinManager.getColorById(R.color.main_list_divider));
        this.mSearchHeader.setBackgroundColor(SkinManager.getColorById(R.color.search_bg_color));
        this.mSearchBtn.setTextColor(SkinManager.getColorById(R.color.search_text_color));
        this.mTabSutraView.setBackgroundColor(SkinManager.getColorById(R.color.main_list_divider));
    }

    @Override
    protected void onResume () {
        super.onResume();
        Logger.log("MainActivity onResume");
        if (this.mCurrentTab == Tab.TAB_HISTORY) {
            this.prepareHistoryData();
        }

        this.mHistoryAdapter.notifyDataSetChanged();
        this.mSearchAdapter.notifyDataSetChanged();
        this.mPrimaryListAdapter.notifyDataSetChanged();
        this.mSecondaryListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart () {
        Logger.log("MainActivity onRestart");
        super.onRestart();
        if (this.mCurrentTab == Tab.TAB_SUTRA && this.mPrimaryListAdapter.isEmpty() &&
            !this.mIsLoading) {
            SutraDbHelper.getInstance().openDataBase();
            this.prepareSutraData();
        }
        this.onRefreshSkin();
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        Logger.log("MainActivity onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        Logger.log("Current tab = " + this.mCurrentTab.index);
        if (this.mCurrentTab == Tab.TAB_SUTRA && this.mPrimaryListAdapter.isEmpty() &&
            !this.mIsLoading) {
            SutraDbHelper.getInstance().openDataBase();
            this.prepareSutraData();
        }
        this.onRefreshSkin();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

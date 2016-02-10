/**
 * Copyright 2013
 *
 * @Author : summerxiaqing
 * @Description :
 */

package com.buddhism.sutra;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
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
import com.buddhism.sutra.data.DbDicSearchListData;
import com.buddhism.sutra.data.DbListener;
import com.buddhism.sutra.data.DbRequest;
import com.buddhism.sutra.data.DbResponseData;
import com.buddhism.sutra.data.SutraDbHelper;
import com.buddhism.util.Logger;
import com.buddhism.util.Utils;

import java.io.IOException;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;

/**
 * @author summerxiaqing
 */
public class SearchResultActivity extends BaseActivity implements DbListener {

    private final int MAX_RETURN_NUM = 50;
    private ProgressBar mProgressBar;
    private boolean mIsLoading = false;
    private ListView mSearchListView = null;
    private SearchListAdapter mSearchAdapter = null;
    private EditText mSearchEdit = null;
    private TextView mSearchBtn = null;
    private View mSearchHeader = null;
    private String mQuery = null;

    private OnItemClickListener mSearchListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            if (SearchResultActivity.this.mSearchAdapter != null
                && position < SearchResultActivity.this.mSearchAdapter.getCount()) {
                // TODO(qingxia): Copy data to clipboard.
            }
        }
    };

    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey (View v, int keyCode, KeyEvent event) {
            if (KeyEvent.KEYCODE_ENTER == keyCode &&
                KeyEvent.ACTION_UP == (event.getAction() & MotionEvent.ACTION_MASK)) {
                SearchResultActivity.this.doSearch();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initView();
        this.initData();
    }

    private void initData () {
        Intent intent = this.getIntent();
        if (intent == null) {
            return;
        }

        this.mQuery = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (!Utils.isInSimpleChineseMode()) {
            try {
                this.mSearchEdit.setText(JChineseConvertor.getInstance().s2t(this.mQuery));
            } catch (IOException e) {
                e.printStackTrace();
                this.mSearchEdit.setText(this.mQuery);
            }
        } else {
            this.mSearchEdit.setText(this.mQuery);
        }

        if (this.mQuery != null && !this.mQuery.isEmpty()) {
            // Search result for query
            this.doSearch();
        }
    }

    private void initView () {
        this.setContentView(R.layout.text_search_list);
        this.mProgressBar = (ProgressBar) this.findViewById(R.id.text_search_loading);
        this.mSearchListView = (ListView) this.findViewById(R.id.text_result_list);
        this.mSearchBtn = (TextView) this.findViewById(R.id.text_search_btn);
        this.mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                SearchResultActivity.this.doSearch();
            }
        });
        this.mProgressBar = (ProgressBar) this.findViewById(R.id.text_search_loading);
        this.mSearchHeader = this.findViewById(R.id.text_search_header);
        this.mSearchEdit = (EditText) this.findViewById(R.id.text_search_edit);
        this.mSearchEdit.setOnKeyListener(this.mKeyListener);
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

        DbRequest.searchDictionary(this, query, MAX_RETURN_NUM);
    }

    private void doSearch () {
        this.mQuery = this.mSearchEdit.getText().toString();
        this.doSearch(this.mQuery);
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

        if (data instanceof DbDicSearchListData) {
            this.setLoadingState(false);
            List<SutraDbHelper.DictionaryItem> listData = ((DbDicSearchListData) data).getData();

            // If there is no result, we just nofity user.
            if (listData == null || listData.isEmpty()) {
                Toast.makeText(this, this.getString(R.string.search_no_word_result),
                    Toast.LENGTH_SHORT).show();
            } else {
                this.mSearchAdapter.setData(listData, true);
                // Scroll search list view to top
                if (!this.mSearchListView.isStackFromBottom()) {
                    this.mSearchListView.setStackFromBottom(true);
                }
                this.mSearchListView.setStackFromBottom(false);
            }
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

    private void setLoadingState (boolean isLoading) {
        if (isLoading == this.mIsLoading) {
            return;
        }

        this.mIsLoading = isLoading;

        this.mProgressBar.setVisibility(this.mIsLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed () {
        super.onBackPressed();
    }

    @Override
    protected void onRefreshSkin () {
        Logger.log("MainActivity onRefreshSkin");
        this.mSearchListView.setDivider(new ColorDrawable(SkinManager
            .getColorById(R.color.main_list_divider)));
        this.mSearchListView
            .setBackgroundColor(SkinManager.getColorById(R.color.main_list_divider));
        this.mSearchAdapter = new SearchListAdapter(this);
        this.mSearchListView.setAdapter(this.mSearchAdapter);
        this.mSearchHeader.setBackgroundColor(SkinManager.getColorById(R.color.search_bg_color));
        this.mSearchBtn.setTextColor(SkinManager.getColorById(R.color.search_text_color));
    }

    @Override
    protected void onResume () {
        super.onResume();
        Logger.log("MainActivity onResume");

        if (this.mSearchAdapter != null) {
            this.mSearchAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart () {
        Logger.log("MainActivity onRestart");
        super.onRestart();
        this.onRefreshSkin();
    }

    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        Logger.log("MainActivity onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
        this.onRefreshSkin();
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

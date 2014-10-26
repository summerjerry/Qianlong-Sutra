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

import com.buddhism.skin.OnSkinChangedListener;
import com.buddhism.skin.SkinManager;
import com.buddhism.sutra.data.HistoryDbHelper;
import com.buddhism.sutra.data.HistoryDbHelper.SutraHistoryTableItem;
import com.buddhism.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import taobe.tec.jcc.JChineseConvertor;

import java.io.IOException;
import java.util.List;

/**
 * @author summerxiaqing
 */
public class HistoryListAdapter extends BaseAdapter implements OnSkinChangedListener {
  public static interface OnDeleteBtnClickListener {
    public void onDeleteBtnClicked(String id);
  }

  private List<SutraHistoryTableItem> mData = null;
  private Context mContext = null;
  private LayoutInflater mInflater;
  private OnDeleteBtnClickListener mDelBtnClickListener = null;

  public HistoryListAdapter(Context context, List<SutraHistoryTableItem> data) {
    this.mContext = context;
    if (this.mContext == null) {
      throw new NullPointerException("PrimaryListViewAdapter context can not be null");
    }
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mData = data;
  }

  public HistoryListAdapter(Context context) {
    this(context, null);
  }

  /**
   * @return the mData
   */
  public List<SutraHistoryTableItem> getData() {
    return this.mData;
  }

  /**
   * @param mData
   *          the mData to set
   */
  public void setData(List<SutraHistoryTableItem> data) {
    if (data == null) {
      return;
    }
    if (!Utils.isListDataEquals(this.mData, data)) {
      this.mData = data;
      this.notifyDataSetChanged();
    }
  }

  /*
   * (non-Javadoc)
   * @see android.widget.Adapter#getCount()
   */
  @Override
  public int getCount() {
    return this.mData == null ? 0 : this.mData.size();
  }

  /*
   * (non-Javadoc)
   * @see android.widget.Adapter#getItem(int)
   */
  @Override
  public Object getItem(int position) {
    return this.mData.get(position);
  }

  /*
   * (non-Javadoc)
   * @see android.widget.Adapter#getItemId(int)
   */
  @Override
  public long getItemId(int position) {
    return 0;
  }

  private int getPositionBySId(String id) {
    if (Utils.isStringEmpty(id) || this.mData == null || this.mData.isEmpty()) {
      return -1;
    }

    int pos = 0;
    for (SutraHistoryTableItem item : this.mData) {
      if (item.sIndex.equalsIgnoreCase(id)) {
        return pos;
      }
      pos++;
    }

    return -1;
  }

  /**
   * Delete item from the history db and the list view
   * @param id
   * @return true means delete success false means delete failed
   */
  public boolean deleteItemBySId(String id) {
    int pos = this.getPositionBySId(id);
    if (pos == -1) {
      return false;
    }

    if (HistoryDbHelper.getInstance().delete(id)) {
      this.mData.remove(pos);
      this.notifyDataSetChanged();
      return true;
    }

    return false;
  }

  public void setOnBtnClickListener(OnDeleteBtnClickListener btnClickListener) {
    this.mDelBtnClickListener = btnClickListener;
  }

  /*
   * (non-Javadoc)
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @SuppressLint("InflateParams")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;

    if (null == convertView) {
      convertView = this.mInflater.inflate(R.layout.history_list_item, null);
      holder = new ViewHolder();
      holder.textView = (TextView) convertView.findViewById(R.id.txt_index_name);
      holder.historyItem = convertView.findViewById(R.id.history_item);
      holder.delBtn = (Button) convertView.findViewById(R.id.history_delete_btn);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    final String sId = this.mData.get(position).sIndex;
    String content = this.mData.get(position).sIndex + " | " + this.mData.get(position).sName;
    if (!Utils.isInSimpleChineseMode()) {
      try {
        holder.textView
            .setText(JChineseConvertor.getInstance().s2t(content));
      } catch (IOException e) {
        e.printStackTrace();
        holder.textView.setText(content);
      }
    } else {
      holder.textView.setText(content);
    }
    holder.textView.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.item_bg));

    holder.historyItem.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.item_bg));
    holder.textView.setTextColor(SkinManager.getColorById(R.color.main_page_text_color));

    holder.delBtn.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        if (HistoryListAdapter.this.mDelBtnClickListener != null) {
          HistoryListAdapter.this.mDelBtnClickListener.onDeleteBtnClicked(sId);
        }
      }

    });

    return convertView;
  }

  public class ViewHolder {
    public TextView textView;
    public View historyItem;
    public Button delBtn;
  }

  /*
   * (non-Javadoc)
   * @see com.buddhism.skin.OnSkinChangedListener#onSkinChanged()
   */
  @Override
  public void onSkinChanged() {
    // TODO(qingxia): Maybe do something later.
  }
}
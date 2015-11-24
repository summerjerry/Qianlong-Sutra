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
import com.buddhism.sutra.data.SutraDbHelper.SutraPrimaryIndexItem;
import com.buddhism.util.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import taobe.tec.jcc.JChineseConvertor;

import java.io.IOException;
import java.util.List;

/**
 * @author summerxiaqing
 */
public class PrimaryListAdapter extends BaseAdapter implements OnSkinChangedListener {
  private List<SutraPrimaryIndexItem> mData = null;
  private Context mContext = null;
  private LayoutInflater mInflater;

  public PrimaryListAdapter(Context context, List<SutraPrimaryIndexItem> data) {
    this.mContext = context;
    if (this.mContext == null) {
      throw new NullPointerException("PrimaryListViewAdapter context can not be null");
    }
    this.mInflater = LayoutInflater.from(this.mContext);
    this.mData = data;
  }

  public PrimaryListAdapter(Context context) {
    this(context, null);
  }

  /**
   * @return the mData
   */
  public List<SutraPrimaryIndexItem> getData() {
    return this.mData;
  }

  /**
   * @param mData
   *          the mData to set
   */
  public void setData(List<SutraPrimaryIndexItem> data) {
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

  /*
   * (non-Javadoc)
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;

    if (null == convertView) {
      convertView = this.mInflater.inflate(R.layout.main_list_item, null);
      holder = new ViewHolder();
      holder.textView = (TextView) convertView.findViewById(R.id.txt_index_name);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if (!Utils.isInSimpleChineseMode()) {
      try {
        holder.textView
            .setText(JChineseConvertor.getInstance().s2t(this.mData.get(position).pName));
      } catch (IOException e) {
        e.printStackTrace();
        holder.textView.setText(this.mData.get(position).pName);
      }
    } else {
      holder.textView.setText(this.mData.get(position).pName);
    }
    holder.textView.setBackgroundDrawable(SkinManager.getDrawableById(R.drawable.item_bg));
    holder.textView.setTextColor(SkinManager.getColorById(R.color.main_page_text_color));

    return convertView;
  }

  public class ViewHolder {
    public TextView textView;
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

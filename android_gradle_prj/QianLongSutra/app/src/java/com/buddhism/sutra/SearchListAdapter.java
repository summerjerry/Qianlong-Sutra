/**
 * Copyright 2013 No CopyRight
 *
 * @Author : summerxiaqing
 * @Description :
 */

package com.buddhism.sutra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buddhism.base.ContextProvider;
import com.buddhism.skin.SkinManager;
import com.buddhism.sutra.data.SutraDbHelper;
import com.buddhism.sutra.data.SutraDbHelper.DictionaryItem;
import com.buddhism.sutra.data.SutraDbHelper.SutraSecondaryIndexItem;
import com.buddhism.util.Utils;

import java.io.IOException;
import java.util.List;

import taobe.tec.jcc.JChineseConvertor;

/**
 * @author summerxiaqing
 */
public class SearchListAdapter extends BaseAdapter {
    private List mData = null;
    private Context mContext = null;
    private LayoutInflater mInflater;

    public SearchListAdapter (Context context, List data) {
        this.mContext = context;
        if (this.mContext == null) {
            throw new NullPointerException("SecondaryListAdapter context can not be null");
        }
        this.mInflater = LayoutInflater.from(this.mContext);
        this.mData = data;
    }

    public SearchListAdapter (Context context) {
        this(context, null);
    }

    /**
     * @return the mData
     */
    public List getData () {
        return this.mData;
    }

    /**
     * @param data
     * @param isNeedDataChanged
     *     the mData to set
     */
    public void setData (List data, boolean isNeedDataChanged) {
        if (data == null) {
            return;
        }
        if (!Utils.isListDataEquals(this.mData, data)) {
            this.mData = data;
            if (isNeedDataChanged) {
                this.notifyDataSetChanged();
            }
        }
    }

    /**
     * Add data to adapter.
     *
     * @param data
     */
    public void addData (List data) {
        if (data == null) {
            return;
        }

        if (this.mData == null) {
            this.mData = data;
            this.notifyDataSetChanged();
        } else {
            this.mData.addAll(data);
            this.notifyDataSetChanged();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount () {
        return this.mData == null ? 0 : this.mData.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem (int position) {
        return this.mData.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId (int position) {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (null == convertView) {
            convertView = this.mInflater.inflate(R.layout.main_list_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.txt_index_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String content = "";

        if (this.mData.get(position) instanceof SutraSecondaryIndexItem) {
            SutraSecondaryIndexItem data = (SutraSecondaryIndexItem) this.mData.get(position);
            content = data.sIndex + " | " + data.sName;
        } else if (this.mData.get(position) instanceof SutraDbHelper.DictionaryItem) {
            DictionaryItem data = (DictionaryItem) this.mData.get(position);
            content = data.keyword + " \n\n" + "       ---"
                + ContextProvider.getApplicationContext().getString(R.string.ding) +
                "\n\n " + data.description;
        }

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
        holder.textView.setTextColor(SkinManager.getColorById(R.color.main_page_text_color));

        return convertView;
    }

    public class ViewHolder {
        public TextView textView;
    }
}

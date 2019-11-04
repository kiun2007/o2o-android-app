package com.kiun.modelcommonsupport.adapter;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/11.
 */

public abstract class ListBaseAdapter extends BaseAdapter implements AdapterMore {
    protected Context context;
    protected ListView listView;
    protected int layoutId = 0;
    protected List mAllDatas;
    protected ItemListener listener;
    protected ItemCreater itemCreater;

    protected ListBaseAdapter(ListView listView, int layoutId){
        this.context = listView.getContext();
        this.layoutId = layoutId;
        this.listView = listView;
        mAllDatas = new ArrayList();
    }

    @Override
    public int getCount() {
        return mAllDatas == null ? 0 : mAllDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mAllDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public void setItemCreate(ItemCreater creater){
        itemCreater = creater;
    }
}

package com.kiun.modelcommonsupport.adapter;

import android.content.ContentProvider;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/18.
 */

public class CompositeAdapter extends ListBaseAdapter {

    int secondLayoutId = 0;
    int rowNumber = 1;
    String secondDataPath = null;

    public CompositeAdapter(ListView listView, int layoutId, int secondLayoutId, String secondDataPath, int rowNumber) {
        super(listView, layoutId);
        this.secondLayoutId = secondLayoutId;
        this.secondDataPath = secondDataPath;
        this.rowNumber = rowNumber;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //先不使用复用.
        View view = LayoutInflater.from(context).inflate(R.layout.item_composite_first, null);
        LinearLayout titleLayout = view.findViewById(R.id.composite_title);
        LayoutInflater.from(context).inflate(layoutId, titleLayout);

        final Object object = getItem(position);

        if(object instanceof JSONObject){
            JSONObject jsonObject = (JSONObject) object;
            JSONExtractor jsonExtractor = new JSONExtractor(jsonObject);

            if(view instanceof ViewGroup){
                ViewGroup viewGroup = (ViewGroup) view;
                List<DriveListener> driveListeners = new ArrayList<>();
                DriveBase.findLoop(viewGroup, driveListeners, null);

                for (final DriveListener driveListener : driveListeners){
                    String path = driveListener.getPath();
                    if(path != null){
                        driveListener.fill(jsonExtractor.extract(path));
                    }
                }
            }

            Object secValue = jsonExtractor.extract(secondDataPath);
            JSONArray secondDataArray = null;
            if(secValue instanceof JSONArray){
                secondDataArray = (JSONArray) secValue;
            }
            if(secondDataArray != null){
                GridView gridView = view.findViewById(R.id.composite_content);
                gridView.setNumColumns(rowNumber);
                GridAdapter adapter = new GridAdapter(context, secondDataArray);
                gridView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        return view;
    }

    @Override
    public void addItems(Object datas) {
        if(datas instanceof JSONArray){
            JSONArray jsonArray = (JSONArray) datas;
            for (int i = 0; i < jsonArray.length(); i ++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if(jsonObject != null) {
                    mAllDatas.add(jsonObject);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void fillItems(Object allDatas) {
        mAllDatas.clear();
        this.addItems(allDatas);
    }

    @Override
    public void clearAll() {

    }

    private class GridAdapter extends BaseAdapter{
        private Context context;
        private JSONArray data;

        GridAdapter(Context context, JSONArray data){
            this.context = context;
            this.data = data;
        }
        @Override
        public int getCount() {
            return data.length();
        }

        @Override
        public Object getItem(int i) {
            return data.optJSONObject(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View contentView = LayoutInflater.from(context).inflate(secondLayoutId, null);
            Object object = getItem(i);
            if(object instanceof JSONObject){
                DriveBase.fillViewData(contentView, object, false);
                if(itemCreater != null){
                    itemCreater.onItemCreate(contentView);
                }
            }
            return contentView;
        }
    }
}

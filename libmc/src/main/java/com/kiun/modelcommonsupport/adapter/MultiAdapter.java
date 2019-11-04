package com.kiun.modelcommonsupport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.kiun.modelcommonsupport.data.BeanBase;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/3/13.
 * 多功能Adapter.
 */

public abstract class MultiAdapter<T extends BeanBase> extends BaseAdapter {

    static class ViewHold
    {
        TextView textViews[] = new TextView[15];
    }

    private ItemListener<T> listener;

    public abstract String[] getTextViewFormat(int position);

    public abstract void getViewHode(View baseView,ViewHold viewHold,int position);

    public abstract int getLayoutId();

    public void dataComplete(){
    }

    public void viewShowed(ViewHold viewHold){
    }

    protected Context context;
    List<T> list;
    protected int textViewIds[] = new int[15];

    private MultiAdapter()
    {
        for(int i = 0; i < 15 ; i ++)
            textViewIds[i] = 0;
        list = new ArrayList<T>();
    }

    protected MultiAdapter(Context context){
        this();
        this.context = context;
    }

    /**
     * 初始化TextView 数组, 必须要和getTextViewFormat方法返回值对应.
     * @param args TextView的R.id值
     */
    public void initTextViews(int ...args)
    {
        if (args.length > 15)
            return;

        for(int i = 0; i < args.length ; i ++) {
            textViewIds[i] = args[i];
        }
    }

    public void setListener(ItemListener<T> listener){
        this.listener = listener;
    }

    private void flushViewDatas(View baseView, ViewHold viewHold, int position)
    {
        getViewHode(baseView, viewHold, position);

        String[] textViewDatas = getTextViewFormat(position);

        if (textViewDatas == null || textViewDatas.length > 15)
            return;

        for (int i = 0; i < textViewDatas.length; i ++)
        {
            if (viewHold.textViews[i] == null)
                break;
            viewHold.textViews[i].setText(textViewDatas[i]);
        }
    }

    public void updateDatas(List<T> newDatas)
    {
        list.clear();
        list.addAll(newDatas);

        dataComplete();
        notifyDataSetChanged();
    }

    private ViewHold initViewHold(View convertView)
    {
        ViewHold viewHold = new ViewHold();
        for(int i = 0; i < 15 ; i ++) {
            if (textViewIds[i] == 0)
                break;
            viewHold.textViews[i] = convertView.findViewById(textViewIds[i]);
        }
        return viewHold;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        if (list != null && list.size() > 0) {
            return list.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHold viewHold;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(getLayoutId(), null);
            viewHold = initViewHold(view);
            view.setTag(viewHold);
        }
        else {
            viewHold = (ViewHold) view.getTag();
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect(i);
                if(listener != null){
                    listener.onItemClick(null, list.get(i), 0);
                }
            }
        });

        viewShowed(viewHold);
        flushViewDatas(view, viewHold, i);
        return view;
    }

    protected void onSelect(int index){
    }
}

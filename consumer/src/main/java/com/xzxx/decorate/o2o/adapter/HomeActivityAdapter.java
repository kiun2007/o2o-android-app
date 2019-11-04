package com.xzxx.decorate.o2o.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amos.modulebase.adapter.ListViewBaseAdapter;
import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.OtherUtils;
import com.amos.modulebase.utils.picasso.PicassoUtil;
import com.kiun.modelcommonsupport.controllers.authority.WebBaseActivity;
import com.xzxx.decorate.o2o.bean.HomeActivityBean;
import com.xzxx.decorate.o2o.consumer.R;

import java.util.ArrayList;

/**
 * 首页活动item适配器
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2018/9/13
 * <br> Copyright: Copyright © 2018 suiji Technology. All rights reserved.
 */
public class HomeActivityAdapter extends ListViewBaseAdapter<HomeActivityBean> {

    //    private int size = 4;

    public HomeActivityAdapter(Context context, ArrayList<HomeActivityBean> dataList) {
        super(context, dataList);
    }
    //
    //    public HomeActivityAdapter(Context context, ArrayList<HomeActivityBean> dataList, int size) {
    //        super(context, dataList);
    //        this.size = size;
    //    }

    @Override
    public int getContentViewId() {
        return R.layout.item_grid_fragment_home_activity;
    }

    @Override
    public View getItemView(int i, View view, ViewHolder viewHolder) {
        ImageView img_content = viewHolder.getView(R.id.img_content);
        TextView txt_content = viewHolder.getView(R.id.txt_content);
        //        View view_root = viewHolder.getView(R.id.view_root);
        //        view_root.getLayoutParams().width = ScreenUtil.getScreenWidthPx() / size;
        final HomeActivityBean bean = dataList.get(i);
        txt_content.setText(bean.getTitle());
        PicassoUtil.loadImage(context, bean.getImgUrl(), img_content);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OtherUtils.getInstance().isFastClick(view)) return;
                //                ToastUtil.showToast(context, bean.getLinkUrl());

                if (!TextUtils.isEmpty(bean.getLinkUrl())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", bean.getLinkUrl());
                    bundle.putString("title", bean.getTitle());
                    //                    Intent intent = new Intent(context, WebBaseActivity.class);
                    //                    intent.putExtras(bundle);
                    //                    context.startActivity(intent);
                    IntentUtil.gotoActivity(context, WebBaseActivity.class, bundle);
                }
            }
        });
        return view;
    }

    //    public void setSize(int size) {
    //        this.size = size;
    //    }
}

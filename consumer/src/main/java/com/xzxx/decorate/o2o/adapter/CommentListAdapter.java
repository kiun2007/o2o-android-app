package com.xzxx.decorate.o2o.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amos.modulebase.adapter.ListViewBaseAdapter;
import com.amos.modulebase.utils.DateUtil;
import com.amos.modulebase.utils.MathUtils;
import com.amos.modulebase.utils.picasso.PicassoUtil;
import com.xzxx.decorate.o2o.bean.MasterCommentInfoVo;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.view.SimpleRatingBar;

import java.util.ArrayList;


/**
 * 商品详情评论列表适配器
 * <p>
 * <br> Author: 叶青
 * <br> Version: 3.0.0
 * <br> Date: 2017/2/7
 */
public class CommentListAdapter extends ListViewBaseAdapter<MasterCommentInfoVo> {

    public CommentListAdapter(Context context, ArrayList<MasterCommentInfoVo> dataList) {
        super(context, dataList);
    }

    @Override
    public int getContentViewId() {
        return R.layout.shop_item_comment_list;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        //LogUtil.e(getClass().getName() + " === " + position);
        //用户头像
        ImageView img_portrait = holder.getView(R.id.img_portrait);
        //商品名称
        TextView txt_name = holder.getView(R.id.txt_name);
        //商品评分
        SimpleRatingBar ratingBar = holder.getView(R.id.ratingBar);

        // 商品规格类型
        TextView txt_type = holder.getView(R.id.txt_type);
        TextView txt_date = holder.getView(R.id.txt_date);
        // 评论详情
        TextView txt_content = holder.getView(R.id.txt_content);
        // 商品评价图片资源
        LinearLayout view_res = holder.getView(R.id.view_res);
        GridView gridView = holder.getView(R.id.gridView);//

        final MasterCommentInfoVo bean = dataList.get(position);

        // 15 40 15 15

        if (bean.getOrderFiles().size() == 0) {
            view_res.setVisibility(View.GONE);
        } else {
            view_res.setVisibility(View.VISIBLE);
            gridView.setAdapter(bean.getAdapter(context));
            if (bean.getAdapter(context).getCount() == 0) {
                gridView.setVisibility(View.GONE);
                gridView.setNumColumns(3);
            } else {
                gridView.setVisibility(View.VISIBLE);
                if (bean.getAdapter(context).getCount() == 1) {
                    gridView.setNumColumns(1);
                } else {
                    gridView.setNumColumns(3);
                }
            }
        }

        try {
            ratingBar.setRating(MathUtils.str2Float(bean.getOrderScore(), 0f));
            if (!TextUtils.isEmpty(bean.getCustomerHeadImg())) {
                PicassoUtil.loadCircleImage(context, bean.getCustomerHeadImg(), img_portrait);
            } else {
                PicassoUtil.loadCircleImage(context, R.drawable.img_default_grey_base, img_portrait);
            }
            txt_name.setText(bean.getCustomerName());
            txt_type.setText(bean.getSecondItemName());
            //20180929140936
            txt_date.setText(DateUtil.formatTime(bean.getCommentTime(),"yyyyMMddHHmmss","MM月dd日"));
            txt_content.setText(bean.getContent());

        } catch (Exception e) {
            e.printStackTrace();
        }
        //        convertView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                Bundle bundle = new Bundle();
        //                bundle.putString(ConstantKey.INTENT_KEY_ID, "");
        //                //                IntentUtil.gotoActivity(context, ShopDetailActivity.class, bundle);
        //            }
        //        });
        return convertView;
    }
}

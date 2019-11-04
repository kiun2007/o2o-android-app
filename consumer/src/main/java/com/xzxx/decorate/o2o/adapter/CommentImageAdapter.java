package com.xzxx.decorate.o2o.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.amos.modulebase.adapter.ListViewBaseAdapter;
import com.amos.modulebase.utils.ScreenUtil;
import com.amos.modulebase.utils.picasso.CropTransformation;
import com.amos.modulebase.utils.picasso.PicassoUtil;
import com.amos.modulebase.utils.picasso.RoundedCornersTransformation;
import com.kiun.modelcommonsupport.ui.rollout.activity.RolloutPreviewActivity;
import com.kiun.modelcommonsupport.ui.rollout.model.RolloutBDInfo;
import com.kiun.modelcommonsupport.ui.rollout.model.RolloutInfo;
import com.squareup.picasso.Transformation;
import com.xzxx.decorate.o2o.bean.MasterCommentFile;
import com.xzxx.decorate.o2o.consumer.R;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 商品详情图片内容适配器
 * <p>
 * <br> Author: 叶青
 * <br> Version: 3.0.0
 * <br> Date: 2017/2/7
 */
public class CommentImageAdapter extends ListViewBaseAdapter<MasterCommentFile> {

    private int width;
    private int height;
    /** 0时素材，1时评论 */
    private int type;
    ArrayList<Transformation> transformations = new ArrayList();

    public CommentImageAdapter(Context context, ArrayList<MasterCommentFile> dataList, int type) {
        super(context, dataList);
        this.type = type;
        transformations.add(new CropTransformation(context, CropTransformation.CropType.RECTANGLE, width * 1f / height, -1, -1, -1));
        transformations.add(new RoundedCornersTransformation(ScreenUtil.dip2px(8), 0));
        if (type == 1) {
            int maxWidth = ScreenUtil.getScreenWidthPx() - ScreenUtil.dip2px(15 + 40 + 15 + 15);
            if (dataList.size() == 1) {
                width = maxWidth;
                height = (int) (maxWidth / 3f * 2);
            } else {

                width = (int) ((maxWidth - ScreenUtil.dip2px(15 * 2)) / 3f);
                height = (int) ((maxWidth - ScreenUtil.dip2px(15 * 2)) / 3f);
            }
        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.shop_item_detail_image;
    }

    @Override
    public View getItemView(final int position, View convertView, ViewHolder holder) {
        //        LogUtil.e(getClass().getName() + " === " + position);
        final ImageView img_content = holder.getView(R.id.img_content);
        final ImageView img_play = holder.getView(R.id.img_play);


        img_content.getLayoutParams().width = width;
        img_content.getLayoutParams().height = height;
        MasterCommentFile bena = dataList.get(position);

        if (bena.getType().equals("1")) {
            img_play.setVisibility(View.VISIBLE);
            //            PicassoUtil.loadRoundImage(context, bena.getVideoCoverImg(), img_content, width * 1f / height);
            PicassoUtil.loadRoundImage(context, bena.getVideoCoverImg(), img_content, com.amos.modulebase.R.drawable.img_default_grey_base, com.amos.modulebase.R.drawable.img_load_error_base, transformations);
        } else {
            img_play.setVisibility(View.GONE);
            //            PicassoUtil.loadRoundImage(context, bena.getUrl(), img_content, width * 1f / height);
            PicassoUtil.loadRoundImage(context, bena.getUrl(), img_content, com.amos.modulebase.R.drawable.img_default_grey_base, com.amos.modulebase.R.drawable.img_load_error_base, transformations);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Bundle bundle = new Bundle();
                //                bundle.putInt(ConstantKey.INTENT_KEY_POSITION, 0);
                //                IntentUtil.gotoActivity(context, ImageBrowseActivity.class, bundle);
                int[] location = new int[2];
                v.getLocationOnScreen(location);

                RolloutBDInfo bdInfo = new RolloutBDInfo();
                bdInfo.x = location[0];
                bdInfo.y = location[1];
                bdInfo.width = v.getWidth();
                bdInfo.height = v.getHeight();

                ArrayList<RolloutInfo> data = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    RolloutInfo info = new RolloutInfo();
                    if (dataList.get(i).getType().equals("1")) {
                        info.videoUrl = dataList.get(i).getUrl();
                        info.url = dataList.get(i).getVideoCoverImg();
                    } else {
                        info.url = dataList.get(i).getUrl();
                    }
                    data.add(info);
                }
                Intent intent = new Intent(context, RolloutPreviewActivity.class);
                intent.putExtra("data", (Serializable) data);
                intent.putExtra("bdinfo", bdInfo);
                intent.putExtra("index", position);
                intent.putExtra("type", 2);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

}

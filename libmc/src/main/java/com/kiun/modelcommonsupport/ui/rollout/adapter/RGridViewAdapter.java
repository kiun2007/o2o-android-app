package com.kiun.modelcommonsupport.ui.rollout.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.ui.rollout.activity.RolloutPreviewActivity;
import com.kiun.modelcommonsupport.ui.rollout.model.RolloutBDInfo;
import com.kiun.modelcommonsupport.ui.rollout.model.RolloutInfo;
import com.kiun.modelcommonsupport.ui.rollout.tools.RGlideUtil;
import com.kiun.modelcommonsupport.ui.views.MCRichEditView;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * arthur GridView 适配器
 */
public class RGridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RolloutInfo> data;
    private RolloutBDInfo bdInfo;
    private MCRichEditView mGridView;
    boolean showDel = false;

    public RGridViewAdapter(Context context, MCRichEditView gridView, ArrayList<RolloutInfo> data, boolean showDel) {
        this.context = context;
        this.data = data;
        this.mGridView = gridView;
        this.showDel = showDel;
        bdInfo = new RolloutBDInfo();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        RolloutInfo info = data.get(position);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context,
                    R.layout.item_media_image, null);
            holder.gridimage = (ImageView) convertView.findViewById(R.id.itemImageView);
            holder.progressBar = convertView.findViewById(R.id.loadingBar);
            holder.viedoImageView = convertView.findViewById(R.id.viedoImageView);
            holder.img_del = convertView.findViewById(R.id.img_del);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RGlideUtil.setImage(context, info.url, holder.gridimage);
        holder.gridimage.setOnClickListener(new ImageOnclick(position, holder.gridimage));
        holder.progressBar.setVisibility(info.isUpload ? View.VISIBLE : View.GONE);
        holder.viedoImageView.setVisibility(info.videoUrl != null ? View.VISIBLE : View.GONE);
        if (showDel) {
            holder.img_del.setVisibility(View.VISIBLE);
        } else {
            holder.img_del.setVisibility(View.GONE);
        }
        holder.img_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(data.get(position).videoUrl)) {
                    mGridView.vedioCount = mGridView.vedioCount - 1;
                }
                data.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public class ViewHolder {
        ImageView gridimage;
        ImageView viedoImageView;
        ImageView img_del;
        ProgressBar progressBar;
    }

    private class ImageOnclick implements View.OnClickListener {

        private int index;
        private ImageView imageView;

        public ImageOnclick(int index, ImageView imageView) {
            this.index = index;
            this.imageView = imageView;
        }

        @Override
        public void onClick(View v) {

            int[] location = new int[2];
            v.getLocationOnScreen(location);

            bdInfo.x = location[0];
            bdInfo.y = location[1];
            bdInfo.width = v.getWidth();
            bdInfo.height = v.getHeight();

            Intent intent = new Intent(context, RolloutPreviewActivity.class);
            intent.putExtra("data", (Serializable) data);
            intent.putExtra("bdinfo", bdInfo);
            intent.putExtra("index", index);
            intent.putExtra("type", 2);
            context.startActivity(intent);
        }
    }

}

package com.kiun.modelcommonsupport.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.BeanBase;
import com.kiun.modelcommonsupport.data.BeanExtractor;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.data.drive.MCDataField;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/4/11.
 */

public final class SuperSimpleAdapter extends ListBaseAdapter {

    private Class<? extends BeanBase> dataModelClz;

    public SuperSimpleAdapter(ListView listView, int layoutId, String modelClass) {
        super(listView, layoutId);
        if (!TextUtils.isEmpty(modelClass)) {
            try {
                dataModelClz = (Class<? extends BeanBase>) Class.forName(modelClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(layoutId, null);
        } else {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && mAllDatas != null && mAllDatas.size() > position) {
                    listener.onItemClick(listView, (BeanBase) mAllDatas.get(position), 0);
                }
            }
        });

        final Object object = getItem(position);
        BeanBase beanData = null;

        if (object instanceof JSONObject && dataModelClz != null) {
            JSONObject jsonObject = (JSONObject) object;
            try {
                beanData = dataModelClz.newInstance();
                MCDataField.fillBean(beanData, jsonObject);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        BeanBase finalBeanData = beanData;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && (object instanceof JSONObject)) {
                    listener.onItemClick(listView, finalBeanData, 0);
                }
            }
        });

        if (view instanceof ViewGroup && finalBeanData != null) {
            BeanExtractor extractor = new BeanExtractor(beanData);
            ViewGroup viewGroup = (ViewGroup) view;
            List<DriveListener> driveListeners = new ArrayList<>();
            DriveBase.findLoop(viewGroup, driveListeners, null);

            for (final DriveListener driveListener : driveListeners) {
                String path = driveListener.getPath();
                if (path != null && path.equals("indexPos")) {
                    driveListener.fill(position == getCount() - 1 ? 2 : (position == 0 ? 0 : 1));
                    continue;
                }
                if (path != null) {
                    driveListener.fill(extractor.extract(path));
                }
                if (driveListener.eventTag() > -1) {
                    View eventView = (View) driveListener;
                    eventView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (listener != null && (object instanceof JSONObject)) {
                                listener.onItemClick(listView, finalBeanData, driveListener.eventTag());
                            }
                        }
                    });
                }
            }

            finalBeanData.viewChange(view);
        }

        if (layoutId == R.layout.item_tick) {
            //            TextView txt_date_consumer = view.findViewById(R.id.txt_date_consumer);
            //            TextView txt_content = view.findViewById(R.id.txt_content);
            //            String discount = jsonObject.optString("discount", "");
            //            String voucherMoney = jsonObject.optString("voucherMoney", "");
            //
            //            if (txt_content != null) {
            //                if (!TextUtils.isEmpty(discount)) {
            //                    double dis = MathUtils.str2Double(discount);
            //                    String disStr = MathUtils.round4String(String.valueOf(dis / 10f), 1);
            //                    if (disStr.endsWith("0")) {
            //                        txt_content.setText(disStr.split("\\.")[0]);
            //                    } else {
            //                        txt_content.setText(disStr);
            //                    }
            //                }
            //            }
            //            if (txt_date_consumer != null) {
            //                txt_date_consumer.setTextColor(context.getResources().getColor(R.color.font_gray));
            //            }
            //
            //            String status = jsonObject.optString("status", "");
            //            //优惠券类型 0可用 1过期 2已使用 3维修费支付时可用券
            //            if (status.equals("0")) {
            //                String expireTime = jsonObject.optString("expireTime", "");
            //                if (!TextUtils.isEmpty(expireTime)) {
            //                    try {
            //                        long i = DateUtil.compareTime(expireTime, DateUtil.getDate("yyyyMMddHHmmss"), "yyyyMMddHHmmss");
            //                        if (i < 0) {
            //                            float lest = Math.abs((i / (24f * 60 * 60 * 1000)));
            //                            if (lest < 5) {
            //                                txt_date_consumer.setText("还有" + (int) (lest + 1) + "天到期");
            //                                txt_date_consumer.setTextColor(context.getResources().getColor(R.color.font_blue));
            //                            }
            //                            LogUtil.i(i + "" + "..." + lest);
            //                        }
            //                    } catch (Exception e) {
            //                        e.printStackTrace();
            //                    }
            //                }
            //            }

            TextView voucherDesc = view.findViewById(R.id.voucherDesc);
            ImageView img_more = view.findViewById(R.id.img_more);
            voucherDesc.post(new Runnable() {
                @Override
                public void run() {
                    if (voucherDesc.getLineCount() <= 1) {
                        img_more.setVisibility(View.GONE);
                    } else {
                        img_more.setVisibility(View.VISIBLE);
                        img_more.setTag("true");
                        voucherDesc.setEllipsize(TextUtils.TruncateAt.END);//
                        voucherDesc.setSingleLine(true);
                        img_more.setImageResource(R.drawable.arrow_down_grey);
                    }
                    img_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            // TODO Auto-generated method stub
                            Boolean flag = Boolean.valueOf((String) img_more.getTag());
                            if (flag) {
                                img_more.setTag("false");
                                voucherDesc.setEllipsize(null);// 展开
                                voucherDesc.setSingleLine(false);
                                img_more.setImageResource(R.drawable.arrow_up_grey);
                            } else {
                                img_more.setTag("true");
                                voucherDesc.setEllipsize(TextUtils.TruncateAt.END);//
                                voucherDesc.setSingleLine(true);
                                img_more.setImageResource(R.drawable.arrow_down_grey);
                            }
                        }
                    });
                }
            });

        }

        return view;
    }

    @Override
    public void addItems(Object datas) {
        if (datas instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) datas;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                if (jsonObject != null) {
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
        mAllDatas.clear();
        notifyDataSetChanged();
    }
}

package com.kiun.modelcommonsupport.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amos.modulebase.utils.DateUtil;
import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.MathUtils;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/4/19.
 */

public class AListAdapter extends ListBaseAdapter {

    private JSONArray jsonArray = null;


    public AListAdapter(ListView listView, int layoutId) {
        super(listView, layoutId);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(layoutId, null);
        } else {
            view = convertView;
        }
        final Object object = getItem(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && (object instanceof JSONObject)) {
                    listener.onItemClick(listView, object, 0);
                }
            }
        });

        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            JSONExtractor jsonExtractor = new JSONExtractor(jsonObject);

            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                List<DriveListener> driveListeners = new ArrayList<>();
                DriveBase.findLoop(viewGroup, driveListeners, null);

                for (final DriveListener driveListener : driveListeners) {
                    String path = driveListener.getPath();
                    if (path != null && path.equals("indexPos")) {
                        driveListener.fill(position == getCount() - 1 ? 2 : (position == 0 ? 0 : 1));
                        continue;
                    }
                    if(path != null && path.equals("indexValue")){
                        driveListener.fill(position);
                        continue;
                    }
                    if (path != null) {
                        driveListener.fill(jsonExtractor.extract(path));
                    }
                    if (driveListener.eventTag() > -1) {
                        View eventView = (View) driveListener;
                        eventView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (listener != null && (object instanceof JSONObject)) {
                                    listener.onItemClick(listView, object, driveListener.eventTag());
                                }
                            }
                        });
                    }
                }
            }
            if (layoutId == R.layout.item_tick) {
                TextView txt_date_consumer = view.findViewById(R.id.txt_date_consumer);
                TextView txt_content = view.findViewById(R.id.txt_content);
                String discount = jsonObject.optString("discount", "");
                String voucherMoney = jsonObject.optString("voucherMoney", "");

                if (txt_content != null) {
                    if (!TextUtils.isEmpty(discount)) {
                        double dis = MathUtils.str2Double(discount);
                        String disStr = MathUtils.round4String(String.valueOf(dis / 10f), 1);
                        if (disStr.endsWith("0")) {
                            txt_content.setText(disStr.split("\\.")[0]);
                        } else {
                            txt_content.setText(disStr);
                        }
                    }
                }
                if (txt_date_consumer != null) {
                    txt_date_consumer.setTextColor(context.getResources().getColor(R.color.font_gray));
                }

                String status = jsonObject.optString("status", "");
                //优惠券类型 0可用 1过期 2已使用 3维修费支付时可用券
                if (status.equals("0")) {
                    String expireTime = jsonObject.optString("expireTime", "");
                    if (!TextUtils.isEmpty(expireTime)) {
                        try {
                            long i = DateUtil.compareTime(expireTime, DateUtil.getDate("yyyyMMddHHmmss"), "yyyyMMddHHmmss");
                            if (i < 0) {
                                float lest = Math.abs((i / (24f * 60 * 60 * 1000)));
                                if (lest < 5) {
                                    txt_date_consumer.setText("还有" + (int) (lest + 1) + "天到期");
                                    txt_date_consumer.setTextColor(context.getResources().getColor(R.color.font_blue));
                                }
                                LogUtil.i(i + "" + "..." + lest);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            TextView txt_money = view.findViewWithTag("txt_money");
            if (txt_money != null) {
                String feeName = jsonObject.optString("feeName", "");
                if (!TextUtils.isEmpty(feeName)) {
                    if (feeName.equals("退款")) {
                        txt_money.setText("-" + txt_money.getText().toString());
                    } else {
                        txt_money.setText("+" + txt_money.getText().toString());
                    }
                }
            }

//            if (layoutId == R.layout.item_balance_record) {
//
//            }
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

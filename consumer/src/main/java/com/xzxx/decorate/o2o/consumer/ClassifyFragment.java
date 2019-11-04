package com.xzxx.decorate.o2o.consumer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AImageView;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.bean.FirstItem;
import com.xzxx.decorate.o2o.requests.classify.FirstItemRequest;
import com.xzxx.decorate.o2o.requests.classify.HotItemRequest;
import com.xzxx.decorate.o2o.requests.classify.SecondItemRequest;
import com.xzxx.decorate.o2o.ui.ItemContentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassifyFragment extends BaseRequestFragment implements AdapterView.OnItemClickListener,
        ItemListener<JSONObject> {

    private List<FirstItem> dataList = new ArrayList<>();
    private AListView classItemListView;
    private MyAdapter myAdapter;
    private FlowLayout flowLayout;
    private AImageView image_adv;
    private Map<String, String[]> listMap = new HashMap<String, String[]>();

    private static String selectedItem;

    @Override
    protected void initView(View view) {
        Log.e("ClassifyFragment", "initView");

        classItemListView = view.findViewById(R.id.id_listView_classify);
        image_adv = view.findViewById(R.id.id_image_adv);
        flowLayout = view.findViewById(R.id.id_flowLayout_classify);
        myAdapter = new MyAdapter(getContext(), R.layout.item_listview_classify, dataList);
        classItemListView.setAdapter(myAdapter);
        classItemListView.setItemListener(this);
        classItemListView.setOnItemClickListener(this);

        commitRequest(new FirstItemRequest());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("ClassifyFragment", "onResume " + selectedItem);
        showSelectItem();
    }

    @Override
    public void onPause() {
        super.onPause();
        selectedItem = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.v("ClassifyFragment", "position " + position);
        flowLayout.removeAllViews();
        requestSecondItem(position);
    }

    public void showFlowText(JSONArray data) {
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.length(); i++) {
            int ranHeight = BasicUtils.dip2px(getContext(), 30);
            final String itemId = data.optJSONObject(i).optString("secondItemId");
            final String itemName = data.optJSONObject(i).optString("secondItemName");
            final String secondItemHTMLUrl = data.optJSONObject(i).optString("secondItemHTMLUrl");
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(flowLayout.getWidth() / 3 - 12, ranHeight);
            lp.setMargins(BasicUtils.dip2px(getContext(), 2), 0, BasicUtils.dip2px(getContext(), 2), 0);
            final TextView tv = new TextView(getContext());
            tv.setPadding(BasicUtils.dip2px(getContext(), 3), 0, BasicUtils.dip2px(getContext(), 3), 0);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setText(itemName);
            tv.setGravity(Gravity.CENTER);
            tv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
            tv.setLines(1);
            tv.setBackgroundResource(R.drawable.bg_flow_layout_tag);
            flowLayout.addView(tv, lp);

            tv.setOnClickListener(new View.OnClickListener() { //添加右侧标签的点击事件
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ItemContentActivity.class);
                    intent.putExtra("secondItemId", itemId);
                    intent.putExtra("secondItemName", itemName);
                    intent.putExtra("secondItemHTMLUrl", secondItemHTMLUrl);
                    startActivity(intent);
                }
            });
        }
    }

    private void requestSecondItem(int position) {
        if (dataList.size() > 0 && position < dataList.size()) {
            FirstItem firstItem = dataList.get(position);
            if (firstItem.getFirstItemId().equals("热门服务")) {
                HotItemRequest secondItemRequest = new HotItemRequest();
                commitRequest(secondItemRequest);
            } else {
                Glide.with(getContext())
                        .load(firstItem.getBannerImgUrl())
                        .into(image_adv);

                SecondItemRequest secondItemRequest = new SecondItemRequest();
                secondItemRequest.firstItemId = firstItem.getFirstItemId();
                commitRequest(secondItemRequest);
            }
            myAdapter.setSelectPosition(position);
            myAdapter.notifyDataSetChanged();
        }
    }

    private void showSelectItem() {
        if (!TextUtils.isEmpty(selectedItem)) {
            if (dataList != null) {
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getFirstItemId().equals(selectedItem)) {
                        requestSecondItem(i);
                    }
                }
            }
        }
    }

    public static void setSelectItem(String item) {
        selectedItem = item;
    }

    private JSONArray insertHOT(JSONArray jsonArray) {
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("firstItemId", "热门服务");
            jsonObject.put("firstItemName", "热门服务");
            jsonObject.put("firstItemOrderNum", 0);
            jsonObject.put("bannerImgUrl", "热门服务");
            jsonObject.put("linkUrl", "热门服务");
            array.put(0, jsonObject);

            for (int i = 0; i < jsonArray.length(); i++) {
                array.put(i + 1, jsonArray.opt(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return array;
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        Log.e("ClassifyFragment", "onDataChanged data " + data);
        if (request instanceof FirstItemRequest) {
            //            fillToView(R.id.id_listView_classify, data, true);
            data = insertHOT((JSONArray) data);
            JsonParser jsonParser = new JsonParser();
            Gson gson = new Gson();
            dataList.clear();
            JsonArray jsonArray = jsonParser.parse(data.toString()).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                FirstItem object = gson.fromJson(element, FirstItem.class);
                dataList.add(object);
            }
            myAdapter.notifyDataSetChanged();
            requestSecondItem(0);
        }

        if (request instanceof SecondItemRequest && data instanceof JSONArray) {
            flowLayout.removeAllViews();
            showFlowText((JSONArray) data);
        }

        if (request instanceof HotItemRequest) {
            JSONObject jsonObject = ((JSONObject) data);
            String bannerImgUrl = jsonObject.optString("bannerImgUrl", "");
            JSONArray jsonArray = jsonObject.optJSONArray("classifySecondItemInfos");
            Glide.with(getContext())
                    .load(bannerImgUrl)
                    .into(image_adv);
            flowLayout.removeAllViews();
            showFlowText(jsonArray);
        }
    }

    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {
        Log.e("ClassifyFragment", "itemData " + itemData + "tag " + tag);
        if (listView == classItemListView) {
            JSONExtractor extractor = new JSONExtractor(itemData);
            String imageurl = (String) extractor.extract("bannerImgUrl");
            String firstItemId = (String) extractor.extract("firstItemId");

            if (firstItemId.equals("热门服务")) {
                HotItemRequest secondItemRequest = new HotItemRequest();
                commitRequest(secondItemRequest);
            } else {
                Glide.with(getContext())
                        .load(imageurl)
                        .into(image_adv);

                SecondItemRequest secondItemRequest = new SecondItemRequest();
                secondItemRequest.firstItemId = firstItemId;
                commitRequest(secondItemRequest);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        private Context mContext;
        private int resourceId;
        private List<FirstItem> array;
        private int selectedPosition;

        public MyAdapter(@NonNull Context context, int resource, List<FirstItem> data) {
            this.mContext = context;
            this.array = data;
            this.resourceId = resource;
        }

        public void setSelectPosition(int position) {
            this.selectedPosition = position;
        }

        @Override
        public int getCount() {
            return array.size();
        }

        @Override
        public Object getItem(int position) {
            return array.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(mContext);
                convertView = inflater.inflate(resourceId, null);
                viewHolder.ll = convertView.findViewById(R.id.id_item_ll_listView_classify);
                viewHolder.view = convertView.findViewById(R.id.id_item_view_listview_classify);
                viewHolder.title = convertView.findViewById(R.id.id_item_text_listView_classify);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FirstItem firstItem = (FirstItem) getItem(position);
            Log.v("ClassifyFragment", "firstItem " + firstItem);
            viewHolder.title.setText(firstItem.getFirstItemName());
            if (selectedPosition == position) {
                viewHolder.ll.setBackgroundColor(Color.WHITE);
                viewHolder.view.setVisibility(View.VISIBLE);
            } else {
                viewHolder.ll.setBackgroundColor(Color.rgb(0xE8, 0xE8, 0XE8));
                viewHolder.view.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder {
            private LinearLayout ll;
            private View view;
            private TextView title;
        }
    }
}

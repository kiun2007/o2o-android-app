package com.xzxx.decorate.o2o.consumer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amos.modulebase.bean.BaseBean;
import com.amos.modulebase.utils.IntentUtil;
import com.amos.modulebase.utils.NetWorkUtil;
import com.amos.modulebase.utils.ScreenUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.utils.EaseLoadUnread;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.controllers.SelectCityActivity;
import com.kiun.modelcommonsupport.controllers.authority.WebBaseActivity;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.hx.ContactListActivity;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.requests.OpenedCityRequest;
import com.kiun.modelcommonsupport.ui.views.AListView;
import com.kiun.modelcommonsupport.utils.MCString;
import com.xzxx.decorate.o2o.adapter.HomeActivityAdapter;
import com.xzxx.decorate.o2o.adapter.OrderAdapter;
import com.xzxx.decorate.o2o.bean.HomeActivityBean;
import com.xzxx.decorate.o2o.loader.GlideImageLoader;
import com.xzxx.decorate.o2o.requests.homepage.BroadcastRequest;
import com.xzxx.decorate.o2o.requests.homepage.HomeActivityRequest;
import com.xzxx.decorate.o2o.requests.homepage.HomeItemRequest;
import com.xzxx.decorate.o2o.requests.homepage.HomePageOrderList;
import com.xzxx.decorate.o2o.ui.ItemContentActivity;
import com.xzxx.decorate.o2o.ui.OrderDetailActivity;
import com.xzxx.decorate.o2o.ui.WaitServiceActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseRequestFragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView
        .OnItemClickListener, OnBannerListener, ItemListener<JSONObject> {

    private Banner banner;
    private static final int REFRESH_COMPLETE = 0X1112;

    private AListView homeItemList;
    private AListView homeOrderList;
    private TextView txt_more;
//    private NavigatorBar navigatorBar;

    JSONArray allCitys;

    private boolean isMore = false;

    @Override
    public void onLeftClick() {
        Intent selectIntent = new Intent(getContext(), SelectCityActivity.class);
        startActivityForResult(selectIntent, 999);
    }

    @Override
    protected void initView(View view) {
//        title = view.findViewById(R.id.title);
        banner = view.findViewById(R.id.home_banner);
        homeOrderList = view.findViewById(R.id.homeOrderList);
        homeItemList = view.findViewById(R.id.homeItemList);
        txt_more = view.findViewById(R.id.txt_more);

        homeOrderList.setItemListener(this);
        homeItemList.setItemListener(this);
        commitRequest(new BroadcastRequest());
        onRefresh(true);
        registerReceiver();
        txt_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isMore) {
                    isMore = true;
                    fillToView(R.id.homeOrderList, orderList, true);
                    txt_more.setText("收起更多");
                } else {
                    isMore = false;
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray.put(orderList.getJSONObject(0));
                        jsonArray.put(orderList.getJSONObject(1));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillToView(R.id.homeOrderList, jsonArray, true);
                    txt_more.setText("查看更多");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh(false);

        if (MainApplication.getInstance().getUserInfo(false) == null) {
            mView.findViewById(R.id.listPaddingView).setVisibility(View.GONE);
            navigatorBar.setUnRead("");
        } else {
            mView.findViewById(R.id.listPaddingView).setVisibility(View.VISIBLE);
            //相当于Fragment的onResume
            unreadShow();
        }

    }

    private void unreadShow() {
        int count = new EaseLoadUnread().unReadCount();
        if (count > 0) {
            navigatorBar.setUnRead(String.valueOf(count));
        } else {
            navigatorBar.setUnRead("");
        }
    }

    String selectCity="";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode != 0) {
            selectCity=(data.getStringExtra("name"));
            navigatorBar.setLeftButtonText(selectCity);
        }
    }

    @Override
    public void onRefresh(boolean isPullDown) {
        if (!NetWorkUtil.isNetworkAvailable()) {
            ToastUtil.showToast(getActivity(), "网络不可用，请检查您的网络");
        }
        // ((BaseRequestAcitity)getActivity()).showProgress(false);
        if (isPullDown) {
            commitRequest(new HomeActivityRequest());
            commitRequest(new HomeItemRequest());
        }
        commitRequest(new HomePageOrderList());
        commitRequest(new OpenedCityRequest());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                unreadShow();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {
            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @Override
    public void onRightClick() {
        Intent intent = new Intent(this.getContext(), ContactListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    @Override
    public void OnBannerClick(int position) {
        try {
            String url = BroadcastRequestData.getJSONObject(position).optString("linkUrl", "");
            if (TextUtils.isEmpty(url)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("url", url);
            bundle.putString("title", "详情");
            IntentUtil.gotoActivity(getActivity(), WebBaseActivity.class, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONArray BroadcastRequestData;
    JSONArray orderList;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        completeRefresh();
        if ((request instanceof BroadcastRequest) && (data instanceof JSONArray)) {
            BroadcastRequestData = (JSONArray) data;
            banner.setImages(MCString.listByArray((JSONArray) data, "pictureUrl")).setImageLoader(new
                    GlideImageLoader()).setOnBannerListener(this).start();
            banner.updateBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        }

        if (request instanceof HomeActivityRequest) {
            initHomeActivity(data);
            //fillToView(R.id.actionPanel, data);
        }

        if (request instanceof HomeItemRequest) {
            fillToView(R.id.homeItemList, data, true);
        }

        if (request instanceof HomePageOrderList) {
            mView.findViewById(R.id.listPaddingView).setVisibility((data instanceof String) ? View.GONE : View.VISIBLE);
            if (data instanceof JSONArray) {
                OrderAdapter.orderConvert((JSONArray) data);
                homeOrderList.setVisibility(View.VISIBLE);

                orderList = (JSONArray) data;
                if (orderList.length() < 1) {
                    homeOrderList.setVisibility(View.GONE);
                } else {
                    JSONArray jsonArray = new JSONArray();
                    if (orderList.length() > 2) {
                        if (!isMore) {
                            try {
                                jsonArray.put(orderList.getJSONObject(0));
                                jsonArray.put(orderList.getJSONObject(1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fillToView(R.id.homeOrderList, jsonArray, true);
                            txt_more.setText("查看更多");
                        } else {
                            fillToView(R.id.homeOrderList, data, true);
                            txt_more.setText("收起更多");
                        }
                        txt_more.setVisibility(View.VISIBLE);
                    } else {
                        fillToView(R.id.homeOrderList, data, true);
                        txt_more.setVisibility(View.GONE);
                    }
                }
            } else if (data instanceof String && TextUtils.isEmpty((String) data)) {
                isMore = false;
                orderList = null;
                homeOrderList.setVisibility(View.GONE);
                txt_more.setVisibility(View.GONE);
            }
        }
        if (request instanceof OpenedCityRequest && data instanceof JSONArray) {
            allCitys = (JSONArray) data;
        }
    }

    @Override
    public void onItemClick(View listView, JSONObject itemData, int tag) {
        if (listView == homeItemList) {
            if (tag == 0) {
                return;
            }
            if (tag == 5) {
                String firstItemId = itemData.optString("firstItemId");
                ClassifyFragment.setSelectItem(firstItemId);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setTabHostFragment(1);
                ClassifyFragment.setSelectItem(firstItemId);
                return;
            }
            String itemIdFormat = String.format("secondItemInfos.[%d].secondItemId", tag - 1);
            String itemNameFormat = String.format("secondItemInfos.[%d].secondItemName", tag - 1);
            String secondItemHTMLUrl = String.format("secondItemInfos.[%d].secondItemHTMLUrl", tag - 1);
            JSONExtractor extractor = new JSONExtractor(itemData);

            Intent intent = new Intent(this.getContext(), ItemContentActivity.class);
            intent.putExtra("secondItemId", extractor.extract(itemIdFormat).toString());
            intent.putExtra("secondItemName", extractor.extract(itemNameFormat).toString());
            intent.putExtra("secondItemHTMLUrl", extractor.extract(secondItemHTMLUrl).toString());
            startActivity(intent);
        }

        if (listView == homeOrderList) {

            if (tag == 0) {
                String orderType = itemData.optString("orderType");
                String orderStatus = itemData.optString("orderStatus");
                if (orderType.equals("1")) {
                    String salesAfterId = itemData.optString("salesAfterId");
                    String salesAfterStatus = itemData.optString("salesAfterStatus");
                    if (salesAfterStatus.equals("3") || salesAfterStatus.equals("2")) {
                        Intent intent = new Intent(getContext(), WaitServiceActivity.class);
                        intent.putExtra("salesAfterId", itemData.optString("salesAfterId"));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                        intent.putExtra("salesAfterId", itemData.optString("salesAfterId"));
                        intent.putExtra("orderStatus", orderStatus);
                        startActivity(intent);
                    }
                    return;
                }

                if (orderStatus.equals("3") || orderStatus.equals("2") || orderStatus.equals("1")) {
                    Intent intent = new Intent(getContext(), WaitServiceActivity.class);
                    intent.putExtra("orderId", itemData.optString("orderId"));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra("orderId", itemData.optString("orderId"));
                    intent.putExtra("orderStatus", orderStatus);
                    startActivity(intent);
                }
            }
        }
    }


    // **************************************************************************新人红包
    private GridView grid_view;
    private HomeActivityAdapter adapter;
    private ArrayList<HomeActivityBean> arrayList;

    private void initHomeActivity(Object data) {
        if (adapter == null) {
            arrayList = new ArrayList<>();
            adapter = new HomeActivityAdapter(getActivity(), arrayList);
            grid_view = mView.findViewById(R.id.grid_view);
            grid_view.setAdapter(adapter);
        }
        arrayList.clear();

        try {
            arrayList.addAll((ArrayList<HomeActivityBean>) BaseBean.toModelList(data.toString(), HomeActivityBean.class));
            // arrayList.addAll((ArrayList<HomeActivityBean>) BaseBean.toModelList(data.toString(), HomeActivityBean.class));
            //            arrayList.remove(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int width = ScreenUtil.getScreenWidthPx();
        int itemWidth = (int) (ScreenUtil.getScreenWidthPx() / 4f);
        if (arrayList.size() > 4) {
            width = (int) (ScreenUtil.getScreenWidthPx() / 4f * arrayList.size());
        } else {
            itemWidth = (ScreenUtil.getScreenWidthPx() / arrayList.size());
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        grid_view.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        grid_view.setNumColumns(arrayList.size());
        grid_view.setColumnWidth(itemWidth); // 设置列表项宽
        grid_view.setStretchMode(GridView.NO_STRETCH);
        adapter.notifyDataSetChanged();
    }

    /** 广播过滤器 */
    protected IntentFilter filter;
    /** 广播接收器 */
    protected BroadcastReceiver receiver;

    public void initIntentFilter() {
        //   添加广播过滤器，在此添加广播过滤器之后，所有的子类都将收到该广播
        filter = new IntentFilter();
        filter.addAction("refresh_home_fragment_list");
        filter.addAction("refresh_home_red");
        filter.addAction("refresh_city");
        filter.addAction("refresh_home_order_list");
    }

    public void registerReceiver() {
        initIntentFilter();
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                    HomeFragment.this.onReceive(intent);
                }
            }
        };
        getActivity().registerReceiver(receiver, filter);
    }

    public void onReceive(Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            //   父类集中处理共同的请求
            if (intent.getAction().equals("refresh_home_fragment_list")) {//
                onRefresh(false);
            } else if (intent.getAction().equals("refresh_home_order_list")) {//
                String orderId = intent.getStringExtra("orderId");
                String salesAfterId = intent.getStringExtra("salesAfterId");
                if (!TextUtils.isEmpty(orderId)) {
                    intent.putExtra("orderId", orderId);
                } else if (!TextUtils.isEmpty(salesAfterId)) {
                    intent.putExtra("salesAfterId", salesAfterId);
                }
                if (orderList == null) {
                    return;
                }

                try {
                    for (int i = 0; i < orderList.length(); i++) {
                        JSONObject jsonObject = orderList.optJSONObject(i);
                        String orderId1 = jsonObject.optString("orderId", "");
                        String salesAfterId1 = jsonObject.optString("salesAfterId", "");
                        if (!TextUtils.isEmpty(orderId)) {
                            if (orderId1.equals(orderId)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    orderList.remove(i);
                                }
                            }
                        } else if (!TextUtils.isEmpty(salesAfterId)) {
                            if (salesAfterId1.equals(orderId)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    orderList.remove(i);
                                }
                            }
                        }
                    }

                    if (orderList.length() > 2) {
                        txt_more.setVisibility(View.VISIBLE);
                        if (isMore) {
                            fillToView(R.id.homeOrderList, orderList, true);
                            txt_more.setText("收起更多");
                        } else {
                            JSONArray jsonArray = new JSONArray();
                            try {
                                jsonArray.put(orderList.getJSONObject(0));
                                jsonArray.put(orderList.getJSONObject(1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            fillToView(R.id.homeOrderList, jsonArray, true);
                            txt_more.setText("查看更多");
                        }
                    } else {
                        txt_more.setVisibility(View.GONE);
                        fillToView(R.id.homeOrderList, orderList, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (intent.getAction().equals("refresh_city")) {//
                onLocationChanged(intent);
            } else if (intent.getAction().equals("refresh_home_red")) {//
                if (MainApplication.getInstance().getUserInfo(false) == null) {
                    navigatorBar.setUnRead("");
                } else {
                    // 相当于Fragment的onResume
                    unreadShow();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void onLocationChanged(Intent intent) {
        //        String cityName, String cityCode, double latitude, double longitude

        if (!TextUtils.isEmpty(selectCity)) {
            return;
        }
        String cityName = intent.getStringExtra("cityName");
        String cityCode = intent.getStringExtra("cityCode");
        if (cityCode.isEmpty()) {
            navigatorBar.setLeftButtonText("定位失败");
            return;
        }

        for (int i = 0; allCitys != null && (i < allCitys.length()); i++) {
            if (allCitys.optJSONObject(i).optString("code").equals(cityCode)) {
                navigatorBar.setLeftButtonText(allCitys.optJSONObject(i).optString("name").replace("市",""));
                //                navigatorBar.getLeft().setTag(cityCode);
                return;
            }
        }
        navigatorBar.setLeftButtonText(cityName + "(未开通)");
    }



}

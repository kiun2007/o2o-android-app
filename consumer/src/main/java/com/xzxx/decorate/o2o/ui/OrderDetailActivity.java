package com.xzxx.decorate.o2o.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.controllers.Refresher;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.fragment.FragmentFactory;
import com.xzxx.decorate.o2o.utils.CustomPop;

import java.lang.reflect.Field;

/**
 * Created by zf on 2018/7/3.
 * 订单详情页面
 */
public class OrderDetailActivity extends BaseActivity implements Refresher {

    private TabLayout mTabLayout = null;
    public ViewPager mViewPager;
    private OrderDetailActivity.PageAdatper pageAdatper;
    private int[] mTabTitles = new int[]{R.string.order_content, R.string.order_progress};

    int curItem = 0;

    @Override
    public int getLayoutId() {
        return R.layout.layout_order_detail;
    }

    @Override
    public void initView() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            curItem = bundle.getInt("curItem", 0);
            String salesAfterId = bundle.getString("salesAfterId", "");
            if (!TextUtils.isEmpty(salesAfterId)) {
                mTabTitles = new int[]{R.string.order_content, R.string.order_progress1};
            }
        }
        mTabLayout = findViewById(R.id.order_detail_tab_layout);
        mViewPager = findViewById(R.id.order_detail_tab_viewpager);
        reflex(mTabLayout);
        pageAdatper = new OrderDetailActivity.PageAdatper(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pageAdatper);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(curItem);

        if (isHideMenu()) {
            getNavigatorBar().getRightButton().setVisibility(View.GONE);
        }
    }

    public void reflex(final TabLayout tabLayout) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    int dp = BasicUtils.dip2px(tabLayout.getContext(), 50);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(0, 0, 0, 0);
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dp;
                        params.rightMargin = dp;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRightClick() {

        //        PopupMenu popup = new PopupMenu(this, getNavigatorBar().getRightButton());
        //        MenuInflater inflater = popup.getMenuInflater();
        //
        //        setIconEnable(popup.getMenu(), true);
        //        inflater.inflate(R.menu.order_menu, popup.getMenu());
        //
        //        if(isHideItem()){
        //            popup.getMenu().findItem(R.id.action_material).setVisible(false);
        //            popup.getMenu().findItem(R.id.action_water_drop).setVisible(false);
        //        }
        //        popup.setOnMenuItemClickListener(this);
        //        popup.show();

        showPop(getNavigatorBar().getRightButton());
    }

    private int statusValue() {
        String orderStatus = getIntent().getStringExtra("orderStatus");
        if (orderStatus.isEmpty()) {
            return -1;
        }
        return Integer.parseInt(orderStatus);
    }

    private boolean isHideItem() {
        return statusValue() < 6;
    }

    private boolean isHideMenu() {
        return statusValue() < 1;
    }

    //    private void setIconEnable(Menu menu, boolean enable) {
    //        try {
    //            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
    //            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
    //            m.setAccessible(true);
    //
    //            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
    //            m.invoke(menu, enable);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    @Override
    //    protected void onDestroy() {
    //        super.onDestroy();
    //    }
    //
    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.order_menu, menu);
    //        return super.onCreateOptionsMenu(menu);
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        return super.onOptionsItemSelected(item);
    //    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }
    //
    //    @Override
    //    public boolean onMenuItemClick(MenuItem menuItem) {
    //        String orderId = getIntent().getStringExtra(OrderInfoRequest.ORDER_ID);
    //        if (menuItem.getItemId() == R.id.action_circles){
    //            Intent complainIntent = new Intent(OrderDetailActivity.this, ComplaintActivity.class);
    //            complainIntent.putExtra(OrderInfoRequest.ORDER_ID, orderId);
    //            startActivity(complainIntent);
    //        }else if (menuItem.getItemId() == R.id.action_material){
    //            Intent applyIntent = new Intent(OrderDetailActivity.this, ApplyAfterServiceActivity.class);
    //            applyIntent.putExtra(OrderInfoRequest.ORDER_ID, orderId);
    //            startActivity(applyIntent);
    //        }else if (menuItem.getItemId() == R.id.action_water_drop){
    //            JSONObject object = new JSONObject();
    //            try {
    //                object.put("orderId", orderId);
    //            } catch (JSONException e) {
    //                e.printStackTrace();
    //            }
    //            getButtonController().actionTag(this, OrderButtonController.TAG_DEL, object);
    //        }
    //        return true;
    //    }

    @Override
    public void onRefresh(int tag) {
        finish();
    }

    private class PageAdatper extends FragmentPagerAdapter {

        public PageAdatper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = FragmentFactory.createOrderFragment(position);
            Bundle bundle = new Bundle();
            bundle.putString("orderId", getIntent().getStringExtra("orderId"));
            bundle.putString("orderStatus", getIntent().getStringExtra("orderStatus"));
            if (getIntent().getStringExtra("salesAfterId") != null) {
                bundle.putString("salesAfterId", getIntent().getStringExtra("salesAfterId"));
            }
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return getString(mTabTitles[position]);
        }
    }

    private void showPop(View v) {
        CustomPop customPop = new CustomPop(this, isHideItem());
        customPop.showAsDropDown(v);
    }
}
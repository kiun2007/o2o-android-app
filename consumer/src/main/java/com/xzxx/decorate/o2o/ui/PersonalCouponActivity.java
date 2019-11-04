package com.xzxx.decorate.o2o.ui;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.fragment.AvaliableCouponFragment;

import java.lang.reflect.Field;

/**
 * Created by zf on 2018/7/12.
 * 我的优惠券页面
 */
public class PersonalCouponActivity extends BaseActivity {

    private TabLayout mTabLayout = null;
    private ViewPager mViewPager;
    private PersonalCouponActivity.PageAdatper pageAdatper;
    private int[] mTabTitles = new int[]{R.string.avliable_coupon, R.string.unavliable_coupon, R.string.invalie_coupon};

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_coupon;
    }

    @Override
    public void initView() {
        mTabLayout = findViewById(R.id.tab_layout_coupon);
        mViewPager = findViewById(R.id.tab_viewpager_coupon);
        reflex(mTabLayout);
        pageAdatper = new PersonalCouponActivity.PageAdatper(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pageAdatper);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void reflex(final TabLayout tabLayout) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    int dp = BasicUtils.dip2px(tabLayout.getContext(), 25);
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
        Intent intent = new Intent(this, InfomationActivity.class);
        intent.putExtra("title", "优惠券说明");
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

    }

    private class PageAdatper extends FragmentPagerAdapter {

        public PageAdatper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AvaliableCouponFragment avaliableCouponFragment = new AvaliableCouponFragment();
            //不可用代表的是用了的，已失效代表的是过期的
            //优惠券类型 0可用 1过期 2已使用 3维修费支付时可用券
            if (position == 0) {
                avaliableCouponFragment.type = "0";
            } else if (position == 1) {
                avaliableCouponFragment.type = "2";
            } else if (position == 2) {
                avaliableCouponFragment.type = "1";
            }
            return avaliableCouponFragment;
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
}

package com.xzxx.decorate.o2o.consumer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.fragment.FragmentFactory;

import java.lang.reflect.Field;

/**
 * 我的订单页面
 */
public class OrderFragment extends Fragment {

    private TabLayout mTabLayout = null;
    private ViewPager mViewPager;
    private PageAdatper pageAdatper;
    private int[] mTabTitles = new int[]{R.string.all_order, R.string.order_to_be_pay, R.string.to_be_evaluate, R.string.after_sale_service};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.tab_viewpager);
        reflex(mTabLayout);
        pageAdatper = new PageAdatper(getChildFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pageAdatper);
        mTabLayout.setupWithViewPager(mViewPager);
        return view;
    }

    public void reflex(final TabLayout tabLayout) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
//                    tabLayout.getWidth()
                    int dp = BasicUtils.dip2px(tabLayout.getContext(), 20);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        tabView.setPadding(dp, 0, dp, 0);
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = -1;
                        params.weight = 1;
//                        params.leftMargin = dp;
//                        params.rightMargin = dp;
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

    private class PageAdatper extends FragmentPagerAdapter {

        public PageAdatper(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.createFragment(position);
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

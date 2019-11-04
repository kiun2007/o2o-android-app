package com.xzxx.decorate.o2o.fragment;

import android.content.Context;

import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;

import java.util.HashMap;

public class FragmentFactory {

    private static HashMap<Integer, BaseRequestFragment> mBaseFragments = new HashMap<Integer, BaseRequestFragment>();
    private static HashMap<Integer, BaseRequestFragment> mBaseCouponFragments = new HashMap<Integer, BaseRequestFragment>();
    private static HashMap<Integer, BaseRequestFragment> mBaseMasterFragments = new HashMap<Integer, BaseRequestFragment>();

    public static BaseRequestFragment createFragment(int pos) {
        BaseRequestFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            baseFragment = new AllOrderFragment();
            ((AllOrderFragment)baseFragment).setPageIndex(pos);
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }

    public static BaseRequestFragment createOrderFragment(int pos) {

        switch (pos) {
            case 0:
                return new OrderContentFragment();
            case 1:
                return new OrderProgressFragment();
        }
        return null;
    }

    public static BaseRequestFragment createCouponFragment(int pos) {
        BaseRequestFragment baseFragment = mBaseCouponFragments.get(pos);
        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new AvaliableCouponFragment();
                    break;
            }
            mBaseCouponFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }

    public static BaseRequestFragment createMasterInfoFragment(int pos) {
        BaseRequestFragment baseFragment = mBaseMasterFragments.get(pos);
        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new WorkingInfoFragment();
                    break;
                case 1:
                    baseFragment = new CustomerEvaluationFragment();
                    break;
            }
            mBaseMasterFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}

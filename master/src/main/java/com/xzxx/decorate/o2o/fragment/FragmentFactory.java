package com.xzxx.decorate.o2o.fragment;

import java.util.HashMap;

public class FragmentFactory {

    private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int pos) {
        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            if (baseFragment == null) {
                baseFragment = new AllOrderFragment();
                ((AllOrderFragment)baseFragment).setPageIndex(pos);
                mBaseFragments.put(pos, baseFragment);
            }
            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
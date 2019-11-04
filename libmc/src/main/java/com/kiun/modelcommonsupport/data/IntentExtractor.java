package com.kiun.modelcommonsupport.data;

import android.content.Intent;

/**
 * Created by kiun_2007 on 2018/8/13.
 */

public class IntentExtractor extends ExtractorBase<Intent> {

    public IntentExtractor(Intent dataSource) {
        super(dataSource);
    }

    @Override
    protected Object extractNoExpression(String path) {
        return mData.getStringExtra(path);
    }
}

package com.kiun.modelcommonsupport.data;

import com.kiun.modelcommonsupport.network.MCBaseRequest;

import java.lang.reflect.Field;

/**
 * Created by kiun_2007 on 2018/8/13.
 */

public class RequestExtractor extends ExtractorBase<MCBaseRequest> {

    public RequestExtractor(MCBaseRequest dataSource) {
        super(dataSource);
    }

    @Override
    protected Object extractNoExpression(String path) {
        try {
            Field field = mData.getClass().getField(path);
            if(field != null){
                return field.get(mData);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

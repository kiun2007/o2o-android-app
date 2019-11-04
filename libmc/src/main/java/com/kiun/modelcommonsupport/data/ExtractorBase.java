package com.kiun.modelcommonsupport.data;

import com.kiun.modelcommonsupport.utils.JexlUtil;
import com.kiun.modelcommonsupport.utils.MCString;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/4/14.
 * 数据提取器
 */
public abstract class ExtractorBase<T> {

    protected T mData = null;
    public ExtractorBase(T dataSource) {
        mData = dataSource;
    }

    /**
     * 根据路径提取数据.
     * @param path 数据类型.
     * @return 数据.
     */
    public final Object extract(String path){

        if (path.equals(".")){
            return mData;
        }
        String[] expString = path.split("[+,\\-,*,/,\\(,\\)]");
        if(expString.length == 1){
            return extractNoExpression(path);
        }else{
            expString = MCString.stringsSort(expString);
            Map<String,Object> map = new HashMap<String,Object>();
            for (String expPath : expString){
                map.put(expPath, extractNoExpression(expPath));
            }
            return JexlUtil.convertToCode(path, map);
        }
    }

    protected abstract Object extractNoExpression(String path);
}
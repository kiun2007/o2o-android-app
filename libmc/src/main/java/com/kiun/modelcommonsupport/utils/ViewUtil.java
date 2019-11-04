package com.kiun.modelcommonsupport.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

public class ViewUtil {

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;

    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getVisibleByFormula(Object data, String goneFormula){

        if(goneFormula.equals("!null")){
            if(data == null){
                return View.VISIBLE;
            }
            return data.toString().isEmpty() ? View.VISIBLE: View.GONE;
        }

        if(goneFormula.equals("=null")){
            if(data == null){
                return View.GONE;
            }
            return data.toString().isEmpty() ? View.GONE : View.VISIBLE;
        }

        if(data instanceof String){
            data = MCString.toNumber(data.toString());
        }

        if (data instanceof Number){

            String[] goneFormulas = goneFormula.split("A|O");
            boolean isAnd = goneFormula.indexOf("A") > -1;
            boolean[] rights = new boolean[goneFormulas.length];

            for (int i = 0; i < goneFormulas.length; i ++){
                String goneForm = goneFormulas[i];
                Comparable value = (Comparable) data;
                Comparable desValue = null;
                if(data instanceof Float){
                    desValue = MCString.toNumber(goneForm.substring(1)).floatValue();
                }else if (data instanceof Integer){
                    desValue = MCString.toNumber(goneForm.substring(1)).intValue();
                }else if (data instanceof Double){
                    desValue = MCString.toNumber(goneForm.substring(1)).doubleValue();
                }else if (data instanceof Long){
                    desValue = MCString.toNumber(goneForm.substring(1)).longValue();
                }

                String[] expStrs = new String[]{"L", "=", "G"};
                String exp = expStrs[value.compareTo(desValue) + 1];

                if(goneForm.startsWith("!")){
                    rights[i] = value.compareTo(desValue) != 0;
                    continue;
                }
                rights[i] = goneForm.startsWith(exp);
            }

            boolean isGone = isAnd ? true : false;
            for (int i = 0; i < rights.length; i ++) {
                if (rights[i]) {
                    if (!isAnd) {
                        isGone = true;
                        break;
                    }
                } else {
                    if (isAnd) {
                        isGone = false;
                        break;
                    }
                }
            }
            return isGone ? View.GONE : View.VISIBLE;
        }
        return View.VISIBLE;
    }
}

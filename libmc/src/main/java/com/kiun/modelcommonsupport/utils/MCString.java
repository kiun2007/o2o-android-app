package com.kiun.modelcommonsupport.utils;

import android.text.TextUtils;

import com.qiniu.android.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kiun_2007 on 2018/8/9.
 */

public class MCString {

    public static List listByArray(JSONArray array, String fieldName) {

        List newList = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            try {
                newList.add(array.getJSONObject(i).opt(fieldName));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newList;
    }

    public static Date dateByFormat(String value, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] patternValues(String[] patternStrs, String rootStr) {

        String[] values = new String[patternStrs.length];
        for (int i = 0; i < patternStrs.length; i++) {
            Matcher matcher = Pattern.compile(patternStrs[i]).matcher(rootStr);
            values[i] = matcher.find() ? matcher.group(1) : null;
        }
        return values;
    }

    public static int asInt(String str) {
        if (str.isEmpty()) {
            return 0;
        }
        int hex = str.startsWith("0x") ? 16 : 10;
        return Integer.parseInt(str.replace("0x", ""), hex);
    }

    public static String[] stringsSort(String[] inStrings) {

        ArrayList<String> newStrings = new ArrayList<String>();
        for (String itemStr : inStrings) {
            if (itemStr.equals("")) {
                continue;
            }
            boolean isWith = false;
            for (int i = 0; i < newStrings.size(); i++) {
                if (itemStr.equals(newStrings.get(i))) {
                    isWith = true;
                    break;
                }
            }
            if (!isWith) {
                newStrings.add(itemStr);
            }
        }
        String[] outString = new String[newStrings.size()];
        for (int i = 0; i < newStrings.size(); i++) {
            outString[i] = newStrings.get(i);
        }
        return outString;
    }

    public static String formatDate(String format, Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        return formatter.format(date);
    }

    public static String bankCardTail(String cardNo) {
        int length = cardNo.length();
        if (length < 16) {
            return null;
        }
        //        int afterLength = (length == 19 ? 3 : 4);
        //        return cardNo.substring(length - afterLength, length);
        int afterLength = 4;
        return "("+cardNo.substring(length - afterLength, length)+")";
    }

    public static String hideCardNo(String cardNo) {
        if (StringUtils.isBlank(cardNo)) {
            return cardNo;
        }

        int length = cardNo.length();
        int beforeLength = 0;
        int afterLength = (length == 19 ? 3 : 4);
        //替换字符串，当前使用“*”
        String replaceSymbol = "*";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            if (i < beforeLength || i >= (length - afterLength)) {
                sb.append(cardNo.charAt(i));
            } else {
                sb.append(replaceSymbol);
            }

            if (i != 0 && (i + 1) % 4 == 0) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public static Number toNumber(String str) {

        if (str.isEmpty()) {
            return 0;
        }

        if (str.indexOf(".") > -1) {
            return Float.parseFloat(str);
        } else {
            return Integer.parseInt(str);
        }
    }

    public static String arrayToString(List<String> array, String separate) {

        StringBuilder stringBuilder = new StringBuilder(2000);
        for (int i = 0; i < array.size(); i++) {
            stringBuilder.append(array.get(i));
            if (i < array.size() - 1) {
                stringBuilder.append(separate);
            }
        }
        return stringBuilder.toString();
    }

    public static String trim(String source, char trimValue) {
        int len = source.length();
        int st = 0;
        while ((st < len) && (source.charAt(len - 1) <= trimValue)) {
            len--;
        }
        return ((st > 0) || (len < source.length())) ? source.substring(st, len) : source;
    }

    public static String dateConvert(String value, String inFormat, String outFormat){
        SimpleDateFormat inDateFormat = new SimpleDateFormat(inFormat);
        SimpleDateFormat outDateFormat = new SimpleDateFormat(outFormat, Locale.CHINA);
        try {
            Date date = inDateFormat.parse(value);
            return outDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decimalFormat(float value, int num, String uint){

        String formatValue = String.format("%%.%df", num);
        String decimalStr = String.format(formatValue, value);
        String[] numbers = decimalStr.split("\\.");
        String intValue = numbers[0];
        String decimalValue = numbers[numbers.length - 1];
        decimalValue = trim(decimalValue, '0');

        if (TextUtils.isEmpty(decimalValue)){//
            return String.format("<font><big>%s</big></font>", intValue);
        }else{
            return String.format("<font><big>%s</big></font><font><small>.%s</small></font>", intValue, decimalValue);
        }
    }
}

package com.kiun.modelcommonsupport.data.drive;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.kiun.modelcommonsupport.data.BeanBase;
import com.kiun.modelcommonsupport.data.BeanExtractor;
import com.kiun.modelcommonsupport.data.ExtractorBase;
import com.kiun.modelcommonsupport.data.IntentExtractor;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.data.RequestExtractor;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AImageView;
import com.kiun.modelcommonsupport.ui.views.ActionView;
import com.kiun.modelcommonsupport.ui.views.EventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/4/14.
 * 显示驱动基础类.
 */

public abstract class DriveBase {

    protected ViewGroup mViewGroup = null;
    protected List<DriveListener> allViews = new LinkedList<>();
    protected List<ActionView> actionViews = new ArrayList<>();

    protected DriveBase(ViewGroup viewGroup){
        mViewGroup = viewGroup;
        findLoop(mViewGroup, allViews, actionViews);
    }

    protected void setActionListener(EventListener listener){
        for (ActionView aView : actionViews) {
            aView.setEventListener(listener);
        }
    }

    /**
     * 从路径中读取数据.
     * @param path
     * @return
     */
    protected abstract Object dataOfPath(String path);

    /**
     * 数据驱动器加载数据.
     */
    public abstract void reload();

    /**
     * 数据驱动器提交数据.
     */
    public abstract void submit();

    protected void findAndFill(){
        for (DriveListener driveListener : allViews){
            String path = driveListener.getPath();

            if(path != null){
                Object data = dataOfPath(path);
                driveListener.fill(data);
            }
        }
    }

    public static List<DriveListener> getDriveListener(View v){

        ViewGroup viewGroup = v instanceof ViewGroup ? (ViewGroup)v : null;

        if(viewGroup == null){
            return null;
        }

        List<DriveListener> driveListeners = new ArrayList<>();
        DriveBase.findLoop(viewGroup, driveListeners, null);
        return driveListeners;
    }

    public static void findLoop(ViewGroup parent, List<DriveListener> driveList, List<ActionView> actionViewList){

        for(int i = 0; i < parent.getChildCount(); i ++){
            View child = parent.getChildAt(i);
            if (child instanceof DriveListener){
                if(driveList != null){
                    driveList.add((DriveListener) child);
                }
                if(child instanceof ActionView && actionViewList != null){
                    actionViewList.add((ActionView) child);
                }
            }
            if(child instanceof ViewGroup){
                findLoop((ViewGroup) child, driveList, actionViewList);
            }
        }
    }

    public static <T extends View> void findLoopClass(ViewGroup parent, List<T> viewList, Class clz){

        for(int i = 0; i < parent.getChildCount(); i ++){
            View child = parent.getChildAt(i);
            if (child.getClass() == clz){
                if(viewList != null){
                    viewList.add((T) child);
                }
            }
            if(child instanceof ViewGroup){
                findLoopClass((ViewGroup) child, viewList, clz);
            }
        }
    }

    public static void fillViewData(View view, Object data, boolean isSelf){
        try {
            ViewGroup viewGroup = view instanceof ViewGroup ? (ViewGroup)view : null;
            ExtractorBase extractor = null;
            if(data instanceof JSONObject || data instanceof JSONArray){
                extractor = new JSONExtractor(data);
            }
            if(data instanceof Intent){
                extractor = new IntentExtractor((Intent) data);
            }
            if(data instanceof MCBaseRequest){
                extractor = new RequestExtractor((MCBaseRequest) data);
            }
            if(data instanceof BeanBase){
                extractor = new BeanExtractor((BeanBase) data);
            }

            if (data instanceof String){
                JSONObject jsonObject = new JSONObject(data.toString());
                extractor = new JSONExtractor(jsonObject);
            }

            if(isSelf){
                if (view instanceof DriveListener){
                    String path = ((DriveListener)view).getPath();
                    if (path == null || path.equals(".")){
                        ((DriveListener)view).fill(data);
                    }else{
                        ((DriveListener)view).fill(extractor.extract(path));
                    }
                }
                return;
            }

            if(viewGroup == null){
                return;
            }

            List<DriveListener> driveListeners = new ArrayList<>();
            DriveBase.findLoop(viewGroup, driveListeners, null);
            for (DriveListener driveListener : driveListeners){
                String path = driveListener.getPath();
                if(path != null){
                    if(extractor != null) {
                        Object value = extractor.extract(path);
                        driveListener.fill(value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历布局，并禁用所有子控件
     * @param viewGroup
     *            布局对象
     */
    public static void disableSubControls(ViewGroup viewGroup, boolean isEnabled) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View v = viewGroup.getChildAt(i);
            if (v instanceof ViewGroup) {
                if (v instanceof Spinner) {
                    Spinner spinner = (Spinner) v;
                    spinner.setClickable(isEnabled);
                    spinner.setEnabled(isEnabled);
                } else if (v instanceof ListView) {
                    ((ListView) v).setClickable(isEnabled);
                    ((ListView) v).setEnabled(isEnabled);
                } else {
                    disableSubControls((ViewGroup) v, isEnabled);
                }
            } else if (v instanceof EditText) {
                ((EditText) v).setEnabled(isEnabled);
                ((EditText) v).setClickable(isEnabled);
                ((EditText) v).setFocusable(isEnabled);
                ((EditText) v).setFocusableInTouchMode(isEnabled);
                if(isEnabled){
                    ((EditText) v).requestFocus();
                }
            } else if (v instanceof Button) {
                ((Button) v).setEnabled(isEnabled);
            } else if (v instanceof AImageView){
                ((AImageView) v).setEditModel(isEnabled);
            }
        }
    }
}

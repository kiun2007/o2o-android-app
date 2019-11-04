package com.kiun.modelcommonsupport.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCHttpCGI;
import com.kiun.modelcommonsupport.network.MCResponse;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;
import com.kiun.modelcommonsupport.ui.views.ACellView;
import com.kiun.modelcommonsupport.ui.views.ACellViewEventer;
import com.kiun.modelcommonsupport.ui.views.NavigatorBar;
import com.kiun.modelcommonsupport.ui.views.NavigatorListener;
import com.phillipcalvin.iconbutton.IconButton;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/8.
 */

public abstract class BaseRequestFragment extends Fragment implements MCUIResponse,FillViewer, NavigatorListener, ACellViewEventer, XRefreshView.XRefreshViewListener {

    protected NavigatorBar navigatorBar;
    protected View mView;

    protected abstract void initView(View view);
    protected abstract int getLayoutId();

    protected XRefreshView getRefreshView(){
        return mView.findViewWithTag("XRefreshView");
    }

    public void completeRefresh(){
        if (getRefreshView() != null){
            getRefreshView().stopRefresh();
        }
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mView == null) {
            mView = inflater.inflate(getLayoutId(), container, false);
            initView(mView);
        }
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }

        navigatorBar = mView.findViewWithTag(NavigatorBar.TAG);
        if(navigatorBar != null){
            navigatorBar.setListener(this);
        }

        List<IconButton> buttons = new ArrayList<>();
        DriveBase.findLoopClass((ViewGroup)mView, buttons, IconButton.class);
        for (IconButton button : buttons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSubmitClick((Button) view);
                }
            });
        }

        List<ACellView> cells = new ArrayList<>();
        DriveBase.findLoopClass((ViewGroup)mView, cells, ACellView.class);
        for (ACellView cellView : cells){
            cellView.setEventer(this);
        }
        if (getRefreshView() != null){
            getRefreshView().setXRefreshViewListener(this);
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onLeftClick() {
        getActivity().finish();
    }

    @Override
    public void onRightClick() {
    }

    public BaseRequestAcitity getBaseActivity(){
        if(!(getActivity() instanceof BaseRequestAcitity)){
            return null;
        }
        return (BaseRequestAcitity) getActivity();
    }

    /**
     根据一个请求实例请求网络端, 如果未设置界面响应者将默认为本控制器响应.
     @param request 请求实例.
     */
    public void commitRequest(final MCBaseRequest request){
        request.setResponse(new MCResponse(this){{setRequest(request);}});
        MCHttpCGI.defaultCenter().requestCGI(request);
    }

    /**
     填充界面到对象.
     @param request 请求对象.
     @param noKeys 不需要填充的对象名称集合.
     @return 是否顺利完成.
     */
    public boolean fillRequest(MCBaseRequest request, String[] noKeys){

        List<DriveListener> driveListeners = DriveBase.getDriveListener(getView());
        if(driveListeners == null){
            return true;
        }

        for (DriveListener driveListener : driveListeners){
            KeyValue keyValue = driveListener.getParam();
            if(keyValue.getError() != null){
                this.onMatchError(keyValue.getError());
                return false;
            }
            try {
                Field field = request.getClass().getField(keyValue.getKey());
                if(field == null){
                    field.set(request, keyValue.getValue());
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void onMatchError(String error){
        Toast.makeText(this.getActivity(), error, Toast.LENGTH_LONG).show();
    }

    public void onBeginRequest(){
    }

    public void onError(Error error){
        completeRefresh();
    }

    public void fillToView(int resId, Object data, boolean isSelf){

        View view = null;
        if(resId == -1){
            view = mView;
        }else{
            view = mView.findViewById(resId);
        }
        DriveBase.fillViewData(view, data, isSelf);
    }

    public void fillToView(int resId, Object data){
        fillToView(resId, data, false);
    }

    public void onSubmitClick(Button button){
    }

    @Override
    public int fillParams(Intent intent, int tag) {
        return ACellViewEventer.NOMAL_Activity;
    }

    @Override
    public void onCellClick(View targetView, int tag) {
    }

    public void onRefresh(){}
    public void onRefresh(boolean isPullDown){}
    public void onLoadMore(boolean isSilence){}
    public void onRelease(float direction){}
    public void onHeaderMove(double headerMovePercent, int offsetY){}
}

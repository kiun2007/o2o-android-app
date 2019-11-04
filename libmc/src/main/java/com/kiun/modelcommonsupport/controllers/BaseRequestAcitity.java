package com.kiun.modelcommonsupport.controllers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.amos.modulebase.executor.LoadingDialogUtil;
import com.andview.refreshview.XRefreshView;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.ServiceError;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCHttpCGI;
import com.kiun.modelcommonsupport.network.MCResponse;
import com.kiun.modelcommonsupport.network.ParamValue;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;
import com.kiun.modelcommonsupport.ui.views.ACellViewEventer;
import com.kiun.modelcommonsupport.ui.views.MediaChanger;
import com.kiun.modelcommonsupport.ui.views.NavigatorBar;
import com.kiun.modelcommonsupport.ui.views.NavigatorListener;
import com.kiun.modelcommonsupport.utils.AppUtil;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.StatusBarUtils;
import com.phillipcalvin.iconbutton.IconButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiun_2007 on 2018/8/8.
 */

public abstract class BaseRequestAcitity extends AppCompatActivity implements MCUIResponse, FillViewer, NavigatorListener, ACellViewEventer, XRefreshView.XRefreshViewListener {

    private NavigatorBar navigatorBar;
    private OrderButtonController buttonController = null;
    public MediaChanger lastMediaChanger;
    private boolean isLogout = false;

    public void completeRefresh() {
        if (getRefreshView() != null) {
            getRefreshView().stopRefresh();
            getRefreshView().stopLoadMore();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        StatusBarUtils.with(this)
                .setColor(-1)
                .init();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getPersimmions();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        addSubmitEvent();

        if (getRefreshView() != null) {
            getRefreshView().setXRefreshViewListener(this);
        }
        initView();
    }

    @Override
    public boolean isDead() {
        return this.isFinishing();
    }

    @Override
    public void finish() {
        super.finish();
    }

    protected void addSubmitEvent() {

        navigatorBar = getWindow().getDecorView().findViewWithTag(NavigatorBar.TAG);
        if (navigatorBar != null) {
            navigatorBar.setListener(this);
        }

        List<IconButton> buttons = new ArrayList<>();
        DriveBase.findLoopClass((ViewGroup) getWindow().getDecorView(), buttons, IconButton.class);

        for (IconButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSubmitClick((Button) view);
                }
            });
        }
    }

    public NavigatorBar getNavigatorBar() {
        return navigatorBar;
    }

    public void setTitle(String title) {
        if (navigatorBar != null) {
            navigatorBar.setTitle(title);
        }
    }

    public void addEvents(int layoutId, ItemListener itemListener) {

        ViewGroup viewGroup = findViewById(layoutId);
        List<DriveListener> driveListeners = new ArrayList<>();
        DriveBase.findLoop(viewGroup, driveListeners, null);

        for (final DriveListener driveListener : driveListeners) {
            if (driveListener.eventTag() > -1) {
                View eventView = (View) driveListener;
                eventView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemListener != null) {
                            itemListener.onItemClick(viewGroup, null, driveListener.eventTag());
                        }
                    }
                });
            }
        }
    }

    protected XRefreshView getRefreshView() {
        return getWindow().getDecorView().findViewWithTag("XRefreshView");
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    public abstract int getLayoutId();

    public abstract void initView();

    /**
     * 根据一个请求实例请求网络端, 如果未设置界面响应者将默认为本控制器响应.
     *
     * @param request
     *         请求实例.
     */
    public void commitRequest(final MCBaseRequest request) {
        request.setResponse(new MCResponse(this) {{
            setRequest(request);
        }});
        MCHttpCGI.defaultCenter().requestCGI(request);
    }

    protected static Field getFieldByClass(Class clz, String name) {
        for (Field field : clz.getFields()) {
            ParamValue fieldValue = field.getAnnotation(ParamValue.class);
            if (fieldValue != null) {
                if (fieldValue.fieldName().equals(name)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 填充界面到对象.
     *
     * @param request
     *         请求对象.
     * @param noKeys
     *         不需要填充的对象名称集合.
     *
     * @return 是否顺利完成.
     */
    public boolean fillRequest(MCBaseRequest request, String[] noKeys) {

        List<DriveListener> driveListeners = DriveBase.getDriveListener(getWindow().getDecorView());
        if (driveListeners == null) {
            return true;
        }

        for (DriveListener driveListener : driveListeners) {
            KeyValue keyValue = driveListener.getParam();
            if (keyValue == null || isWith(keyValue.getKey(), noKeys)) {
                continue;
            }
            if (keyValue.getError() != null) {
                this.onMatchError(keyValue.getError());
                return false;
            }
            try {
                if (keyValue.getKey() == null) {
                    continue;
                }
                Field field = request.getClass().getField(keyValue.getKey());
                if (field == null) {
                    field = getFieldByClass(request.getClass(), keyValue.getKey());
                }
                if (field != null) {
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

    private boolean isWith(String key, String[] keys) {
        if (keys == null) {
            return false;
        }
        for (String item : keys) {
            if (item.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void onMatchError(String error) {
        Toast.makeText(this.getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    public void onBeginRequest() {
    }

    protected void onTokenInvalid() {
    }

    public void onError(Error error) {
        completeRefresh();
        if (error != null) {
            ServiceError serviceError = (ServiceError) error;
            if(serviceError.getErrorCode()==8003){//用户未注册
                MCDialogManager.showMessage(this, "该号码未注册！", "请检查是否填写正确", "重新填写", R.drawable.svg_icon_prompt_big);
                return;
            }

            if (serviceError.getErrorCode() == 8033 || serviceError.getErrorCode() == 8034) {
                if (isLogout) {
                    return;
                }
                isLogout = true;
                MCDialogManager.showMessage(this, "", "登录已失效,请重新登录", "重新登录", R.drawable.svg_fail).setListener(new ItemListener() {
                    @Override
                    public void onItemClick(View listView, Object itemData, int tag) {
                        onTokenInvalid();
                        isLogout = false;
                    }
                });
                return;
            }
            //            参数校验失败:{applyContent=售后留言不能为空.}
            String content = error.getMessage();
            if (content.contains("=")) {
                try {
                    String[] contents = content.split("=");
                    content = contents[1].replace("}", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MCDialogManager.showMessage(this, "", content, null, R.drawable.svg_fail);
            //            MCDialogManager.showMessage(this, "发生错误!", error.getMessage(), null,R.drawable.svg_fail);
        }
    }

    public void fillToView(int resId, Object data) {
        fillToView(resId, data, false);
    }

    public void fillToView(int resId, Object data, boolean self) {

        View view = null;
        if (resId < 0) {
            view = this.getWindow().getDecorView();
        } else {
            view = findViewById(resId);
        }

        DriveBase.fillViewData(view, data, self);
    }

    @Override
    public int fillParams(Intent intent, int tag) {
        return ACellViewEventer.NOMAL_Activity;
    }

    @Override
    public void onCellClick(View targetView, int tag) {
    }

    public void onSubmitClick(Button button) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MediaChanger.MEDIA_INPUT_TAG) {

            if (resultCode != -1) {
                View mediaView = findViewById(resultCode);
                if (mediaView instanceof MediaChanger) {
                    ((MediaChanger) mediaView).onMediaEnter(data.getStringExtra("BITMAP"), data.getStringExtra("VIDEO"));
                }
            } else {
                if (lastMediaChanger != null) {
                    if (data.getStringExtra("BITMAP") != null || data.getStringExtra("VIDEO") != null) {
                        lastMediaChanger.onMediaEnter(data.getStringExtra("BITMAP"), data.getStringExtra("VIDEO"));
                    } else {
                        Uri uri = data.getData();
                        String path = AppUtil.getRealFilePath(getBaseContext(), uri);
                        lastMediaChanger.onMediaEnter(path, null);
                    }
                    lastMediaChanger = null;
                } else {
                    Log.e("Meida", "接受照片回调的控件必须设置 android:id");
                }
            }
        }

        if (buttonController != null && resultCode > 0) {
            buttonController.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getPersimmions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void onRefresh() {
    }

    public void onRefresh(boolean isPullDown) {
    }

    public void onLoadMore(boolean isSilence) {
    }

    public void onRelease(float direction) {
    }

    public void onHeaderMove(double headerMovePercent, int offsetY) {
    }

    public OrderButtonController getButtonController() {
        if (buttonController == null) {
            buttonController = new OrderButtonController(this);
        }
        return buttonController;
    }

    public void setLastMediaChanger(MediaChanger lastMediaChanger) {
        this.lastMediaChanger = lastMediaChanger;
    }


    // ************************************************************************等待对话框
    /** 等待对话框 */
    private LoadingDialogUtil loadingDialogUtil;

    public void showProgress() {
        showProgress(true);
    }

    public void showProgress(boolean cancelable) {
        showProgress(this.getString(com.amos.modulebase.R.string.process_handle_wait), cancelable);
    }

    public void showProgress(String processMsg, boolean cancelable) {

        // if (baseUI == null || baseUI.isDestroyed() || baseUI.isFinishing()) {
        if (this.isFinishing()) {
            return;
        }

        if (loadingDialogUtil == null) {
            loadingDialogUtil = new LoadingDialogUtil(this);
        }

        if (TextUtils.isEmpty(processMsg)) {
            processMsg = this.getString(com.amos.modulebase.R.string.process_handle_wait);
        }
        loadingDialogUtil.showDialog(processMsg, cancelable);
    }

    public void dismissProgress() {
        if (this.isFinishing()) {
            return;
        }

        if (loadingDialogUtil != null) {
            loadingDialogUtil.dismissDialog();
        }
    }
}

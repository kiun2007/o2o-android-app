package com.kiun.modelcommonsupport.controllers;

import android.widget.Button;
import com.kiun.modelcommonsupport.ui.rollout.model.MediaManager;
import com.kiun.modelcommonsupport.ui.views.MCRichEditView;
import com.kiun.modelcommonsupport.utils.MCDialogManager;

/**
 * Created by kiun_2007 on 2018/9/5.
 * 媒体上传抽象类.
 */
public abstract class MediaUploadActivity extends BaseRequestAcitity implements MediaManager {

    protected MCRichEditView mediaEditView;

    /**
     * 多媒体控件ID.
     * @return 多媒体控件MCRichEditView 在界面的id.
     */
    public abstract int getMediaEditViewId();

    @Override
    public void initView() {
        mediaEditView = findViewById(getMediaEditViewId());
    }

    @Override
    public void onSubmitClick(Button button) {
        if(!mediaEditView.isUploadAll()){
            MCDialogManager.info(this, "文件正在上传中请稍后");
            return;
        }
        mediaEditView.upLoadAllMedia(this);
    }
}
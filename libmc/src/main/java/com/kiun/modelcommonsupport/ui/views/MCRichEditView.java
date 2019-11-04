package com.kiun.modelcommonsupport.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.controllers.CameraActivity;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.ui.rollout.adapter.RGridViewAdapter;
import com.kiun.modelcommonsupport.ui.rollout.model.MediaManager;
import com.kiun.modelcommonsupport.ui.rollout.model.RolloutInfo;
import com.kiun.modelcommonsupport.utils.MCFilePutManager;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kiun_2007 on 2018/8/14.
 * 富媒体编辑显示控件.
 */
public class MCRichEditView extends GridView implements DriveListener, MediaChanger {

    private ArrayList<RolloutInfo> mediaDatas = new ArrayList<>();
    RGridViewAdapter photoAdapter = null;
    int captureButtonId = 0;
    public int vedioCount = 0;
    String dataPath = null;
    boolean uploadFlag = false;
    boolean showDel = false;

    public MCRichEditView(Context context) {
        this(context, null);
    }

    public MCRichEditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public MCRichEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MCRichEditView);

        captureButtonId = array.getResourceId(R.styleable.MCRichEditView_captureButton, -1);
        showDel = array.getBoolean(R.styleable.MCRichEditView_showDel, false);
        dataPath = array.getString(R.styleable.MCRichEditView_dataPath);
        photoAdapter = new RGridViewAdapter(getContext(), this, mediaDatas, showDel);
        setAdapter(photoAdapter);
        setNumColumns(3);
        array.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (captureButtonId > -1) {
            View view = null;
            if (getContext() instanceof Activity) {
                view = ((Activity) getContext()).findViewById(captureButtonId);
            }

            if (view != null) {
                if (getContext() instanceof BaseRequestAcitity) {
                    final BaseRequestAcitity acitity = (BaseRequestAcitity) getContext();
                    view.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mediaDatas.size() >= 9) {
                                Toast.makeText(getContext(), "最多上传9个媒体文件", Toast.LENGTH_LONG);
                                return;
                            }
                            Intent intent = new Intent(acitity, CameraActivity.class);
                            //                            vedioCount = 0;
                            //                            for (int i = 0; i < mediaDatas.size(); i++) {
                            //                                RolloutInfo info = mediaDatas.get(i);
                            //                                if (!TextUtils.isEmpty(info.videoUrl)) {
                            //                                    vedioCount = vedioCount + 1;
                            //                                }
                            //                            }
                            if (vedioCount != 0) {
                                intent.putExtra("only", true);
                            }
                            intent.putExtra(MediaChanger.MEDIA_INPUT_ID, getId());
                            acitity.startActivityForResult(intent, MediaChanger.MEDIA_INPUT_TAG);
                        }
                    });
                }
            }
        }
    }

    public boolean isUploadAll() {
        if (!uploadFlag) {
            return true;
        }

        for (RolloutInfo mediaInfo : mediaDatas) {
            if (mediaInfo.videoUrl != null) { //视频
                if (!mediaInfo.videoUrl.startsWith("http") || !mediaInfo.url.startsWith("http")) {
                    return false;
                } else {
                    mediaInfo.isUpload = false;
                    photoAdapter.notifyDataSetChanged();
                }
            } else {
                if (!mediaInfo.url.startsWith("http")) {
                    return false;
                } else {
                    mediaInfo.isUpload = false;
                    photoAdapter.notifyDataSetChanged();
                }
            }
        }
        uploadFlag = false;
        return true;
    }

    public void upLoadAllMedia(MediaManager mediaManager) {

        uploadFlag = true;
        if (isUploadAll()) {
            mediaManager.uploadComplete();
            return;
        }

        for (RolloutInfo mediaInfo : mediaDatas) {
            mediaInfo.isUpload = true;
            MCFilePutManager.manager().putFileByData(mediaInfo.url, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info.isOK()) {
                        String keyValue = info.response.optString("key");
                        mediaInfo.url = String.format("http://pawx04z5h.bkt.clouddn.com/%s", keyValue);
                    }
                    if (isUploadAll()) {
                        mediaManager.uploadComplete();
                    }
                }
            });
            if (mediaInfo.videoUrl != null) {
                MCFilePutManager.manager().putFileByData(mediaInfo.videoUrl, new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        if (info.isOK()) {
                            String keyValue = info.response.optString("key");
                            mediaInfo.videoUrl = String.format("http://pawx04z5h.bkt.clouddn.com/%s", keyValue);
                        }
                        if (isUploadAll()) {
                            mediaManager.uploadComplete();
                        }
                    }
                });
            }
        }

        photoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //  AT_MOST参数表示控件可以自由调整大小，最大不超过Integer.MAX_VALUE/4
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        mediaDatas.clear();
        if (data instanceof JSONArray) {
            vedioCount = 0;
            JSONArray array = (JSONArray) data;
            for (int i = 0; i < array.length(); i++) {
                JSONObject file = array.optJSONObject(i);
                boolean isVedio = "1".endsWith(file.optString("type"));

                RolloutInfo rolloutInfo = new RolloutInfo();
                rolloutInfo.url = isVedio ? file.optString("videoCoverImg") : file.optString("url");
                rolloutInfo.videoUrl = isVedio ? file.optString("url") : null;
                rolloutInfo.width = 720;
                rolloutInfo.height = 1280;
                if (isVedio) {
                    vedioCount++;
                }
                mediaDatas.add(rolloutInfo);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public int eventTag() {
        return 0;
    }

    @Override
    public KeyValue getParam() {
        if (dataPath != null && mediaDatas.size() > 0) {
            List<Map<String, String>> mediaMaps = new ArrayList<>();
            for (RolloutInfo mediaInfo : mediaDatas) {
                Map<String, String> mediaMap = new HashMap<>();
                mediaMap.put("fileUrl", mediaInfo.videoUrl != null ? mediaInfo.videoUrl : mediaInfo.url);
                mediaMap.put("fileType", mediaInfo.videoUrl != null ? "1" : "0");
                if (mediaInfo.videoUrl != null) {
                    mediaMap.put("videoCoverImg", mediaInfo.url);
                }
                mediaMaps.add(mediaMap);
            }

            KeyValue keyValue = new KeyValue(dataPath, mediaMaps);
            return keyValue;
        }
        return null;
    }

    @Override
    public void onMediaEnter(String imageUrl, String videoUrl) {

        if (videoUrl != null) {
            vedioCount++;
        }
        RolloutInfo rolloutInfo = new RolloutInfo();
        rolloutInfo.url = imageUrl;
        rolloutInfo.videoUrl = videoUrl;
        rolloutInfo.width = 720;
        rolloutInfo.height = 1280;
        mediaDatas.add(rolloutInfo);
        photoAdapter.notifyDataSetChanged();
    }
}

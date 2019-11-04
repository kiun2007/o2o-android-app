package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.controllers.CameraActivity;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.MCFilePutManager;
import com.kiun.modelcommonsupport.utils.ViewUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/4/14.
 */

public class ARelativeLayout extends RelativeLayout implements DriveListener, MediaChanger {

    private int eventTag = -1;
    private String goneFormula = null;
    private String dataPath = null;
    private String paramName = null;
    private String format = null;
    private float ratio = 0;
    private ImageView imageView = null;
    private int imageId;
    private boolean onlyPhoto = false;
    private int iconViewId;
    private int infoViewId;
    private int cameraViewType;
    private String imageUrl;
    Path mPath = new Path();
    RectF mRect = new RectF();
    int radius = 0;

    public ARelativeLayout(Context context) {
        this(context, null);
    }

    public ARelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ARelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ARelativeLayout);

        dataPath = array.getString(R.styleable.ARelativeLayout_dataPath);
        goneFormula = array.getString(R.styleable.ARelativeLayout_goneFormula);
        ratio = array.getFloat(R.styleable.ARelativeLayout_ratio, 0);

        imageId = array.getResourceId(R.styleable.ARelativeLayout_hasImageId, -1);
        onlyPhoto = array.getBoolean(R.styleable.ARelativeLayout_onlyPhoto, false);
        iconViewId = array.getResourceId(R.styleable.ARelativeLayout_iconViewId, -1);
        infoViewId = array.getResourceId(R.styleable.ARelativeLayout_infoViewId, -1);
        radius = array.getDimensionPixelOffset(R.styleable.ARelativeLayout_radius, 0);
        paramName = array.getString(R.styleable.ARelativeLayout_paramName);
        cameraViewType = array.getInt(R.styleable.ARelativeLayout_cameraViewType, 0);
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (imageId > 0) {
            imageView = findViewById(imageId);
            if (getContext() instanceof BaseRequestAcitity) {
                final BaseRequestAcitity acitity = (BaseRequestAcitity) getContext();
                this.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoCameraActivity(acitity);
                    }
                });
            }
        }
    }

    public void gotoCameraActivity(BaseRequestAcitity acitity) {
        Intent intent = new Intent(acitity, CameraActivity.class);
        if (onlyPhoto) {
            intent.putExtra("only", true);
        }
        intent.putExtra("TYPE", cameraViewType);
        intent.putExtra(MediaChanger.MEDIA_INPUT_ID, getId());
        acitity.startActivityForResult(intent, MediaChanger.MEDIA_INPUT_TAG);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }

    @Override
    public void fill(Object data) {

        if (imageView instanceof AImageView) {
            AImageView aImageView = (AImageView) imageView;
            imageUrl = data.toString();
            aImageView.fill(data);
            findViewById(iconViewId).setVisibility(GONE);
            findViewById(infoViewId).setVisibility(VISIBLE);
            return;
        }

        if (goneFormula == null) {
            return;
        }
        setVisibility(ViewUtil.getVisibleByFormula(data, goneFormula));
    }

    public void setInfo(String info) {
        if (infoViewId > -1 && findViewById(infoViewId) instanceof ViewGroup) {
            TextView textView = findViewById(infoViewId).findViewWithTag("IMAGE_TAG");
            if (textView != null) {
                textView.setText(info);
            }
        }
    }

    @Override
    public int eventTag() {
        return this.eventTag;
    }

    @Override
    public KeyValue getParam() {
        if (dataPath != null) {

            KeyValue keyValue = null;
            if (imageUrl != null) {
                keyValue = new KeyValue(paramName != null ? paramName : dataPath, imageUrl);
            } else {
                keyValue = new KeyValue(null, null, "照片未选择");
            }

            return keyValue;
        }
        return null;
    }

    public void setEventTag(int eventTag) {
        this.eventTag = eventTag;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (ratio != 0) {
            float height = width * ratio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void onMediaEnter(String bitmapUrl, String videoUrl) {
        if (imageView != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(bitmapUrl));
        }

        if (iconViewId > -1 && findViewById(iconViewId) instanceof ViewGroup) {
            //            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            //            findViewById(iconViewId).setLayoutParams(layoutParams);
            findViewById(iconViewId).setVisibility(GONE);
        }

        if (infoViewId > -1 && findViewById(infoViewId) instanceof ViewGroup) {
            findViewById(infoViewId).setVisibility(VISIBLE);
        }

        setInfo("正在上传...");
        MCFilePutManager.manager().putFileByData(bitmapUrl, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    setInfo("点击修改");
                    String keyValue = info.response.optString("key");
                    imageUrl = String.format("http://pawx04z5h.bkt.clouddn.com/%s", keyValue);
                } else {
                    setInfo("上传失败,");
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
        mRect.set(0, 0, w, h);
        mPath.addRoundRect(mRect, radius, radius, Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}

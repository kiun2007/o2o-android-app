package com.kiun.modelcommonsupport.ui.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.controllers.BaseRequestAcitity;
import com.kiun.modelcommonsupport.controllers.CameraActivity;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.utils.AppUtil;
import com.kiun.modelcommonsupport.utils.MCActionSheet;
import com.kiun.modelcommonsupport.utils.MCFilePutManager;
import com.kiun.modelcommonsupport.utils.OnActionSheetSelectListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

/**
 * Created by kiun_2007 on 2018/4/14.
 * 自动图片设置.
 */
@SuppressLint("AppCompatCustomView")
public class AImageView extends ImageView implements DriveListener, MediaChanger {

    private float[] radiusArray = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private String dataPath = null;
    private int radius = 0;
    private Paint mPaint;
    private Context context;
    private String imageUrl;
    private boolean isEditModel;
    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE = 0;
    // 相册选择回传吗
    public final static int GALLERY_REQUEST_CODE = 1;

    public AImageView(Context context) {
        this(context, null);
    }

    public AImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AImageView);
        dataPath = array.getString(R.styleable.AImageView_dataPath);
        radius = array.getDimensionPixelOffset(R.styleable.AImageView_radius, 0);
        String srcName = array.getString(R.styleable.AImageView_srcName);
        if(srcName != null){
            String[] names = srcName.split(":");
            if (names.length == 2){
                int resId = MainApplication.getInstance().getResId(names[1], names[0]);
                if(resId > 0){
                    setImageDrawable(MainApplication.getInstance().getResources().getDrawable(resId));
                }
            }else if (names.length == 1){
                setImageDrawable(MainApplication.getInstance().getBitmapByName(srcName));
            }
        }
        setRadius(radius, radius, radius, radius);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        boolean editModel = array.getBoolean(R.styleable.AImageView_isEditModel, false);
        array.recycle();

        if(!(getContext() instanceof BaseRequestAcitity)){
            return;
        }
        if(!editModel){
            return;
        }

        BaseRequestAcitity acitity = (BaseRequestAcitity) getContext();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isEditModel){
                    return;
                }

                String[] items = {"拍照", "相册"};
                MCActionSheet.showActionSheet(getContext(), items, "取消", new OnActionSheetSelectListener() {
                    @Override
                    public void onItemAction(Dialog dialog, int index) {
                        dialog.dismiss();

                        switch (index) {
                            case 0:
                                break;
                            case 1:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                                } else {
                                    Intent intent = new Intent(acitity, CameraActivity.class);
                                    intent.putExtra("only", true);
                                    intent.putExtra(MediaChanger.MEDIA_INPUT_ID, getId());
                                    acitity.startActivityForResult(intent, MediaChanger.MEDIA_INPUT_TAG);
                                    acitity.setLastMediaChanger(AImageView.this);
                                }
                                break;
                            case 2:
                                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_REQUEST_CODE);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                    ((Activity)getContext()).startActivityForResult(intent, MediaChanger.MEDIA_INPUT_TAG);
                                    acitity.setLastMediaChanger(AImageView.this);
                                }
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if ((data instanceof String) && !((String) data).isEmpty()) {
            Glide.with(this.getContext().getApplicationContext()).load(data).into(this);
            imageUrl = data.toString();
        }
    }

    public void setRadius(float leftTop, float rightTop, float rightBottom, float leftBottom) {
        radiusArray[0] = radiusArray[1] = leftTop;
        radiusArray[2] = radiusArray[3] = rightTop;
        radiusArray[4] = radiusArray[5] = rightBottom;
        radiusArray[6] = radiusArray[7] = leftBottom;
    }

    @Override
    public int eventTag() {
        return 0;
    }

    @Override
    public KeyValue getParam() {
        if(dataPath != null){

            KeyValue keyValue = null;
            if(imageUrl != null){
                keyValue = new KeyValue(dataPath, imageUrl);
            }else{
                keyValue = new KeyValue(null, null, "照片未选择");
            }

            return keyValue;
        }
        return null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();
        if (mDrawable == null) {
            return; // couldn't resolve the URI
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;     // nothing to draw (empty bounds)
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }
            canvas.translate(getPaddingLeft(), getPaddingTop());
            if (radius > 0) {//当为圆形模式的时候
                Bitmap bitmap = drawable2Bitmap(mDrawable);
                mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()),
                        radius, radius, mPaint);
            } else {
                if (mDrawMatrix != null) {
                    canvas.concat(mDrawMatrix);
                }
                mDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    /**
     * drawable转换成bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //根据传递的scaletype获取matrix对象，设置给bitmap
        Matrix matrix = getImageMatrix();
        if (matrix != null) {
            canvas.concat(matrix);
        }
        drawable.draw(canvas);
        return bitmap;
    }

    private void setImageAndUpload(String path) {
//        String path = AppUtil.getRealFilePath(context, bitmapUri);
        setImageBitmap(BitmapFactory.decodeFile(path));
        Log.i("========", path);
        MCFilePutManager.manager().putFileByData(path, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if(info.isOK()){
                    String keyValue = info.response.optString("key");
                    imageUrl = String.format("http://pawx04z5h.bkt.clouddn.com/%s", keyValue);
                }else {
                }
            }
        });
    }

    public void setEditModel(boolean editModel) {
        isEditModel = editModel;
    }

    @Override
    public void onMediaEnter(String imageUrl, String videoUrl) {
        setImageAndUpload(imageUrl);
    }
}

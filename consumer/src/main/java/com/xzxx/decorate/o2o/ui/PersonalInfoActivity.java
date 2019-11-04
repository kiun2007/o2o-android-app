package com.xzxx.decorate.o2o.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.AImageView;
import com.kiun.modelcommonsupport.ui.views.ALinearLayout;
import com.kiun.modelcommonsupport.ui.views.ATextView;
import com.kiun.modelcommonsupport.ui.views.MediaChanger;
import com.kiun.modelcommonsupport.utils.AppUtil;
import com.kiun.modelcommonsupport.utils.DateUtil;
import com.kiun.modelcommonsupport.utils.MCActionSheet;
import com.kiun.modelcommonsupport.utils.MCDialogManager;
import com.kiun.modelcommonsupport.utils.MCFilePutManager;
import com.kiun.modelcommonsupport.utils.OnActionSheetSelectListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.userinfo.InfoModifyRequest;
import com.xzxx.decorate.o2o.requests.userinfo.InfoRequest;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by zf on 2018/6/25.
 * 个人资料页面
 */
public class PersonalInfoActivity extends BaseActivity {

    private final static String TAG = "PersonalInfoActivity";

    // 生日编辑框
    private TextView tvBirthday;
    private ATextView txt_phone;
    // 性别编辑框
    private TextView tvGander;
    private AImageView head_image_view;

    private ALinearLayout contentPanel;

    private boolean isEdit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_personal_info;
    }

    @Override
    public void initView() {
        commitRequest(new InfoRequest());
        contentPanel = findViewById(R.id.infoContentLayout);
        tvGander = findViewById(R.id.tv_gander);
        tvBirthday = findViewById(R.id.tv_birthday);
        txt_phone = findViewById(R.id.txt_phone);
        head_image_view = findViewById(R.id.head_image_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        dismissProgress();
        if (request instanceof InfoRequest) {
            fillToView(-1, data);
            String phone = txt_phone.getText().toString();
            try {
                if (!TextUtils.isEmpty(phone)) {
                    txt_phone.setText(phone.replace(phone.substring(3, 7), "****"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (request instanceof InfoModifyRequest) {
            MCDialogManager.info(this, "资料修改成功");
        }
    }

    @Override
    public void onRightClick() {
        isEdit = !isEdit;
        contentPanel.setEnabled(isEdit);
        getNavigatorBar().setRightButtonText(isEdit ? "保存" : "编辑");
        if (isEdit) {
            ((TextView) findViewById(R.id.tv_birthday)).setHint("请选择生日");
        } else {
            ((TextView) findViewById(R.id.tv_birthday)).setHint("");
        }
        boolean isMan = sexConvert(isEdit);


        if (!isEdit) {
            InfoModifyRequest infoModifyRequest = new InfoModifyRequest();
            infoModifyRequest.sex = isMan ? "0" : "1";
            infoModifyRequest.birthday = tvBirthday.getText().toString().replace("-", "");

            try {
                long i = com.amos.modulebase.utils.DateUtil.compareTime(infoModifyRequest.birthday, com.amos.modulebase.utils.DateUtil.getDate("yyyyMMdd"), "yyyyMMdd");
                if (i < 0) {
                    ToastUtil.showToast(PersonalInfoActivity.this, "生日不能大于当前日期");
                    isEdit = !isEdit;
                    contentPanel.setEnabled(isEdit);
                    getNavigatorBar().setRightButtonText(isEdit ? "保存" : "编辑");
                    if (isEdit) {
                        ((TextView) findViewById(R.id.tv_birthday)).setHint("请选择生日");
                    } else {
                        ((TextView) findViewById(R.id.tv_birthday)).setHint("");
                    }
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            showProgress(false);
            if (TextUtils.isEmpty(path)) {
                if (fillRequest(infoModifyRequest, null)) {
                    commitRequest(infoModifyRequest);
                }
            } else {
                setImageAndUpload(path, infoModifyRequest);
            }
        }
    }

    private boolean sexConvert(boolean isToRadio) {
        boolean isGanderMan = tvGander.getText().toString().equals("男");
        RadioButton maleRadio = findViewById(R.id.radio_male);
        RadioButton femaleRadio = findViewById(R.id.radio_female);
        findViewById(R.id.sexRadioGroup).setVisibility(isToRadio ? View.VISIBLE : View.GONE);
        tvGander.setVisibility(isToRadio ? View.GONE : View.VISIBLE);
        if (isToRadio) {
            maleRadio.setChecked(isGanderMan);
            femaleRadio.setChecked(!isGanderMan);
        } else {
            tvGander.setText(maleRadio.isChecked() ? "男" : "女");
            return maleRadio.isChecked();
        }
        return false;
    }

    public void onClick(View view) {
        if (!isEdit) {
            return;
        }
        chooseBirthday();
    }

    private void chooseBirthday() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(PersonalInfoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Log.d(TAG, "onDateSet: year: " + year + ", month: " + month + ", dayOfMonth: " + dayOfMonth);
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String birthdayStr = DateUtil.dateToStr(calendar.getTime());
                        try {
                            long i = com.amos.modulebase.utils.DateUtil.compareTime(birthdayStr, com.amos.modulebase.utils.DateUtil.getDate(com.amos.modulebase.utils.DateUtil.YMD), com.amos.modulebase.utils.DateUtil.YMD);
                            if (i < 0) {
                                ToastUtil.showToast(PersonalInfoActivity.this, "生日不能大于当前日期");
                                //birthdayStr = com.amos.modulebase.utils.DateUtil.getDate(com.amos.modulebase.utils.DateUtil.YMD);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tvBirthday.setText(birthdayStr);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void chooseGander() {
        final String[] items = {"男", "女"};
        MCActionSheet.showActionSheet(PersonalInfoActivity.this, items, "取消", new OnActionSheetSelectListener() {
            @Override
            public void onItemAction(Dialog dialog, int index) {
                dialog.dismiss();
                if (index > 0) tvGander.setText(items[index - 1]);
            }
        });
    }

    private void chooseHeadImg() {
        Log.i(TAG, "click head image view ");
    }

    String path = "";
    String imageUrl = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MediaChanger.MEDIA_INPUT_TAG) {
            if (resultCode != -1) {
                View mediaView = findViewById(resultCode);
                if (mediaView instanceof MediaChanger) {
                    lastMediaChanger = (MediaChanger) mediaView;
                    path = data.getStringExtra("BITMAP");
                    head_image_view.setImageBitmap(BitmapFactory.decodeFile(path));
                    //                    lastMediaChanger.onMediaEnter(path, data.getStringExtra("VIDEO"));
                    lastMediaChanger = null;
                }
            } else {
                if (lastMediaChanger != null) {
                    //                if (data.getStringExtra("BITMAP") != null || data.getStringExtra("VIDEO") != null) {
                    //                    lastMediaChanger.onMediaEnter(data.getStringExtra("BITMAP"), data.getStringExtra("VIDEO"));
                    //                } else {
                    Uri uri = data.getData();
                    path = AppUtil.getRealFilePath(getBaseContext(), uri);
                    head_image_view.setImageBitmap(BitmapFactory.decodeFile(path));
                    //                lastMediaChanger.onMediaEnter(path, null);
                    lastMediaChanger = null;
                } else {
                    Log.e("Meida", "接受照片回调的控件必须设置 android:id");
                }
            }
        }
    }

    private void setImageAndUpload(String path, final InfoModifyRequest infoModifyRequest) {
        //        String path = AppUtil.getRealFilePath(context, bitmapUri);
        Log.i("========", path);
        MCFilePutManager.manager().putFileByData(path, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    String keyValue = info.response.optString("key");
                    imageUrl = String.format("http://pawx04z5h.bkt.clouddn.com/%s", keyValue);
                    if (fillRequest(infoModifyRequest, null)) {
                        infoModifyRequest.headImg = imageUrl;
                        commitRequest(infoModifyRequest);
                    }
                }
            }
        });
    }
}

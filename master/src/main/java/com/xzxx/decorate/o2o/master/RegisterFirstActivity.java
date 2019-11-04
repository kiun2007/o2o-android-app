package com.xzxx.decorate.o2o.master;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.amos.modulebase.utils.ToastUtil;
import com.kiun.modelcommonsupport.MainApplication;
import com.kiun.modelcommonsupport.controllers.SelectCityActivity;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.MCUserInfo;
import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.ARelativeLayout;
import com.kiun.modelcommonsupport.ui.views.MediaChanger;
import com.kiun.modelcommonsupport.ui.views.UserEditText;
import com.xzxx.decorate.o2o.BaseActivity;
import com.xzxx.decorate.o2o.requests.AuditErrorInfoRequest;
import com.xzxx.decorate.o2o.requests.FirstInRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zf on 2018/7/23.
 * 师傅注册页面
 */
public class RegisterFirstActivity extends BaseActivity implements View.OnClickListener {

    TextView editRegisterJob;
    TextView edit_register_city;
    RadioButton radio_register_male;
    String cityCode = null;
    String professionId = null;
    MCUserInfo userInfo = null;

    ArrayList<View> arrayListTips = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.layout_register_first;
    }

    @Override
    public void initView() {
        getNavigatorBar().setLeftButtonVisibility(View.GONE);
        editRegisterJob = findViewById(R.id.edit_register_job);
        radio_register_male = findViewById(R.id.radio_register_male);
        edit_register_city = findViewById(R.id.edit_register_city);
        findViewById(R.id.rl_skillcell).setOnClickListener(this);
        findViewById(R.id.city_cellview).setOnClickListener(this);

        arrayListTips.add(findViewById(R.id.txt_tips1));
        arrayListTips.add(findViewById(R.id.txt_tips2));
        arrayListTips.add(findViewById(R.id.txt_tips3));
        arrayListTips.add(findViewById(R.id.txt_tips4));
        arrayListTips.add(findViewById(R.id.txt_tips5));
        arrayListTips.add(findViewById(R.id.txt_tips6));

        if (getIntent().getBooleanExtra("Audit", false)) {
            commitRequest(new AuditErrorInfoRequest());

            ((UserEditText) findViewById(R.id.et_name)).editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        arrayListTips.get(0).setVisibility(View.GONE);
                        //                    }else{
                        //                        arrayListTips.get(0).setVisibility(View.GONE);
                    }
                }
            });

            ((UserEditText) findViewById(R.id.et_idcardNo)).editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        arrayListTips.get(2).setVisibility(View.GONE);
                    }
                }
            });

            ((RadioGroup) findViewById(R.id.radiogroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    arrayListTips.get(1).setVisibility(View.GONE);
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {

        if (request instanceof FirstInRequest) {
            //            MCUserInfo userInfo = MainApplication.getInstance().getUserInfo(false);
            userInfo.aduitType = "0";
            MainApplication.getInstance().saveUserInfo(userInfo);
            Intent intent = new Intent(this, RegisterSecondActivity.class);
            startActivity(intent);
            finish();

        }

        if (request instanceof AuditErrorInfoRequest && data instanceof JSONObject) {
            //oppositeOfIdcard
            fillToView(-1, data);
            cityCode = ((JSONObject) data).optString("serviceCity");
            professionId = ((JSONObject) data).optString("professionId");

            JSONArray array = ((JSONObject) data).optJSONArray("auditErrorLocations");
            for (int i = 0; i < array.length(); i++) {
                int location = array.optJSONObject(i).optInt("location");

                try {
                    if (location < arrayListTips.size()) {
                        arrayListTips.get(location).setVisibility(View.VISIBLE);
                    }

                    if (location == 3) {
                        findViewById(R.id.if_faceOfIdcard).setVisibility(View.GONE);
                    }

                    if (location == 4) {
                        findViewById(R.id.if_oppositeOfIdCard).setVisibility(View.GONE);
                    }

                    if (location == 5) {
                        findViewById(R.id.if_handOfIdCard).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //                View errorView = findViewById(R.id.allContentLayout).findViewWithTag(String.format("error_%s", array.optJSONObject(i).optString("location")));
                //                //description
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rl_skillcell) {
            Intent intent = new Intent(this, SkillItemActivity.class);
            startActivityForResult(intent, 1020);
        } else if (view.getId() == R.id.city_cellview) {
            Intent intent = new Intent(this, SelectCityActivity.class);
            startActivityForResult(intent, 1021);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MediaChanger.MEDIA_INPUT_TAG) {
            if (resultCode != -1) {
                View mediaView = findViewById(resultCode);
                try {
                    if (mediaView instanceof MediaChanger) {
                        if (mediaView.getId() == R.id.rl_faceOfIdcard) {
                            arrayListTips.get(3).setVisibility(View.GONE);
                        } else if (mediaView.getId() == R.id.rl_oppositeOfIdCard) {
                            arrayListTips.get(4).setVisibility(View.GONE);
                        } else if (mediaView.getId() == R.id.rl_handOfIdCard) {
                            arrayListTips.get(5).setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultCode == SkillItemActivity.RESULT_CODE && requestCode == 1020) {
            editRegisterJob.setText(data.getStringExtra(SkillItemActivity.SKILL_NAMES));
            professionId = data.getStringExtra(SkillItemActivity.SKILL_IDS);
        }

        if (requestCode == 1021 && resultCode != 0) {
            String name = data.getStringExtra("name");
            edit_register_city.setText(name);
            cityCode = data.getStringExtra("code");
        }
    }

    @Override
    public void onSubmitClick(Button button) {
        FirstInRequest firstInRequest = new FirstInRequest();
        fillRequest(firstInRequest);

        if (TextUtils.isEmpty(firstInRequest.masterName)) {
            ToastUtil.showToast(this, "请输入您的姓名");
            return;
        }

        if (TextUtils.isEmpty(firstInRequest.idcardNo) || !isIDCard((firstInRequest.idcardNo))) {
            ToastUtil.showToast(this, "请填写正确的身份证号");
            return;
        }

        if (cityCode == null) {
            ToastUtil.showToast(this, "请选择服务城市");
            return;
        }
        if (professionId == null) {
            ToastUtil.showToast(this, "请选择职业");
            return;
        }

        if (TextUtils.isEmpty(firstInRequest.faceOfIdcard)) {
            ToastUtil.showToast(this, "请选择身份证正面照片");
            return;
        }
        if (TextUtils.isEmpty(firstInRequest.oppositeOfIdCard)) {
            ToastUtil.showToast(this, "请选择身份证反面照片");
            return;
        }
        if (TextUtils.isEmpty(firstInRequest.handOfIdCard)) {
            ToastUtil.showToast(this, "请选择手持身份证照片");
            return;
        }

        firstInRequest.gender = radio_register_male.isChecked() ? "0" : "1";
        firstInRequest.city = cityCode;
        firstInRequest.profession = professionId;
        commitRequest(firstInRequest);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     *         待校验的字符串
     *
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches("[1-9]{2}[0-9]{4}(19|20)[0-9]{2}((0[1-9]{1})|(1[1-2]{1}))((0[1-9]{1})|([1-2]{1}[0-9]{1}|(3[0-1]{1})))[0-9]{3}[0-9xX]{1}", idCard);
    }


    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private boolean granted = false;

    ARelativeLayout rl_faceOfIdcard;
    ARelativeLayout rl_oppositeOfIdCard;
    ARelativeLayout rl_handOfIdCard;
    ARelativeLayout clickView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rl_faceOfIdcard = findViewById(R.id.rl_faceOfIdcard);
        rl_oppositeOfIdCard = findViewById(R.id.rl_oppositeOfIdCard);
        rl_handOfIdCard = findViewById(R.id.rl_handOfIdCard);
        //6.0动态权限获取
        getPermissions();

        userInfo = MainApplication.getInstance().getUserInfo(false);
        remove();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
                granted = true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
                granted = false;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                }
                setListener();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        clickView = null;
        setListener();
    }

    private void setListener() {
        if (granted) {
            if (clickView != null) {
                clickView.gotoCameraActivity(RegisterFirstActivity.this);
            }
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickView = (ARelativeLayout) view;
                if (granted) {
                    clickView.gotoCameraActivity(RegisterFirstActivity.this);
                } else {
                    getPermissions();
                }
            }
        };
        rl_faceOfIdcard.setOnClickListener(onClickListener);
        rl_oppositeOfIdCard.setOnClickListener(onClickListener);
        rl_handOfIdCard.setOnClickListener(onClickListener);
    }

    private void remove() {
        if (TextUtils.isEmpty(userInfo.aduitType)) {
            SharedPreferences sp = this.getSharedPreferences("AppSave", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.remove(MainApplication.USER_INFO);
            editor.commit();
        }
    }

    private boolean fillRequest(MCBaseRequest request) {

        List<DriveListener> driveListeners = DriveBase.getDriveListener(getWindow().getDecorView());
        if (driveListeners == null) {
            return true;
        }

        for (DriveListener driveListener : driveListeners) {
            KeyValue keyValue = driveListener.getParam();
            if (keyValue == null) {
                continue;
            }
            //            if (keyValue.getError() != null) {
            //                this.onMatchError(keyValue.getError());
            //                return false;
            //            }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}

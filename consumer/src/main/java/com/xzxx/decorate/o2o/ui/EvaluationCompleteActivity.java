package com.xzxx.decorate.o2o.ui;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amos.modulebase.bean.ResponseBean;
import com.amos.modulebase.mvp.model.HttpOkBiz;
import com.amos.modulebase.utils.ScreenUtil;
import com.amos.modulebase.utils.ToastUtil;
import com.amos.modulebase.utils.http.HttpRequestCallBack;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.ui.views.MCRichEditView;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.kiun.modelcommonsupport.utils.HttpRequestBiz;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.CommentInfoRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 评价完成页面
 */
public class EvaluationCompleteActivity extends BaseActivity {

    private View btn_evaluation_complete;
    private View view_root;
    private MCRichEditView richEditView;
    private FlowLayout id_evaluation_tags;
    private View txt_tips;
    private String commentId = "";

    @Override
    public int getLayoutId() {
        return R.layout.layout_evaluation_complete;
    }

    @Override
    public void initView() {
        btn_evaluation_complete = findViewById(R.id.btn_evaluation_complete);
        txt_tips = findViewById(R.id.txt_tips);
        view_root = findViewById(R.id.view_root);
        richEditView = findViewById(R.id.richEditView);
        id_evaluation_tags = findViewById(R.id.id_evaluation_tags);
        CommentInfoRequest request = new CommentInfoRequest();
        request.orderId = getIntent().getStringExtra("orderId");
        commitRequest(request);

        btn_evaluation_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delComment();
            }
        });
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof CommentInfoRequest && data instanceof String && TextUtils.isEmpty((String) data)) {
            txt_tips.setVisibility(View.VISIBLE);
            view_root.setVisibility(View.GONE);
        } else if (request instanceof CommentInfoRequest && !(data instanceof String)) {
            view_root.setVisibility(View.VISIBLE);
            txt_tips.setVisibility(View.GONE);
            JSONObject jsonObject = (JSONObject) data;
            commentId = jsonObject.optString("commentId", "");
            //            try {
            //                jsonObject.put("orderFiles",new JSONArray("[{\"fileId\":\"201809141755670010319279\",\"url\":\"http://pawx04z5h.bkt.clouddn.com/FnrhqYS0kONJ2_tbt2vuxFlSsXfn\",\"type\":\"0\",\"videoCoverImg\":\"\",\"persistentId\":\"\",\"orderId\":\"201809141755670010319278\",\"busiId\":\"201809141755670010319278\"},{\"fileId\":\"201809141755670010319280\",\"url\":\"http://pawx04z5h.bkt.clouddn.com/Fpi8gIuzFOSa6-ZYuUExFjwruR0w\",\"type\":\"0\",\"videoCoverImg\":\"\",\"persistentId\":\"\",\"orderId\":\"201809141755670010319278\",\"busiId\":\"201809141755670010319278\"}]"));
            //            } catch (JSONException e) {
            //                e.printStackTrace();
            //            }
            //            fillToView(-1, jsonObject);
            fillToView(-1, data);

            JSONArray jsonArray = jsonObject.optJSONArray("impressionInfos");
            if (jsonArray != null && jsonArray.length() > 0) {
                initViewGroup(jsonArray, id_evaluation_tags);
            }
        }
    }

    private void delComment() {
        if (TextUtils.isEmpty(commentId)) {
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        HttpRequestBiz httpRequestBiz = new HttpRequestBiz();
        HashMap<String, String> params;
        map.put("commentId", commentId);
        params = httpRequestBiz.getBaseParams("order/customer/commentDelete", map);

        //        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack(OrderProgressBean.class, true, "") {
        HttpOkBiz.getInstance().sendPost(httpRequestBiz.requestUrl, params, new HttpRequestCallBack() {

            @Override
            public void onSuccess(ResponseBean result) {
                ToastUtil.showToast(EvaluationCompleteActivity.this, "删除成功");
                finish();
            }

            @Override
            public void onFail(ResponseBean result) {
                //                callBack.onFail(result);
                ToastUtil.showToast(EvaluationCompleteActivity.this, result.getInfo());
            }
        });

    }

    private void initViewGroup(JSONArray citys, FlowLayout flowLayout) {

        flowLayout.removeAllViews();
        for (int i = 0; i < citys.length(); i++) {
            //            int ranHeight = BasicUtils.dip2px(this, 30);
            int ranWidth = (ScreenUtil.getScreenWidthPx() - ScreenUtil.dip2px(40)) / 3 - ScreenUtil.dip2px(15);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ranWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(BasicUtils.dip2px(this, 7), 0, BasicUtils.dip2px(this, 7), 0);
            TextView tv = new TextView(this);
                        tv.setPadding(0, BasicUtils.dip2px(this, 5), 0, BasicUtils.dip2px(this, 5));
            tv.setTextColor(Color.parseColor("#999999"));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tv.setText(citys.optString(i));
            //            tv.setTag(citys.optJSONObject(i).optString("code"));
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.shape_radius_gray_checkbox);
            tv.setLines(1);
            //            tv.setOnClickListener(this);
            flowLayout.addView(tv, lp);
        }
    }
}

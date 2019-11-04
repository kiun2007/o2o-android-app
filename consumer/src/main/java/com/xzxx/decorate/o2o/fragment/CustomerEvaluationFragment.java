package com.xzxx.decorate.o2o.fragment;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.ScreenUtil;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.adapter.CommentListAdapter;
import com.xzxx.decorate.o2o.bean.MasterCommentInfoVo;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.MasterInfoCommentRequest;
import com.xzxx.decorate.o2o.requests.order.MasterInfoImpressRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zf on 2018/8/12.
 * 客户评价页面
 */
public class CustomerEvaluationFragment extends BaseRequestFragment {

    private FlowLayout flowLayoutC;
    private ListView list_view_evaluation;
    private ArrayList<MasterCommentInfoVo> arrayList = new ArrayList<>();
    private CommentListAdapter adapter;

    @Override
    protected void initView(View view) {
        list_view_evaluation = view.findViewById(R.id.list_view_evaluation);

        list_view_evaluation.addHeaderView(initHeader());
        adapter = new CommentListAdapter(getActivity(), arrayList);
        list_view_evaluation.setAdapter(adapter);

        onRefresh(true);
        //        getRefreshView().setAutoLoadMore(true);
    }

    private View initHeader() {
        View view = View.inflate(getActivity(), R.layout.layout_customer_evaluation_header, null);
        flowLayoutC = view.findViewById(R.id.flow_layout_evaluation);
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_customer_evaluation;
    }

    // 好有印象
    JSONArray impressJSONArray = null;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        completeRefresh();
        if (request instanceof MasterInfoImpressRequest) {
            Log.i("123456", "data  " + "MasterInfoImpressRequest");
            if (data instanceof JSONArray) {
                impressJSONArray = (JSONArray) data;
                showFlowText(impressJSONArray);
            } else {
                impressJSONArray = null;
                flowLayoutC.removeAllViews();
            }
        }

        if (request instanceof MasterInfoCommentRequest) {
            Log.i("123456", "data  " + "MasterInfoCommentRequest");
            if (((MasterInfoCommentRequest) request).pageNo == 1) {
                arrayList.clear();
            }

            if (data instanceof JSONArray) {
                JSONArray commentJSONArray = (JSONArray) data;
                for (int i = 0; i < commentJSONArray.length(); i++) {
                    JSONObject jsonObject = commentJSONArray.optJSONObject(i);
                    MasterCommentInfoVo bean = new MasterCommentInfoVo();
                    try {
                        bean.init(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    arrayList.add(bean);
                }
            }
            initListView();
            //            list_view_evaluation.getLayoutParams().height = com.amos.modulebase.utils.ViewUtil.getListViewHeight(list_view_evaluation);
        }

        if ((impressJSONArray == null || impressJSONArray.length() < 1) && arrayList.size() <= 0) {
            LogUtil.i(".......................");
        }
    }

    private void initListView() {

        //        String string = "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p511118051.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p647099823.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p462657443.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1910825503.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p1910926158.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p909265336.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2248676081.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p443461818.jpg," +
        //                "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1374588202.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p1910926158.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p909265336.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2248676081.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p443461818.jpg," +
        //                "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1374588202.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p1910926158.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p909265336.jpg" +
        //                ",https://img3.doubanio.com/view/photo/s_ratio_poster/public/p2248676081.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p443461818.jpg," +
        //                "https://img3.doubanio.com/view/photo/s_ratio_poster/public/p1374588202.jpg" +
        //                ",https://img1.doubanio.com/view/photo/s_ratio_poster/public/p647422117.jpg";
        //
        //        String[] strs = string.split(",");
        //
        //
        //        for (int i = 0; i < strs.length; i++) {
        //            MasterCommentInfoVo bean = new MasterCommentInfoVo();
        //            //            bean.setId(i + "");
        //            bean.setCustomerName(getString(R.string.app_name) + i);
        //            bean.setContent(getString(R.string.app_name) + i);
        //            bean.setCommentTime(getString(R.string.app_name) + i);
        //            bean.setSecondItemName(getString(R.string.app_name) + i);
        //            bean.initTest(getActivity());
        //            arrayList.add(bean);
        //
        //        }
        //
        //        adapter = new CommentListAdapter(getActivity(), arrayList);
        //        list_view_evaluation.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    MasterInfoImpressRequest orderMasterInfoRequest;
    MasterInfoCommentRequest masterInfoCommentRequest;

    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //    }

    public void showFlowText(JSONArray data) {
        flowLayoutC.removeAllViews();
        for (int i = 0; i < data.length(); i++) {
            try {
                int ranHeight = BasicUtils.dip2px(getActivity(), 30);
                int ranWidth = (ScreenUtil.getScreenWidthPx() - ScreenUtil.dip2px(30)) / 3 - ScreenUtil.dip2px(15);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ranWidth, ranHeight);
                lp.setMargins(BasicUtils.dip2px(getActivity(), 7), 0, BasicUtils.dip2px(getActivity(), 7), 0);
                TextView tv = new TextView(getActivity());
                tv.setTextColor(Color.parseColor("#888888"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                String content = data.getJSONObject(i).optString("description", "") + "(" + data.getJSONObject(i).optString("num", "") + ")";
                tv.setText(content);
                tv.setGravity(Gravity.CENTER);
                tv.setLines(3);
                //                tv.setBackgroundResource(R.drawable.bg_flow_layout_tag);
                flowLayoutC.addView(tv, lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRefresh(boolean isSilence) {
        String masterId = getArguments().getString("masterId", "");
        masterId="201809211507670005129171";
        orderMasterInfoRequest = new MasterInfoImpressRequest();
        orderMasterInfoRequest.masterId = masterId;
        commitRequest(orderMasterInfoRequest);
        masterInfoCommentRequest = new MasterInfoCommentRequest();
        masterInfoCommentRequest.masterId = masterId;
        masterInfoCommentRequest.pageNo = 1;
        commitRequest(masterInfoCommentRequest);
    }

    @Override
    public void onLoadMore(boolean isSilence) {
        masterInfoCommentRequest.pageNo++;
        commitRequest(masterInfoCommentRequest);
    }
}
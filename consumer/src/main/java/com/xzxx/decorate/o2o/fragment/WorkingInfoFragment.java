package com.xzxx.decorate.o2o.fragment;

import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amos.modulebase.utils.MathUtils;
import com.kiun.modelcommonsupport.controllers.BaseRequestFragment;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.OrderMasterInfoRequest;
import com.xzxx.decorate.o2o.ui.MaterInformationActivity;
import com.xzxx.decorate.o2o.view.CircularLayout;
import com.xzxx.decorate.o2o.view.SpiderWebScoreView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zf on 2018/8/12.
 * 工作信息页面
 */
public class WorkingInfoFragment extends BaseRequestFragment {

    private FlowLayout flowLayout;
    private SpiderWebScoreView spiderWebScoreView;
    private CircularLayout circularLayout;

    @Override
    protected void initView(View view) {
        flowLayout = view.findViewById(R.id.flow_layout_skill);

        spiderWebScoreView = view.findViewById(R.id.spiderScore);
        circularLayout = view.findViewById(R.id.circular);

        //        ((BaseRequestAcitity) getActivity()).showProgress(false);
    }

    public void showFlowText(JSONArray data) {
        flowLayout.removeAllViews();
        for (int i = 0; i < data.length(); i++) {
            try {
                int ranHeight = BasicUtils.dip2px(getContext(), 30);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ranHeight);
                lp.setMargins(BasicUtils.dip2px(getContext(), 20), 0, BasicUtils.dip2px(getContext(), 20), 0);
                TextView tv = new TextView(getContext());
                tv.setPadding(BasicUtils.dip2px(getContext(), 20), 0, BasicUtils.dip2px(getContext(), 20), 0);
                tv.setTextColor(Color.parseColor("#888888"));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tv.setText(data.getJSONObject(i).optString("skillName"));
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setLines(3);
                tv.setBackgroundResource(R.drawable.bg_flow_layout_tag);
                flowLayout.addView(tv, lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_working_info;
    }

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        if (request instanceof OrderMasterInfoRequest) {
            Log.i("123456", "data  " + "OrderMasterInfoRequest");
            //            ((BaseRequestAcitity) getActivity()).dismissProgress();
            JSONObject jsonObject = (JSONObject) data;
            try {
                ((MaterInformationActivity) getActivity()).initData(jsonObject);

                String masterGoodRate = jsonObject.optString("masterGoodRate", "0");
                String masterOverRate = jsonObject.optString("masterOverRate", "0");
                String masterScore = jsonObject.optString("masterScore", "0");

                ((TextView) getView().findViewById(R.id.masterGoodRate)).setText(MathUtils.str2Double(masterGoodRate) * 100 + "%");
                ((TextView) getView().findViewById(R.id.masterOverRate)).setText(String.format("超越%s的同行", MathUtils.str2Double(masterOverRate) * 100 + "%"));
                ((TextView) getView().findViewById(R.id.masterScore)).setText(masterScore);

                showFlowText(jsonObject.optJSONArray("skills"));
                JSONArray jsonArray = jsonObject.optJSONArray("masterItemInfos");
                if (jsonArray != null) {
                    ArrayList<Score> arrayList = new ArrayList<>();
                    //                    ArrayList<String> arrayListStr = new ArrayList<>();
                    //                    ArrayList<Integer> arrayListInt = new ArrayList<>();
                    int length = jsonArray.length();
                    if (length > 6) {
                        length = 6;
                    }

                    int max = 0;
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonItem = jsonArray.optJSONObject(i);
                        int num = jsonItem.optInt("secondItemNum");
                        String name = jsonItem.optString("secondItemName", "");
                        if (num > max) {
                            max = num;
                        }
                        //                        arrayListInt.add(num);
                        //                        arrayListStr.add(jsonItem.optString("secondItemName "));
                        arrayList.add(new Score(num, name));
                    }

                    while (arrayList.size() < 6) {
                        arrayList.add(new Score(0, ""));
                        //                        arrayListInt.add(0);
                        //                        arrayListStr.add("");
                    }

                    //设置师傅的评分
                    setup(max, spiderWebScoreView, circularLayout, arrayList.get(0), arrayList.get(1), arrayList.get(2), arrayList.get(3), arrayList.get(4), arrayList.get(5));
                }
                //                setup(spiderWebScoreView, circularLayout, new Score(jsonArray.optInt("secondItemNum ")), new Score(8.0f), new Score(5.0f), new Score(5.0f), new Score(8.0f), new Score(7.0f));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setup(int max, SpiderWebScoreView spiderWebScoreView, CircularLayout circularLayout, Score... scores) {
        spiderWebScoreView.setScores(max, assembleScoreArray(scores));

        circularLayout.removeAllViews();
        for (Score score : scores) {
            TextView scoreTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.score, circularLayout, false);
            scoreTextView.setText(String.valueOf(score.name));
            if (score.iconId != 0) {
                scoreTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, score.iconId, 0);
            }
            circularLayout.addView(scoreTextView);
        }
    }

    private float[] assembleScoreArray(Score... scores) {
        float[] scoreArray = new float[scores.length];
        for (int w = 0; w < scores.length; w++) {
            scoreArray[w] = scores[w].score;
        }
        return scoreArray;
    }

    private static class Score {
        private float score;
        private int iconId;
        private String name;

        private Score(float score, String name) {
            this.score = score;
            this.name = name;
        }
    }

    @Override
    public void onResume() {
        super.onResume();//201809281123670038750919
        String masterId = getArguments().getString("masterId", "");//
        masterId="201809281123670038750919";
        OrderMasterInfoRequest orderMasterInfoRequest = new OrderMasterInfoRequest();
        orderMasterInfoRequest.masterId = masterId;
        commitRequest(orderMasterInfoRequest);
    }
}

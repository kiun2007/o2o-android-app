package com.xzxx.decorate.o2o.ui;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amos.modulebase.utils.LogUtil;
import com.amos.modulebase.utils.MathUtils;
import com.amos.modulebase.utils.ScreenUtil;
import com.amos.modulebase.utils.picasso.PicassoUtil;
import com.amos.modulebase.widget.CustomScrollView;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.ui.views.FlowLayout;
import com.kiun.modelcommonsupport.utils.BasicUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xzxx.decorate.o2o.adapter.CommentListAdapter;
import com.xzxx.decorate.o2o.bean.MasterCommentInfoVo;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.MasterInfoCommentRequest;
import com.xzxx.decorate.o2o.requests.order.MasterInfoImpressRequest;
import com.xzxx.decorate.o2o.requests.order.OrderMasterInfoRequest;
import com.xzxx.decorate.o2o.view.CircularLayout;
import com.xzxx.decorate.o2o.view.SpiderWebScoreView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 师傅资料页面
 * Created by zf on 2018/6/19.
 */
public class MaterInformationActivity extends BaseActivity {

    private SmartRefreshLayout refresh_view;
    private TabLayout mTabLayout = null;
    private TabLayout mTabLayoutGone = null;
    private View view_tab_layout_gone = null;
    private int[] mTabTitles = new int[]{R.string.working_info, R.string.customer_evaluation};

    private ImageView img_logo;
    private TextView txt_name;
    private TextView masterProfession;
    private TextView masterSex;
    private TextView masterAge;
    private LinearLayout view_info;
    private LinearLayout view_comment;
    private CustomScrollView scroll_view;

    @Override
    public int getLayoutId() {
        return R.layout.layout_master_info;
    }

    @Override
    public void initView() {
        refresh_view = findViewById(R.id.refresh_view);
        img_logo = findViewById(R.id.img_logo);
        txt_name = findViewById(R.id.txt_name);
        masterProfession = findViewById(R.id.masterProfession);
        masterSex = findViewById(R.id.masterSex);
        masterAge = findViewById(R.id.masterAge);

        mTabLayout = findViewById(R.id.master_tab_layout);
        mTabLayoutGone = findViewById(R.id.master_tab_layout_gone);
        view_tab_layout_gone = findViewById(R.id.view_tab_layout_gone);
        view_info = findViewById(R.id.view_info);
        view_comment = findViewById(R.id.view_comment);
        scroll_view = findViewById(R.id.scroll_view);

        initWork();
        initComment();

        view_info.post(new Runnable() {
            @Override
            public void run() {
                view_info.getLayoutParams().height = ScreenUtil.getScreenHeightPx() - mTabLayout.getTop() + ScreenUtil.dip2px(45) + ScreenUtil.getStatusBarHeight() * 2;
            }
        });

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayoutGone.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < mTabTitles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(mTabTitles[i]), i);
            mTabLayoutGone.addTab(mTabLayoutGone.newTab().setText(mTabTitles[i]), i);
        }
        reflex(mTabLayout);
        reflex(mTabLayoutGone);

        TabLayout.OnTabSelectedListener selectedListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtil.i(tab.getPosition() + ".....0");
                switch (tab.getPosition()) {

                    case 0:
                        view_info.setVisibility(View.VISIBLE);
                        view_comment.setVisibility(View.GONE);
                        refresh_view.setEnableLoadMore(false);
                        mTabLayout.getTabAt(0).select();
                        mTabLayoutGone.getTabAt(0).select();
                        break;
                    case 1:
                        view_info.setVisibility(View.GONE);
                        view_comment.setVisibility(View.VISIBLE);
                        refresh_view.setEnableLoadMore(true);
                        mTabLayout.getTabAt(1).select();
                        mTabLayoutGone.getTabAt(1).select();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
        mTabLayout.addOnTabSelectedListener(selectedListener);
        mTabLayoutGone.addOnTabSelectedListener(selectedListener);

        refresh_view.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                onLoadMore1(false);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                onRefresh1(false);
            }
        });

        scroll_view.setOnScrollChangeListener(new CustomScrollView.OnScrollChangeListener() {
            @Override
            public void onChange(int x, int y, int oldx, int oldy) {
                float scrollY = scroll_view.getScrollY();
                int headContentHeight = ScreenUtil.dip2px(210);
                if (scrollY <= headContentHeight) {
                    if (view_tab_layout_gone.getVisibility() == View.VISIBLE) {
                        view_tab_layout_gone.setVisibility(View.GONE);
                    }
                    //                    mTabLayout.setVisibility(View.VISIBLE);
                } else {
                    if (view_tab_layout_gone.getVisibility() == View.GONE) {
                        view_tab_layout_gone.setVisibility(View.VISIBLE);
                        //                        view_tab_layout_gone.post(new Runnable() {
                        //                            @Override
                        //                            public void run() {
                        //                                //                                                ((LinearLayout.LayoutParams)mTabLayoutGone.getLayoutParams()).setMargins(0,mTabLayout.getTop(),0,0);
                        //                                view_tab_layout_gone.setVisibility(View.VISIBLE);
                        //                            }
                        //                        });
                    }

                }
                //                LogUtil.i(mTabLayout.getTop() + "   " + scrollY);
            }
        });

        view_info.setVisibility(View.VISIBLE);
        view_comment.setVisibility(View.GONE);
        refresh_view.setEnableLoadMore(false);
    }

    public void reflex(final TabLayout tabLayout) {
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    int dp = BasicUtils.dip2px(tabLayout.getContext(), 25);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);
                        TextView mTextView = (TextView) mTextViewField.get(tabView);
                        mTextView.setText(mTabTitles[i]);
                        tabView.setPadding(0, 0, 0, 0);
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dp;
                        params.rightMargin = dp;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        refresh_view.finishLoadMore();
        refresh_view.finishRefresh();
        onDataChangedWork(data, request);
        onDataChangedC(data, request);
    }

    public void initData(JSONObject jsonObject) {
        //                "masterName":"石先生",
        //                "masterHeadImg":"http://pawx04z5h.bkt.clouddn.com/2018/08/ae456325-399f-419f-afcd-df5ce3f28760.png",
        //                "masterProfession":"检修电路",
        //                "masterSex":"0",
        //                "masterBirthday":"19930721",
        String masterName = jsonObject.optString("masterName", "");
        String masterHeadImgStr = jsonObject.optString("masterHeadImg", "");
        String masterProfessionStr = jsonObject.optString("masterProfession", "");
        String masterSexStr = jsonObject.optString("masterSex", "").equals("0") ? "男" : "女";
        String masterBirthday = jsonObject.optString("masterBirthday", "");

        txt_name.setText(masterName);
        masterSex.setText(masterSexStr);
        masterProfession.setText(masterProfessionStr);
        if (!TextUtils.isEmpty(masterHeadImgStr)) {
            PicassoUtil.loadRoundImage(this, masterHeadImgStr, img_logo, 15 / 18f);
        }
        //        masterBirthday
        masterAge.setText(masterBirthday);
        if (TextUtils.isEmpty(masterBirthday)) {
            masterAge.setText("未知");
        } else {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                Date bithday = format.parse(masterBirthday);
                int age = getAgeByBirth(bithday);
                masterAge.setText(age + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                //                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                //                    age += 1;
                //                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    //*******************************************************
    private FlowLayout flowLayout;
    private SpiderWebScoreView spiderWebScoreView;
    private CircularLayout circularLayout;

    private void initWork() {
        flowLayout = findViewById(R.id.flow_layout_skill);
        spiderWebScoreView = findViewById(R.id.spiderScore);
        circularLayout = findViewById(R.id.circular);
    }

    public void onDataChangedWork(Object data, MCBaseRequest request) {
        if (request instanceof OrderMasterInfoRequest) {
            Log.i("123456", "data  " + "OrderMasterInfoRequest");
            //            ((BaseRequestAcitity) getActivity()).dismissProgress();
            JSONObject jsonObject = (JSONObject) data;
            try {
                initData(jsonObject);

                String masterGoodRate = jsonObject.optString("masterGoodRate", "0");
                String masterOverRate = jsonObject.optString("masterOverRate", "0");
                String masterScore = jsonObject.optString("masterScore", "0");

                int rate = (int) (MathUtils.str2Double(masterGoodRate) * 100);
                ((TextView) findViewById(R.id.masterGoodRate)).setText(rate + "%");
                int overRate = (int) (MathUtils.str2Double(masterOverRate) * 100);
                ((TextView) findViewById(R.id.masterOverRate)).setText(String.format("超越%d%%的同行", overRate));
                ((TextView) findViewById(R.id.masterScore)).setText(masterScore);

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

    public void showFlowText(JSONArray data) {
        flowLayout.removeAllViews();
        for (int i = 0; i < data.length(); i++) {
            try {
                int ranHeight = BasicUtils.dip2px(this, 30);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ranHeight);
                lp.setMargins(BasicUtils.dip2px(this, 20), 0, BasicUtils.dip2px(this, 20), 0);
                TextView tv = new TextView(this);
                tv.setPadding(BasicUtils.dip2px(this, 20), 0, BasicUtils.dip2px(this, 20), 0);
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

    private void setup(int max, SpiderWebScoreView spiderWebScoreView, CircularLayout circularLayout, Score... scores) {
        spiderWebScoreView.setScores(max, assembleScoreArray(scores));

        circularLayout.removeAllViews();
        for (Score score : scores) {
            TextView scoreTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.score, circularLayout, false);
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
        String masterId = "201809051530670018559756";
        //        masterId = "201809281123670038750919";
        OrderMasterInfoRequest orderMasterInfoRequest = new OrderMasterInfoRequest();
        orderMasterInfoRequest.masterId = masterId;
        commitRequest(orderMasterInfoRequest);
        onRefresh1(false);
    }

    //*******************************************************
    private FlowLayout flowLayoutC;
    private ListView list_view_evaluation;
    private View txt_nodata;
    private ArrayList<MasterCommentInfoVo> arrayList = new ArrayList<>();
    private CommentListAdapter adapter;

    private void initComment() {
        list_view_evaluation = findViewById(R.id.list_view_evaluation);
        txt_nodata = findViewById(R.id.txt_nodata);
        list_view_evaluation.addHeaderView(initHeader());
        adapter = new CommentListAdapter(this, arrayList);
        list_view_evaluation.setAdapter(adapter);
    }

    private View initHeader() {
        View view = View.inflate(this, R.layout.layout_customer_evaluation_header, null);
        flowLayoutC = view.findViewById(R.id.flow_layout_evaluation);
        return view;
    }

    public void onDataChangedC(Object data, MCBaseRequest request) {
        //        completeRefresh();
        if (request instanceof MasterInfoImpressRequest) {
            Log.i("123456", "data  " + "MasterInfoImpressRequest");
            if (data instanceof JSONArray) {
                JSONArray impressJSONArray = (JSONArray) data;
                showFlowTextC(impressJSONArray);
            } else {
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

            if (arrayList.size() <= 0) {
                list_view_evaluation.setVisibility(View.GONE);
                txt_nodata.setVisibility(View.VISIBLE);
            } else {
                list_view_evaluation.setVisibility(View.VISIBLE);
                txt_nodata.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
            //            initListView();
            //            list_view_evaluation.getLayoutParams().height = com.amos.modulebase.utils.ViewUtil.getListViewHeight(list_view_evaluation);
        }

        //        if ((impressJSONArray == null || impressJSONArray.length() < 1) && arrayList.size() <= 0) {
        //            LogUtil.i(".......................");
        //        }
    }

    MasterInfoImpressRequest orderMasterInfoRequest;
    MasterInfoCommentRequest masterInfoCommentRequest;

    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //    }

    public void showFlowTextC(JSONArray data) {
        flowLayoutC.removeAllViews();
        for (int i = 0; i < data.length(); i++) {
            try {
                int ranHeight = BasicUtils.dip2px(this, 30);
                int ranWidth = (ScreenUtil.getScreenWidthPx() - ScreenUtil.dip2px(30)) / 3 - ScreenUtil.dip2px(15);
                ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ranWidth, ranHeight);
                lp.setMargins(BasicUtils.dip2px(this, 7), 0, BasicUtils.dip2px(this, 7), 0);
                TextView tv = new TextView(this);
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

    //    @Override
    public void onRefresh1(boolean isSilence) {
        String masterId = "201809051530670018559756";
        //        masterId = "201809211507670005129171";
        orderMasterInfoRequest = new MasterInfoImpressRequest();
        orderMasterInfoRequest.masterId = masterId;
        commitRequest(orderMasterInfoRequest);
        masterInfoCommentRequest = new MasterInfoCommentRequest();
        masterInfoCommentRequest.masterId = masterId;
        masterInfoCommentRequest.pageNo = 1;
        commitRequest(masterInfoCommentRequest);
    }

    //    @Override
    public void onLoadMore1(boolean isSilence) {
        masterInfoCommentRequest.pageNo++;
        commitRequest(masterInfoCommentRequest);
    }

}

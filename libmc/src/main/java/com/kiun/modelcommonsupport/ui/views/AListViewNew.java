package com.kiun.modelcommonsupport.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kiun.modelcommonsupport.R;
import com.kiun.modelcommonsupport.adapter.AListAdapter;
import com.kiun.modelcommonsupport.adapter.CompositeAdapter;
import com.kiun.modelcommonsupport.adapter.ItemListener;
import com.kiun.modelcommonsupport.adapter.ListBaseAdapter;
import com.kiun.modelcommonsupport.adapter.ListItenListener;
import com.kiun.modelcommonsupport.adapter.SuperSimpleAdapter;
import com.kiun.modelcommonsupport.camera.util.ScreenUtils;
import com.kiun.modelcommonsupport.data.JSONExtractor;
import com.kiun.modelcommonsupport.data.KeyValue;
import com.kiun.modelcommonsupport.data.drive.DriveListener;
import com.kiun.modelcommonsupport.network.MCBaseRequest;
import com.kiun.modelcommonsupport.network.MCListRequest;
import com.kiun.modelcommonsupport.network.responses.MCUIResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by kiun_2007 on 2018/4/11.
 * 超级列表, 可以设置简单的适配器功能.
 */
public class AListViewNew extends ListView implements DriveListener, MCUIResponse {

    private static final String Tag = AListViewNew.class.getSimpleName();
    private String dataPath = null;
    private String modelClass = null;
    private int listItemLayout = 0;
    private int listSecondLayout = 0;
    String secondDataPath = null;
    private ListBaseAdapter aListAdapter = null;
    private ItemListener itemListener;
    private int bottomMargin = 0;
    private boolean isAutoHeight = false;
    private boolean isNoDataHide = false;

    public AListViewNew(Context context) {
        this(context, null);
    }

    public AListViewNew(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AListViewNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AListView);

        modelClass = array.getString(R.styleable.AListView_modelClass);
        dataPath = array.getString(R.styleable.AListView_dataPath);
        listItemLayout = array.getResourceId(R.styleable.AListView_simpleLayout, -1);
        listSecondLayout = array.getResourceId(R.styleable.AListView_secondLayout, -1);
        isAutoHeight = array.getBoolean(R.styleable.AListView_isAutoHeight, false);
        isNoDataHide = array.getBoolean(R.styleable.AListView_isNoDataHide, false);
        bottomMargin = array.getDimensionPixelOffset(R.styleable.AListView_bottomMargin, 0);
        secondDataPath = array.getString(R.styleable.AListView_secondPath);

        if (listItemLayout > -1 && listSecondLayout == -1) {
            if (modelClass != null) {
                aListAdapter = new SuperSimpleAdapter(this, listItemLayout, modelClass);
            } else {
                aListAdapter = new AListAdapter(this, listItemLayout);
            }
            this.setAdapter(aListAdapter);
        } else if (listItemLayout > -1 && listSecondLayout > -1) {

            if (secondDataPath != null) {
                aListAdapter = new CompositeAdapter(this,
                        listItemLayout, listSecondLayout, secondDataPath, array.getInteger(R.styleable.AListView_numColumns, 3));
                this.setAdapter(aListAdapter);
            }
        }
        if (isAutoHeight) {
            setVisibility(GONE);
        }
    }

    void hideSelf() {
        if (isNoDataHide) {
            this.setVisibility(this.getAdapter().getCount() == 0 ? View.GONE : View.VISIBLE);
        }
    }

    //    @Override
    //    protected void onAttachedToWindow() {
    //        super.onAttachedToWindow();
    //        ViewParent viewParent = this.getParent();
    //        if (viewParent instanceof XRefreshView) {
    //            mXRefreshView = (XRefreshView) viewParent;
    //            mXRefreshView.setXRefreshViewListener(this);
    //            mXRefreshView.setAutoLoadMore(true);
    //        }
    //        hideSelf();
    //    }
    //
    //    @Override
    //    protected void onDetachedFromWindow() {
    //        super.onDetachedFromWindow();
    //        if (mXRefreshView != null) {
    //            mXRefreshView.setXRefreshViewListener(null);
    //            mXRefreshView = null;
    //        }
    //    }
    //
    //    /**
    //     * 设置列表的请求.
    //     *
    //     * @param request
    //     *         列表请求.
    //     */
    //    public void setListRequest(MCListRequest request) {
    //        listRequest = request;
    //        listRequest.setResponse(new MCResponse(this));
    //        MCHttpCGI.defaultCenter().requestCGI(listRequest);
    //    }

    @Override
    public String getPath() {
        return dataPath;
    }

    @Override
    public void fill(Object data) {
        if (data instanceof JSONArray) {
            if (aListAdapter != null) {
                aListAdapter.fillItems(data);
            }
        }
        if (data instanceof String) {
            if (aListAdapter != null) {
                aListAdapter.clearAll();
            }
        }
        if (isAutoHeight) {
            setVisibility(VISIBLE);
            autoHeight();
        }
        hideSelf();
    }

    @Override
    public int eventTag() {
        return -1;
    }

    private void autoHeight() {
        ListAdapter listAdapter = this.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, this);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = totalHeight + (this.getDividerHeight() * (listAdapter.getCount() - 1));
        ((MarginLayoutParams) params).setMargins(0, 0, 0, bottomMargin);
        this.setLayoutParams(params);
    }

    @Override
    public KeyValue getParam() {
        return null;
    }

    TextView textViewHeader;

    @Override
    public void onDataChanged(Object data, MCBaseRequest request) {
        MCListRequest listRequest = (MCListRequest) request;

        if (this.itemListener instanceof ListItenListener) {
            ((ListItenListener) this.itemListener).onItemDataChanged(this, data);
        }

        if (data instanceof JSONObject && secondDataPath != null) {
            JSONExtractor extractor = new JSONExtractor(data);
            data = extractor.extract(secondDataPath);
        }

        if (aListAdapter != null) {
            if (listRequest.pageNo == 1) {
                aListAdapter.fillItems(data);
            } else {
                aListAdapter.addItems(data);
            }
        }

        if (data instanceof String && listRequest.pageNo == 1) {
            if (TextUtils.isEmpty((String) data)) {
                if (textViewHeader == null) {
                    textViewHeader = new TextView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth(getContext()));
                    params.gravity = Gravity.CENTER;
                    textViewHeader.setLayoutParams(params);
                    textViewHeader.setGravity(Gravity.CENTER);
                    textViewHeader.setText("暂时没有任何数据");
                    textViewHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                this.removeHeaderView(textViewHeader);
                this.addHeaderView(textViewHeader);
            }
        } else {
            if (textViewHeader != null) {
                this.removeHeaderView(textViewHeader);
            }
        }
    }

    @Override
    public void onBeginRequest() {
    }

    @Override
    public void onError(Error error) {

    }

    @Override
    public boolean isDead() {
        return false;
    }

    //    @Override
    //    public void onRefresh() {
    //        listRequest.pageNo = 1;
    //        MCHttpCGI.defaultCenter().requestCGI(listRequest);
    //    }
    //
    //    @Override
    //    public void onRefresh(boolean isPullDown) {
    //
    //    }
    //
    //    @Override
    //    public void onLoadMore(boolean isSilence) {
    //        if (getAdapter().getCount() > 0 && getAdapter().getCount() % listRequest.pageSize == 0) {
    //            listRequest.pageNo++;
    //            MCHttpCGI.defaultCenter().requestCGI(listRequest);
    //        } else {
    //            mXRefreshView.stopLoadMore();
    //        }
    //    }
    //
    //    @Override
    //    public void onRelease(float direction) {
    //
    //    }
    //
    //    @Override
    //    public void onHeaderMove(double headerMovePercent, int offsetY) {
    //    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
        if (aListAdapter != null) {
            aListAdapter.setListener(itemListener);
        }
    }

    //    @Override
    //    public void onRefresh(int tag) {
    //        onRefresh();
    //    }
}

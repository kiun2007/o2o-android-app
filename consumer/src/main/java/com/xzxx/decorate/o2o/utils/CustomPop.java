package com.xzxx.decorate.o2o.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amos.modulebase.utils.OtherUtils;
import com.kiun.modelcommonsupport.controllers.OrderButtonController;
import com.xzxx.decorate.o2o.consumer.R;
import com.xzxx.decorate.o2o.requests.order.OrderInfoRequest;
import com.xzxx.decorate.o2o.ui.ApplyAfterServiceActivity;
import com.xzxx.decorate.o2o.ui.ComplaintActivity;
import com.xzxx.decorate.o2o.ui.OrderDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * <p>
 * <br> Author: 叶青
 * <br> Version: 1.0.0
 * <br> Date: 2018/9/25
 * <br> Copyright: Copyright © 2018 xTeam Technology. All rights reserved.
 */
public class CustomPop {

    /** 上下文 */
    private Context mContext;
    /** 弹出窗口. */
    private PopupWindow mPopupWindow;
    private TextView action_material;
    private TextView action_circles;
    private TextView action_water_drop;
    private boolean isHideItem = false;

    /**
     * 构造方法
     *
     * @param context
     */
    public CustomPop(Context context, boolean isHideItem) {
        this.isHideItem = isHideItem;
        init(context);
    }

    /**
     * 初始化菜单
     */
    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.view_pop_content, null);

        action_material = view.findViewById(R.id.action_material);
        action_circles = view.findViewById(R.id.action_circles);
        action_water_drop = view.findViewById(R.id.action_water_drop);

        if (isHideItem) {
            action_material.setVisibility(View.GONE);
            action_water_drop.setVisibility(View.GONE);
        } else {
            action_material.setVisibility(View.VISIBLE);
            action_water_drop.setVisibility(View.VISIBLE);
        }


        action_material.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OtherUtils.getInstance().isFastClick(view)) return;

                if (mContext instanceof OrderDetailActivity) {
                    String orderId = ((OrderDetailActivity) mContext).getIntent().getStringExtra(OrderInfoRequest.ORDER_ID);
                    Intent complainIntent = new Intent(mContext, ApplyAfterServiceActivity.class);
                    complainIntent.putExtra(OrderInfoRequest.ORDER_ID, orderId);
                    mContext.startActivity(complainIntent);
                }
                dismiss();
            }
        });

        action_circles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (OtherUtils.getInstance().isFastClick(view)) return;
                if (mContext instanceof OrderDetailActivity) {
                    String orderId = ((OrderDetailActivity) mContext).getIntent().getStringExtra(OrderInfoRequest.ORDER_ID);
                    Intent complainIntent = new Intent(mContext, ComplaintActivity.class);
                    complainIntent.putExtra(OrderInfoRequest.ORDER_ID, orderId);
                    mContext.startActivity(complainIntent);
                }
                dismiss();
            }
        });

        action_water_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof OrderDetailActivity) {
                    String orderId = ((OrderDetailActivity) mContext).getIntent().getStringExtra(OrderInfoRequest.ORDER_ID);
                    JSONObject object = new JSONObject();
                    try {
                        object.put("orderId", orderId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((OrderDetailActivity) mContext).getButtonController().actionTag(((OrderDetailActivity) mContext), OrderButtonController.TAG_DEL, object);
                }
                dismiss();
            }
        });
        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
    }


    /**
     * 作为指定View的下拉控制显示.
     *
     * @param parent
     *         所指定的View
     */
    public void showAsDropDown(View parent) {
        mPopupWindow.showAsDropDown(parent);
    }

    /**
     * <p/>  作为指定View的下拉控制显示.
     *
     * @param parent
     *         所指定的View
     * @param xoff
     *         x坐标偏移
     * @param yoff
     *         y坐标偏移
     * @param gravity
     *         对齐方式
     */
    public void showAsDropDown(View parent, int xoff, int yoff, int gravity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPopupWindow.showAsDropDown(parent, xoff, yoff, gravity);
        } else {
            mPopupWindow.showAsDropDown(parent);
        }
    }

    /**
     * 隐藏菜单.
     */
    public void dismiss() {
        mPopupWindow.dismiss();
    }

    /**
     * 当前菜单是否正在显示.
     */
    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }
}

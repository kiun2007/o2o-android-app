package com.kiun.modelcommonsupport.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.amos.modulebase.utils.ScreenUtil;
import com.kiun.modelcommonsupport.R;

/**
 * @author konghui
 * @version 1.0
 * @ClassName MCActionSheet
 * @Description TODO
 * @date 2018-08-30 19:07
 **/
public class MCActionSheet extends Dialog {
    private View mContentView;

    public MCActionSheet(@NonNull Context context) {
        super(context);
    }

    public MCActionSheet(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MCActionSheet(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder {
        private Context context;
        private String[] titleItems;
        private String cancelTitle;
        private OnActionSheetSelectListener listener;
        private String type="";

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context, String type) {
            this.context = context;
            this.type = type;
        }

        public Builder setItems(String[] items) {
            this.titleItems = items;
            return this;
        }

        public Builder setCancelString(String cancel) {
            this.cancelTitle = cancel;
            return this;
        }

        public Builder setListener(OnActionSheetSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public MCActionSheet create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.mc_action_sheet, null);
            LinearLayout actionSheetItemContainer = view.findViewById(R.id.action_sheet_container);
            if(!TextUtils.isEmpty(type)){
                //1
                actionSheetItemContainer.setPadding(0,0,0,0);
            }
            final MCActionSheet mcActionSheet = new MCActionSheet(context, R.style.Dialog);
            if (titleItems != null) {
                for (int i = 0; i < titleItems.length; i++) {
                    final int position = i;
                    Button btn = (Button) inflater.inflate(R.layout.mc_action_sheet_item, null);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    btn.setText(titleItems[i]);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MCAnimationUtil.translateDown(context, view, new MCAnimationListener() {
                                @Override
                                public void animationFinish() {
                                    if (listener != null) {
                                        listener.onItemAction(mcActionSheet, position + 1);
                                    }
                                }
                            });
                        }
                    });
                    layoutParams.setMargins(0, 8, 0, 0);
                    if(!TextUtils.isEmpty(type)) {
                        layoutParams.setMargins(0, 1, 0, 0);
                        //2
                        btn.setLayoutParams(layoutParams);
                        btn.setTextColor(context.getResources().getColor(R.color.font_content));
                        btn.setBackgroundColor(context.getResources().getColor(R.color.white));
                    }
                    actionSheetItemContainer.addView(btn);
                }
            }

            Button btnCancel = view.findViewById(R.id.action_cancel);
            if(!TextUtils.isEmpty(type)) {
                //3
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnCancel.getLayoutParams();
                params.setMargins(0, ScreenUtil.dip2px(10), 0, 0);
                btnCancel.setTextColor(context.getResources().getColor(R.color.font_content));
                btnCancel.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MCAnimationUtil.translateDown(context, view, new MCAnimationListener() {
                        @Override
                        public void animationFinish() {
                            if (listener != null) {
                                listener.onItemAction(mcActionSheet, 0);
                            }
                        }
                    });
                }
            });

            Window window = mcActionSheet.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.getDecorView().setPadding(0, 0, 0, 0);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            mcActionSheet.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mcActionSheet.setCancelable(false);

            mcActionSheet.setmContentView(view);

            return mcActionSheet;
        }
    }

    @Override
    public void show() {
        super.show();
        MCAnimationUtil.translateUp(getContext(), mContentView);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public void setmContentView(View mContentView) {
        this.mContentView = mContentView;
    }

    public static void showActionSheet(Context context, String[] items, String cancel, OnActionSheetSelectListener listener) {
        MCActionSheet.Builder builder = new MCActionSheet.Builder(context);
        builder.setCancelString(cancel).setItems(items).setListener(listener);
        builder.create().show();
    }

    public static void showActionSheet(Context context, String[] items, String cancel,String type, OnActionSheetSelectListener listener) {
        MCActionSheet.Builder builder = new MCActionSheet.Builder(context,type);
        builder.setCancelString(cancel).setItems(items).setListener(listener);
        builder.create().show();
    }
}

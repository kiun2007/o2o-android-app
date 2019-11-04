package com.xzxx.decorate.o2o.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kiun.modelcommonsupport.data.drive.DriveBase;
import com.phillipcalvin.iconbutton.IconButton;
import com.xzxx.decorate.o2o.consumer.R;

/**
 * Created by zf on 2018/7/12.
 */
public class CommonDialog {

    public static void costDialog(Context context, Object data) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();
        alertDialog.setContentView(R.layout.dialog_order_cost_detail);

        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.white);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AlertDialog_AppCompat);

        DriveBase.fillViewData(window.getDecorView(), data, false);
        //window.getDecorView()
        final TextView closeText = window.findViewById(R.id.id_order_content_dialog_close);
        closeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}

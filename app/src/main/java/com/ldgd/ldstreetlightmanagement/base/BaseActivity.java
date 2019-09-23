package com.ldgd.ldstreetlightmanagement.base;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by ldgd on 2019/9/23.
 * 功能：
 * 说明：自定义Activity 基类
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected ProgressDialog mProgress;

    protected void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void stopProgress() {
        mProgress.cancel();
    }

    protected void showProgress() {
        mProgress = ProgressDialog.show(this, "", "Loading...");
    }

}

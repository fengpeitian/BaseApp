package com.fpt.base_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.fpt.base.util.AndroidUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        findViewById(R.id.bt_test).setOnClickListener(view -> test1());
    }

    private void test1() {

    }



















    /**
     * 申请权限
     */
    private void requestPermission() {
        AndroidUtils.checkPermission(new PermissionUtils.FullCallback() {
            @Override
            public void onGranted(List<String> permissionsGranted) { }

            @Override
            public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) { }
        }, PermissionConstants.STORAGE);
    }

}

package com.kiscode.okpermission;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 传统系统自带权限申请
 * * 1. 检查权限 ContextCompat.checkSelfPermission(this, Manifest.permission.XXX)
 * * 2. 请求权限 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION)
 * * 3. 权限处理结果回调  onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
 * * 4. 权限被用户禁止 提示用户需要权限的原因 shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
 * Author: keno
 */
public class OriginalPermissionSampleActivity extends AppCompatActivity {
    private static final int REQUESTCODE_CAMERA_PERMISSION = 297;
    private static final int REQUESTCODE_PERMISSION_SETTING = 297;
    private static final String TAG = "TraditionalPermission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traditional_permission_sample);

        findViewById(R.id.btn_open_camera).setOnClickListener(v -> requestCameraPermission());
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this
                , new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}
                , REQUESTCODE_CAMERA_PERMISSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult");

        if (requestCode == REQUESTCODE_CAMERA_PERMISSION) {
            List<String> defineList = new ArrayList<>();
            List<String> defineNotAskList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permission + " granted");
                } else if (shouldShowRequestPermissionRationale(permission)) {
                     /*
                        1. 应用按照后第一次访问、直接返回false
                        2. 第一次请求权限时用户拒绝、下一次返回true
                        3. 第二次请求权限时用户拒绝，并选择了“不再提醒”，返回false
                        4. 设备系统设置中禁止当前应用获取该权限的授权，返回false
                        */
                    defineList.add(permission);
                } else {
                    defineNotAskList.add(permission);
                }
            }

            if (defineList.isEmpty() && defineNotAskList.isEmpty()) {
                takePhoto();
                return;
            }

            if (!defineList.isEmpty()) {
                //再次申请权限
                new AlertDialog.Builder(OriginalPermissionSampleActivity.this)
                        .setMessage("拍照功能需要您同意相机和定位权限方可使用")
                        .setCancelable(false)
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("确定", (dialog, which) -> requestCameraPermission())
                        .create()
                        .show();
            } else {
                //再次申请权限
                new AlertDialog.Builder(OriginalPermissionSampleActivity.this)
                        .setMessage("您需要在设置中开启相机和定位权限")
                        .setCancelable(false)
                        .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("前往设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUESTCODE_PERMISSION_SETTING);
                        })
                        .create()
                        .show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_PERMISSION_SETTING) {
            Log.i(TAG, "resultCode:" + resultCode);
            requestCameraPermission();
        }
    }

    private void takePhoto() {
        String msg = "-------------------takePhoto-------------------";
        Log.i(TAG, "-------------------takePhoto-------------------");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
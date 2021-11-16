package com.kiscode.okpermission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 动态权限申请 官方Api展示
 * 1. 检查权限 ContextCompat.checkSelfPermission(this, Manifest.permission.XXX)
 * 2. 请求权限 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION)
 * 3. 权限处理结果回调  onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
 * 4. 权限被用户禁止 提示用户需要权限的原因 shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
 * Author: keno
 * CreateDate: 2020/8/30 9:59
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PERMISSION = 358;
    private static final int REQUEST_CODE_MULTIPLE_PERMISSION = 1358;
    private Button btnRequestSinglePermission;
    private Button btnRequestMultiplePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnRequestSinglePermission = findViewById(R.id.btn_request_single_permission);
        btnRequestMultiplePermission = findViewById(R.id.btn_request_multiple_permission);

        btnRequestSinglePermission.setOnClickListener(this);
        btnRequestMultiplePermission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_single_permission:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestSinglePermission();
                } else {
                    openCamera();
                }
                break;
            case R.id.btn_request_multiple_permission:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestMultipePermission();
                } else {
                    contactLocationTask();
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //success
                    Log.i(TAG, "onRequestPermissionsResult success");
                    openCamera();
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    //failure
                    Log.i(TAG, "onRequestPermissionsResult failure");
                    Toast.makeText(MainActivity.this, "onRequestPermissionsResult failure,can't use camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_MULTIPLE_PERMISSION:
                //
                List<String> needRequestPermissionList = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    Log.i(TAG, permissions[i] + " granted is:"
                            + (grantResults[i] == PackageManager.PERMISSION_GRANTED));
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        needRequestPermissionList.add(permissions[i]);
                    }
                }

                if (needRequestPermissionList.isEmpty()) {
                    Log.i(TAG, "onRequestPermissionsResult success,all granted");
                    //获取到所有权限
                    contactLocationTask();
                } else {
                    //未获取全部权限
                    Log.i(TAG, "onRequestPermissionsResult failure，未获取全部权限");
                }
                break;
        }
    }

    private void requestMultipePermission() {
//        int permissionResult =ContextCompat.checkSelfPermission(this,Manifest.permission_group)
        requestMultipePermissions(REQUEST_CODE_MULTIPLE_PERMISSION
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS});
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestSinglePermission() {
        int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionResult == PackageManager.PERMISSION_GRANTED) {
            openCamera();
            Log.i(TAG, "permission granted");
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            //提示用户请求权限的原因(当该权限被用户手动禁止后，或者点击取消)
            /*
            1. 应用按照后第一次访问、直接返回false
            2. 第一次请求权限时用户拒绝、下一次返回true
            3. 第二次请求权限时用户拒绝，并选择了“不再提醒”，返回false
            4. 设备系统设置中禁止当前应用获取该权限的授权，返回false
            */
            Log.i(TAG, "shouldShowRequestPermissionRationale");
            showRequestPermissionRationale();
        } else {
            requestCameraPermission();
        }
    }

    private void requestMultipePermissions(int requestCode, String[] permissions) {
        List<String> needRequestPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            //遍历检查权限组的权限，将未申请的权限加入集合中
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                needRequestPermissionList.add(permission);
            }
        }

        if (!needRequestPermissionList.isEmpty()) {
            int size = needRequestPermissionList.size();
            String[] needPermissions = needRequestPermissionList.toArray(new String[size]);
            ActivityCompat.requestPermissions(this, needPermissions, requestCode);
        } else {
            Log.i(TAG, "all PERMISSION_GRANTED");
        }
    }


    private void requestCameraPermission() {
        Log.i(TAG, "permission define,start requestPermissions");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION);
    }

    private void showRequestPermissionRationale() {
        new AlertDialog.Builder(this)
                .setMessage("The App Need Camera permission take photo")
                .setPositiveButton("Request Camera permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestCameraPermission();
                    }
                })
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void openCamera() {
        Log.i(TAG, "--------------------------openCamera--------------------------");
    }


    private void contactLocationTask() {
        Log.i(TAG, "--------------------------contactLocationTask--------------------------");
    }

}
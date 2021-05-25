package com.permission.kiscode;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.okpermission.kiscode.OkPermission;
import com.okpermission.kiscode.PermissionBuilder;

import java.util.Arrays;

/**
 * Description: OkPermission用法示例
 * Author: kisCode
 * Date : 2021/5/25 16:34
 **/
public class OkPermissionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "OkPermission";
    private static final String PHONE_NUMBER = "10086";
    private Button btnTakePhoto, btnCallPhone, btnMultiple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_permission);
        initView();
    }

    private void initView() {
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnCallPhone = findViewById(R.id.btn_call_phone);
        btnCallPhone = findViewById(R.id.btn_multiple_permission);

        btnTakePhoto.setOnClickListener(this);
        btnCallPhone.setOnClickListener(this);
        btnCallPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                OkPermission.with(this)
                        .permission(new String[]{Manifest.permission.CAMERA})
                        .request(new PermissionBuilder.OnPermissionRequestCallBack() {
                            @Override
                            public void onRequestPermissionsResult(boolean allGranted, @NonNull String[] grantPermissions, @NonNull String[] denyPermissions) {
                                if (allGranted) {
                                    takePhoto();
                                } else {
                                    Toast.makeText(OkPermissionActivity.this, "Deny permission " + Arrays.toString(denyPermissions), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                break;
            case R.id.btn_call_phone:
                OkPermission.with(this)
                        .permission(new String[]{Manifest.permission.CALL_PHONE})
                        .request((allGranted, grantPermissions, denyPermissions) -> {
                            if (allGranted) {
                                callPhone(PHONE_NUMBER);
                            } else {
                                Toast.makeText(OkPermissionActivity.this, "Deny permission " + Arrays.toString(denyPermissions), Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.btn_multiple_permission:
                //申请多个权限
                OkPermission.with(this)
                        .permission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,Manifest.permission.CAMERA})
                        .request(new PermissionBuilder.OnPermissionRequestCallBack() {
                            @Override
                            public void onRequestPermissionsResult(boolean allGranted, @NonNull String[] grantPermissions, @NonNull String[] denyPermissions) {
                                Log.i(TAG, "allGranted:" + allGranted
                                        + "\tgrantPermissions:" + Arrays.toString(grantPermissions)
                                        + "\tdenyPermissions:" + Arrays.toString(denyPermissions)
                                );
                            }
                        });
                break;
        }
    }

    private void callPhone(String phoneNum) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + phoneNum);
            intent.setData(data);
            startActivity(intent);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void takePhoto() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        } catch (SecurityException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

}
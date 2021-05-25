package com.permission.kiscode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Description: 官方用法示例
 * Author: kisCode
 * Date : 2021/5/25 16:34
 **/
public class PermissionSampleActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Permission";
    private static final int REQUEST_CODE_CALL_PHONE = 872;
    private static final int REQUEST_CODE_CAMERA = 846;
    private static final String PHONE_NUMBER = "10086";
    private Button btnTakePhoto;
    private Button btnCallPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnCallPhone = findViewById(R.id.btn_call_phone);

        btnTakePhoto.setOnClickListener(this);
        btnCallPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                }
                break;
            case R.id.btn_call_phone:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    callPhone(PHONE_NUMBER);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_CALL_PHONE);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CALL_PHONE) {
            if (grantResults != null && grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone(PHONE_NUMBER);
            } else {
                Toast.makeText(this, "You denied CALL_PHONE permission", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults != null && grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "You denied CAMERA permission", Toast.LENGTH_SHORT).show();
            }
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
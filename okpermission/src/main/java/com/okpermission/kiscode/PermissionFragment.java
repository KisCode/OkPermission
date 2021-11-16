package com.okpermission.kiscode;


import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Request permission by invisible fragment
 **/
public class PermissionFragment extends Fragment {

    public static final int REQUEST_CODE_PERMISSION = 10086;
    private OkPermission.PermissionBuilder.OnPermissionRequestCallBack onPermissionRequestCallBack;

    public static PermissionFragment getPermissionFragment() {
        return new PermissionFragment();
    }


    public void request(@NonNull String[] permissions,
                        OkPermission.PermissionBuilder.OnPermissionRequestCallBack onPermissionRequestCallBack) {
        this.onPermissionRequestCallBack = onPermissionRequestCallBack;

        if (hasPermission(permissions)) {
            onPermissionRequestCallBack.onRequestPermissionsResult(true, permissions, new String[]{});
        } else {
            requestPermissions(permissions, REQUEST_CODE_PERMISSION);
        }
    }

    private boolean hasPermission(@NonNull String[] permissions) {
        boolean hasPermission = true;
        for (String permission : permissions) {
            hasPermission &= ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (onPermissionRequestCallBack != null) {
            List<String> grantList = new ArrayList<>();
            List<String> denyList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    grantList.add(permissions[i]);
                } else {
                    denyList.add(permissions[i]);
                }
            }
            onPermissionRequestCallBack.onRequestPermissionsResult(grantList.size() == permissions.length
                    , grantList.toArray(new String[0])
                    , denyList.toArray(new String[0]));
        }
    }
}
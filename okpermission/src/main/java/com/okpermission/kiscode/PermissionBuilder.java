package com.okpermission.kiscode;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class PermissionBuilder {
    private static final String TAG_PERMISSION_FRAGMENT = "TAG_PERMISSION_FRAGMENT";

    private Fragment fragment;
    private FragmentActivity activity;
    private String[] permissions;


    public PermissionBuilder(Fragment fragment) {
        this.fragment = fragment;
    }

    public PermissionBuilder(FragmentActivity activity) {
        this.activity = activity;
    }

    public PermissionBuilder permission(String[] permissions) {
        this.permissions = permissions;
        return this;
    }


    /***
     * Request permission
     * @return PermissionBuilder itself
     */
    public void request(OnPermissionRequestCallBack callBack) {
        PermissionFragment permissionFragment = (PermissionFragment) getFragmentManager().findFragmentByTag(TAG_PERMISSION_FRAGMENT);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
            getFragmentManager().beginTransaction().add(permissionFragment, TAG_PERMISSION_FRAGMENT).commitNow();
        }
        permissionFragment.request(permissions, callBack);
    }

    private FragmentManager getFragmentManager() {
        if (fragment != null) {
            return fragment.getChildFragmentManager();
        } else {
            return activity.getSupportFragmentManager();
        }
    }

    public interface OnPermissionRequestCallBack {
        void onRequestPermissionsResult(boolean allGranted, @NonNull String[] grantPermissions, @NonNull String[] denyPermissions);
    }

}
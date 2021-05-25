package com.okpermission.kiscode;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class OkPermission {
    public static PermissionBuilder with(FragmentActivity activity) {
        return new PermissionBuilder(activity);
    }

    public static PermissionBuilder with(Fragment fragment) {
        return new PermissionBuilder(fragment);
    }

} 
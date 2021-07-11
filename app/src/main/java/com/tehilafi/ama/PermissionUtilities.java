package com.tehilafi.ama;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class PermissionUtilities {
    public static void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, requestStringPermissions(), 100);
    }

    public static String[] requestStringPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        return permissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String[] requestBackgroundPermissino(){
        String[] permission = {Manifest.permission.ACCESS_BACKGROUND_LOCATION};
        return permission;
    }
}

package com.flavordemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.facebook.react.ReactActivity;
import com.huawei.hms.api.HuaweiMobileServicesUtil;

public class HMSActivity extends ReactActivity {

    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };

    public static final String TAG = "MainActivity map";
    public static final int REQUEST_CODE = 234;

    @Override
    protected String getMainComponentName() {
        return "flavorDemo";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate:hzj");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "is MobileServicesAvailable "+ HuaweiMobileServicesUtil.isHuaweiMobileServicesAvailable(this));
        if (!hasPermissions(this, RUNTIME_PERMISSIONS)) {
            Log.d(TAG, "Missing permissions: ");
            ActivityCompat.requestPermissions(this, RUNTIME_PERMISSIONS, REQUEST_CODE);
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "missing permission" + " " + permission);
                    return false;
                }
            }
        }
        return true;
    }

}

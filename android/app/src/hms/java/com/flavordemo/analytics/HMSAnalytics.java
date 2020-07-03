package com.flavordemo.analytics;

import android.os.Bundle;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.flavordemo.utils.JsonUtils;
import com.huawei.hianalytics.hms.HiAnalyticsTools;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;



public class HMSAnalytics extends ReactContextBaseJavaModule {

    private HiAnalyticsInstance instance;

    public HMSAnalytics(ReactApplicationContext reactContext) {
        super(reactContext);
        HiAnalyticsTools.enableLog();
        instance = HiAnalytics.getInstance(reactContext);
    }

    @Override
    public String getName() {
        return "HMSAnalytics";
    }

    @ReactMethod
    public void logEvent(String eventName, String bundleString) {
        Bundle bundle = JsonUtils.jsonStringToBundle(bundleString);
        instance.onEvent(eventName, bundle);
        Toast.makeText(getReactApplicationContext(), "eventName: " + eventName + " bundle: " + bundleString, Toast.LENGTH_SHORT).show();
    }
}

package com.flavordemo.packages;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.flavordemo.account.HMSLogin;
import com.flavordemo.analytics.HMSAnalytics;
import com.flavordemo.iap.HMSIap;
import com.flavordemo.map.HMSMapLocation;
import com.flavordemo.map.MapViewManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HMSPackages implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList();
        modules.add(new HMSLogin(reactContext));
        modules.add(new HMSAnalytics(reactContext));
        modules.add(new HMSMapLocation(reactContext));
        modules.add(new HMSIap(reactContext));
        return modules;
    }

    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.asList(
                new MapViewManager()
        );
    }
}
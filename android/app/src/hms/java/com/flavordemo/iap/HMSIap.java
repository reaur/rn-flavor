package com.flavordemo.iap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.flavordemo.utils.JsonUtils;
import com.huawei.hianalytics.hms.HiAnalyticsTools;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapClient;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;

import org.json.JSONException;

import java.util.ArrayList;


public class HMSIap extends ReactContextBaseJavaModule {

    private ReactApplicationContext mContext;
    private Activity activity;

    public HMSIap(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
    }

    @Override
    public String getName() {
        return "HMSIap";
    }

    @ReactMethod
    public void getProducts() {
        activity = getCurrentActivity();
        loadProduct();
    }

    private void loadProduct() {
        IapClient iapClient = Iap.getIapClient(activity);
        Task<ProductInfoResult> task = iapClient.obtainProductInfo(createProductInfoReq());
        task.addOnSuccessListener(result -> {
            if (result != null && !result.getProductInfoList().isEmpty()) {
                for (ProductInfo productInfo : result.getProductInfoList()) {
                    Toast.makeText(activity, productInfo.getProductName(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show());
    }

    private ProductInfoReq createProductInfoReq() {
        ProductInfoReq req = new ProductInfoReq();
        req.setPriceType(IapClient.PriceType.IN_APP_CONSUMABLE);
        ArrayList<String> productIds = new ArrayList();
        productIds.add("product1");
        productIds.add("product2");
        req.setProductIds(productIds);
        return req;
    }

}

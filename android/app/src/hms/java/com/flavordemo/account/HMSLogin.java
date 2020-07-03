package com.flavordemo.account;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.flavordemo.consts.Constant;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;



public class HMSLogin extends ReactContextBaseJavaModule {
    private ReactApplicationContext mContext;
    private Promise mPromise;
    private Activity activity;

    public static final String TAG = "HMSLogin";

    private HuaweiIdAuthService mAuthManager;
    private HuaweiIdAuthParams mAuthParam;


    public HMSLogin(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mContext = reactContext;
        this.mContext.addActivityEventListener(mActivityEventListener);
        mAuthParam = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
    }

    @Override
    public String getName() {
        return "HMSLogin";
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN) {
                Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
                if (authHuaweiIdTask.isSuccessful()) {
                    AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                    Toast.makeText(getReactApplicationContext(), huaweiAccount.getDisplayName() + " signIn success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getReactApplicationContext(), "Failed To signIn", Toast.LENGTH_LONG).show();
                }
            }
            if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN_CODE) {
                Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
                if (authHuaweiIdTask.isSuccessful()) {
                    AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                    Toast.makeText(getReactApplicationContext(), "ServerAuthCode " + huaweiAccount.getAuthorizationCode(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getReactApplicationContext(), "Failed To signIn", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @ReactMethod
    public void Login(Promise promise) {
        mPromise = promise;
        activity = getCurrentActivity();

        mAuthManager = HuaweiIdAuthManager.getService(activity, mAuthParam);

        if (checkActivity(activity, promise)) {
            try {
                activity.startActivityForResult(mAuthManager.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
            } catch (Exception e) {
                promise.reject("START_ACTIVITY_ERROR", "START_ACTIVITY_ERROR");
                promise = null;
            }
        }
    }

    @ReactMethod
    public void Logout() {
        if (checkActivity(activity, null)) {
            Task<Void> signOutTask = mAuthManager.signOut();
            signOutTask.addOnSuccessListener(aVoid -> Toast.makeText(getReactApplicationContext(), "Your HMS Account has log out Success", Toast.LENGTH_LONG).show()).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getReactApplicationContext(), "Your HMS Account has log out ERROR", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean checkActivity(Activity activity, Promise promise) {
        if (activity == null && promise != null) {
            promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist");
        }
        return activity != null;
    }

}
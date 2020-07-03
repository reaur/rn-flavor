package com.flavordemo.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.SettingsClient;

/**
 * service wrapper around HMS Location Kit
 */
public class LocationService {

    public static final String TAG = "LocationService";
    public static final int PERMISSION_REQ_CODE = 1324;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;

    private ReactContext context;


    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    interface OnLocationUpdate {
        void onLocationUpdate(Location location);
    }


    private OnLocationUpdate onLocationUpdate;



    LocationService(ReactContext context, OnLocationUpdate onLocationUpdate) {
        this.context = context;
        this.onLocationUpdate = onLocationUpdate;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * requests runtime permissions to get the users location
     */
    void requestPermissions() {
        Log.d(TAG, "requestPermissions");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context.getCurrentActivity() != null) {
            context.getCurrentActivity().requestPermissions(permissions, PERMISSION_REQ_CODE);
        }
    }

    /**
     * check if the use has granted this app the permissions to get his location
     * @return
     */
    private boolean checkPermissions() {
        Log.d(TAG, "checkPermissions");

        boolean allGranted = true;
        for (String permission : permissions) {
            if (context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                allGranted = false;
            }
        }
        Log.d(TAG, "Permissions granted");
        return allGranted;
    }

    /**
     * location callback to be invoked when there is a new map updates
     */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            Log.d(TAG, "is location available: ${p0.isLocationAvailable}");
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                for (Location l : locationResult.getLocations()) {
                    Log.d(TAG, "got location : " + l.getLatitude() + ", " + l.getLongitude());
                    onLocationUpdate.onLocationUpdate(l);
                }
            }

        }
    };

    /**
     * initials and register callbacks
     */
    private void init() {
        Log.d(TAG, "init");

        if (!this.checkPermissions()) {
            return;
        }

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest ll = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        settingsClient.checkLocationSettings(ll).addOnSuccessListener(locationSettingsResponse -> {
            Log.d(TAG, "checkLocationSettings ");
            listenToLocationUpdates();
        }).addOnFailureListener(e -> Log.d(TAG, "checkLocationSettings failed " + e.toString()));

    }

    /**
     * request last location and invoke the callback
     * @param onLocationUpdate
     */
    public void getLastLocation(OnLocationUpdate onLocationUpdate) {
        Log.d(TAG, "getLastLocation");
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> onLocationUpdate.onLocationUpdate(location));
    }

    void listenToLocationUpdates() {
        Log.d(TAG, "listenToLocationUpdates");
        if (locationRequest == null) {
            this.init();
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper()).addOnSuccessListener(res -> {
            Log.d(TAG, "fused LOCATION UPDATE SUCCESS ");
        }).addOnFailureListener(error -> {
            Log.d(TAG, "fused LOCATION UPDATE FAILED " + error.toString());
        });
    }

    /**
     * removes the callbacks
     */
    public void stopListeningToLocationUpdates() {
        Log.d(TAG, "stopListeningToLocationUpdates");
        if (locationRequest == null) {
            return;
        }

        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

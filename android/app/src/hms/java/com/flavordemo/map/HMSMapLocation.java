//  Created by react-native-create-bridge

package com.flavordemo.map;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


import java.util.HashMap;
import java.util.Map;


public class HMSMapLocation extends ReactContextBaseJavaModule {
    public static final String TAG = "HMSMapLocation";
    private static ReactApplicationContext reactContext = null;
    public static final String EVENT_LOCATION_UPDATE = "LOCATION_UPDATE";

    private LocationService locationService;

    public HMSMapLocation(ReactApplicationContext context) {
        // Pass in the context to the constructor and save it so you can emit events
        super(context);
        locationService = new LocationService(context, this::emitLocationUpdate);
        locationService.requestPermissions();
        reactContext = context;
    }

    /**
     * @return unique name of this module
     */
    @Override
    public String getName() {
        // Tell React the name of the module
        return "HsmLocation";
    }

    /**
     * return map of constants to be used in the javascript side
     * @return
     */
    @Override
    public Map<String, Object> getConstants() {
        // Export any constants to be used in your native module
        final Map<String, Object> constants = new HashMap<>();
        constants.put("EVENT_LOCATION_UPDATE", "LOCATION_UPDATE");
        constants.put("LATITUDE", "lat");
        constants.put("LONGITUDE", "lng");

        return constants;
    }

    /**
     * return last known location if any,
     */
    @ReactMethod
    public void getLastLocation() {
        Log.d(TAG , "getLastLocation");
        //todo: not yet implemented!
    }

    /**
     * request location updates from the locationService, location will be emitted
     * with event named: {@link #EVENT_LOCATION_UPDATE}
     */
    @ReactMethod
    public void requestLocationUpdates() {
        Log.d(TAG, "requestLocation");
        this.locationService.listenToLocationUpdates();
    }

    /**
     * emits a location value when new location update is received from the location service
     * @param position the new user's location to be emitted
     */
    public void emitLocationUpdate(Location position) {
        WritableMap map = Arguments.createMap();
        map.putDouble("lat", position.getLatitude());
        map.putDouble("lng", position.getLongitude());
        //map.putDouble("accuracy",position.getAccuracy());
        //map.putString("provider",position.getProvider());
        emitDeviceEvent(EVENT_LOCATION_UPDATE, map);
    }

    /**
     * send the event to react native
     * @param eventName the name of the event
     * @param eventData the values that will received in the javascript side
     */
    private static void emitDeviceEvent(String eventName, @Nullable WritableMap eventData) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, eventData);
    }
}

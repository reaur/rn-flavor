//  Created by react-native-create-bridge

package com.flavordemo.map;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.flavordemo.R;
import com.flavordemo.utils.Utils;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.Circle;
import com.huawei.hms.maps.model.CircleOptions;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.LatLngBounds;
import com.huawei.hms.maps.model.MarkerOptions;



public class MapViewManager extends SimpleViewManager<View> implements OnMapReadyCallback {
    public static final String REACT_CLASS = "MapBridge";

    public static final String EVENT_MARKER_CLICKED = "MARKER_CLICKED";
    public static final String EVENT_MAP_READY = "MAP_READY";
    public static final String TAG = "MapBridge map ";

    private static final boolean ENABLE_COMPASS = true;
    private static final boolean ENABLE_ZOOM_CONTROLS = true;

    /*
    public static final String STATE_COMPONENT_DID_MOUNT = "mount";
    public static final String STATE_COMPONENT_DID_UNMOUNT = "unmount";
    private static final long FADE_IN_DURATION = 2000;//2 seconds
    */

    private MapView mapView;
    private HuaweiMap huaweiMap;
    private BitmapDescriptor bitmapDescriptor;

    private LocationService locationService;
    private Circle currentLocation;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public int getId() {
        return mapView.getId();
    }


    @Override
    public View createViewInstance(ThemedReactContext context) {
        mapView = (MapView) LayoutInflater.from(context).inflate(R.layout.map, null, false);
        mapView.getMapAsync(this);
        mapView.onCreate(null);
        // onMount(mapView,true);
        mapView.onStart();
        mapView.onResume();
        mapView.setOnClickListener(v -> {
            Log.d(TAG, "map view clicked!!!");
        });
        locationService = new LocationService(context, this::onLocationUpdate);
        return mapView;
    }

    /**
     * NOT USED!
     * links component's lifecycle with the maps lifecycle, for better performance
     * called when components state is changed
     *
     * @param view      instance of the mapView
     * @param isMounted the state of the hosting component
     */
    @ReactProp(name = "isMounted")
    public void onMount(MapView view, boolean isMounted) {

        if (true)
            return;
        //this code leads to unexpected behaviour, requires debugging
        if (isMounted) {
            mapView.onResume();
        } else {
            mapView.onPause();
        }
    }


    /**
     * register a long press listener
     */
    public void setOnLongPress() {
        huaweiMap.setOnMapLongClickListener(this::createMarker);
    }

    /**
     * create a marker at certain position with default title, snippet and the default icon
     *
     * @param position location of the marker
     */
    public void createMarker(LatLng position) {
        if (null != bitmapDescriptor)
            createMarker(-1, position, " title! ", "created from long press");
        else
            Toast.makeText((ReactContext) mapView.getContext(), "Map is not ready yet", Toast.LENGTH_LONG).show();
    }

    /**
     * create a marker and adds it to the map
     *
     * @param id       the order of the marker, used for navigating the marker from javascript
     * @param position position of the marker
     * @param title    title of the marker to be send back to javascript side
     * @param snippet  marker description to be sent back to javascript side
     */
    public void createMarker(int id, LatLng position, String title, String snippet) {
        MarkerOptions m = new MarkerOptions();

        m.position(position);
        m.title(title);
        m.snippet(snippet);

        m.icon(bitmapDescriptor);
        huaweiMap.addMarker(m).setTag(id);

    }


    /**
     * registers a click listener (callback) to be invoked when a marker is clicked
     */
    public void setOnMarkerClick() {

        huaweiMap.setOnMarkerClickListener(marker -> {

            sendMarkerClickedEvent((int) marker.getTag(),
                    marker.getPosition().latitude,
                    marker.getPosition().longitude,
                    marker.getTitle(),
                    marker.getSnippet());

            return false;
        });
    }

    /**
     * list of marker received from react native to be drawn on the map
     *
     * @param mapView instance of the mapView
     * @param markers list of marker to be received when the component's state in changed on the react native side
     */
    @ReactProp(name = "markers")
    public void setMarkers(MapView mapView, ReadableArray markers) {

        if (null != bitmapDescriptor) {
            Log.d(TAG, "map markers, data length: " + markers.size());
            if (markers.size() == 0) {
                return;
            }
            ReadableMap marker = null;
            huaweiMap.clear();

            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

            for (int i = 0; i < markers.size(); i++) {
                marker = markers.getMap(i);
                if (marker == null) continue;
                double lat = marker.getDouble("lat");
                double lng = marker.getDouble("lng");

                createMarker(marker.getInt("id"),
                        new LatLng(lat, lng),
                        marker.getString("title"),
                        marker.getString("snippet"));

                bounds.include(new LatLng(lat, lng));
            }
            huaweiMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 8));
        } else
            Toast.makeText((ReactContext) mapView.getContext(), "Map is not ready yet", Toast.LENGTH_LONG).show();

        // selectMarker(null , marker);
    }

    /**
     * Preparing the marker icon from url
     */
    @ReactProp(name = "markerIcon")
    public void setMarkers(MapView mapView, String iconLink) {
        if (iconLink != null) {
            new Thread(() -> {
                try {
                    bitmapDescriptor = Utils.markerIconFromUrl(iconLink);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * animate the camera to move to a selected marker
     *
     * @param mapView an instance of the MapView
     * @param marker  marker object received from react native when the component's state changes,
     *                needed the get the location where the camera will move
     */
    @ReactProp(name = "selectedMarker")
    public void selectMarker(MapView mapView, ReadableMap marker) {
        if (huaweiMap == null) {
            return;
        }
        Log.d(TAG, "selectedMarker, ");

        double lat = marker.getDouble("lat");
        double lng = marker.getDouble("lng");

        huaweiMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
    }

    /**
     * @param mapView             an instance of the mapView
     * @param showCurrentLocation the state from javascript that tells whether to show the current
     *                            location on map or not
     */
    @ReactProp(name = "showCurrentLocation")
    public void showCurrentLocation(MapView mapView, boolean showCurrentLocation) {
        Log.d(TAG, "showCurrentLocation " + (showCurrentLocation ? "true " : "false "));

        huaweiMap.setMyLocationEnabled(true);


//        locationService.getLastLocation(l -> {
//
//        });
//        if (showCurrentLocation) {
//            locationService.listenToLocationUpdates();
//        } else {
//            locationService.stopListeningToLocationUpdates();
//        }
    }

    /**
     * Not used, replaced with existing MyLocation feature in HuaweiMap {@link HuaweiMap#setMyLocationEnabled(boolean)}
     * gets called when there is a new location update but only when the
     * method {@link  #showCurrentLocation(MapView, boolean)} is called first with value true
     *
     * @param location the user's new location
     */
    public void onLocationUpdate(Location location) {
        Log.d(TAG, "onLocationUpdate " + location.getLatitude() + " ,  " + location.getLongitude());
//        createMarker(-1, new LatLng(location.getLatitude(), location.getLatitude()),
//                "You", "this shows your current location");
//

        if (currentLocation == null) {
            currentLocation = huaweiMap.addCircle(
                    new CircleOptions().center(
                            new LatLng(location.getLatitude(), location.getLongitude())).radius(100)
            );
            Log.d(TAG, "currentLocation, added circle");
        } else {
            currentLocation.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
        }


    }

    /**
     * get called when the map is ready to be manipulated (draw markers...)
     *
     * @param huaweiMap instance of huawei map
     */
    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        Log.d(TAG, "map OnMapReady ++++++++++++++++++++++++++++++++");
        this.huaweiMap = huaweiMap;


        this.huaweiMap.getUiSettings().setCompassEnabled(ENABLE_COMPASS);
        this.huaweiMap.getUiSettings().setZoomControlsEnabled(ENABLE_ZOOM_CONTROLS);

        setOnLongPress();
        setOnMarkerClick();
        sendEventMapReady();
    }


    /**
     * send the MAP_READY event to javascript side
     */
    public void sendEventMapReady() {
        WritableMap event = Arguments.createMap();

        event.putString("eventName", EVENT_MAP_READY);

        Log.d(TAG, "sendMarkerClickEvent");
        System.out.println("asdfafdasfdasdfasdfa map marker clicked");
        Log.d(TAG, "Map native element clicked");

        ReactContext reactContext = (ReactContext) mapView.getContext();

        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event);
    }

    /**
     * send the MARKER_CLICKED event to the javascript side
     *
     * @param id      the order of the marker in the array list
     * @param lat     latitude of the marker
     * @param lng     longitude of the marker
     * @param title   title of the marker
     * @param snippet snippet of the marker
     */
    public void sendMarkerClickedEvent(int id, double lat, double lng, String title, String snippet) {
        WritableMap event = Arguments.createMap();

        event.putString("eventName", EVENT_MARKER_CLICKED);

        event.putDouble("lat", lat);
        event.putDouble("lng", lng);
        event.putString("title", title);
        event.putString("snippet", snippet);
        event.putInt("id", id);

        Log.d(TAG, "sendMarkerClickEvent");
        System.out.println("asdfafdasfdasdfasdfa map marker clicked");
        Log.d(TAG, "Map native element clicked");

        ReactContext reactContext = (ReactContext) mapView.getContext();

        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                getId(),
                "topChange",
                event);
    }
}

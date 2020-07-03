package com.flavordemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;

import java.net.URL;

public class Utils {
    public static final String TAG = "map Utils";
    public static final int MARKER_WIDTH= 75, MARKER_HEIGHT=120;

    /**
     * downloads the bitmap from the link and scaling it down
     * @param link link to image (marker icon)
     * @return bitmapDescriptor to be used by the map the show the icon as marker in the map
     */
    public static BitmapDescriptor markerIconFromUrl(String link) {
        Log.d(TAG, "Utils > bitmapFromUrl "+link );
        if("default".equals(link)){
            return null ;
        }
        try {//dimens : 250 * 235
            URL url = new URL(link);
            Bitmap b = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            //b = Bitmap.createScaledBitmap(b, MARKER_WIDTH, MARKER_HEIGHT, false);
            b = resize(b , MARKER_WIDTH,MARKER_HEIGHT);

            Log.d(TAG , "Utils > bitmap info "+ b.getByteCount() + " " + b.getHeight() + "X" + b.getWidth() );
           return BitmapDescriptorFactory.fromBitmap(b);
        } catch (Exception e) {
            Log.d(TAG  , "failed to get bitmap " +  e.toString());
        }
        return null;
    }

    /**
     * resize a map while keeping the aspect ratio, the image dimension will equal
     * or be smaller than the specified dimension
     * @param image the bitmap to be resized
     * @param maxWidth target width
     * @param maxHeight target height
     * @return new scaled down bitmap
     */
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

}

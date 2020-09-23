package com.tehilafi.ama;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class Common {
    public static final String KEY_REQUESTING_LOCATION_UPDATES="LocationUpdateEnable";

    public static String GetLocayionText(Location mLocation) {
        return mLocation==null?"Unknown Location":new StringBuilder().append(mLocation.getAltitude()).append("/").append(mLocation.getLongitude()).toString();
    }

    public static CharSequence GetLocayionTitle(MyBackgroundService myBackgroundService) {
        return String.format("Location update: %1$s", DateFormat.getInstance().format(new Date()));
    }

    public static void setLocationRequestingLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
               .edit()
               .putBoolean(KEY_REQUESTING_LOCATION_UPDATES,value)
                .apply();

    }

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_REQUESTING_LOCATION_UPDATES,false);
    }
}

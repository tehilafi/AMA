package com.tehilafi.ama;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListViewAdapte extends ArrayAdapter<ListView_item> {

    private Context context;
    private int mResource;

    public ListViewAdapte(@NonNull Context context, int resource, @NonNull ArrayList<ListView_item> objects) {
        super( context, resource, objects );
        this.context = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView imageView = convertView.findViewById(R.id.profileuserID);
        TextView textdate = convertView.findViewById(R.id.dateID);
        TextView textdes = convertView.findViewById(R.id.desID);

        imageView.setImageResource(getItem(position).getImage());
        textdate.setText(getItem(position).getDate());
        textdes.setText(getItem(position).getDes());

        return convertView;
    }

    public static class Common {
        public static final String KEY_REQUESTING_LOCATION_UPDATES = "LocationUpdateEnable" ;

        public static String getLocationText(Location mLocation) {
            return mLocation == null ? "Unknown Location" : new StringBuilder().append(mLocation.getLatitude()).append( "/").append(mLocation.getLongitude()).toString();
        }

        public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService) {
            return String.format("Location update: %1$s", DateFormat.getDateInstance().format(new Date()));
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

    public static class SendLocationToActivity {
        private Location location;

        public SendLocationToActivity(Location location){
            this.location = location;
        }
        public Location getLocation(){
            return location;
        }
        public void setLocation(Location location){
            this.location = location;
        }
    }
}

package com.tehilafi.ama;

import android.location.Location;

public class sendLocationToActivity {
    private Location location;

    public sendLocationToActivity(Location location)
    {
        this.location=location;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

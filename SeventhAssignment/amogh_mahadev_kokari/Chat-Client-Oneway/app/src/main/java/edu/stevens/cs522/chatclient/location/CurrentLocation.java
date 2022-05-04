package edu.stevens.cs522.chatclient.location;

import android.content.Context;

import edu.stevens.cs522.chatclient.R;

public class CurrentLocation {

    private final Double latitude;

    private final Double longitude;

    private CurrentLocation(Context context) {
        this.latitude = Double.parseDouble(context.getString(R.string.latitude));
        this.longitude = Double.parseDouble(context.getString(R.string.longitude));
    }

    public static CurrentLocation getLocation(Context context) {
        return new CurrentLocation(context);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

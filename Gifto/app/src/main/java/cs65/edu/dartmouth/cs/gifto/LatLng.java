package cs65.edu.dartmouth.cs.gifto;

import java.io.Serializable;

/**
 * Created by jesst_000 on 2/26/2018.
 *
 * a LatLng with a default constructor
 * Using "transient" wouldn't work, because the data isn't sent to the database
 * (i.e. if you have a class that stores a fullsize image and a thumbnail version,
 * you can label the thumbnail as transient because you don't need to save both to the database)
 *
 * this LatLng has a method which easily converts it to a google LatLng, if you need it
 */

public class LatLng implements Serializable{
    double latitude;
    double longitude;

    public LatLng(){}

    public LatLng(double lat, double lng){
        latitude = lat;
        longitude = lng;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public com.google.android.gms.maps.model.LatLng toGoogleLatLng(){
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }
}

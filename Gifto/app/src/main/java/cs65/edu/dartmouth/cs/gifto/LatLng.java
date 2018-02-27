package cs65.edu.dartmouth.cs.gifto;

/**
 * Created by jesst_000 on 2/26/2018.
 */

public class LatLng {
    public double latitude;
    public double longitude;


    LatLng(){

    }

    LatLng(double lat, double lng){
        latitude = lat;
        longitude = lng;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public com.google.android.gms.maps.model.LatLng toGoogleLatLng(){
        return new com.google.android.gms.maps.model.LatLng(latitude, longitude);
    }
}

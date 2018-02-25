package cs65.edu.dartmouth.cs.gifto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;                     // google map
    private LocationManager lm;                 // finds last known location
    private String provider;                    // also used to find last known location
    private Marker currentMarker;               // user's current location

    int size = 0;   // number of points in locList. used for restoreInstanceState

    Context appContext;         // for keeping the service running
    SharedPreferences pref;     // stores units_int and other things
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // save things during orientation change
        mapFragment.setRetainInstance(true);

        // see which units to use
        pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        initLocationManager();  // get last known location

        mapFragment.setRetainInstance(true);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if ( Build.VERSION.SDK_INT > 23 && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        Location location = lm.getLastKnownLocation(provider);    // so you have default location
        LatLng firstMarker;
        if(location != null){
            firstMarker = new LatLng(location.getLatitude(), location.getLongitude());
        } else{
            // Add a marker in Hanover if no last location found
            firstMarker = new LatLng(43.7022, -72.2896);
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(firstMarker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstMarker,17));
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void onDestroy() {
        lm.removeUpdates(this);
        currentMarker.remove();
        super.onDestroy();
    }

    // user has chosen to save this entry to the database
    public void saveToDatabase(final Context c){
        new Thread(new Runnable(){
            //private Handler handler = StartActivity.mHandler;
            public void run(){
//                // talk to database
//                DataSource dataSource = new DataSource(c);
//                dataSource.openDb();
//                long id = dataSource.insertEntry(entry);
//                dataSource.closeDb();
            }
        }).start();
    }

    // use this to get last known location of user (to improve UX)
    public void initLocationManager() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // String provider = lm.GPS_PROVIDER;	// only use GPS. dangerous if user turn GPS off
        // better option
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    // can also get bearings, alt, etc.
        provider = lm.getBestProvider(criteria, true);    // only get GPS if it's on
    }

    // save all the info if the screen is rotated
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    // get info that was saved before screen rotation
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // update the user's current location
        if (currentMarker != null) currentMarker.remove();
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        // move camera to this position
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        Log.d("Jess", latLng.latitude + "," + latLng.longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}


package cs65.edu.dartmouth.cs.gifto;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;                     // google map
    private LocationManager lm;                 // finds last known location
    private String provider;                    // also used to find last known location
    private Marker currentMarker;               // user's current location
    private ArrayList<Marker> gifts;
    private ArrayList<Gift> giftObjects;
    private DatabaseReference giftsData;

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

        gifts = new ArrayList<>();
        giftsData = Util.databaseReference.child("gifts");

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

        if (Build.VERSION.SDK_INT > 23 && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        Location location = lm.getLastKnownLocation(provider);    // so you have default location
        LatLng firstMarker;
        if (location != null) {
            firstMarker = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            // Add a marker in Hanover if no last location found
            firstMarker = new LatLng(43.7022, -72.2896);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                Log.d("Jess", "onMapClick");
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Place a gift here?")
                        .setPositiveButton("YES!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // TODO: change test gift into real gift
                                /*
                                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                                Toast.makeText(getApplicationContext(), "Choose animal to deliver gift", Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                */
                                // add gift to global gift database
                                Calendar c = Calendar.getInstance();
                                MapGift gift = new MapGift("fish", Util.userID, Util.name, "test message", "cat", new cs65.edu.dartmouth.cs.gifto.LatLng(latLng.latitude, latLng.longitude), c.getTimeInMillis());
                                giftsData.push().setValue(gift);
                                // add gift to your map
                                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.gift_icon)));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d("Jess", "onMarkerClick");
                float results[] = new float[1];
                Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude(), results);
                if(results[0] < 50) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                // remove gift from database everyone can see,
                                // and into your personal database
                                    giftsData.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                double lat = snapshot.child("location").child("latitude").getValue(Double.class);
                                                double lng = snapshot.child("location").child("longitude").getValue(Double.class);
                                                if(lat == marker.getPosition().latitude && lng == marker.getPosition().longitude) {
                                                    String giftName = snapshot.child("giftName").getValue(String.class);
                                                    String friendName = snapshot.child("userName").getValue(String.class);
                                                    Log.d("Jess", "gift data so far: " + giftName + ", " + friendName);
                                                    long time = snapshot.child("timePlaced").getValue(Long.TYPE);
                                                    cs65.edu.dartmouth.cs.gifto.LatLng location = new cs65.edu.dartmouth.cs.gifto.LatLng(lat, lng);
                                                    Gift gift = new Gift(giftName, true, friendName, time, location);
                                                    // move to user's database
                                                    Util.databaseReference.child("users").child(Util.userID).child("gifts").push().setValue(gift);
                                                    // delete from public database
                                                    giftsData.child(snapshot.getKey()).removeValue();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                    // remove gift from your map
                                    marker.remove();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            //need a title for setIcon to work
                            .setMessage("Pick up gift?")
                            // TODO: place icon of the specific gift here
                            // get picture of gift using something stored in database
                            .setIcon(R.drawable.gift_icon);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "You are too far away to pick up this gift", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });

        // default if no photo of animal found
        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
        // display a gift or animal on the map instead
        //if(...) icon = BitmapDescriptorFactory.fromResource(R.drawable./*name of png */);
        //currentMarker = mMap.addMarker(new MarkerOptions().position(firstMarker).icon(icon));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 17));

        // display pre-existing gifts on the map
        giftsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Gift gift = snapshot.getValue(Gift.class);
                    // TODO: determine which gift icon to use. Using generic marker for now
                    MarkerOptions markerOptions = new MarkerOptions().position(gift.getLocation().toGoogleLatLng()).icon(BitmapDescriptorFactory.fromResource(R.drawable.gift_icon));
                    //MarkerOptions markerOptions = new MarkerOptions().position(gift.getLocation().toGoogleLatLng()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Marker marker = mMap.addMarker(markerOptions);
                    gifts.add(marker);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    public void onDestroy() {
        lm.removeUpdates(this);
        //currentMarker.remove();
        super.onDestroy();
    }

    // user has chosen to save this entry to the database
    public void saveToDatabase(final Context c) {
        new Thread(new Runnable() {
            //private Handler handler = StartActivity.mHandler;
            public void run() {
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // get info that was saved before screen rotation
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("Jess", latLng.latitude + "," + latLng.longitude);
        // update the user's current location
        //if (currentMarker != null) currentMarker.remove();
        //currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        // move camera to this position
        //Log.d("Jess", latLng.latitude + "," + latLng.longitude);
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


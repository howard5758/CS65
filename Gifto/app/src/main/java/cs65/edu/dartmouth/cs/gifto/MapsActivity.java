package cs65.edu.dartmouth.cs.gifto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;                     // google map
    private LocationManager lm;                 // finds last known location
    private String provider;                    // also used to find last known location
    private Marker currentMarker;               // user's current location
    private ArrayList<Marker> gifts;
    private ArrayList<Gift> giftObjects;
    private DatabaseReference giftsData;
    private LatLng savedLatLng;

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

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Gifto");

        //navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
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
                                Intent intent = new Intent(getBaseContext(), GiftChooser.class);
                                savedLatLng = latLng;
                                startActivityForResult(intent, 1);
                                // add gift to global gift database
//                                Calendar c = Calendar.getInstance();
//                                MapGift gift = new MapGift("fish", Util.userID, Util.name, "test message", "cat", new cs65.edu.dartmouth.cs.gifto.LatLng(latLng.latitude, latLng.longitude), c.getTimeInMillis());
//                                giftsData.push().setValue(gift);
//                                // add gift to your map
//                                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.gift_icon)));
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    final String[] nickname = {"null"};
                    final String message[] = {"no message"};
                    giftsData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                double lat = snapshot.child("location").child("latitude").getValue(Double.class);
                                double lng = snapshot.child("location").child("longitude").getValue(Double.class);
                                if (lat == marker.getPosition().latitude && lng == marker.getPosition().longitude) {
                                    nickname[0] = snapshot.child("userNickname").getValue(String.class);
                                    message[0] = snapshot.child("message").getValue(String.class);
                                    builder.setMessage(nickname[0] + ": " + message[0]);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
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
                            .setTitle("Pick up gift?")
                            .setIcon(R.drawable.gift_icon);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String giftName = data.getStringExtra("giftName");
                String animalName =data.getStringExtra("animalName");
                String message = data.getStringExtra("message");
                LatLng latLng = savedLatLng;

                Calendar c = Calendar.getInstance();
                MapGift gift = new MapGift(giftName, Util.userID, Util.name, message, animalName, new cs65.edu.dartmouth.cs.gifto.LatLng(latLng.latitude, latLng.longitude), c.getTimeInMillis());
                giftsData.push().setValue(gift);
                // add gift to your map
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.gift_icon)));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_garden) {
            finish();
        } else if (id == R.id.nav_map) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        NavigationView navigationView = findViewById(R.id.nav_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                View headerView = navigationView.getHeaderView(0);
                if (profile.getDisplayName() != null && !profile.getDisplayName().equals("")) {
                    TextView tv = headerView.findViewById(R.id.nav_header_text);
                    tv.setText(profile.getDisplayName());
                }
                if (profile.getPhotoUrl() != null) {
                    ImageView navImage = headerView.findViewById(R.id.nav_image);
                    navImage.setImageURI(profile.getPhotoUrl());
                }
            }
        }
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, UserProfile.class));
            return true;
        } else if (id == R.id.action_logout) {
            Util.firebaseAuth.signOut();
            Util.showActivity(this, LoginActivity.class);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
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


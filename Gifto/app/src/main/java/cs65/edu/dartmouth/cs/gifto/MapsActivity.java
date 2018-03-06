package cs65.edu.dartmouth.cs.gifto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.microedition.khronos.opengles.GL;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;                     // google map
    private LocationManager lm;                 // finds last known location
    private String provider;                    // also used to find last known location
    private ArrayList<Marker> gifts;
    private ArrayList<MapGift> giftList;
    private DatabaseReference giftsData;
    private LatLng savedLatLng;
    private Location location;
    private MySQLiteHelper helper;
    public MapGift gift;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // save things during orientation change
        mapFragment.setRetainInstance(true);

        helper = new MySQLiteHelper(this);

        gifts = new ArrayList<>();
        giftsData = Util.databaseReference.child("gifts");
        giftList = helper.fetchAllMapGifts();

        // see which units to use
        //pref = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

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

        checkpermissions();
    }

    public void checkpermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (Build.VERSION.SDK_INT > 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was not granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        initLocationManager();  // get last known location
                        location = lm.getLastKnownLocation(provider);    // so you have default location
                        mMap.setMyLocationEnabled(true);
                    }
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_DENIED) {
                    }
                }
            break;
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng firstMarker;
        if (location != null) {
            firstMarker = new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            // Add a marker in Hanover if no last location found
            firstMarker = new LatLng(43.7022, -72.2896);
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            initLocationManager();  // get last known location
            location = lm.getLastKnownLocation(provider);    // so you have default location
            mMap.setMyLocationEnabled(true);
        }

        // allow users to place gift anywhere
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                Log.d("Jess", "onMapClick");
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Place a gift here?")
                        .setPositiveButton("YES!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getBaseContext(), GiftChooser.class);
                                // remember where to put the gift
                                savedLatLng = latLng;
                                // open gift creation activity
                                startActivityForResult(intent, 1);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // cancel
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // allow user to pick up gifts
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Log.d("Jess", "onMarkerClick");
                float results[] = new float[1];
                // compare gift's location to user's location
                // user has to be within certain distance of gift to pick it up
                if(ContextCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_DENIED || !mMap.isMyLocationEnabled()){
                    Toast.makeText(getBaseContext(), "You must enable location tracking to pick up gifts", Toast.LENGTH_SHORT).show();
                } else if(mMap.getMyLocation() != null) {
                    Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude(), results);
                    if (results[0] < 50) {
                        // user is close enough to pick up gift
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        // store as array because we need the listener to be able to access/change it
                        String nickname;
                        String message;
                        String username = Util.userID;
                        for(MapGift gift_i: giftList){
                            if(gift_i.getLocation().latitude == marker.getPosition().latitude && gift_i.getLocation().longitude == marker.getPosition().longitude){
                                nickname = gift_i.getUserNickname();
                                message = gift_i.getMessage();
                                gift = gift_i;
                                if (username.equals(Util.userID)) {
                                    builder.setTitle("Remove your gift?");
                                } else {
                                    builder.setTitle("Pick up gift?");
                                }
                                builder.setMessage(nickname + ": " + message);
                                int box_num = gift.getGiftBox();
                                String box = Globals.INT_TO_BOX.get(box_num);
                                int id;
                                if((id = Util.getImageIdFromName(box)) == Util.getImageIdFromName("")) id = R.drawable.gift_icon;
                                builder.setIcon(id);
                                break;
                            }
                        }
                        // user decided to pick up gift
                        builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    String giftName = gift.getGiftName();
                                    String friendName = gift.getUserNickname();
                                    long time = gift.getTimePlaced();
                                    Gift gift_new = new Gift(giftName, true, friendName, time, gift.getLocation());
                                    gift.setGiftBox(gift.getGiftBox());
                                    helper.insertGift(gift_new, true);
                                    helper.removeMapGift(gift.getId());
                                    if(Globals.ITEM_TO_TYPE.get(giftName) != null){
                                            InventoryItem item = helper.fetchinventoryItemByName(giftName);
                                            if(item == null) item = new InventoryItem(giftName, 0);
                                            //item.setItemAmount(item.getItemAmount()+1);
                                            helper.insertInventory(item, true);
                                            //helper.close();
                                    }
                                    if(giftName != null && !giftName.equals("")){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                                            builder.setTitle("Congratulations!")
                                                    .setMessage("You received a " + giftName)
                                                    .setPositiveButton("Yay!", null)
                                                    .setIcon(Util.getImageIdFromName(giftName));
                                            builder.show();
                                    }
                                    giftList.remove(gift);
                                // remove gift from your map
                                marker.remove();
                            }
                        });
                        // user decides to not pick up this gift
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing other than closing the alert dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You are too far away to pick up this gift", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        // useful google built-in functions to determine user location and interact with map
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

        // zoom in on user's current location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 17));
    }

    private void personal_gift_popup(MapGift value1) {
        final MapGift value = value1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    if (value.getGiftName() == null || value.getGiftName().equals(""))
                        builder.setTitle("You've received a message!");
                    else {
                        builder.setTitle("You've received a " + value.getGiftName());
                        builder.setIcon(Util.getImageIdFromName(value.getGiftName()));
                        MySQLiteHelper helper = new MySQLiteHelper(getBaseContext());
                        if (Globals.ITEM_TO_TYPE.get(value.getGiftName()) != null) {
                            InventoryItem item = helper.fetchinventoryItemByName(value.getGiftName());
                            if(item.getItemName() == null || item.getItemName().equals("")) {
                                item = new InventoryItem();
                                item.setItemName(value.getGiftName());
                                item.setItemAmount(1);
                                item.setPresent(-1);
                            }
                            helper.insertInventory(item, true);
                        }
                    }
                    builder.setMessage(value.getUserName() + ": " + value.getMessage())
                            .setPositiveButton("OK!", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    helper.removeMapGift(value.getId());
                    Gift gift = new Gift(value.getGiftName(), true, value.getSendTo(), value.getTimePlaced(), value.getLocation());
                    gift.setGiftBox(value.getGiftBox());
                    helper.insertGift(gift, true);
                }
            }
        });
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

    // user is placing a gift
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String giftName = data.getStringExtra("giftName");
                String animalName =data.getStringExtra("animalName");
                String message = data.getStringExtra("message");
                String sendTo = data.getStringExtra("sendTo");
                LatLng latLng = savedLatLng;

                Calendar c = Calendar.getInstance();
                Random rand = new Random();
                int randomNum = rand.nextInt((Globals.ANIMAL_TO_BOX_LIST.get(animalName).size() - 1) + 1);
                String giftbox_name = Globals.ANIMAL_TO_BOX_LIST.get(animalName).get(randomNum);
                int giftbox = 0;
                for(int i=0; i<Globals.INT_TO_BOX.size(); i++) {
                    if(Globals.INT_TO_BOX.get(i) == giftbox_name) {
                        giftbox = i;
                        break;
                    }
                }
                String receiver;
                if(sendTo == null || sendTo.equals(""))  receiver = null;
                else {
                    receiver = sendTo;
                    Toast.makeText(getBaseContext(), "Gift sent to " + receiver, Toast.LENGTH_SHORT).show();
                }
                MapGift gift = new MapGift(giftName, Util.email, Util.name, message, animalName, new cs65.edu.dartmouth.cs.gifto.LatLng(latLng.latitude, latLng.longitude), c.getTimeInMillis(), receiver, giftbox);
                helper.insertMapGift(gift);
                giftList.add(gift);
                // remove inventory object from sql and from firebase
                MySQLiteHelper helper = new MySQLiteHelper(this);
                if(Globals.ITEM_TO_TYPE.get(giftName) != null) {
                    InventoryItem item = helper.fetchinventoryItemByName(giftName);
                    item.setItemAmount(item.getItemAmount() - 1);
                    helper.insertInventory(item, true);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // don't add anything to the map
            }
        }
    }//onActivityResult

    // user has selected item from navbar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_garden) {
            // user has to have come from the garden. So, finish this activity to return to garden
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

        // display user's nickname and profile pic in navbar
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
        // map must be the item selected if you are in this navbar
        // see "MainActivity" comments for more details
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    public void onAttachedToWindow(){
        giftsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("sendTo").getValue() == null){
                    cs65.edu.dartmouth.cs.gifto.LatLng latLng = dataSnapshot.child("location").getValue(cs65.edu.dartmouth.cs.gifto.LatLng.class);
                    int id;
                    if(dataSnapshot.child("giftBox").getValue(Integer.class) != null) {
                        if ((id = Util.getImageIdFromName(Globals.INT_TO_BOX.get(dataSnapshot.child("giftBox").getValue(Integer.class)))) == Util.getImageIdFromName(""))
                            id = R.drawable.gift_icon;
                    } else id = R.drawable.gift_icon;
                    Marker marker = mMap.addMarker(new MarkerOptions().position(latLng.toGoogleLatLng()).icon(BitmapDescriptorFactory.fromResource(id)));
                    gifts.add(marker);
                    giftList.add(dataSnapshot.getValue(MapGift.class));
                } else if(Util.email.equals(dataSnapshot.child("sendTo").getValue(String.class))){
                    personal_gift_popup(dataSnapshot.getValue(MapGift.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                cs65.edu.dartmouth.cs.gifto.LatLng latLng = dataSnapshot.child("location").getValue(cs65.edu.dartmouth.cs.gifto.LatLng.class);
                Marker marker_found = null;
                for(Marker marker : gifts){
                    if(marker.getPosition().latitude == latLng.latitude && marker.getPosition().longitude == latLng.longitude){
                        marker_found = marker;
                        marker.remove();
                    }
                }
                if(marker_found != null) gifts.remove(marker_found);
                giftList.remove(dataSnapshot.getValue(MapGift.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // display pre-existing gifts on the map
        giftList = helper.fetchAllMapGifts();
        for(MapGift gift: giftList){
            if(gift.getSendTo() == null) {
                int id;
                if((id = Util.getImageIdFromName(Globals.INT_TO_BOX.get(gift.getGiftBox()))) == Util.getImageIdFromName("")) id = R.drawable.gift_icon;
                MarkerOptions markerOptions = new MarkerOptions().position(gift.getLocation().toGoogleLatLng()).icon(BitmapDescriptorFactory.fromResource(id));
                Marker marker = mMap.addMarker(markerOptions);
                gifts.add(marker);
            }else if(Util.email.equals(gift.getSendTo())){
                personal_gift_popup(gift);
            }
        }
    }

    // allow user to access settings and logout from map
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

    // if back pressed and drawer open, close drawer
    // otherwise, close map
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
}


package cs65.edu.dartmouth.cs.gifto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;                     // google map
    private LocationManager lm;                 // finds last known location
    private String provider;                    // also used to find last known location
    private ArrayList<Marker> gifts;            // list of markers currently on the map
    private ArrayList<MapGift> giftList;        // the gifts corresponding to those markers
    private DatabaseReference giftsData;        // the firebase with all the gifts in it
    private LatLng savedLatLng;                 // location user wants to put a gift
    private Location location;                  // used to estimate where the googlemaps camera should zoom
    private MySQLiteHelper helper;              // used to access sql
    public MapGift gift;                        // used when picking up gifts

    // initialize
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // save things during orientation change
        mapFragment.setRetainInstance(true);

        // get ready to access database
        helper = new MySQLiteHelper(this);

        // load all the map gifts at the beginning, and save them in array
        // so you don't need to loop through database again
        gifts = new ArrayList<>();
        giftsData = Util.databaseReference.child("gifts");
        giftList = helper.fetchAllMapGifts();

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

        // check if you can use the user's location.
        // Limit what the user can do if they deny permission
        checkpermissions();
    }

    // check if you can use the user's location
    public void checkpermissions(){
        if(Build.VERSION.SDK_INT < 23)
            return;
        if (Build.VERSION.SDK_INT > 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    }

    // initialize things which use your location, if the user granted permission
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        initLocationManager();  // get last known location
                        location = lm.getLastKnownLocation(provider);    // so you have default location
                        mMap.setMyLocationEnabled(true);    // listens to user's movements
                    } // not granted.
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
//
//        if(ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
//            initLocationManager();  // get last known location
//            location = lm.getLastKnownLocation(provider);    // so you have default location
//            //mMap.setMyLocationEnabled(true);
//        }

        // allow users to place gift anywhere
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                Log.d("Jess", "onMapClick");
                // ask user if they want to place a gift where they've clicked
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setMessage("Place a gift here?")
                        .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
                            // start giftchooser activity
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

                // make the alert dialog prettier
                TextView messageView = dialog.findViewById(android.R.id.message);
                messageView.setGravity(Gravity.CENTER_HORIZONTAL);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dialog.getButton(AlertDialog.BUTTON_POSITIVE).getLayoutParams();
                lp.weight = 1;
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(lp);
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(lp);
                View leftspacer = ((LinearLayout)dialog.getButton(AlertDialog.BUTTON_POSITIVE).getParent()).getChildAt(1);
                leftspacer.setVisibility(View.GONE);

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
                    if (results[0] < 100) {
                        // user is close enough to pick up gift
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        // display message and who sent it in the alert
                        String nickname;
                        String message;
                        String username;
                        // figure out which gift the user has selected,
                        // by comparing the marker's location to the gifts' locations
                        for(MapGift gift_i: giftList){
                            if(gift_i.getLocation().latitude == marker.getPosition().latitude && gift_i.getLocation().longitude == marker.getPosition().longitude){
                                nickname = gift_i.getUserNickname();
                                // if user hasn't set a nickname for themselves yet,
                                // display their username instead
                                if(nickname == null || nickname.equals("")){
                                    nickname = gift_i.getUserName();
                                }
                                message = gift_i.getMessage();
                                username = gift_i.getUserName();
                                // remember which gift the user clicked on.
                                // this will allow the alert dialog button click listener to know this information
                                // without having to loop through the gift list again
                                gift = gift_i;
                                // you are the one who placed this gift
                                if (username.equals(Util.userID)) {
                                    builder.setTitle("Remove your gift?");
                                } // someone else placed this gift
                                else {
                                    builder.setTitle("Pick up gift?");
                                } // display the gift's message in the alert
                                builder.setMessage(nickname + ": " + message);
                                // which gift box image should be used in the icon
                                int box_num = gift.getGiftBox();
                                String box = Globals.INT_TO_BOX.get(box_num);
                                // if the app has been updated, and you can't find an image associated with this gift
                                // use a default image instead
                                int id;
                                if((id = Util.getImageIdFromName(box)) == Util.getImageIdFromName("")) id = R.drawable.gift_icon;
                                builder.setIcon(id);
                                // only pick up one gift
                                break;
                            }
                        }
                        // user will click this if they want to pick up the gift
                        builder.setPositiveButton("YES!", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    // get information to be stored in the gift history
                                    String giftName = gift.getGiftName();
                                    String friendName = gift.getUserName();
                                    long time = gift.getTimePlaced();
                                    Gift gift_new = new Gift(giftName, true, friendName, time, gift.getLocation());
                                    gift_new.setGiftBox(gift.getGiftBox());
                                    // update the gift history (both in sql and firebase)
                                    helper.insertGift(gift_new, true);
                                    // don't let this gift get picked up again
                                    helper.removeMapGift(gift.getId());
                                    // update the user's inventory, if they've received an item in addition to the message
                                    if(Globals.ITEM_TO_TYPE.get(giftName) != null){
                                            InventoryItem item = helper.fetchinventoryItemByName(giftName);
                                            // if the user has never bought/received this item before,
                                            // add a new InventoryItem to their database
                                            if(item.getItemName().equals("")) item = new InventoryItem(giftName, 0);
                                            item.setPresent(-1);
                                            // keep track of how many of this item the user has
                                            helper.removeInventoryItem(item.getItemName());
                                            item.setItemAmount(item.getItemAmount() + 1);
                                            helper.insertInventory(item, true);
                                    }
                                    // only show this alert if there was an item inside the gift
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
                        // user will click this if they decide to not pick up gift
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // do nothing other than closing the alert dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        // make the alert pretty
                        TextView messageView = dialog.findViewById(android.R.id.message);
                        messageView.setGravity(Gravity.CENTER_HORIZONTAL);
                        TextView titleView = dialog.findViewById(android.R.id.title);
                        if(titleView != null) titleView.setGravity(Gravity.CENTER_HORIZONTAL);
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dialog.getButton(AlertDialog.BUTTON_POSITIVE).getLayoutParams();
                        lp.weight = 1;
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(lp);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(lp);
                        View leftspacer = ((LinearLayout)dialog.getButton(AlertDialog.BUTTON_POSITIVE).getParent()).getChildAt(1);
                        leftspacer.setVisibility(View.GONE);
                    } // you were too far away to pick up the gift
                    else {
                        Toast.makeText(getApplicationContext(), "You are too far away to pick up this gift", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        // useful google built-in functions to determine user location and interact with map
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);

        // zoom in on user's current location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(firstMarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(firstMarker, 17));
    }

    // gets called if someone has sent a message specifically to you
    // (rather than just putting it on the map)
    private void personal_gift_popup(MapGift value1) {
        // final so thread can access it
        final MapGift value = value1;
        // prevents popup from being called at inopportune time
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    // user has received message
                    if (value.getGiftName() == null || value.getGiftName().equals(""))
                        builder.setTitle("You've received a message!");
                    // user has received an item (and maybe a message)
                    else {
                        builder.setTitle("You've received a " + value.getGiftName());
                        builder.setIcon(Util.getImageIdFromName(value.getGiftName()));
                        MySQLiteHelper helper = new MySQLiteHelper(getBaseContext());
                        // get a picture of the item they received
                        if (Globals.ITEM_TO_TYPE.get(value.getGiftName()) != null) {
                            // if user has never owned this item before, fetchinventoryitem will return null
                            // this if statement handles that case
                            InventoryItem item = helper.fetchinventoryItemByName(value.getGiftName());
                            if(item.getItemName() == null || item.getItemName().equals("")) {
                                item = new InventoryItem();
                                item.setItemName(value.getGiftName());
                                item.setItemAmount(1);
                                item.setPresent(-1);
                            } // add one more instance of this item to user's inventory
                            helper.insertInventory(item, true);
                        }
                    }
                    String user = value.getUserNickname();
                    if(user == null || user.equals("")) user = value.getUserName();
                    builder.setMessage(user + ": " + value.getMessage())
                            .setPositiveButton("OK!", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // again, make it pretty
                    TextView messageView = dialog.findViewById(android.R.id.message);
                    messageView.setGravity(Gravity.CENTER_HORIZONTAL);
                    TextView titleView = dialog.findViewById(android.R.id.title);
                    if(titleView != null) titleView.setGravity(Gravity.CENTER_HORIZONTAL);
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) dialog.getButton(AlertDialog.BUTTON_POSITIVE).getLayoutParams();
                    lp.weight = 1;
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setLayoutParams(lp);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(lp);
                    View leftspacer = ((LinearLayout)dialog.getButton(AlertDialog.BUTTON_POSITIVE).getParent()).getChildAt(1);
                    leftspacer.setVisibility(View.GONE);

                    // update firebase, letting it know gift was received
                    // and put this gift in the user's gift history
                    helper.removeMapGift(value.getId());
                    Gift gift = new Gift(value.getGiftName(), true, value.getUserName(), value.getTimePlaced(), value.getLocation());
                    gift.setGiftBox(value.getGiftBox());
                    helper.insertGift(gift, true);
                }
            }
        });
    }

    // use this to get last known location of user (to improve UX)
    public void initLocationManager() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // better option
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);    // can also get bearings, alt, etc.
        provider = lm.getBestProvider(criteria, true);    // only get GPS if it's on
    }

    // user has returned from giftChooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            // user decided to send a gift
            if(resultCode == Activity.RESULT_OK){
                // get info from giftchooser (which animal sent gift, what inventory item (if any)
                // is the user sending. is it a gift for a specific person, or a gift for the whole map?
                String giftName = data.getStringExtra("giftName");
                String animalName =data.getStringExtra("animalName");
                String message = data.getStringExtra("message");
                String sendTo = data.getStringExtra("sendTo");
                LatLng latLng = savedLatLng;

                Calendar c = Calendar.getInstance();
                Random rand = new Random();
                // which gift box should be displayed on the map
                int randomNum = rand.nextInt((Globals.ANIMAL_TO_BOX_LIST.get(animalName).size() - 1) + 1);
                // each animal can only send certain gift boxes. which one will they send?
                String giftbox_name = Globals.ANIMAL_TO_BOX_LIST.get(animalName).get(randomNum);
                // gift box stored as int in database, but string in ANIMAL_TO_BOX_LIST,
                // so convert it
                int giftbox = 0;
                for(int i=0; i<Globals.INT_TO_BOX.size(); i++) {
                    if(Globals.INT_TO_BOX.get(i).equals(giftbox_name)) {
                        giftbox = i;
                        break;
                    }
                } // figure out who to send gift to
                String receiver;
                if(sendTo == null || sendTo.equals(""))  receiver = null;
                else {
                    receiver = sendTo;
                    Toast.makeText(getBaseContext(), "Gift sent to " + receiver, Toast.LENGTH_SHORT).show();
                } // update database
                MapGift gift = new MapGift(giftName, Util.email, Util.name, message, animalName, new cs65.edu.dartmouth.cs.gifto.LatLng(latLng.latitude, latLng.longitude), c.getTimeInMillis(), receiver, giftbox);
                helper.insertMapGift(gift);
                giftList.add(gift);
                // remove inventory object from sql and from firebase (if they've sent an item)
                MySQLiteHelper helper = new MySQLiteHelper(this);
                if(Globals.ITEM_TO_TYPE.get(giftName) != null) {
                    InventoryItem item = helper.fetchinventoryItemByName(giftName);
                    item.setItemAmount(item.getItemAmount() - 1);
                    helper.removeInventoryItem(item.getItemName());
                    helper.insertInventory(item, true);
                }
            } // user does not want to send a gift anymore
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
            // you are already on map. do nothing
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        // populate the navigation drawer with user information
        NavigationView navigationView = findViewById(R.id.nav_view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            View headerView = navigationView.getHeaderView(0);
            for (UserInfo profile : user.getProviderData()) {
                if (profile.getDisplayName() != null && !profile.getDisplayName().equals("")) {
                    TextView tv = headerView.findViewById(R.id.nav_header_text);
                    tv.setText(profile.getDisplayName());
                } else {
                    TextView tv = headerView.findViewById(R.id.nav_header_text);
                    tv.setText(profile.getEmail());
                }
                if (profile.getPhotoUrl() != null) {
                    ImageView navImage = headerView.findViewById(R.id.nav_image);
                    navImage.setImageURI(profile.getPhotoUrl());
                }
            }
            ImageView navImage = headerView.findViewById(R.id.nav_image);
            if (Util.photo != null) {
                navImage.setImageURI(Util.photo);
            } else navImage.setImageResource(R.drawable.dog);
        }
        // map has separate navBar (I can't use the same navbar unless I initialize the mapFragment
        // in the main activity, and pretty much copy all the map activity code into here
        // so to keep the code organized, mapActicity has it's own navBar, and this navbar will
        // always have item 1 selected
        navigationView.getMenu().getItem(1).setChecked(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);
        }
    }

    // prevents the individual popup from crashing the app.
    // (it would have been called too early otherwise)
    public void onAttachedToWindow(){
        // update the map in real time, adding and deleting
        giftsData.addChildEventListener(new ChildEventListener() {
            @Override
            // someone has put a new gift on the map
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // the gift can be picked up by anyone
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
                } // the gift is specifically for you!
                else if(Util.email.equals(dataSnapshot.child("sendTo").getValue(String.class))){
                    personal_gift_popup(dataSnapshot.getValue(MapGift.class));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            // someone has picked up a gift on the map
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
            // a gift meant for anyone
            if(gift.getSendTo() == null) {
                int id;
                if((id = Util.getImageIdFromName(Globals.INT_TO_BOX.get(gift.getGiftBox()))) == Util.getImageIdFromName("")) id = R.drawable.gift_icon;
                MarkerOptions markerOptions = new MarkerOptions().position(gift.getLocation().toGoogleLatLng()).icon(BitmapDescriptorFactory.fromResource(id));
                Marker marker = mMap.addMarker(markerOptions);
                gifts.add(marker);
            } // a gift meant for you
            else if(Util.email.equals(gift.getSendTo())){
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_friend) {
            DialogFragment newFragment = MapsActivity.MyAlertDialogFragment.newInstance();
            newFragment.show(getFragmentManager(), "dialog");
        }
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

    public void doPositiveClick(String email, String nickname) {
        Friend friend = new Friend();
        friend.setEmail(email);
        friend.setNickname(nickname);

        MySQLiteHelper db = new MySQLiteHelper(this);
        db.insertFriend(friend, true);
    }

    public void doNegativeClick() {
    }

    public static class MyAlertDialogFragment extends DialogFragment {
        public static MapsActivity.MyAlertDialogFragment newInstance() {
            MapsActivity.MyAlertDialogFragment frag = new MapsActivity.MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", "Add a Friend");
            frag.setArguments(args);
            return frag;
        }

        @SuppressLint("InflateParams")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final String title = getArguments().getString("title");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.friend_dialog, null);

            AlertDialog.Builder alert = new AlertDialog.Builder((getActivity()));
            final EditText emaile = view.findViewById(R.id.dialog_email);
            final EditText nicknamee = view.findViewById(R.id.dialog_nickname);
            return alert.setView(view).setTitle(title)
                    .setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String email = emaile.getText().toString();
                                    String nickname = nicknamee.getText().toString();
                                    ((MapsActivity) getActivity()).doPositiveClick(email, nickname);
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MapsActivity) getActivity()).doNegativeClick();
                                }
                            }).create();
        }
    }
}


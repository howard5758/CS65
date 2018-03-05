package cs65.edu.dartmouth.cs.gifto;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    private ValueEventListener listener;
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Sensor mGravity;

    double gSum;
    double dgX;
    double last_roll;
    double last_pitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);


        //navigation view
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // fragment manager
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment = new Garden();
        transaction.replace(R.id.content, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // set globals for Firebase authentication
        Util.firebaseAuth = FirebaseAuth.getInstance();
        Util.firebaseUser = Util.firebaseAuth.getCurrentUser();
        Util.databaseReference = FirebaseDatabase.getInstance().getReference();

        // if there's no user then try to sign them in
        final MySQLiteHelper datasource = new MySQLiteHelper(this);
        datasource.deleteAll();
        if(Util.firebaseUser == null) {
            Util.showActivity(this, LoginActivity.class);
        }
        // if we know who it is then try to download all of their data and put it into SQLite
        else {
            Util.userID = Util.firebaseUser.getUid();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Util.email = user.getEmail();
                for (UserInfo profile : user.getProviderData()) {
                    Util.name = profile.getDisplayName();
                }
            }
            listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                        // insert all the animals
                        if (userSnapshot.getKey().equals("animals")) {
                            for (DataSnapshot animalData : userSnapshot.getChildren()) {
                                Animal animal = new Animal();
                                animal.setPresent(Integer.parseInt(String.valueOf(animalData.child("present").getValue())));
                                animal.setAnimalName((String) animalData.child("animalName").getValue());
                                animal.setNumVisits(Integer.parseInt(String
                                        .valueOf(animalData.child("numVisits").getValue())));
                                animal.setRarity(Integer.parseInt(String
                                        .valueOf(animalData.child("rarity").getValue())));
                                animal.setPersistence(Long.parseLong(String.valueOf(animalData.child("persistence").getValue())));
                                datasource.insertAnimal(animal, false);
                            }
                        }

                        // insert all the friends
                        else if (userSnapshot.getKey().equals("friends")) {
                            for (DataSnapshot friendData : userSnapshot.getChildren()) {
                                Friend friend = new Friend();
                                friend.setEmail((String) friendData.child("friendEmail").getValue());
                                friend.setNickname((String)friendData.child("nickname").getValue());
                                datasource.insertFriend(friend, false);
                            }
                        }

                        // insert all the gifts
                        else if (userSnapshot.getKey().equals("gifts")) {
                            for (DataSnapshot giftData : userSnapshot.getChildren()) {
                                Gift gift = new Gift();
                                gift.setGiftName((String)giftData.child("giftName").getValue());
                                if(Long.getLong(String.valueOf(giftData.child("timePlaced").getValue())) != null) gift.setTime((Long.getLong(String.valueOf(giftData.child("time").getValue()))));
                                else gift.setTime(0);
                                gift.setFriendName((String)giftData.child("friendName").getValue());
                                gift.setSent((boolean) giftData.child("sent").getValue());
                                gift.setGiftBox(Integer.parseInt(String.valueOf(giftData.child("giftBox").getValue())));
                                gift.setLocation(new cs65.edu.dartmouth.cs.gifto.LatLng(
                                        (Double.parseDouble(String.valueOf(giftData.child("location").child("latitude")
                                                .getValue()))),
                                        (Double.parseDouble(String.valueOf(giftData.child("location").child("longitude")
                                                .getValue())))));

                                // try to insert it
                                datasource.insertGift(gift, false);
                            }
                        }

                        // insert all the items
                        else if (userSnapshot.getKey().equals("items")) {

                            for (DataSnapshot itemData : userSnapshot.getChildren()) {
                                InventoryItem item = new InventoryItem();
                                item.setItemName((String) itemData.child("itemName").getValue());
                                item.setItemAmount(Integer.parseInt(String.
                                        valueOf(itemData.child("itemAmount").getValue())));
                                if(String.valueOf(itemData.child("present").getValue()) != null) item.setPresent(Integer.parseInt(String.valueOf(itemData.child("present").getValue())));
                                else item.setPresent(-1);
                                datasource.insertInventory(item, false);

                            }


                        }
                    }
                    // we only want to download once, so end listener after it executes once
                    endListener();
                    Util.completed = true;
                    Garden.check_animals();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            Util.databaseReference.child("users").child(Util.userID).addValueEventListener(listener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // make sure the sensor is running
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

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
            if (Util.photo != null) {
                ImageView navImage = headerView.findViewById(R.id.nav_image);
                navImage.setImageURI(Util.photo);
            }
        }
        // map has separate navBar (I can't use the same navbar unless I initialize the mapFragment
        // here, and pretty much copy all the map activity code into here
        // so to keep the code organized, mapActicity has it's own navBar, and this navbar will
        // always have item 0 selected
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void onDestroy() {
        super.onDestroy();
        // unregister sensor when app ends. Still want it to run in background throughout app though
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment newFragment;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        int id = item.getItemId();

        if (id == R.id.nav_garden) {
            newFragment = new Garden();
            transaction.replace(R.id.content, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(this, MapsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // helper function, since a listener can't end itself directly
    private void endListener() {
        Util.databaseReference.child("users").child(Util.userID).removeEventListener(listener);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_LIGHT) {
            float light = sensorEvent.values[0];
            Util.nightTime = light < 5;
        }

        else if (sensor.getType() == Sensor.TYPE_GRAVITY) {
            double gX = sensorEvent.values[0];
            double gY = sensorEvent.values[1];
            double gZ = sensorEvent.values[2];
            double roll = Math.atan2(gZ, gZ) * 180 / Math.PI;
            double pitch = Math.sqrt((gX*gX) + (gZ*gZ));

            gSum = Math.sqrt((gX*gX) + (gY*gY) + (gZ*gZ));
            if (gSum != 0) {
                gX /= gSum;
                gY /= gSum;
                gZ /= gSum;
            }

            if (gZ != 0) {
                roll = Math.atan2(gX, gZ) * 180 / Math.PI;
            }

            if (pitch != 0) {
                pitch = Math.atan2(gY, pitch) * 180 / Math.PI;
            }

            dgX = (roll - last_roll);

            // if device orientation is close to vertical – rotation around x is almost undefined – skip!
            if (gY > 0.99) dgX = 0;

            // if rotation was too intensive – more than 180 degrees – skip it
            if (dgX > 180) dgX = 0;
            if (dgX < -180) dgX = 0;

            Util.angle += dgX;
            Log.d("angle", String.valueOf(Util.angle));

            last_roll = roll;
            last_pitch = pitch;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

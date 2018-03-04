package cs65.edu.dartmouth.cs.gifto;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ValueEventListener listener;
    Animal animal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
                                animal.setAnimalName((String) animalData.child("animalName").getValue());
                                animal.setNumVisits(Integer.parseInt(String
                                        .valueOf(animalData.child("numVisits").getValue())));
                                animal.setRarity(Integer.parseInt(String
                                        .valueOf(animalData.child("rarity").getValue())));
                                animal.setPersistence((Long) animalData.child("persistence").getValue());
                                datasource.insertAnimal(animal);
                            }
                        }

                        // insert all the friends
                        else if (userSnapshot.getKey().equals("friends")) {
                            for (DataSnapshot friendData : userSnapshot.getChildren()) {
                                Friend friend = new Friend();
                                friend.setEmail((String) friendData.child("friendEmail").getValue());
                                friend.setNickname((String)friendData.child("nickname").getValue());
                                datasource.insertFriend(friend);
                            }
                        }

                        // insert all the gifts
                        else if (userSnapshot.getKey().equals("gifts")) {
                            for (DataSnapshot giftData : userSnapshot.getChildren()) {
                                Gift gift = new Gift();
                                gift.setGiftName((String)giftData.child("giftName").getValue());
                                gift.setTime((Long.getLong(String.valueOf(giftData.child("time").getValue()))));
                                gift.setFriendName((String)giftData.child("friendName").getValue());
                                gift.setSent((boolean) giftData.child("sent").getValue());
                                gift.setGiftBox(Integer.parseInt(String.valueOf(giftData.child("giftBox").getValue())));
                                gift.setLocation(new cs65.edu.dartmouth.cs.gifto.LatLng(
                                        (Double.parseDouble(String.valueOf(giftData.child("location").child("latitude")
                                                .getValue()))),
                                        (Double.parseDouble(String.valueOf(giftData.child("location").child("longitude")
                                                .getValue())))));

                                // try to insert it
                                datasource.insertGift(gift);
                            }
                        }

                        // insert all the items
                        else if (userSnapshot.getKey().equals("items")) {

                            for (DataSnapshot itemData : userSnapshot.getChildren()) {
                                InventoryItem item = new InventoryItem();
                                item.setItemName((String) itemData.child("itemName").getValue());
                                item.setItemAmount(Integer.parseInt(String.
                                        valueOf(itemData.child("itemAmount").getValue())));
                                datasource.insertInventory(item);

                            }


                        }
                    }
                    // we only want to download once, so end listener after it executes once
                    endListener();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            Util.databaseReference.child("users").child(Util.userID).addValueEventListener(listener);
        }

        // test code for inserting data
        ArrayList<Animal> animals;
        if (Util.userID != null) {
//            MySQLiteHelper db = new MySQLiteHelper(this);
//            Animal a1 = new Animal("pink squirrel", 15, 2, 12000, 1);
//            Animal a2 = new Animal("kangaroo", 16, 3, 12001, 1);
//            Animal a3 = new Animal("owl",1,3,12000,0);
//            Animal a4 = new Animal("cat",1,3,12000,1);
//            Gift g1 = new Gift("fish1", false, "john1", 10000, new cs65.edu.dartmouth.cs.gifto.LatLng(12, 15));
//            Gift g2 = new Gift("fish2", true, "john2", 10001, new cs65.edu.dartmouth.cs.gifto.LatLng(13, 16));
//            InventoryItem i1 = new InventoryItem("money", 300);
//            //InventoryItem i2 = new InventoryItem("money2", 5);
//            Friend f1 = new Friend("john1", "johnny1");
//            Friend f2 = new Friend("john2", "johnny2");
//            MapGift mg1 = new MapGift("watermelon bag", "username1", "usernickname1", "message1", "animalname1", g1.getLocation(), 12000, "john1");
//            MapGift mg2 = new MapGift("red box", "username2", "usernickname2", "message2", "animalname2", g2.getLocation(), 12001, "john2");
//
//            db.insertAnimal(a1);
//            db.insertAnimal(a2);
//            db.insertAnimal(a3);
//            db.insertAnimal(a4);
//            db.insertGift(g1);
//            db.insertGift(g2);
//            db.insertInventory(i1);
//            //db.insertInventory(i2);
//            db.insertMapGift(mg1);
//            db.insertMapGift(mg2);
//            db.insertFriend(f1);
//            db.insertFriend(f2);
//
//            Animal animal1 = db.fetchAnimalByName("cat1");
//            Animal animal2 = db.fetchAnimalByName("cat2");
//            ArrayList<Animal> animall = db.fetchAllAnimals();
//
//            Gift gift1 = db.fetchGiftByName(g1.getGiftName());
//            Gift gift2 = db.fetchGiftByName(g2.getGiftName());
//            ArrayList<Gift> giftl = db.fetchAllGifts();
//
//            InventoryItem item1 = db.fetchinventoryItemByName("money1");
//            InventoryItem item2 = db.fetchinventoryItemByName("money2");
//            ArrayList<InventoryItem> iteml = db.fetchAllInventoryItems();
//
//            Friend friend1 = db.fetchFriendByEmail("john1");
//            Friend friend2 = db.fetchFriendByEmail("john2");
//            ArrayList<Friend> friendl = db.fetchAllFriends();
//
//            MapGift mapGift1 = db.fetchMapGiftByName(mg1.getId());
//            MapGift mapGift2 = db.fetchMapGiftByName(mg2.getId());
//            ArrayList<MapGift> mapGiftl = db.fetchAllMapGifts();
//
//            Log.d("olivermct", "insert and fetch completed");
//
//            db.removeAnimal(a1.getAnimalName());
//            db.removeGift(g1.getId());
//            db.removeFriend(f1.getFirebaseId());
//            db.removeInventoryItem(i1.getItemName());
//            db.removeMapGift(mg1.getId());
//
//            animall = db.fetchAllAnimals();
//            giftl = db.fetchAllGifts();
//            iteml = db.fetchAllInventoryItems();
//            friendl = db.fetchAllFriends();
//            mapGiftl = db.fetchAllMapGifts();
//
//            animal1 = db.fetchAnimalByName("cat1");
//
//            Log.d("olivermct", "remove completed");
        }
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
        // map has separate navBar (I can't use the same navbar unless I initialize the mapFragment
        // here, and pretty much copy all the map activity code into here
        // so to keep the code organized, mapActicity has it's own navBar, and this navbar will
        // always have item 0 selected
        navigationView.getMenu().getItem(0).setChecked(true);
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
}

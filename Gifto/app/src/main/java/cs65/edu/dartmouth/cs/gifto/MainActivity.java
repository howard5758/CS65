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

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                                friend.setName((String) friendData.child("name").getValue());
                                friend.setNickname((String)friendData.child("nickname").getValue());
                                datasource.insertFriend(friend);
                            }
                        }

                        // insert all the gifts
                        else if (userSnapshot.getKey().equals("gifts")) {
                            for (DataSnapshot giftData : userSnapshot.getChildren()) {
                                Gift gift = new Gift();
                                gift.setGiftName((String)giftData.child("giftName").getValue());
                                gift.setTime((Long) giftData.child("time").getValue());
                                gift.setFriendName((String)giftData.child("friendName").getValue());
                                gift.setSent((boolean) giftData.child("sent").getValue());
                                gift.setLocation(new cs65.edu.dartmouth.cs.gifto.LatLng(
                                        (giftData.child("location").child("latitude")
                                                .getValue(Double.class)),
                                        (giftData.child("location").child("longitude")
                                                .getValue(Double.class))));

                                // try to insert it
                                try {
                                    datasource.insertGift(gift);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        // insert all the items
                        else if (userSnapshot.getKey().equals("items")) {
                            for (DataSnapshot itemData : userSnapshot.getChildren()) {
                                InventoryItem item = new InventoryItem();
                                item.setItemType((String) itemData.child("itemType").getValue());
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
            Friend friend = new Friend("john", "johnny");
            animal = new Animal("cat", 6, 15, 15000);
            Animal an = new Animal("dog", 1, 300, 10000);
            Gift gift = new Gift("fish", false, friend.getName(), 1000, new cs65.edu.dartmouth.cs.gifto.LatLng(23, 21));
            MapGift mapGift = new MapGift(gift.getGiftName(), friend.getName(),
                    friend.getNickname(), "Hello",
                    animal.getAnimalName(), gift.getLocation(), gift.getTime());
            MySQLiteHelper helper = new MySQLiteHelper(this);
            helper.insertFriend(friend);
            helper.insertAnimal(animal);
            helper.insertAnimal(an);
            helper.insertMapGift(mapGift);
            try {
                helper.insertGift(gift);
            } catch (IOException e) {
                e.printStackTrace();
            }
            helper.incrementVisits(animal);
            InventoryItem item = new InventoryItem("money", 200);
            helper.insertInventory(item);
            animals = helper.fetchAllAnimals();
            Log.d("animal size", String.valueOf(animals.size()));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment newFragment = new Garden();
        transaction.replace(R.id.content, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            startActivity(new Intent(this, Preferences.class));
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

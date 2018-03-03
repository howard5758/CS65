package cs65.edu.dartmouth.cs.gifto;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Oliver on 2/24/2018.
 *
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static boolean failed_insert = false;

    private static final String DATABASE_NAME = "gifto.db";
    private static final int DATABASE_VERSION = 1;

    private static final String FRIEND_TITLE = "friends";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FRIEND_NAME = "friendName";
    private static final String COLUMN_FRIEND_NICKNAME = "nickname";

    private static final String GIFT_TITLE = "gifts";
    private static final String COLUMN_GIFT = "giftName";
    private static final String COLUMN_SENT = "sent";
    private static final String COLUMN_TOFROM = "toFrom";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";

    private static final String ANIMAL_TITLE = "animals";
    private static final String COLUMN_ANIMAL_NAME = "animalName";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_VISITS = "visits";
    private static final String COLUMN_PERSISTENCE = "persistence";

    private static final String INVENTORY_TITLE = "inventory";
    private static final String COLUMN_INVENTORY_NAME = "inventoryName";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";

    private static final String MAP_GIFT_TITLE = "mapGift";
    private static final String COLUMN_FIREBASE_ID = "firebaseId";
    private static final String COLUMN_MESSAGE = "message";

    private static final String COLUMN_FIREBASE_FLAG = "flag";


    private String[] friends_columns = { COLUMN_ID, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME, COLUMN_FIREBASE_FLAG };

    private String[] gifts_columns = { COLUMN_ID, COLUMN_FIREBASE_ID, COLUMN_GIFT, COLUMN_SENT, COLUMN_TOFROM,
            COLUMN_TIME, COLUMN_LOCATION, COLUMN_FIREBASE_FLAG };

    private String [] animals_columns = { COLUMN_ID, COLUMN_ANIMAL_NAME, COLUMN_RARITY,
            COLUMN_VISITS, COLUMN_PERSISTENCE, COLUMN_FIREBASE_FLAG };

    private String[] inventory_columns = { COLUMN_ID, COLUMN_INVENTORY_NAME,
            COLUMN_TYPE, COLUMN_AMOUNT, COLUMN_FIREBASE_FLAG };

    private String[] map_gifts_columns = { COLUMN_ID, COLUMN_FIREBASE_ID, COLUMN_GIFT, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME, COLUMN_MESSAGE, COLUMN_ANIMAL_NAME,
            COLUMN_LOCATION, COLUMN_TIME, COLUMN_FIREBASE_FLAG };

    private String[] titles = {FRIEND_TITLE, GIFT_TITLE, ANIMAL_TITLE, INVENTORY_TITLE, MAP_GIFT_TITLE};

    private ArrayList<String[]> columns = new ArrayList<>(0);

    private static final String CREATE_FRIENDS_TABLE = "create table " + FRIEND_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FRIEND_NAME + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_GIFTS_TABLE = "create table " + GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIREBASE_ID + " TEXT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_SENT + " INTEGER NOT NULL, " +
            COLUMN_TOFROM + " TEXT, " +
            COLUMN_TIME + " DATETIME NOT NULL, " +
            COLUMN_LOCATION + " BLOB, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_ANIMAL_TABLE = "create table " + ANIMAL_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_RARITY + " INTEGER NOT NULL, " +
            COLUMN_VISITS + " INTEGER NOT NULL, " +
            COLUMN_PERSISTENCE + " INTEGER, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_INVENTORY_TABLE = "create table " + INVENTORY_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_INVENTORY_NAME + " TEXT, " +
            COLUMN_TYPE + " INTEGER, " +
            COLUMN_AMOUNT + " INTEGER NOT NULL, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_MAP_GIFTS_TABLE = "create table " + MAP_GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIREBASE_ID + " TEXT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_FRIEND_NAME + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT, " +
            COLUMN_MESSAGE + " TEXT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_LOCATION + " BLOB, " +
            COLUMN_TIME + " DATETIME NOT NULL, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";


    // Constructor
    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        columns.add(friends_columns);
        columns.add(gifts_columns);
        columns.add(animals_columns);
        columns.add(inventory_columns);
        columns.add(map_gifts_columns);
    }

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_GIFTS_TABLE);
        db.execSQL(CREATE_ANIMAL_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
        db.execSQL(CREATE_MAP_GIFTS_TABLE);
    }

    // Insert a friend
    /*** username must be unique! ***/
    void insertFriend(Friend friend) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        if (isOnline()) {
            Log.d("if", "online");
            Util.databaseReference.child("users").
                    child(Util.userID).child("friends").child(friend.getName()).setValue(friend);
            flagged = 0;
            if (failed_insert) {
                insertFlagged();
            }
        } else {
            Log.d("if", "not connected");
            failed_insert = true;
        }


        // now insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FRIEND_NAME, friend.getName());
        values.put(COLUMN_FRIEND_NICKNAME, friend.getNickname());
        values.put(COLUMN_FIREBASE_FLAG, flagged);

        database.insert(FRIEND_TITLE, null, values);
        database.close();
    }

    // Insert a gift
    void insertGift(Gift gift) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        String key = "";
        if (isOnline()) {
            Log.d("if", "online");
            key = Util.databaseReference.child("users").child(Util.userID).child("gifts").push().getKey();
            Util.databaseReference.child("users").
                    child(Util.userID).child("gifts").child(key).setValue(gift);
            flagged = 0;
            if (failed_insert) {
                insertFlagged();
            }
        } else {
            Log.d("if", "not connected");
            failed_insert = true;
        }


        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_GIFT, gift.getGiftName());
        values.put(COLUMN_FIREBASE_ID, key);
        values.put(COLUMN_SENT, gift.isSent());
        values.put(COLUMN_TOFROM, gift.getFriendName());
        values.put(COLUMN_TIME, gift.getTime());
        try {
            values.put(COLUMN_LOCATION, toByte(gift.getLocation()));        // converting to byte throws IOException
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.put(COLUMN_FIREBASE_FLAG, flagged);

        database.insert(GIFT_TITLE, null, values);
        database.close();
    }

    // Insert an animal
    void insertAnimal(Animal animal) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        if (isOnline()) {
            Log.d("if", "online");
            Util.databaseReference.child("users").
                    child(Util.userID).child("animals").child(animal.getAnimalName()).setValue(animal);
            flagged = 0;
            if (failed_insert) {
                insertFlagged();
            }
        } else {
            Log.d("if", "not connected");
            failed_insert = true;
        }


        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANIMAL_NAME, animal.getAnimalName());
        values.put(COLUMN_RARITY, animal.getRarity());
        values.put(COLUMN_VISITS, animal.getNumVisits());
        values.put(COLUMN_PERSISTENCE, animal.getPersistence());
        values.put(COLUMN_FIREBASE_FLAG, flagged);

        database.insert(ANIMAL_TITLE, null, values);
        database.close();
    }

    // insert inventory item
    // also use this if you want to change the amount of an item
    void insertInventory(InventoryItem item) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        if (isOnline()) {
            Log.d("if", "online");
            Util.databaseReference.child("users").
                    child(Util.userID).child("items").child(item.getItemName()).setValue(item);
            flagged = 0;
            if (failed_insert) {
                insertFlagged();
            }
        } else {
            Log.d("if", "not connected");
            failed_insert = true;
        }


        // insert into SQL
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, item.getItemType());
        values.put(COLUMN_INVENTORY_NAME, item.getItemName());
        values.put(COLUMN_AMOUNT, item.getItemAmount());
        values.put(COLUMN_FIREBASE_FLAG, flagged);


        database.insert(INVENTORY_TITLE, null, values);
        database.close();
    }

    /* Insert map gift
     * must be connected to the internet
     * returns the Firebase generated ID if successfully inserted, empty string if not */
    String insertMapGift(MapGift gift) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        String id = "";
        if (isOnline()) {
            Log.d("if", "online");
            id = Util.databaseReference.child("gifts").push().getKey();
            gift.setId(id);
            Util.databaseReference.child("gifts").child(id).setValue(gift);
            if (failed_insert) {
                insertFlagged();
            }
        } else {
            Log.d("if", "not connected");
        }

        return id;
    }

    /* remove animal by name
     * also deletes from Firebase at the specified name */
    void removeAnimal(String name) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_ANIMAL_NAME + "='" + name + "'";
        database.delete(ANIMAL_TITLE, whereClause, null);
        database.close();

        Util.databaseReference.child("users").child(Util.userID).child("animals").child(name).removeValue();
    }

    /* remove friend by name
     * also deletes from Firebase at the specified name */
    void removeFriend(String name) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_FRIEND_NAME + "='" + name + "'";
        database.delete(FRIEND_TITLE, whereClause, null);
        database.close();

        Util.databaseReference.child("users").child(Util.userID).child("friends").child(name).removeValue();
    }

    /* remove gift by name
     * also deletes from Firebase at the specified name */
    void removeGift(String name) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_GIFT + "='" + name + "'";
        database.delete(GIFT_TITLE, whereClause, null);
        database.close();

        Util.databaseReference.child("users").child(Util.userID).child("gifts").child(name).removeValue();
    }

    /* remove inventory item by name
     * also deletes from Firebase at the specified name */
    void removeInventoryItem(String name) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = COLUMN_INVENTORY_NAME + "='" + name + "'";
        database.delete(INVENTORY_TITLE, whereClause, null);
        database.close();

        Util.databaseReference.child("users").child(Util.userID).child("items").child(name).removeValue();
    }

    /* removes a mapgift from firebase (not stored in SQL anyway)
     * returns true if successfully removed, false if not connected to internet */
    boolean removeMapGift(String id) {
        if (isOnline()) {
            Util.databaseReference.child("gifts").child(id).removeValue();
            return true;
        }
        return false;
    }

    /* Method to fetch all the animals currently stored in SQLite
     * returns ArrayList with animal objects
     * returns empty ArrayList if table is empty */
    ArrayList<Animal> fetchAllAnimals() {
        ArrayList<Animal> animals = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(ANIMAL_TITLE, animals_columns,
                null, null, null, null, null);

        cursor.moveToFirst(); //Move the cursor to the first row.
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Animal animal = cursorToAnimal(cursor);
                animals.add(animal);
                cursor.moveToNext();
            }
        }

        // Make sure to close the cursor
        cursor.close();
        database.close();

        return animals;
    }

    ArrayList<Gift> fetchAllGifts() {
        ArrayList<Gift> gifts = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(GIFT_TITLE, gifts_columns,
                null, null, null, null, null);

        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {
            Gift gift = cursorToGift(cursor);
            gifts.add(gift);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        database.close();

        return gifts;
    }

    /* Method to fetch all the mapgifts currently stored in Firebase
     * must be connected to internet to access
     * returns ArrayList with mapgift objects
     * returns empty ArrayList if table is empty or not connected to internet */
    ArrayList<MapGift> fetchAllMapGifts() {
        final ArrayList<MapGift> gifts = new ArrayList<>(0);
        if (isOnline()) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MapGift gift = new MapGift();

                        gift.setId(String.valueOf(snapshot.child(COLUMN_FIREBASE_ID).getValue()));
                        gift.setGiftName(String.valueOf(snapshot.child(COLUMN_GIFT).getValue()));
                        gift.setAnimalName(String.valueOf(snapshot.child(COLUMN_ANIMAL_NAME).getValue()));
                        gift.setMessage(String.valueOf(snapshot.child(COLUMN_MESSAGE).getValue()));
                        gift.setUserName(String.valueOf(snapshot.child(COLUMN_FRIEND_NAME).getValue()));
                        gift.setUserNickname(String.valueOf(snapshot.child(COLUMN_FRIEND_NICKNAME).getValue()));
                        if (snapshot.child("timePlaced").getValue() != null) {
                            gift.setTimePlaced(Long.parseLong(String.valueOf(snapshot.child("timePlaced").getValue())));
                        }
                        gift.setLocation(new LatLng(Double.parseDouble(String.valueOf(snapshot.
                                child(COLUMN_LOCATION).child("latitude").getValue())),
                                Double.parseDouble(String.valueOf(snapshot.
                                        child(COLUMN_LOCATION).child("longitude").getValue()))));

                        gifts.add(gift);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            Util.databaseReference.child("gifts").addValueEventListener(listener);
        }

        return gifts;
    }

    /* Method to fetch all the inventory items currently stored in SQLite
     * returns ArrayList with inventory item objects
     * returns empty ArrayList if table is empty */
    ArrayList<InventoryItem> fetchAllInventoryItems() {
        ArrayList<InventoryItem> items = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                InventoryItem item = cursorToInventoryItem(cursor);
                items.add(item);
                cursor.moveToNext();
            }
        }

        cursor.close();
        database.close();

        return items;
    }

    /* Method to fetch all the friends currently stored in SQLite
     * returns ArrayList with friend objects
     * returns empty ArrayList if table is empty */
    ArrayList<Friend> fetchAllFriends() {
        final ArrayList<Friend> friends = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(FRIEND_TITLE, friends_columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Friend friend = cursorToFriend(cursor);
                friends.add(friend);
                cursor.moveToNext();
            }
        }

        cursor.close();
        database.close();

        return friends;
    }

    /* Method to return a single Animal specified by the name
     * returns the animal if it exists
     * returns an empty animal object if it does not exist
     * queries SQLite, not firebase */
    Animal fetchAnimalByName(String name) {
        Animal animal = new Animal();
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_ANIMAL_NAME + "='" + name + "'";
        Cursor cursor = database.query(ANIMAL_TITLE, animals_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            animal = cursorToAnimal(cursor);
        }

        cursor.close();
        database.close();

        return animal;
    }

    /* Method to return a single Gift specified by the name
     * returns the gift if it exists
     * returns an empty gift object if it does not exist
     * queries SQLite, not firebase */
    Gift fetchGiftByName(String name) {
        Gift gift = new Gift();
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_GIFT + "='" + name + "'";
        Cursor cursor = database.query(GIFT_TITLE, gifts_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            gift = cursorToGift(cursor);
        }

        cursor.close();
        database.close();

        return gift;
    }

    /* Method to return a single inventory item specified by the name
     * returns the item if it exists
     * returns an empty item if it does not exist
     * queries SQLite, not firebase */
    InventoryItem fetchinventoryItemByName(String name) {
        InventoryItem item = new InventoryItem();
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_INVENTORY_NAME + "='" + name + "'";
        Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            item = cursorToInventoryItem(cursor);
        }

        cursor.close();
        database.close();

        return item;
    }

    /* Method to return a single Friend specified by the name (not nickname)
     * returns the friend if it exists
     * returns an empty friend object if it does not exist
     * queries SQLite, not firebase */
    Friend fetchFriendByName(String name) {
        Friend friend = new Friend();
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_FRIEND_NAME + "='" + name + "'";
        Cursor cursor = database.query(FRIEND_TITLE, friends_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            friend = cursorToFriend(cursor);
        }

        cursor.close();
        database.close();

        return friend;
    }

    /* Method to return a single map gift specified by the name
     * returns the map gift if it exists
     * returns an empty map gift if it does not exist or not connected to internet */
    MapGift fetchMapGiftByName(final String id) {
        final MapGift gift = new MapGift();
        if (isOnline()) {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals(id)) {
                            gift.setId(String.valueOf(snapshot.child(COLUMN_FIREBASE_ID).getValue()));
                            gift.setGiftName(String.valueOf(snapshot.child(COLUMN_GIFT).getValue()));
                            gift.setAnimalName(String.valueOf(snapshot.child(COLUMN_ANIMAL_NAME).getValue()));
                            gift.setMessage(String.valueOf(snapshot.child(COLUMN_MESSAGE).getValue()));
                            gift.setUserName(String.valueOf(snapshot.child(COLUMN_FRIEND_NAME).getValue()));
                            gift.setUserNickname(String.valueOf(snapshot.child(COLUMN_FRIEND_NICKNAME).getValue()));
                            if (snapshot.child("timePlaced").getValue() != null) {
                                gift.setTimePlaced(Long.parseLong(String.valueOf(snapshot.child("timePlaced").getValue())));
                            }
                            gift.setLocation(new LatLng(Double.parseDouble(String.valueOf(snapshot.
                                    child(COLUMN_LOCATION).child("latitude").getValue())),
                                    Double.parseDouble(String.valueOf(snapshot.
                                            child(COLUMN_LOCATION).child("longitude").getValue()))));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            Util.databaseReference.child("gifts").addValueEventListener(listener);
        }
        return gift;
    }

    // delete every row in every table (probably so you can remake a new one)
    void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MySQLiteHelper.GIFT_TITLE, null, null);
        database.delete(MySQLiteHelper.FRIEND_TITLE, null, null);
        database.delete(MySQLiteHelper.ANIMAL_TITLE, null, null);
        database.delete(MySQLiteHelper.INVENTORY_TITLE, null, null);
        database.delete(MySQLiteHelper.MAP_GIFT_TITLE, null, null);
    }

    /* Goes through every table in SQLite, gets the flagged data, and inserts it to firebase */
    private void insertFlagged() {
        SQLiteDatabase database = getWritableDatabase();

        for (int i = 0; i < columns.size(); i++) {
            String clause = COLUMN_FIREBASE_FLAG + "='1'";
            Cursor cursor = database.query(titles[i], columns.get(i), clause,
                    null, null, null, null);

            cursor.moveToFirst();
            while (cursor.getCount() > 0 && !cursor.isAfterLast()) {
                if (i == 0) {
                    Friend friend = cursorToFriend(cursor);
                    Util.databaseReference.child("users").
                            child(Util.userID).child("friends").child(friend.getName()).setValue(friend);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIREBASE_FLAG, 0);
                    database.update(FRIEND_TITLE, values, COLUMN_FRIEND_NAME + "='" + friend.getName() + "'", null);
                } else if (i == 1) {
                    Gift gift = cursorToGift(cursor);
                    String key = Util.databaseReference.child("users").child(Util.userID).child("gifts").push().getKey();
                    Util.databaseReference.child("users").
                            child(Util.userID).child("gifts").child(key).setValue(gift);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIREBASE_FLAG, 0);
                    database.update(GIFT_TITLE, values, COLUMN_GIFT + "='" + gift.getGiftName() + "'", null);
                } else if (i == 2) {
                    Animal animal = cursorToAnimal(cursor);
                    Util.databaseReference.child("users").
                            child(Util.userID).child("animals").child(animal.getAnimalName()).setValue(animal);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIREBASE_FLAG, 0);
                    database.update(ANIMAL_TITLE, values, COLUMN_ANIMAL_NAME + "='" + animal.getAnimalName() + "'", null);
                } else if (i == 3) {
                    InventoryItem item = cursorToInventoryItem(cursor);
                    Util.databaseReference.child("users").
                            child(Util.userID).child("items").child(item.getItemName()).setValue(item);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIREBASE_FLAG, 0);
                    database.update(INVENTORY_TITLE, values, COLUMN_INVENTORY_NAME + "='" + item.getItemName() + "'", null);
                }
            }
        }
    }

    /* Turns a cursor into a single animal object
     * cursor checking done before this method is called */
    private Animal cursorToAnimal(Cursor cursor) {
        Animal animal = new Animal();
        animal.setAnimalName(cursor.getString(1));
        animal.setRarity(cursor.getInt(2));
        animal.setNumVisits(cursor.getInt(3));
        animal.setPersistence(cursor.getLong(4));

        return animal;
    }

    /* Turns a cursor into a single gift object
     * cursor checking done before this method is called */
    private Gift cursorToGift(Cursor cursor) {
        Gift gift = new Gift();
        gift.setId(cursor.getString(1));
        gift.setGiftName(cursor.getString(2));
        if (cursor.getInt(3) == 0) {
            gift.setSent(false);
        } else {
            gift.setSent(true);
        }

        gift.setFriendName(cursor.getString(4));
        gift.setTime(cursor.getLong(5));
        try {
            gift.setLocation(toLatLng(cursor.getBlob(6)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gift;
    }

    /* Turns a cursor into a single inventory item object
     * cursor checking done before this method is called */
    private InventoryItem cursorToInventoryItem(Cursor cursor) {
        InventoryItem item = new InventoryItem();
        item.setItemName(cursor.getString(1));
        item.setItemType(cursor.getInt(2));
        item.setItemAmount(cursor.getInt(3));

        return item;
    }

    /* Turns a cursor into a single friend object
     * cursor checking done before this method is called */
    private Friend cursorToFriend(Cursor cursor) {
        Friend friend = new Friend();
        friend.setName(cursor.getString(1));
        friend.setNickname(cursor.getString(2));

        return friend;
    }

    /* checks if the user is online
     * used to see if should insert into firebase or just mark flag in SQL */
    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    // convert a LatLng to a byte array to be stored as a BLOB
    @TargetApi(19)
    private byte[] toByte(cs65.edu.dartmouth.cs.gifto.LatLng latLng) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (DataOutputStream out = new DataOutputStream(baos)) {
                out.writeDouble(latLng.latitude);
                out.writeDouble(latLng.longitude);
            }
            return baos.toByteArray();
        }
    }

    // convert a BLOB back to a LatLng
    @TargetApi(19)
    private static cs65.edu.dartmouth.cs.gifto.LatLng toLatLng(byte[] bytes) throws IOException {
        ArrayList<Double> list = new ArrayList<>(0);
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes)){
            try(DataInputStream in = new DataInputStream(bais)){
                while (in.available() > 0) {
                    list.add(in.readDouble());
                }
            }
        }

        return new cs65.edu.dartmouth.cs.gifto.LatLng(list.get(0), list.get(1));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + GIFT_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + ANIMAL_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TITLE);
        onCreate(db);
    }
}

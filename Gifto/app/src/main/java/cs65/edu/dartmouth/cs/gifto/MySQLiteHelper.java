package cs65.edu.dartmouth.cs.gifto;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
 * This file handles communication between SQLite and Firebase (contrary to what the name suggests)
 * Some interactions with Firebase take place directly in the MapsActivity. This is intentional
 *   to make sure that all the phones are sinced as quickly as possible with items on the map.
 *   For information specific to the user, the methods in this file are fast and secure enough.
 *
 * There are four SQLite tables: Friends, Animals, Gifts, and Inventory items. See the related
 *   classes for more information
 *
 * In Firebase, there are two main categories. The first is for users. Only taht specific user
 *   has access to their information. There are four branches off of each user corresponding
 *   to each table in SQLite. The second category is for gifts on the map. Everyone has access to
 *   this,and it stores the information of a MapGift (see class). Since you have to be connected
 *   to the internet to use the map, there is no point in having an SQLite table for mapGifts
 *
 * General flow:
 * When inserting, the method will first try to insert to Firebase. If this is unsuccessful, the
 *   information is flagged. The information is then inserted into SQLite. Upon the first successful
 *   insertion to Firebase, this will then try to upload all the items flagged in SQLite.
 *
 * For speed and offline use, All fetching is done with SQLite.
 * Firebase exists to sync data if a user signs in to another phone and to sync data on the map with
 *   other phones.
 *
 * All methods are threaded to improve performance.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    private static boolean failed_insert = false;

    private static final String DATABASE_NAME = "gifto.db";
    private static final int DATABASE_VERSION = 1;

    private static final String FRIEND_TITLE = "friends";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FRIEND_EMAIL = "friendEmail";
    private static final String COLUMN_FRIEND_NICKNAME = "nickname";

    private static final String GIFT_TITLE = "gifts";
    private static final String COLUMN_GIFT = "giftName";
    private static final String COLUMN_SENT = "sent";
    private static final String COLUMN_TOFROM = "toFrom";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_GIFT_BOX = "giftBox";

    private static final String ANIMAL_TITLE = "animals";
    private static final String COLUMN_ANIMAL_NAME = "animalName";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_VISITS = "visits";
    private static final String COLUMN_PERSISTENCE = "persistence";
    private static final String COLUMN_PRESENT = "present";

    private static final String INVENTORY_TITLE = "inventory";
    private static final String COLUMN_INVENTORY_NAME = "inventoryName";
    private static final String COLUMN_AMOUNT = "amount";

    private static final String MAP_GIFT_TITLE = "mapGift";
    private static final String COLUMN_FIREBASE_ID = "firebaseId";
    private static final String COLUMN_MESSAGE = "message";

    private static final String COLUMN_FIREBASE_FLAG = "flag";


    private String[] friends_columns = { COLUMN_ID, COLUMN_FRIEND_EMAIL,
            COLUMN_FRIEND_NICKNAME, COLUMN_FIREBASE_ID, COLUMN_FIREBASE_FLAG };

    private String[] gifts_columns = { COLUMN_ID, COLUMN_FIREBASE_ID, COLUMN_GIFT, COLUMN_SENT,
            COLUMN_TOFROM, COLUMN_TIME, COLUMN_GIFT_BOX, COLUMN_LOCATION, COLUMN_FIREBASE_FLAG };

    private String [] animals_columns = { COLUMN_ID, COLUMN_ANIMAL_NAME, COLUMN_RARITY,
            COLUMN_VISITS, COLUMN_PERSISTENCE, COLUMN_FIREBASE_FLAG, COLUMN_PRESENT };

    private String[] inventory_columns = { COLUMN_ID, COLUMN_INVENTORY_NAME,
            COLUMN_AMOUNT, COLUMN_PRESENT, COLUMN_FIREBASE_FLAG };

    private String[] titles = {FRIEND_TITLE, GIFT_TITLE, ANIMAL_TITLE,
            INVENTORY_TITLE, MAP_GIFT_TITLE};

    private ArrayList<String[]> columns = new ArrayList<>(0);

    private static final String CREATE_FRIENDS_TABLE = "create table " + FRIEND_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FRIEND_EMAIL + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT, " +
            COLUMN_FIREBASE_ID + " TEXT, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_GIFTS_TABLE = "create table " + GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FIREBASE_ID + " TEXT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_SENT + " INTEGER NOT NULL, " +
            COLUMN_TOFROM + " TEXT, " +
            COLUMN_TIME + " DATETIME NOT NULL, " +
            COLUMN_GIFT_BOX + " INTEGER, " +
            COLUMN_LOCATION + " BLOB, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";

    private static final String CREATE_ANIMAL_TABLE = "create table " + ANIMAL_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_RARITY + " INTEGER NOT NULL, " +
            COLUMN_VISITS + " INTEGER NOT NULL, " +
            COLUMN_PERSISTENCE + " INTEGER, " +
            COLUMN_FIREBASE_FLAG + " INTEGER, " +
            COLUMN_PRESENT + " INTEGER );";

    private static final String CREATE_INVENTORY_TABLE = "create table " + INVENTORY_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_INVENTORY_NAME + " TEXT, " +
            COLUMN_AMOUNT + " INTEGER NOT NULL, " +
            COLUMN_PRESENT + " INTEGER, " +
            COLUMN_FIREBASE_FLAG + " INTEGER );";


    // Constructor
    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        columns.add(friends_columns);
        columns.add(gifts_columns);
        columns.add(animals_columns);
        columns.add(inventory_columns);
    }

    // Create table schema if not exists
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_GIFTS_TABLE);
        db.execSQL(CREATE_ANIMAL_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    /* Insert a friend
     * when this is called you do not need to know the FirebaseId of the friend
     * the method will automatically create and set an Id for the given friend
     * so, after this method is called, the friend object will have an id inside it */
    void insertFriend(final Friend friend, final boolean insert_firebase) {
        // first try to insert into Firebase
        // if user is offline, this will flag the data in SQLite to be uploaded later
        Runnable runnable = new Runnable() {
            public void run() {
                int flagged = 0;
                if (insert_firebase) {
                    flagged = 1;
                    if (Util.isOnline()) {
                        String key = Util.databaseReference.child("users").child(Util.userID)
                                .child("friends").push().getKey();
                        friend.setFirebaseId(key);
                        Util.databaseReference.child("users").
                                child(Util.userID).child("friends").child(key).setValue(friend);
                        flagged = 0;
                        if (failed_insert) {
                            insertFlagged();
                        }
                    } else {
                        failed_insert = true;
                    }
                }


                // now insert into SQL
                SQLiteDatabase database = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_FRIEND_EMAIL, friend.getEmail());
                values.put(COLUMN_FRIEND_NICKNAME, friend.getNickname());
                values.put(COLUMN_FIREBASE_ID, friend.getFirebaseId());
                values.put(COLUMN_FIREBASE_FLAG, flagged);

                database.insert(FRIEND_TITLE, null, values);
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* insert a gift
     * does the same thing as a friend when you insert with setting the firebase Id */
    void insertGift(final Gift gift, final boolean insert_firebase) {
        // first try to insert into Firebase
        // if user is offline, this will flag the data in SQLite to be uploaded later
        Runnable runnable = new Runnable() {
            public void run() {
                int flagged = 0;
                String key;
                if (insert_firebase) {
                    flagged = 1;
                    if (Util.isOnline()) {
                        key = Util.databaseReference.child("users").child(Util.userID)
                                .child("gifts").push().getKey();
                        gift.setId(key);
                        Util.databaseReference.child("users").
                                child(Util.userID).child("gifts").child(key).setValue(gift);
                        flagged = 0;
                        if (failed_insert) {
                            insertFlagged();
                        }
                    } else {
                        failed_insert = true;
                    }
                }


                // insert into SQL
                SQLiteDatabase database = getReadableDatabase();
                ContentValues values = new ContentValues();

                values.put(COLUMN_GIFT, gift.getGiftName());
                values.put(COLUMN_FIREBASE_ID, gift.getId());
                values.put(COLUMN_SENT, gift.isSent());
                values.put(COLUMN_TOFROM, gift.getFriendName());
                values.put(COLUMN_TIME, gift.getTime());
                values.put(COLUMN_GIFT_BOX, gift.getGiftBox());
                try {
                    values.put(COLUMN_LOCATION, toByte(gift.getLocation()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values.put(COLUMN_FIREBASE_FLAG, flagged);

                database.insert(GIFT_TITLE, null, values);
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Insert an animal
     * does not have firebase Id so does not return Firebase Id */
    void insertAnimal(final Animal animal, final boolean insert_firebase) {
        // first try to insert into Firebase
        // if user is offline, this will flag the data in SQLite to be uploaded later
        Runnable runnable = new Runnable() {
            public void run() {
                int flagged = 0;
                if (insert_firebase) {
                    flagged = 1;
                    if (Util.isOnline()) {
                        Util.databaseReference.child("users").child(Util.userID).child("animals")
                                .child(animal.getAnimalName()).setValue(animal);
                        flagged = 0;
                        if (failed_insert) {
                            insertFlagged();
                        }
                    } else {
                        failed_insert = true;
                    }
                }


                // insert into SQL
                SQLiteDatabase database = getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_ANIMAL_NAME, animal.getAnimalName());
                values.put(COLUMN_RARITY, animal.getRarity());
                values.put(COLUMN_VISITS, animal.getNumVisits());
                values.put(COLUMN_PERSISTENCE, animal.getPersistence());
                values.put(COLUMN_FIREBASE_FLAG, flagged);
                values.put(COLUMN_PRESENT, animal.getPresent());

                database.insert(ANIMAL_TITLE, null, values);
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* insert inventory item
     * also use this if you want to change the amount of an item */
    void insertInventory(final InventoryItem item, final boolean insert_firebase) {
        // first try to insert into Firebase
        // if user is offline, this will flag the data in SQLite to be uploaded later
        Runnable runnable = new Runnable() {
            public void run() {
                int flagged = 0;
                if (insert_firebase) {
                    flagged = 1;
                    if (Util.isOnline()) {
                        Util.databaseReference.child("users").child(Util.userID)
                                .child("items").child(item.getItemName()).setValue(item);
                        flagged = 0;
                        if (failed_insert) {
                            insertFlagged();
                        }
                    } else {
                        failed_insert = true;
                    }
                }


                // insert into SQL
                SQLiteDatabase database = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_INVENTORY_NAME, item.getItemName());
                values.put(COLUMN_AMOUNT, item.getItemAmount());
                values.put(COLUMN_PRESENT, item.getPresent());
                values.put(COLUMN_FIREBASE_FLAG, flagged);


                database.insert(INVENTORY_TITLE, null, values);
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Insert map gift
     * must be connected to the internet
     * returns the Firebase generated ID if successfully inserted, empty string if not */
    void insertMapGift(final MapGift gift) {
        // first try to insert into Firebase
        // if user is offline, this will flag the data in SQLite to be uploaded later
        Runnable runnable = new Runnable() {
            public void run() {
                String id;
                if (Util.isOnline()) {
                    id = Util.databaseReference.child("gifts").push().getKey();
                    gift.setId(id);
                    Util.databaseReference.child("gifts").child(id).setValue(gift);
                    if (failed_insert) {
                        insertFlagged();
                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* remove animal by name
     * also deletes from Firebase at the specified name */
    void removeAnimal(final String name) {
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getWritableDatabase();
                String whereClause = COLUMN_ANIMAL_NAME + "='" + name + "'";
                database.delete(ANIMAL_TITLE, whereClause, null);
                database.close();

                Util.databaseReference.child("users").child(Util.userID)
                        .child("animals").child(name).removeValue();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* remove inventory item by name
     * also deletes from Firebase at the specified name */
    void removeInventoryItem(final String name) {
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getWritableDatabase();
                String whereClause = COLUMN_INVENTORY_NAME + "='" + name + "'";
                database.delete(INVENTORY_TITLE, whereClause, null);
                database.close();

                Util.databaseReference.child("users").child(Util.userID)
                        .child("items").child(name).removeValue();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* removes a mapgift from firebase (not stored in SQL anyway)
     * returns true if successfully removed, false if not connected to internet */
    void removeMapGift(String id) {
        if (Util.isOnline()) {
            Util.databaseReference.child("gifts").child(id).removeValue();
        }
    }

    /* Method to fetch all the animals currently stored in SQLite
     * returns ArrayList with animal objects
     * returns empty ArrayList if table is empty */
    ArrayList<Animal> fetchAllAnimals() {
        final ArrayList<Animal> animals = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(ANIMAL_TITLE, animals_columns,
                        null, null, null, null, null);

                cursor.moveToFirst(); //Move the cursor to the first row.
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Animal animal = new Animal();
                        cursorToAnimal(cursor, animal);
                        animals.add(animal);
                        cursor.moveToNext();
                    }
                }

                // Make sure to close the cursor
                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return animals;
    }

    /* Method to fetch all the gifts currently stored in SQLite
     * returns ArrayList with gift objects
     * returns empty ArrayList if table is empty */
    ArrayList<Gift> fetchAllGifts() {
        final ArrayList<Gift> gifts = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(GIFT_TITLE, gifts_columns,
                        null, null, null, null, null);

                cursor.moveToFirst(); //Move the cursor to the first row.
                while (!cursor.isAfterLast()) {
                    Gift gift = new Gift();
                    cursorToGift(cursor, gift);
                    gifts.add(gift);
                    cursor.moveToNext();
                }

                // Make sure to close the cursor
                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return gifts;
    }

    /* Method to fetch all the mapgifts currently stored in Firebase
     * must be connected to internet to access
     * returns ArrayList with mapgift objects
     * returns empty ArrayList if table is empty or not connected to internet */
    ArrayList<MapGift> fetchAllMapGifts() {
        final ArrayList<MapGift> gifts = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                if (Util.isOnline()) {
                    ValueEventListener listener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                MapGift gift = new MapGift();

                                gift.setId(String.valueOf(snapshot
                                        .child(COLUMN_FIREBASE_ID).getValue()));
                                gift.setGiftName(String.valueOf(snapshot
                                        .child(COLUMN_GIFT).getValue()));
                                gift.setAnimalName(String.valueOf(snapshot
                                        .child(COLUMN_ANIMAL_NAME).getValue()));
                                gift.setMessage(String.valueOf(snapshot
                                        .child(COLUMN_MESSAGE).getValue()));
                                gift.setUserName(String.valueOf(snapshot
                                        .child(COLUMN_FRIEND_EMAIL).getValue()));
                                gift.setUserNickname(String.valueOf(snapshot
                                        .child(COLUMN_FRIEND_NICKNAME).getValue()));
                                gift.setGiftBox(Integer.parseInt(String.valueOf(snapshot
                                        .child(COLUMN_GIFT_BOX).getValue())));
                                if (snapshot.child("timePlaced").getValue() != null) {
                                    gift.setTimePlaced(Long.parseLong(String.valueOf(snapshot
                                            .child("timePlaced").getValue())));
                                }
                                gift.setLocation(new LatLng(Double.parseDouble(String.valueOf(
                                        snapshot.child(COLUMN_LOCATION).child("latitude")
                                                .getValue())), Double.parseDouble(String.valueOf(
                                                        snapshot.child(COLUMN_LOCATION)
                                                                .child("longitude").getValue()))));

                                gifts.add(gift);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };

                    Util.databaseReference.child("gifts").addListenerForSingleValueEvent(listener);
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return gifts;
    }

    /* Method to fetch all the inventory items currently stored in SQLite
     * returns ArrayList with inventory item objects
     * returns empty ArrayList if table is empty */
    ArrayList<InventoryItem> fetchAllInventoryItems() {
        final ArrayList<InventoryItem> items = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns,
                        null, null, null, null, null);

                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        InventoryItem item = new InventoryItem();
                        cursorToInventoryItem(cursor, item);
                        items.add(item);
                        cursor.moveToNext();
                    }
                }

                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return items;
    }

    /* Method to fetch all the friends currently stored in SQLite
     * returns ArrayList with friend objects
     * returns empty ArrayList if table is empty */
    ArrayList<Friend> fetchAllFriends() {
        final ArrayList<Friend> friends = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                Cursor cursor = database.query(FRIEND_TITLE, friends_columns,
                        null, null, null, null, null);

                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    while (!cursor.isAfterLast()) {
                        Friend friend = new Friend();
                        cursorToFriend(cursor, friend);
                        friends.add(friend);
                        cursor.moveToNext();
                    }
                }

                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return friends;
    }

    /* Method to return a single Animal specified by the name
     * returns the animal if it exists
     * returns an empty animal object if it does not exist
     * queries SQLite, not firebase */
    Animal fetchAnimalByName(final String name) {
        final ArrayList<Animal> animals = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                String clause = COLUMN_ANIMAL_NAME + "='" + name + "'";
                Cursor cursor = database.query(ANIMAL_TITLE, animals_columns, clause,
                        null, null, null, null);

                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Animal animal = new Animal();
                    cursorToAnimal(cursor, animal);
                    animals.add(animal);
                }

                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // add animal to user's database if it's new to user
        if(animals.size() > 0) return animals.get(0);
        else return null;
    }

    /* Method to return a single inventory item specified by the name
     * returns the item if it exists
     * returns an empty item if it does not exist
     * queries SQLite, not firebase */
    InventoryItem fetchinventoryItemByName(final String name) {
        final ArrayList<InventoryItem> items = new ArrayList<>(0);
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getReadableDatabase();
                String clause = COLUMN_INVENTORY_NAME + "='" + name + "'";
                Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns, clause,
                        null, null, null, null);

                cursor.moveToFirst();
                InventoryItem item = new InventoryItem();
                if (cursor.getCount() > 0) {
                    cursorToInventoryItem(cursor, item);
                    items.add(item);
                }
                else{
                    if(name.equals("money")) {
                        item.setItemAmount(-1);
                        items.add(item);
                    }
                    else{
                        item.setItemAmount(0);
                        items.add(item);
                    }
                }

                cursor.close();
                database.close();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(items.size() > 0) return items.get(0);
        else return null;
    }

    // delete every row in every table (probably so you can remake a new one)
    void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MySQLiteHelper.GIFT_TITLE, null, null);
        database.delete(MySQLiteHelper.FRIEND_TITLE, null, null);
        database.delete(MySQLiteHelper.ANIMAL_TITLE, null, null);
        database.delete(MySQLiteHelper.INVENTORY_TITLE, null, null);
    }

    /* Goes through every table in SQLite, gets the flagged data, and inserts it to firebase */
    private void insertFlagged() {
        Runnable runnable = new Runnable() {
            public void run() {
                SQLiteDatabase database = getWritableDatabase();

                for (int i = 0; i < columns.size(); i++) {
                    String clause = COLUMN_FIREBASE_FLAG + "='1'";
                    Cursor cursor = database.query(titles[i], columns.get(i), clause,
                            null, null, null, null);

                    cursor.moveToFirst();
                    while (cursor.getCount() > 0 && !cursor.isAfterLast()) {
                        // friends that haven't been inserted
                        if (i == 0) {
                            Friend friend = new Friend();
                            cursorToFriend(cursor, friend);
                            String key = Util.databaseReference.child("users")
                                    .child(Util.userID).child("friends").push().getKey();
                            Util.databaseReference.child("users").
                                    child(Util.userID).child("friends").child(key).setValue(friend);
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_FIREBASE_FLAG, 0);
                            database.update(FRIEND_TITLE, values, COLUMN_FRIEND_EMAIL +
                                    "='" + friend.getEmail() + "'", null);
                            ContentValues id = new ContentValues();
                            id.put(COLUMN_FIREBASE_ID, key);
                            database.update(FRIEND_TITLE, id, COLUMN_FRIEND_EMAIL + "='"
                                    + friend.getEmail() + "'", null);
                        }

                        // gifts that haven't been inserted
                        else if (i == 1) {
                            Gift gift = new Gift();
                            cursorToGift(cursor, gift);
                            String key = Util.databaseReference.child("users")
                                    .child(Util.userID).child("gifts").push().getKey();
                            Util.databaseReference.child("users").
                                    child(Util.userID).child("gifts").child(key).setValue(gift);
                            ContentValues values = new ContentValues();
                            values.put(COLUMN_FIREBASE_FLAG, 0);
                            database.update(GIFT_TITLE, values, COLUMN_GIFT + "='" +
                                    gift.getGiftName() + "'", null);
                        }

                        // animals that haven't been inserted
                        else if (i == 2) {
                            Animal animal = new Animal();
                            cursorToAnimal(cursor, animal);
                            if (animal.getAnimalName()!=null && !animal.getAnimalName().equals("")){
                                Util.databaseReference.child("users").
                                        child(Util.userID).child("animals").child(animal
                                        .getAnimalName()).setValue(animal);
                                ContentValues values = new ContentValues();
                                values.put(COLUMN_FIREBASE_FLAG, 0);
                                database.update(ANIMAL_TITLE, values, COLUMN_ANIMAL_NAME
                                        + "='" + animal.getAnimalName() + "'", null);
                            }
                        }

                        // inventory items that haven't been inserted
                        else if (i == 3) {
                            InventoryItem item = new InventoryItem();
                            cursorToInventoryItem(cursor, item);
                            if (item.getItemName() != null && !item.getItemName().equals("")) {
                                Util.databaseReference.child("users").child(Util.userID)
                                        .child("items").child(item.getItemName()).setValue(item);
                                ContentValues values = new ContentValues();
                                values.put(COLUMN_FIREBASE_FLAG, 0);
                                database.update(INVENTORY_TITLE, values,
                                        COLUMN_INVENTORY_NAME + "='" +
                                                item.getItemName() + "'", null);
                            }
                        }
                    }
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* Turns a cursor into a single animal object
     * Usually you will pass in an empty animal and it will populate it. This makes it thread safe
     * cursor checking done before this method is called */
    private void cursorToAnimal(Cursor cursor, Animal animal) {
        animal.setAnimalName(cursor.getString(1));
        animal.setRarity(cursor.getInt(2));
        animal.setNumVisits(cursor.getInt(3));
        animal.setPersistence(cursor.getLong(4));
        animal.setPresent(cursor.getInt(6));
    }

    /* Turns a cursor into a single gift object
     * Usually you will pass in an empty gift and it will populate it. This makes it thread safe
     * cursor checking done before this method is called */
    private void cursorToGift(Cursor cursor, Gift gift) {
        gift.setId(cursor.getString(1));
        gift.setGiftName(cursor.getString(2));
        if (cursor.getInt(3) == 0) {
            gift.setSent(false);
        } else {
            gift.setSent(true);
        }

        gift.setFriendName(cursor.getString(4));
        gift.setTime(cursor.getLong(5));
        gift.setGiftBox(cursor.getInt(6));
        try {
            gift.setLocation(toLatLng(cursor.getBlob(7)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Turns a cursor into a single inventory item object
     * Usually you will pass in an empty inventory item and it will populate it. This makes it
     * thread safe cursor checking done before this method is called */
    private void cursorToInventoryItem(Cursor cursor, InventoryItem item) {
        item.setItemName(cursor.getString(1));
        item.setItemAmount(cursor.getInt(2));
        item.setPresent(cursor.getInt(3));
    }

    /* Turns a cursor into a single friend object
     * Usually you will pass in an empty friend and it will populate it. This makes it thread safe
     * cursor checking done before this method is called */
    private void cursorToFriend(Cursor cursor, Friend friend) {
        friend.setEmail(cursor.getString(1));
        friend.setNickname(cursor.getString(2));
        friend.setFirebaseId(cursor.getString(3));
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

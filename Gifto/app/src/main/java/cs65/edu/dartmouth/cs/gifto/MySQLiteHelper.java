package cs65.edu.dartmouth.cs.gifto;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    static final String FRIEND_TITLE = "friends";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FRIEND_NAME = "friendName";
    private static final String COLUMN_FRIEND_NICKNAME = "nickname";

    static final String GIFT_TITLE = "gifts";
    private static final String COLUMN_GIFT = "giftName";
    private static final String COLUMN_SENT = "sent";
    private static final String COLUMN_TOFROM = "toFrom";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";

    static final String ANIMAL_TITLE = "animals";
    private static final String COLUMN_ANIMAL_NAME = "animalName";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_VISITS = "visits";
    private static final String COLUMN_PERSISTENCE = "persistence";

    static final String INVENTORY_TITLE = "inventory";
    private static final String COLUMN_INVENTORY_NAME = "inventoryName";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";

    static final String MAP_GIFT_TITLE = "mapGift";
    private static final String COLUMN_MESSAGE = "message";

    private static final String COLUMN_FIREBASE_FLAG = "flag";


    private String[] friends_columns = { COLUMN_ID, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME, COLUMN_FIREBASE_FLAG };

    private String[] gifts_columns = { COLUMN_ID, COLUMN_GIFT, COLUMN_SENT, COLUMN_TOFROM,
            COLUMN_TIME, COLUMN_LOCATION, COLUMN_FIREBASE_FLAG };

    private String [] animals_columns = { COLUMN_ID, COLUMN_ANIMAL_NAME, COLUMN_RARITY,
            COLUMN_VISITS, COLUMN_PERSISTENCE, COLUMN_FIREBASE_FLAG };

    private String[] inventory_columns = { COLUMN_ID, COLUMN_INVENTORY_NAME,
            COLUMN_TYPE, COLUMN_AMOUNT, COLUMN_FIREBASE_FLAG };

    private String[] map_gifts_columns = { COLUMN_ID, COLUMN_GIFT, COLUMN_FRIEND_NAME,
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
    void insertGift(Gift gift) throws IOException {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        if (isOnline()) {
            Log.d("if", "online");
            Util.databaseReference.child("users").
                    child(Util.userID).child("gifts").child(gift.getGiftName()).setValue(gift);
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
        values.put(COLUMN_SENT, gift.isSent());
        values.put(COLUMN_TOFROM, gift.getFriendName());
        values.put(COLUMN_TIME, gift.getTime());
        values.put(COLUMN_LOCATION, toByte(gift.getLocation()));        // converting to byte throws IOException
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

    // increment the number of times an animal has been seen
//    void incrementVisits(final Animal animal) {
//        final DatabaseReference ref = Util.databaseReference.child("users")
//                .child(Util.userID).child("animals");
//
//        // need to check if they have seen the animal before first
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // if animal exists, then increment the counter and add to both databases
//                if (dataSnapshot.hasChild(animal.getAnimalName())) {
//                    animal.setNumVisits(animal.getNumVisits() + 1);
//                    insertAnimal(animal);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

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
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, item.getItemName());
        values.put(COLUMN_INVENTORY_NAME, item.getItemType());
        values.put(COLUMN_AMOUNT, item.getItemAmount());
        values.put(COLUMN_FIREBASE_FLAG, flagged);


        database.insert(INVENTORY_TITLE, null, values);
        database.close();
    }

    // insert map gift
    void insertMapGift(MapGift gift) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        int flagged = 1;
        if (isOnline()) {
            Log.d("if", "online");
            Util.databaseReference.child("gifts").push().setValue(gift);
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
        values.put(COLUMN_FRIEND_NAME, gift.getUserName());
        values.put(COLUMN_FRIEND_NICKNAME, gift.getUserNickname());
        values.put(COLUMN_MESSAGE, gift.getMessage());
        values.put(COLUMN_ANIMAL_NAME, gift.getAnimalName());
        try {
            values.put(COLUMN_LOCATION, toByte(gift.getLocation()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        values.put(COLUMN_TIME, gift.getTimePlaced());
        values.put(COLUMN_FIREBASE_FLAG, flagged);


        database.insert(MAP_GIFT_TITLE, null, values);
        database.close();
    }

    // Remove an entry by giving its index
    // title is the title of the database you want to use
    void removeEntry(String title, long rowIndex) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = new String[] { String.valueOf(rowIndex) };
        database.delete(title, whereClause, whereArgs);
        database.close();
    }

    ArrayList<Animal> fetchAllAnimals() {
        ArrayList<Animal> animals = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(ANIMAL_TITLE, animals_columns,
                null, null, null, null, null);

        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {
            Animal animal = cursorToAnimal(cursor);
            animals.add(animal);
            cursor.moveToNext();
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

    ArrayList<MapGift> fetchAllMapGifts() {
        ArrayList<MapGift> gifts = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(MAP_GIFT_TITLE, map_gifts_columns,
                null, null, null, null, null);

        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {
            MapGift gift = cursorToMapGift(cursor);
            gifts.add(gift);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        database.close();

        return gifts;
    }

    ArrayList<InventoryItem> fetchAllInventoryItems() {
        ArrayList<InventoryItem> items = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            InventoryItem item = cursorToInventoryItem(cursor);
            items.add(item);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();

        return items;
    }

    ArrayList<Friend> fetchAllFriends() {
        ArrayList<Friend> friends = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(FRIEND_TITLE, friends_columns,
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Friend friend = cursorToFriend(cursor);
            friends.add(friend);
            cursor.moveToNext();
        }

        cursor.close();
        database.close();

        return friends;
    }

    Animal fetchAnimalByName(String name) {
        Animal animal = new Animal();
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_ANIMAL_NAME + "='" + name + "'";
        Cursor cursor = database.query(ANIMAL_TITLE, animals_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        animal.setAnimalName(name);
        animal.setRarity(cursor.getInt(3));
        animal.setNumVisits(cursor.getInt(2));
        animal.setPersistence(cursor.getInt(4));

        cursor.close();
        database.close();

        return animal;
    }

    Gift fetchGiftByName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_GIFT + "='" + name + "'";
        Cursor cursor = database.query(GIFT_TITLE, gifts_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        Gift gift =  cursorToGift(cursor);

        cursor.close();
        database.close();

        return gift;
    }

    InventoryItem fetchinventoryItemByName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_INVENTORY_NAME + "='" + name + "'";
        Cursor cursor = database.query(INVENTORY_TITLE, inventory_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        InventoryItem item =  cursorToInventoryItem(cursor);

        cursor.close();
        database.close();

        return item;
    }

    Friend fetchFriendByName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_FRIEND_NAME + "='" + name + "'";
        Cursor cursor = database.query(FRIEND_TITLE, friends_columns, clause,
                null, null, null, null);

        cursor.moveToFirst();
        Friend friend = cursorToFriend(cursor);

        cursor.close();
        database.close();

        return friend;
    }

    MapGift fetchMapGiftByName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        String clause = COLUMN_GIFT + "='" + name + "'";
        Cursor cursor = database.query(MAP_GIFT_TITLE, map_gifts_columns, clause,
                null, null,null,null);

        cursor.moveToFirst();
        MapGift mapGift = cursorToMapGift(cursor);

        cursor.close();
        database.close();

        return mapGift;
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

    private void insertFlagged() {
        SQLiteDatabase database = getWritableDatabase();

        for (int i = 0; i < columns.size(); i++) {
            String clause = COLUMN_FIREBASE_FLAG + "='1'";
            Cursor cursor = database.query(titles[i], columns.get(i), clause,
                    null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (i == 0) {
                    Friend friend = cursorToFriend(cursor);
                    Util.databaseReference.child("users").
                            child(Util.userID).child("friends").child(friend.getName()).setValue(friend);
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_FIREBASE_FLAG, 0);
                    database.update(FRIEND_TITLE, values, COLUMN_FRIEND_NAME + "='" + friend.getName() + "'", null);
                } else if (i == 1) {
                    Gift gift = cursorToGift(cursor);
                    Util.databaseReference.child("users").
                            child(Util.userID).child("gifts").child(gift.getGiftName()).setValue(gift);
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

    private Animal cursorToAnimal(Cursor cursor) {
        Animal animal = new Animal();
        animal.setAnimalName(cursor.getString(1));
        animal.setRarity(cursor.getInt(2));
        animal.setNumVisits(cursor.getInt(3));
        animal.setPersistence(cursor.getLong(4));

        return animal;
    }

    private Gift cursorToGift(Cursor cursor) {
        Gift gift = new Gift();
        gift.setGiftName(cursor.getString(1));
        if (cursor.getInt(2) == 0) {
            gift.setSent(false);
        } else {
            gift.setSent(true);
        }

        gift.setFriendName(cursor.getString(3));
        gift.setTime(cursor.getLong(4));
        try {
            gift.setLocation(toLatLng(cursor.getBlob(5)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gift;
    }

    private MapGift cursorToMapGift(Cursor cursor) {
        MapGift gift = new MapGift();
        gift.setGiftName(cursor.getString(1));
        gift.setUserName(cursor.getString(2));
        gift.setUserNickname(cursor.getString(3));
        gift.setMessage(cursor.getString(4));
        gift.setAnimalName(cursor.getString(5));
        try {
            gift.setLocation(toLatLng(cursor.getBlob(6)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        gift.setTimePlaced(cursor.getLong(7));

        return gift;
    }

    private InventoryItem cursorToInventoryItem(Cursor cursor) {
        InventoryItem item = new InventoryItem();
        item.setItemName(cursor.getString(0));
        item.setItemType(cursor.getInt(1));
        item.setItemAmount(cursor.getInt(2));

        return item;
    }

    private Friend cursorToFriend(Cursor cursor) {
        Friend friend = new Friend();
        friend.setName(cursor.getString(1));
        friend.setNickname(cursor.getString(2));

        return friend;
    }

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

package cs65.edu.dartmouth.cs.gifto;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Oliver on 2/24/2018.
 *
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
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
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";

    static final String MAP_GIFT_TITLE = "mapGift";
    private static final String COLUMN_MESSAGE = "message";


    private String[] friends_columns = { COLUMN_ID, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME };

    private String[] gifts_columns = { COLUMN_ID, COLUMN_GIFT, COLUMN_SENT, COLUMN_TOFROM,
            COLUMN_TIME, COLUMN_LOCATION };

    private String [] animals_columns = { COLUMN_ID, COLUMN_ANIMAL_NAME, COLUMN_RARITY,
            COLUMN_VISITS, COLUMN_PERSISTENCE };

    private String[] inventory_columns = { COLUMN_ID, COLUMN_TYPE, COLUMN_AMOUNT };

    private String[] map_gifts_columns = { COLUMN_ID, COLUMN_GIFT, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME, COLUMN_MESSAGE, COLUMN_ANIMAL_NAME,COLUMN_LOCATION,COLUMN_TIME};

    private static final String CREATE_FRIENDS_TABLE = "create table " + FRIEND_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FRIEND_NAME + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT );";

    private static final String CREATE_GIFTS_TABLE = "create table " + GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_SENT + " INTEGER NOT NULL, " +
            COLUMN_TOFROM + " TEXT, " +
            COLUMN_TIME + " DATETIME NOT NULL, " +
            COLUMN_LOCATION + " BLOB );";

    private static final String CREATE_ANIMAL_TABLE = "create table " + ANIMAL_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_RARITY + " INTEGER NOT NULL, " +
            COLUMN_VISITS + " INTEGER NOT NULL, " +
            COLUMN_PERSISTENCE + " INTEGER );";

    private static final String CREATE_INVENTORY_TABLE = "create table " + INVENTORY_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_AMOUNT + " INTEGER NOT NULL );";

    private static final String CREATE_MAP_GIFTS_TABLE = "create table " + MAP_GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_FRIEND_NAME + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT, " +
            COLUMN_MESSAGE + " TEXT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_LOCATION + " BLOB, " +
            COLUMN_TIME + " DATETIME NOT NULL );";


    // Constructor
    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        Util.databaseReference.child("users").
                child(Util.userID).child("friends").child(friend.getName()).setValue(friend);

        // now insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FRIEND_NAME, friend.getName());
        values.put(COLUMN_FRIEND_NICKNAME, friend.getNickname());

        database.insert(FRIEND_TITLE, null, values);
        database.close();
    }

    // Insert a gift
    void insertGift(Gift gift) throws IOException {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        Util.databaseReference.child("users").
                child(Util.userID).child("gifts").child(gift.getGiftName()).setValue(gift);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GIFT, gift.getGiftName());
        values.put(COLUMN_SENT, gift.isSent());
        values.put(COLUMN_TOFROM, gift.getFriendName());
        values.put(COLUMN_TIME, gift.getTime());
        values.put(COLUMN_LOCATION, toByte(gift.getLocation()));        // converting to byte throws IOException

        database.insert(GIFT_TITLE, null, values);
        database.close();
    }

    // Insert an animal
    void insertAnimal(Animal animal) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        Util.databaseReference.child("users").
                child(Util.userID).child("animals").child(animal.getAnimalName()).setValue(animal);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANIMAL_NAME, animal.getAnimalName());
        values.put(COLUMN_RARITY, animal.getRarity());
        values.put(COLUMN_VISITS, animal.getNumVisits());
        values.put(COLUMN_PERSISTENCE, animal.getPersistence());

        database.insert(ANIMAL_TITLE, null, values);
        database.close();
    }

    // increment the number of times an animal has been seen
    void incrementVisits(final Animal animal) {
        final DatabaseReference ref = Util.databaseReference.child("users")
                .child(Util.userID).child("animals");

        // need to check if they have seen the animal before first
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if animal exists, then increment the counter and add to both databases
                if (dataSnapshot.hasChild(animal.getAnimalName())) {
                    animal.setNumVisits(animal.getNumVisits() + 1);
                    insertAnimal(animal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // insert inventory item
    // also use this if you want to change the amount of an item
    void insertInventory(InventoryItem item) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        Util.databaseReference.child("users").
                child(Util.userID).child("items").child(item.getItemType()).setValue(item);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, item.getItemType());
        values.put(COLUMN_AMOUNT, item.getItemAmount());

        database.insert(INVENTORY_TITLE, null, values);
        database.close();
    }

    // insert map gift
    void insertMapGift(MapGift gift) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        Util.databaseReference.child("gifts").push().setValue(gift);

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

    //TODO return all rows in the tables

    // delete every row in every table (probably so you can remake a new one)
    void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MySQLiteHelper.GIFT_TITLE, null, null);
        database.delete(MySQLiteHelper.FRIEND_TITLE, null, null);
        database.delete(MySQLiteHelper.ANIMAL_TITLE, null, null);
        database.delete(MySQLiteHelper.INVENTORY_TITLE, null, null);
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

    // convert a LatLng to a byte array to be stored as a BLOB
    @TargetApi(19)
    private byte[] toByte(LatLng latLng) throws IOException {
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
    private static LatLng toLatLng(byte[] bytes) throws IOException, ClassNotFoundException {
        ArrayList<Double> list = new ArrayList<>(0);
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes)){
            try(DataInputStream in = new DataInputStream(bais)){
                while (in.available() > 0) {
                    list.add(in.readDouble());
                }
            }
        }

        return new LatLng(list.get(0), list.get(1));
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

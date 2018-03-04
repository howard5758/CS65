package cs65.edu.dartmouth.cs.gifto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;

/**
 * Created by Oliver on 2/24/2018.
 *
 * This class is used for storing all the Firebase data as well as handling specific Firebase tasks
 * The top four variables are assigned in onCreate of MainActivity and should always be used
 *   in lieu of local variables
 */

class Util {
    static FirebaseAuth firebaseAuth;
    static FirebaseUser firebaseUser;
    static DatabaseReference databaseReference;
    static String userID;
    static String name;
    static String email;

    static void showDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setTitle("Error!");
        builder.setPositiveButton("ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static void showActivity(Context context, Class c) {
        Intent intent = new Intent(context, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /* checks if the user is online
     * used to see if should insert into firebase or just mark flag in SQL */
    static boolean isOnline() {
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

    // name = the String used for things like Gift.giftName, Animal.animalName, etc.
    // returns the image that this gift/animal/toy/whatever is associated with
    public static int getImageIdFromName(String name){
        switch (name){
            case "banana":
                return R.drawable.banana;
            case "tuna":
                return R.drawable.tuna;
            case "pool":
                return R.drawable.pool;
            case "tree":
                return R.drawable.tree;
            case "tennis ball":
                return (R.drawable.tennis_ball);
            case "alligator":
                return (R.drawable.alligator);
            case "cat":
                return (R.drawable.cat);
            case "dog":
                return (R.drawable.dog_side);
            case "kangaroo":
                return (R.drawable.kangaroo);
            case "monkey":
                return (R.drawable.monkey);
            case "owl":
                return (R.drawable.owl);
            case "squirrel":
                return (R.drawable.squirrel);
            case "American Shorthair":
                return (R.drawable.shorthair);
            default:
                return (R.drawable.tiffany_box);
        }
    }

}

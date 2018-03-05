package cs65.edu.dartmouth.cs.gifto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
    static Uri photo;
    static boolean completed = false;
    static boolean nightTime = false;
    static double angle = 0;
    static boolean shaking;

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
            case "alligator":
                return (R.drawable.alligator);
            case "sleeping alligator":
                return R.drawable.alligator_sleep;
            case "blue bag":
                return R.drawable.bag_blue;
            case "green bag":
                return R.drawable.bag_green;
            case "pink bag":
                return R.drawable.bag_pink;
            case "watermelon bag":
                return R.drawable.bag_watermelon;
            case "red bag":
                return R.drawable.bag_red;
            case "banana":
                return R.drawable.banana;
            case "bat":
                return R.drawable.bat;
            case "sleeping bat":
                return R.drawable.bat_sleep;
            case "bg_magic":
                return R.drawable.bg_magic;
            case "bg_plain":
                return R.drawable.bg_plain;
            case "bg_savannah":
                return R.drawable.bg_savannah;
            case "blue box":
                return R.drawable.box_blue;
            case "green box":
                return R.drawable.box_green;
            case "purple box":
                return R.drawable.box_purple;
            case "red box":
                return R.drawable.box_red;
            case "yellow box":
                return R.drawable.box_yellow;
            case "boxing glove":
                return R.drawable.boxing_glove;
            case "butterfly teaser":
                return R.drawable.butterfly_teaser;
            case "cat":
                return (R.drawable.cat);
            case "cat head":
                return (R.drawable.cat_head);
            case "sleeping cat":
                return (R.drawable.cat_sleep);
            case "white cushion":
                return (R.drawable.cursion_white);
            case "blue cloud cushion":
                return (R.drawable.cusion_cloud_blue);
            case "orange cloud cushion":
                return (R.drawable.cusion_cloud_orange);
            case "green cushion":
                return (R.drawable.cusion_green);
            case "pink cushion":
                return (R.drawable.cusion_pink);
            case "purple cushion":
                return (R.drawable.cusion_purple);
            case "yellow cushion":
                return (R.drawable.cusion_yellow);
            case "Corgi":
                return (R.drawable.dog);
            case "Corgi back":
                return (R.drawable.dog_back);
            case "sleeping Corgi":
                return (R.drawable.dog_sleep);
            case "Corgi side":
                return (R.drawable.dog_side);
            case "flower pot 1":
                return (R.drawable.flower_pot1);
            case "flower pot 2":
                return (R.drawable.flower_pot2);
            case "flower pot 3":
                return (R.drawable.flower_pot3);
            case "food mat":
                return (R.drawable.food_mat);
            case "giant acorn":
                return (R.drawable.giant_acorn);
            case "gift_icon":
                return (R.drawable.gift_icon);
            case "grapes":
                return (R.drawable.grapes);
            case "hotdog":
                return (R.drawable.hotdog);
            case "kangaroo":
                return (R.drawable.kangaroo);
            case "sleeping kangaroo":
                return (R.drawable.kangaroo_sleep);
            case "lucky bag":
                return (R.drawable.lucky_bag);
            case "magic egg":
                return (R.drawable.magic_egg);
            case "blue mitten":
                return (R.drawable.mitten_blue);
            case "yellow mitten":
                return (R.drawable.mitten_yellow);
            case "black mole bag":
                return (R.drawable.mole_black);
            case "blue mole bag":
                return (R.drawable.mole_blue);
            case "green mole bag":
                return (R.drawable.mole_green);
            case "purple mole bag":
                return (R.drawable.mole_purple);
            case "monkey":
                return (R.drawable.monkey);
            case "monkey head":
                return (R.drawable.monkey_head);
            case "sleeping monkey":
                return (R.drawable.monkey_sleep);
            case "monkey on tree with banana":
                return (R.drawable.monkey_tree_banana);
            case "monkey on tree":
                return (R.drawable.monkey_tree);
            case "owl":
                return (R.drawable.owl);
            case "sleeping owl":
                return (R.drawable.owl_sleep);
            case "pile of leaves":
                return (R.drawable.pile_leaves);
            case "pool":
                return (R.drawable.pool);
            case "brown pot":
                return (R.drawable.pot_brown);
            case "green pot":
                return (R.drawable.pot_green);
            case "salad":
                return (R.drawable.salad);
            case "American Shorthair head":
                return (R.drawable.shorthair_head);
            case "sleeping American Shorthair":
                return (R.drawable.shorthair_sleep);
            case "American Shorthair":
                return (R.drawable.shorthair);
            case "Siamese Cat":
                return (R.drawable.siamese);
            case "sleeping Siamese Cat":
                return (R.drawable.siamese_sleep);
            case "Siamese Cat head":
                return (R.drawable.siamese_head);
            case "squirrel":
                return (R.drawable.squirrel);
            case "sleeping squirrel":
                return (R.drawable.squirrel_sleep);
            case "sleeping pink squirrel":
                return (R.drawable.squirrel_pink_sleep);
            case "pink squirrel":
                return (R.drawable.squirrel_pink);
            case "tennis ball":
                return (R.drawable.tennis_ball);
            case "tiffany box":
                return (R.drawable.tiffany_box);
            case "blue toy fish":
                return (R.drawable.toy_fish_blue);
            case "yellow toy fish":
                return (R.drawable.toy_fish_yellow);
            case "toy reindeer":
                return (R.drawable.toy_reindeer);
            case "tuna":
                return R.drawable.tuna;
            case "tree":
                return R.drawable.tree;
            case "trunk":
                return R.drawable.trunk;
            case "unicorn with grass":
                return R.drawable.unicorn_grass;
            case "unicorn":
                return R.drawable.unicorn;
            case "sleeping unicorn":
                return R.drawable.unicorn_sleep;
            case "giggling unicorn":
                return R.drawable.unicorn_smile;
            case "envelope":
                return R.drawable.envelope;
            case "dog food bag":
                return R.drawable.dog_food_bag;

            default:
                return (R.drawable.tiffany_box);

        }
    }


}

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
            case "sleeping alligator":
                return R.drawable.alligator_sleep;
            case "sleeping bat":
                return R.drawable.bat_sleep;
            case "bat":
                return R.drawable.bat;
            case "cat's head":
                return R.drawable.cat_head;
            case "sleeping cat":
                return R.drawable.cat_sleep;
            case "sleeping kangaroo":
                return R.drawable.kangaroo_sleep;
            //ANIMAL_TO_TYPE.put("monkey's head", 1);
            //ANIMAL_TO_TYPE.put("sleeping monkey", 1);
            //ANIMAL_TO_TYPE.put("monkey on tree with banana", 1);
            //ANIMAL_TO_TYPE.put("monkey on tree", 1);
//            ANIMAL_TO_TYPE.put("sleeping owl", 0);
//            ANIMAL_TO_TYPE.put("American Shorthair's head", 1);
//            ANIMAL_TO_TYPE.put("Sleeping American Shorthair", 1);
//            ANIMAL_TO_TYPE.put("sleeping Siamese Cat", 1);
//            ANIMAL_TO_TYPE.put("Siamese Cat", 1);
//            ANIMAL_TO_TYPE.put("Siamese Cat's head", 1);
//            ANIMAL_TO_TYPE.put("sleeping pink squirrel", 0);
//            ANIMAL_TO_TYPE.put("sleeping squirrel", 0);
//            ANIMAL_TO_TYPE.put("unicorn with grass", 2);
//            ANIMAL_TO_TYPE.put("sleeping unicorn", 2);
//            ANIMAL_TO_TYPE.put("giggling unicorn", 2);
            case "unicorn":
                return R.drawable.unicorn;
//            ANIMAL_TO_TYPE.put("Corgi's back", 1);
            case "Corgi":
                return R.drawable.dog;
//            ANIMAL_TO_TYPE.put("sleeping Corgi", 1);
//            ANIMAL_TO_TYPE.put("Corgi's side", 1);
            case "boxing glove":
                return R.drawable.boxing_glove;
            case "butterfly teaser":
                return R.drawable.butterfly_teaser;
            case "white cursion":
                return R.drawable.cursion_white;
            case "blue cloud cusion":
                return R.drawable.cusion_cloud_blue;
//            ITEM_TO_TYPE.put("orange cloud cusion",1);
//            ITEM_TO_TYPE.put("green cusion",1);
//            ITEM_TO_TYPE.put("pink cusion",1);
//            ITEM_TO_TYPE.put("purple cusion",1);
//            ITEM_TO_TYPE.put("yellow cusion",1);
//            ITEM_TO_TYPE.put("flower pot 1",1);
//            ITEM_TO_TYPE.put("flower pot 2",1);
//            ITEM_TO_TYPE.put("flower pot 3",1);
//            ITEM_TO_TYPE.put("giant acorn",2);
//            ITEM_TO_TYPE.put("grapes",0);
//            ITEM_TO_TYPE.put("hotdog",0);
//            ITEM_TO_TYPE.put("magic egg",1);
//            ITEM_TO_TYPE.put("blue mitten",1);
//            ITEM_TO_TYPE.put("yellow mitten",1);
//            ITEM_TO_TYPE.put("brown pot",1);
//            ITEM_TO_TYPE.put("green pot",1);
//            ITEM_TO_TYPE.put("salad",0);
//            ITEM_TO_TYPE.put("yellow cusion",1);
//            ITEM_TO_TYPE.put("blue toy fish",1);
//            ITEM_TO_TYPE.put("yellow toy fish",1);
//            ITEM_TO_TYPE.put("toy reindeer",1);
            case "blue bag":
                return R.drawable.bag_blue;
            case "green bag":
                return R.drawable.bag_green;
            case "pink bag":
                return R.drawable.bag_pink;
            case "red bag":
                return R.drawable.bag_red;
            case "watermelon bag":
                return R.drawable.bag_watermelon;
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
            case "black mole bag":
                return R.drawable.mole_black;
            case "blue mole bag":
                return R.drawable.mole_blue;
            case "green mole bag":
                return R.drawable.mole_green;
            case "purple mole bag":
                return R.drawable.mole_purple;
            case "tiffany box":
                return R.drawable.gift_icon;
//            GIFT_TO_TYPE.put("god food bag",1);
            default:
                return (R.drawable.gift_icon);
        }
    }

}

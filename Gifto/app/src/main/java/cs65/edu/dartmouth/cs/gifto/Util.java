package cs65.edu.dartmouth.cs.gifto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Oliver on 2/24/2018.
 *
 */

class Util {
    static FirebaseAuth firebaseAuth;
    static FirebaseUser firebaseUser;
    static DatabaseReference databaseReference;
    static String userID;

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

}

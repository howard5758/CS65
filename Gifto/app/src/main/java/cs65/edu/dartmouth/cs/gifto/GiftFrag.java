package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Oliver on 2/11/2018.
 *
 */

public class GiftFrag extends Activity {
    private DatabaseReference giftsHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("collection", "onCreate");
        setContentView(R.layout.collection);

//        giftsHistory = Util.databaseReference.child("users").child(Util.userID).child("gifts");

    }

}

package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Oliver on 2/11/2018.
 *
 */

public class GiftFrag extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("collection", "onCreate");
        setContentView(R.layout.collection);
    }
//    public View onCreateView(LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.gift_fragment, container, false);
//
//        return view;
//
//    }
}

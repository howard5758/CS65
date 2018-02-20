package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Oliver on 2/19/2018.
 *
 */

public class Collection extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);
        if (getIntent().getBooleanExtra("food", false)) {
            Toast.makeText(this, "Food", Toast.LENGTH_SHORT).show();
        } else if (getIntent().getBooleanExtra("gifts", false)) {
            Toast.makeText(this, "Gifts", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Animals", Toast.LENGTH_SHORT).show();
        }
    }
//    public View onCreateView(LayoutInflater inflater,
//                        ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.collection, container, false);
//
//        return view;
//    }
}

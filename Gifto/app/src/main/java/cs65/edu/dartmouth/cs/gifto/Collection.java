package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Oliver on 2/19/2018.
 *
 */

public class Collection extends ListActivity {

    TextView title;
    ArrayList<String> pngList;
    collection_adapter collection_adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        pngList = new ArrayList<>();
        title = (TextView) findViewById(R.id.list_title);

        if (getIntent().getBooleanExtra("food", false)) {

            title.setText("FOOD");

        } else if (getIntent().getBooleanExtra("gifts", false)) {

            title.setText("GIFTS");

        } else {

            title.setText("PETS");
        }

        pngList.add("test1");
        pngList.add("test2");
        pngList.add("test3");
        collection_adapter = new collection_adapter(this, R.layout.list_collection, pngList);
        setListAdapter(collection_adapter);

    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);


        Intent intent = new Intent(this, purchase_screen.class);
        startActivity(intent);
    }

    public class collection_adapter extends ArrayAdapter<String>{

        LayoutInflater inflater;

        public collection_adapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // make sure view is never null
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_collection, parent, false);
            }

            TextView namee = (TextView) view.findViewById(R.id.first_line);
            TextView price = (TextView) view.findViewById(R.id.second_line);
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            image.setImageResource(R.drawable.alligator);
            namee.setText(getItem(position));
            price.setText("0");

            return view;
        }


    }

}

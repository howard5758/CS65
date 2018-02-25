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

    Boolean goodies, gifts, pets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        goodies = getIntent().getBooleanExtra("goodies", false);
        gifts = getIntent().getBooleanExtra("gifts", false);
        pets = getIntent().getBooleanExtra("pets", false);

        pngList = new ArrayList<>();
        title = (TextView) findViewById(R.id.list_title);

        if (goodies) {

            title.setText("GOODIES");
            pngList.add("banana");
            pngList.add("tuna");
            pngList.add("pool");
            pngList.add("tree");
            pngList.add("tennis ball");

        } else if (gifts) {

            title.setText("GIFTS");


        } else {

            title.setText("PETS");
            pngList.add("alligator");
            pngList.add("cat");
            pngList.add("dog");
            pngList.add("kangaroo");
            pngList.add("monkey");
            pngList.add("owl");
            pngList.add("squirrel");

        }

        collection_adapter = new collection_adapter(this, R.layout.list_collection, pngList);
        setListAdapter(collection_adapter);

    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);


        Intent intent = new Intent(this, purchase_screen.class);
        intent.putExtra("object", pngList.get(position));
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
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            switch (getItem(position)){
                case "banana":
                    image.setImageResource(R.drawable.banana);
                    break;
                case "tuna":
                    image.setImageResource(R.drawable.tuna);
                    break;
                case "pool":
                    image.setImageResource(R.drawable.pool);
                    break;
                case "tree":
                    image.setImageResource(R.drawable.tree);
                    break;
                case "tennis ball":
                    image.setImageResource(R.drawable.tennis_ball);
                    break;
                case "alligator":
                    image.setImageResource(R.drawable.alligator);
                    break;
                case "cat":
                    image.setImageResource(R.drawable.cat);
                    break;
                case "dog":
                    image.setImageResource(R.drawable.dog_side);
                    break;
                case "kangaroo":
                    image.setImageResource(R.drawable.kangaroo);
                    break;
                case "monkey":
                    image.setImageResource(R.drawable.monkey);
                    break;
                case "owl":
                    image.setImageResource(R.drawable.owl);
                    break;
                case "squirrel":
                    image.setImageResource(R.drawable.squirrel);
                    break;
            }


            namee.setText(getItem(position));

            return view;
        }


    }

}

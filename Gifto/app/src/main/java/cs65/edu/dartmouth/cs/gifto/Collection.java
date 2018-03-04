package cs65.edu.dartmouth.cs.gifto;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Oliver on 2/19/2018.
 *
 */

public class Collection extends ListActivity {

    TextView title;
    item_adapter item_adapter;
    pet_adapter pet_adapter;

    Boolean goodies, gifts, pets, selection;

    public ArrayList<InventoryItem> goodiesCollection;
    public ArrayList<Animal> petCollection;

    MySQLiteHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        helper = new MySQLiteHelper(this);
        petCollection = new ArrayList<>();
        goodiesCollection = new ArrayList<>();
        listInit();


        goodies = getIntent().getBooleanExtra("goodies", false);
        gifts = getIntent().getBooleanExtra("gifts", false);
        pets = getIntent().getBooleanExtra("pets", false);
        selection = getIntent().getBooleanExtra("selection", false);

        title = (TextView) findViewById(R.id.list_title);

        if (goodies) {

            title.setText("GOODIES");
            item_adapter = new item_adapter(this, R.layout.list_collection, goodiesCollection);
            setListAdapter(item_adapter);


        } else if (gifts) {

            title.setText("GIFTS");


        } else if (pets){

            title.setText("PETS");
            pet_adapter = new pet_adapter(this, R.layout.list_collection, petCollection);
            setListAdapter(pet_adapter);
        } else if(selection){
            title.setText("Choose an item to place!");
            int loc_type = getIntent().getIntExtra("loc_type", 0);
            ArrayList<InventoryItem> selection_list = helper.fetchAllInventoryItems();
            Iterator<InventoryItem> iter = selection_list.iterator();

            while(iter.hasNext()) {
                InventoryItem i = iter.next();
                if(i.getItemType() != loc_type){
                    iter.remove();
                }
            }
            item_adapter = new item_adapter(this, R.layout.list_collection, selection_list);
            setListAdapter(item_adapter);
        }

    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);

        if(selection){
            finish();
        }
        else {
            Intent intent = new Intent(this, purchase_screen.class);
            if (goodies) {
                intent.putExtra("name", goodiesCollection.get(position).getItemName());
                intent.putExtra("type", "goodies");
                intent.putExtra("actual_object", goodiesCollection.get(position));
            } else if (pets) {
                intent.putExtra("name", petCollection.get(position).getAnimalName());
                intent.putExtra("type", "pets");
                intent.putExtra("actual_object", petCollection.get(position));
            }

            startActivity(intent);
        }
    }

    public class item_adapter extends ArrayAdapter<InventoryItem>{

        LayoutInflater inflater;

        public item_adapter(Context context, int textViewResourceId, ArrayList<InventoryItem> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // make sure view is never null
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_collection, parent, false);
            }


            TextView namee = (TextView) view.findViewById(R.id.first_line);
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            image.setImageResource(Util.getImageIdFromName(getItem(position).getItemName()));

            namee.setText(getItem(position).getItemName() + " amount: " + getItem(position).getItemAmount());

            return view;
        }


    }


    public class gift_adapter extends ArrayAdapter<Gift>{

        LayoutInflater inflater;

        public gift_adapter(Context context, int textViewResourceId, ArrayList<Gift> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // make sure view is never null
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_collection, parent, false);
            }


            TextView namee = (TextView) view.findViewById(R.id.first_line);
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            image.setImageResource(Util.getImageIdFromName(getItem(position).getGiftName()));

            namee.setText(getItem(position).getGiftName());

            return view;
        }
    }

    public class pet_adapter extends ArrayAdapter<Animal>{

        LayoutInflater inflater;

        public pet_adapter(Context context, int textViewResourceId, ArrayList<Animal> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // make sure view is never null
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_collection, parent, false);
            }


            TextView namee = (TextView) view.findViewById(R.id.first_line);
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            image.setImageResource(Util.getImageIdFromName(getItem(position).getAnimalName()));

            namee.setText(getItem(position).getAnimalName());

            return view;
        }


    }

    public void listInit() {
        Animal a_1 = new Animal();
        a_1.setAnimalName("alligator");
        Animal a_2 = new Animal();
        a_2.setAnimalName("cat");
        Animal a_3 = new Animal();
        a_3.setAnimalName("dog");
        Animal a_4 = new Animal();
        a_4.setAnimalName("kangaroo");
        Animal a_5 = new Animal();
        a_5.setAnimalName("monkey");
        Animal a_6 = new Animal();
        a_6.setAnimalName("owl");
        Animal a_7 = new Animal();
        a_7.setAnimalName("squirrel");

        petCollection.add(a_1);
        petCollection.add(a_2);
        petCollection.add(a_3);
        petCollection.add(a_4);
        petCollection.add(a_5);
        petCollection.add(a_6);
        petCollection.add(a_7);

//        InventoryItem i_1 = new InventoryItem();
//        i_1.setItemName("banana");
//        i_1.setItemType(1);
//        InventoryItem i_2 = new InventoryItem();
//        i_2.setItemName("pool");
//        i_2.setItemType(2);
//        InventoryItem i_3 = new InventoryItem();
//        i_3.setItemName("tennis ball");
//        i_3.setItemType(3);
//        InventoryItem i_4 = new InventoryItem();
//        i_4.setItemName("tree");
//        i_4.setItemType(2);
//        InventoryItem i_5 = new InventoryItem();
//        i_5.setItemName("tuna");
//        i_5.setItemType(1);
//
//        goodiesCollection.add(i_1);
//        goodiesCollection.add(i_2);
//        goodiesCollection.add(i_3);
//        goodiesCollection.add(i_4);
//        goodiesCollection.add(i_5);

    }

}

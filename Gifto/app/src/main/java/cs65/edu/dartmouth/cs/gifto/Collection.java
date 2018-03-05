package cs65.edu.dartmouth.cs.gifto;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    public static TextView title;
    item_adapter item_adapter;
    pet_adapter pet_adapter;

    Boolean goodies, gifts, pets, selection;

    public ArrayList<String> goodiesCollection;
    public ArrayList<Animal> petCollection;
    ArrayList<String> selection_list;

    int loc_type;

    MySQLiteHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        helper = new MySQLiteHelper(this);
        petCollection = new ArrayList<>();
        goodiesCollection = new ArrayList<>();
        selection_list = new ArrayList<>();
        goodiesCollection.addAll(Globals.ITEM_TO_TYPE.keySet());
        Log.d("master", goodiesCollection.get(5));
        selection_list.addAll(Globals.ITEM_TO_TYPE.keySet());
        //listInit();


        goodies = getIntent().getBooleanExtra("goodies", false);
        gifts = getIntent().getBooleanExtra("gifts", false);
        pets = getIntent().getBooleanExtra("pets", false);
        selection = getIntent().getBooleanExtra("selection", false);

        title = (TextView) findViewById(R.id.list_title);

        if (goodies) {
            InventoryItem moneyy = helper.fetchinventoryItemByName("money");
            if (moneyy.getItemAmount() == -1){
                moneyy.setItemName("money");
                moneyy.setItemAmount(300);
                helper.insertInventory(moneyy, true);
            }
            title.setText("GOODIES" + String.valueOf(helper.fetchinventoryItemByName("money").getItemAmount()));
            item_adapter = new item_adapter(this, R.layout.list_collection, goodiesCollection);
            setListAdapter(item_adapter);


        } else if (gifts) {

            title.setText("GIFTS");


        } else if (pets){

            title.setText("PETS");
            petCollection = helper.fetchAllAnimals();
            pet_adapter = new pet_adapter(this, R.layout.list_collection, petCollection);
            setListAdapter(pet_adapter);



        } else if(selection){
            title.setText("Choose an item to place!");
            loc_type = getIntent().getIntExtra("loc_type", 0);

            Iterator<String> iter = selection_list.iterator();

            while(iter.hasNext()) {
                String i = iter.next();
                if(loc_type == 1 && Globals.ITEM_TO_TYPE.get(i) != 0){
                    iter.remove();
                }
                else if(loc_type == 2 && Globals.ITEM_TO_TYPE.get(i) == 0){
                    iter.remove();
                }
                else if(loc_type == 3 && Globals.ITEM_TO_TYPE.get(i) == 0){
                    iter.remove();
                }
                else if(loc_type == 4 && Globals.ITEM_TO_TYPE.get(i) != 1){
                    iter.remove();
                }
                else{
                    if(helper.fetchinventoryItemByName(i).getItemAmount() == 0){
                        iter.remove();
                    }
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
            InventoryItem temp = helper.fetchinventoryItemByName(selection_list.get(position));
            int prev = temp.getItemAmount();
            temp.setItemAmount(prev);


            if (loc_type == 1){
                if(!Garden.item1_name.getText().equals("")){
                    temp.setItemAmount(prev - 1);
                    InventoryItem prev_item = helper.fetchinventoryItemByName((String)Garden.item1_name.getText());
                    prev_item.setPresent(-1);
                    helper.removeInventoryItem(prev_item.getItemName());
                    helper.insertInventory(prev_item, true);
                }
                if(!Garden.animal1_name.getText().equals("")){
                    Animal prev_animal = helper.fetchAnimalByName((String)Garden.animal1_name.getText());
                    prev_animal.setPresent(-1);
                    helper.removeAnimal(prev_animal.getAnimalName());
                    helper.insertAnimal(prev_animal, true);
                    Garden.animal1_name.setText("");
                    Garden.pet1.setImageDrawable(null);
                }
                Garden.loc1.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                Garden.item1_name.setText(selection_list.get(position));
                temp.setPresent(1);
            }
            else if(loc_type == 2){
                if(!Garden.item2_name.getText().equals("")){
                    InventoryItem prev_item = helper.fetchinventoryItemByName((String)Garden.item2_name.getText());
                    prev_item.setPresent(-1);
                    helper.removeInventoryItem(prev_item.getItemName());
                    helper.insertInventory(prev_item, true);
                }
                if(!Garden.animal2_name.getText().equals("")){
                    Animal prev_animal = helper.fetchAnimalByName((String)Garden.animal2_name.getText());
                    prev_animal.setPresent(-1);
                    helper.removeAnimal(prev_animal.getAnimalName());
                    helper.insertAnimal(prev_animal, true);
                    Garden.animal2_name.setText("");
                    Garden.pet2.setImageDrawable(null);
                }
                Garden.loc2.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                Garden.item2_name.setText(selection_list.get(position));
                temp.setPresent(2);
            }
            else if(loc_type == 3){
                if(!Garden.item3_name.getText().equals("")){
                    InventoryItem prev_item = helper.fetchinventoryItemByName((String)Garden.item3_name.getText());
                    prev_item.setPresent(-1);
                    helper.removeInventoryItem(prev_item.getItemName());
                    helper.insertInventory(prev_item, true);
                }
                if(!Garden.animal3_name.getText().equals("")){
                    Animal prev_animal = helper.fetchAnimalByName((String)Garden.animal3_name.getText());
                    prev_animal.setPresent(-1);
                    helper.removeAnimal(prev_animal.getAnimalName());
                    helper.insertAnimal(prev_animal, true);
                    Garden.animal3_name.setText("");
                    Garden.pet3.setImageDrawable(null);
                }
                Garden.loc3.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                Garden.item3_name.setText(selection_list.get(position));
                temp.setPresent(3);
            }
            else if(loc_type == 4) {
                if(!Garden.item4_name.getText().equals("")){
                    InventoryItem prev_item = helper.fetchinventoryItemByName((String)Garden.item4_name.getText());
                    prev_item.setPresent(-1);
                    helper.removeInventoryItem(prev_item.getItemName());
                    helper.insertInventory(prev_item, true);
                }
                if(!Garden.animal4_name.getText().equals("")){
                    Animal prev_animal = helper.fetchAnimalByName((String)Garden.animal4_name.getText());
                    prev_animal.setPresent(-1);
                    helper.removeAnimal(prev_animal.getAnimalName());
                    helper.insertAnimal(prev_animal, true);
                    Garden.animal4_name.setText("");
                    Garden.pet4.setImageDrawable(null);
                }
                Garden.loc4.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                Garden.item4_name.setText(selection_list.get(position));
                temp.setPresent(4);
            }
            helper.removeInventoryItem(selection_list.get(position));
            helper.insertInventory(temp, true);
            finish();
        }
        else {
            Intent intent = new Intent(this, purchase_screen.class);
            if (goodies) {
                intent.putExtra("name", goodiesCollection.get(position));
                intent.putExtra("type", "goodies");

            } else if (pets) {
                intent.putExtra("name", petCollection.get(position).getAnimalName());
                intent.putExtra("type", "pets");

            }

            startActivity(intent);
        }
    }

    public class item_adapter extends ArrayAdapter<String>{

        LayoutInflater inflater;

        public item_adapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View view, ViewGroup parent) {

            // make sure view is never null
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_collection, parent, false);
            }


            TextView namee = (TextView) view.findViewById(R.id.first_line);
            ImageView image = (ImageView) view.findViewById(R.id.small_image);

            image.setImageResource(Util.getImageIdFromName(getItem(position)));
            if(selection) {
                namee.setText(getItem(position) + " amount: " + helper.fetchinventoryItemByName(getItem(position)).getItemAmount());
            } else {
                namee.setText(getItem(position));
            }

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

            namee.setText(getItem(position).getAnimalName() + "   At " + String.valueOf(getItem(position).getPresent()));

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

package cs65.edu.dartmouth.cs.gifto;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Oliver on 2/19/2018.
 *
 */

public class Collection extends ListActivity {

    public static TextView title, money;
    item_adapter item_adapter;
    pet_adapter pet_adapter;
    gift_adapter gift_adapter;
    Boolean goodies, gifts, pets, selection;
    public ArrayList<String> goodiesCollection, shopCollection;
    public ArrayList<Animal> petCollection;
    public ArrayList<Gift> giftCollection;
    ArrayList<String> selection_list;

    int loc_type;

    MySQLiteHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection);

        // initialize everything
        helper = new MySQLiteHelper(this);
        petCollection = new ArrayList<>();
        goodiesCollection = new ArrayList<>();
        shopCollection = new ArrayList<>();
        selection_list = new ArrayList<>();
        shopCollection.addAll(Globals.ITEM_TO_TYPE.keySet());
        giftCollection = new ArrayList<>();
        selection_list.addAll(Globals.ITEM_TO_TYPE.keySet());

        // get intent type
        goodies = getIntent().getBooleanExtra("goodies", false);
        gifts = getIntent().getBooleanExtra("gifts", false);
        pets = getIntent().getBooleanExtra("pets", false);
        selection = getIntent().getBooleanExtra("selection", false);

        title = (TextView) findViewById(R.id.list_title);
        money = (TextView) findViewById(R.id.money);

        if (goodies) {
            // shop collection
            InventoryItem moneyy = helper.fetchinventoryItemByName("money");
            if (moneyy.getItemAmount() == -1){

                moneyy.setItemName("money");
                moneyy.setItemAmount(300);
                helper.insertInventory(moneyy, true);
            }
            title.setText("SHOP");
            money.setText("You have "+ String.valueOf(helper.fetchinventoryItemByName("money").getItemAmount())+" coins");
            // make sure the item is purchasable (some are exclusive to animal gift!)
            for(String i: shopCollection){
                if(Globals.ITEM_TO_PRICE.containsKey(i)){
                    goodiesCollection.add(i);
                }
            }
            item_adapter = new item_adapter(this, R.layout.list_collection, goodiesCollection);
            setListAdapter(item_adapter);

        // not much for gift, just show history
        } else if (gifts) {

            title.setText("GIFTS");
            giftCollection = helper.fetchAllGifts();
            gift_adapter = new gift_adapter(this, R.layout.list_collection, giftCollection);
            setListAdapter(gift_adapter);
        // pet history
        } else if (pets){

            title.setText("PETS");
            petCollection = helper.fetchAllAnimals();
            pet_adapter = new pet_adapter(this, R.layout.list_collection, petCollection);
            setListAdapter(pet_adapter);

        // select an item to place
        } else if(selection){

            title.setText("Choose an item to place!");
            loc_type = getIntent().getIntExtra("loc_type", 0);

            // make sure the items can be place on the location
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

    // go to purchase_screen activity by clicking, or place an item
    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {
        super.onListItemClick(parent, v, position, id);

        // place the item
        if(selection){

            InventoryItem temp = helper.fetchinventoryItemByName(selection_list.get(position));
            // cant put same item in garden
            if(temp.getPresent() != -1){
                Toast.makeText(getBaseContext(), "Can't put same item in garden!", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                int prev = temp.getItemAmount();
                temp.setItemAmount(prev - 1);

                if (loc_type == 1) {
                    // replace original item
                    if (!Garden.item1_name.getText().equals("")) {

                        InventoryItem prev_item = helper.fetchinventoryItemByName((String) Garden.item1_name.getText());
                        prev_item.setPresent(-1);
                        //prev_item.setItemAmount(prev_item.getItemAmount() + 1);
                        helper.removeInventoryItem(prev_item.getItemName());
                        helper.insertInventory(prev_item, true);
                    }
                    // original animal left after changing the item
                    if (!Garden.animal1_name.getText().equals("")) {

                        Animal prev_animal = helper.fetchAnimalByName((String) Garden.animal1_name.getText());
                        prev_animal.setPresent(-1);
                        helper.removeAnimal(prev_animal.getAnimalName());
                        helper.insertAnimal(prev_animal, true);
                        Garden.animal1_name.setText("");
                        Garden.pet1.setImageDrawable(null);
                    }
                    Garden.loc1.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                    Garden.item1_name.setText(selection_list.get(position));
                    temp.setPresent(1);

                // same stuff
                } else if (loc_type == 2) {

                    if (!Garden.item2_name.getText().equals("")) {

                        InventoryItem prev_item = helper.fetchinventoryItemByName((String) Garden.item2_name.getText());
                        prev_item.setPresent(-1);
                        prev_item.setItemAmount(prev_item.getItemAmount() + 1);
                        helper.removeInventoryItem(prev_item.getItemName());
                        helper.insertInventory(prev_item, true);
                    }

                    if (!Garden.animal2_name.getText().equals("")) {

                        Animal prev_animal = helper.fetchAnimalByName((String) Garden.animal2_name.getText());
                        prev_animal.setPresent(-1);
                        helper.removeAnimal(prev_animal.getAnimalName());
                        helper.insertAnimal(prev_animal, true);
                        Garden.animal2_name.setText("");
                        Garden.pet2.setImageDrawable(null);
                    }
                    Garden.loc2.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                    Garden.item2_name.setText(selection_list.get(position));
                    temp.setPresent(2);
                // same stuff
                } else if (loc_type == 3) {

                    if (!Garden.item3_name.getText().equals("")) {

                        InventoryItem prev_item = helper.fetchinventoryItemByName((String) Garden.item3_name.getText());
                        prev_item.setPresent(-1);
                        prev_item.setItemAmount(prev_item.getItemAmount() + 1);
                        helper.removeInventoryItem(prev_item.getItemName());
                        helper.insertInventory(prev_item, true);
                    }

                    if (!Garden.animal3_name.getText().equals("")) {

                        Animal prev_animal = helper.fetchAnimalByName((String) Garden.animal3_name.getText());
                        prev_animal.setPresent(-1);
                        helper.removeAnimal(prev_animal.getAnimalName());
                        helper.insertAnimal(prev_animal, true);
                        Garden.animal3_name.setText("");
                        Garden.pet3.setImageDrawable(null);
                    }
                    Garden.loc3.setImageResource(Util.getImageIdFromName(selection_list.get(position)));
                    Garden.item3_name.setText(selection_list.get(position));
                    temp.setPresent(3);

                // same stuff
                } else if (loc_type == 4) {

                    if (!Garden.item4_name.getText().equals("")) {
                        InventoryItem prev_item = helper.fetchinventoryItemByName((String) Garden.item4_name.getText());
                        prev_item.setPresent(-1);
                        prev_item.setItemAmount(prev_item.getItemAmount() + 1);
                        helper.removeInventoryItem(prev_item.getItemName());
                        helper.insertInventory(prev_item, true);
                    }

                    if (!Garden.animal4_name.getText().equals("")) {

                        Animal prev_animal = helper.fetchAnimalByName((String) Garden.animal4_name.getText());
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
        }
        // rather simple for the other parts
        else {

            // go to purchase_screen
            Intent intent = new Intent(this, purchase_screen.class);
            if (goodies) {
                intent.putExtra("name", goodiesCollection.get(position));
                intent.putExtra("type", "goodies");
            } else if (pets) {
                intent.putExtra("name", petCollection.get(position).getAnimalName());
                intent.putExtra("type", "pets");
            }
            else if(gifts){
                intent.putExtra("name", giftCollection.get(position).getGiftName());
                intent.putExtra("type", "gifts");
            }

            startActivity(intent);
        }
    }

    ///////////////////////////////////////////////////////////////
    // a few list adapters
    //////////////////////////////////////////////////////////////

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

            image.setImageResource(Util.getImageIdFromName(Globals.INT_TO_BOX.get(getItem(position).getGiftBox())));

            namee.setText(getItem(position).getFriendName());

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

            if(getItem(position).getPresent() == -1){
                namee.setText(getItem(position).getAnimalName() + "   GONE!");
            }
            else {
                namee.setText(getItem(position).getAnimalName() + "   HERE!");
            }
            return view;
        }
    }
}
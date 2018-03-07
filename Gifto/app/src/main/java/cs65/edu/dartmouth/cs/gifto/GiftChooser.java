package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jess on 2/28/2018.
 *
 *
 * Activity to choose a gift to put on the map
 * There are three spinners: the animal you want to use, the gift you want to send, and the friend
 *   you want to send it to
 * There is also an EditText to input a message if you would so like
 *
 * If you don't care who to send the gift to, then there is an option to send it to everyone
 */

public class GiftChooser extends AppCompatActivity {

    Spinner spinner_animal;
    Spinner spinner_gift;
    Spinner spinner_friends;
    EditText editText_message;
    TextView price_text;
    int cost;

    MySQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_chooser);

        // find all the necessary inputs
        helper = new MySQLiteHelper(this);
        spinner_animal = findViewById(R.id.spinner_animal);
        spinner_gift = findViewById(R.id.spinner_gift);
        spinner_friends = findViewById(R.id.spinner_friends);
        editText_message = findViewById(R.id.editText_message);
        price_text = findViewById(R.id.text_price);

        final List<String> spinnerArray =  new ArrayList<>();
        final List<String> spinnerArray_friends =  new ArrayList<>();

        // set the items in the friend spinner
        ArrayAdapter<String> adapter_friends = new ArrayAdapter<>(
                getBaseContext(), android.R.layout.simple_spinner_item, spinnerArray_friends);
        adapter_friends.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArray_friends.add("Let anyone pick up the gift");
        ArrayList<Friend> friends = helper.fetchAllFriends();
        for(Friend friend : friends){
            if(friend.getEmail() != null) spinnerArray_friends.add(friend.getEmail());
        }
        spinner_friends.setAdapter(adapter_friends);

        // figure out which animals user can use to deliver gift
        // uses "animals" info from firebase
        DatabaseReference ref = Util.databaseReference.child("users")
                .child(Util.userID).child("animals");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                    if(snapShot.child("present").getValue() != null) {
                        // add all the animals to the spinner
                        if(Integer.parseInt(String.valueOf(
                                snapShot.child("present").getValue())) > -1) {
                            spinnerArray.add(snapShot.child("animalName").getValue(String.class));
                        }
                    }
                }

                // if there are no animals, then don't let user send a gift
                if(spinnerArray.size() == 0) {
                    Intent returnIntent = new Intent();
                    Toast.makeText(getBaseContext(),
                            "Need an animal to deliver gift", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                }

                // otherwise, set up the spinner
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getBaseContext(), android.R.layout.simple_spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_animal.setAdapter(adapter);

                    // which items the user can send in the gift
                    // (bigger animals can send bigger gifts)
                    final List<String> spinnergift_array =  new ArrayList<>();
                    spinnergift_array.add("Message");
                    int animal_type;
                    // deal with potential changes to database/constants
                    if(Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(0)) == null) animal_type = 0;
                    else animal_type = Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(0));
                    // populate the gift spinner using the currently selected animal (first animal in spinner)
                    for(String gift_type : Globals.ITEM_TO_TYPE.keySet()){
                        if(Globals.ITEM_TO_TYPE.get(gift_type) <= animal_type){
                            spinnergift_array.add(gift_type);
                        }
                    }

                    ArrayAdapter<String> adapter_gift = new ArrayAdapter<>(getBaseContext(),
                            android.R.layout.simple_spinner_item, spinnergift_array);
                    adapter_gift.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    spinner_gift.setAdapter(adapter_gift);
                    spinner_gift.setEnabled(true);

                    // if user selects different animal, the set of gifts this animal can send
                    // may be different. So update the spinner
                    spinner_animal.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView,
                                                   View selectedItemView, int position, long id) {
                            spinnergift_array.clear();
                            final int animal_type;
                            // in case something is out of date or something
                            if(Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(position)) == null) {
                                animal_type = 0;
                            }
                            // figure out how large the animal is
                            else {
                                animal_type=Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(position));
                            }
                            if(animal_type == 0) {
                                Toast.makeText(getBaseContext(),
                                        "Need bigger animal to send item with message",
                                        Toast.LENGTH_SHORT).show();
                                spinnergift_array.add("");
                                spinner_gift.setEnabled(false);
                                ((BaseAdapter) spinner_gift.getAdapter()).notifyDataSetChanged();
                            } else {
                                spinner_gift.setEnabled(true);
                                // can always send message
                                spinnergift_array.add("Message");
                                // which actual items can you send?
                                DatabaseReference ref2 = Util.databaseReference.child("users")
                                        .child(Util.userID).child("items");
                                ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                                            String gift_type = snapShot.child("itemName")
                                                    .getValue(String.class);
                                            int item_amount;
                                            if(snapShot.child("itemAmount").getValue() == null) {
                                                item_amount = 0;
                                            }
                                            else {
                                                item_amount = Integer.parseInt(String.valueOf(
                                                        snapShot.child("itemAmount").getValue()));
                                            }
                                            if(Globals.ITEM_TO_TYPE.get(gift_type) != null){
                                                int item_type = Globals.ITEM_TO_TYPE.get(gift_type);
                                                if(item_type > 0 && item_type <= animal_type
                                                        && item_amount > 0){
                                                    spinnergift_array.add(gift_type);
                                                }
                                            }
                                        }
                                        ((BaseAdapter) spinner_gift.getAdapter())
                                                .notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }

                    });

                    // User gets penalized for sending gift to specific person
                    // (because we want people to use the map, and have lots of gifts to choose from)
                    // this penalty is bigger for larger gifts.
                    // so inform the user what the penalty will be each time they select an item
                    spinner_gift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView,
                                                   View view, int i, long l) {
                            String gift_name = spinnergift_array.get(i);
                            int item_type;
                            if(Globals.ITEM_TO_TYPE.get(gift_name) != null){
                                item_type = Globals.ITEM_TO_TYPE.get(gift_name);
                            } else {
                                item_type = 0;
                            }
                            ArrayList<String> size_strings = new ArrayList<>
                                    (Arrays.asList("message", "small gift", "large gift"));
                            ArrayList<Integer> cost_strings = new ArrayList<>
                                    (Arrays.asList(1, 5, 15));
                            String size = size_strings.get(item_type);
                            cost = cost_strings.get(item_type);
                            String text = "Price to send " + size +
                                    " to a specific person: " + cost + " coins";
                            price_text.setText(text);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // prevent android from automatically focusing on the edit text
        // because otherwise it will scroll right past the animal and item options,
        // thus the user may not see them
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    // user has decided to send or cancel gift
    public void onClick(View view) {
        Intent returnIntent = new Intent();

        // send gift info to mapview, so it can put the new gift on the map and in the database
        if(view == findViewById(R.id.button_send)) {

            // make sure animal left the garden
            Animal onMission = helper.fetchAnimalByName((String)spinner_animal.getSelectedItem());
            int present = onMission.getPresent();
            if(present == 1) {
                Garden.pet1.setImageDrawable(null);
                Garden.animal1_name.setText("");
                Garden.item1_name.setText("");
                Garden.loc1.setImageDrawable(null);
                ArrayList<InventoryItem> food = helper.fetchAllInventoryItems();
                for(InventoryItem i: food){
                    if(i.getPresent() == 1){
                        i.setPresent(-1);
                        helper.removeInventoryItem(i.getItemName());
                        helper.insertInventory(i, true);
                        break;
                    }
                }
            }
            else if(present == 2){
                Garden.pet2.setImageDrawable(null);
                Garden.animal2_name.setText("");
            }
            else if(present == 3){
                Garden.pet3.setImageDrawable(null);
                Garden.animal3_name.setText("");
            }
            else if(present == 4){
                Garden.pet4.setImageDrawable(null);
                Garden.animal4_name.setText("");
            }
            helper.removeAnimal(onMission.getAnimalName());
            onMission.setPresent(-1);
            helper.insertAnimal(onMission, true);

            // tell the map activity what the user has selected here
            if(!spinner_gift.isEnabled()) returnIntent.putExtra("giftName", "");
            else if (spinner_gift.getSelectedItemPosition() == 0) returnIntent.putExtra("giftName", "");
            else returnIntent.putExtra("giftName", (String)spinner_gift.getSelectedItem());
            returnIntent.putExtra("animalName", (String)spinner_animal.getSelectedItem());
            returnIntent.putExtra("message", editText_message.getText().toString());
            if(spinner_friends.getSelectedItemPosition() == 0) returnIntent.putExtra("sendTo", "");
            else returnIntent.putExtra("sendTo", (String)spinner_friends.getSelectedItem());
            if(spinner_friends.getSelectedItemPosition() > 0){
                InventoryItem money = helper.fetchinventoryItemByName("money");
                money.setItemAmount(money.getItemAmount()-cost);
                helper.removeInventoryItem("money");
                helper.insertInventory(money, true);
            }
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    // focus on edit text only if the user has selected it
    public void onClick_edit(View view){
        EditText editText = findViewById(R.id.editText_message);
        editText.requestFocus();
    }
}

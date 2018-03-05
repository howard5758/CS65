package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GiftChooser extends AppCompatActivity {

    Spinner spinner_animal;
    Spinner spinner_gift;
    EditText editText_message;

    MySQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_chooser);

        helper = new MySQLiteHelper(this);
        spinner_animal = findViewById(R.id.spinner_animal);
        spinner_gift = findViewById(R.id.spinner_gift);
        editText_message = findViewById(R.id.editText_message);

        final Globals Globals = new Globals();

        final List<String> spinnerArray =  new ArrayList<String>();

        // figure out which animals user can use to deliver gift
        // uses "animals" info from firebase
        DatabaseReference ref = Util.databaseReference.child("users").child(Util.userID).child("animals");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                    if(snapShot.child("present").getValue() != null) {
                        if(snapShot.child("present").getValue(Integer.class) > -1) spinnerArray.add(snapShot.child("animalName").getValue(String.class));
                    }
                }
                if(spinnerArray.size() == 0) {
                    Intent returnIntent = new Intent();
                    Toast.makeText(getBaseContext(), "Need an animal to deliver gift", Toast.LENGTH_SHORT);
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                    finish();
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getBaseContext(), android.R.layout.simple_spinner_item, spinnerArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_animal.setAdapter(adapter);

                    // which items the user can send in the gift
                    final List<String> spinnergift_array =  new ArrayList<String>();
                    spinnergift_array.add(" ");
                    int animal_type;
                    if(Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(0)) == null) animal_type = 0;
                    else animal_type = Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(0));
                    for(String gift_type : Globals.ITEM_TO_TYPE.keySet()){
                        if(Globals.ITEM_TO_TYPE.get(gift_type) <= animal_type){
                            spinnergift_array.add(gift_type);
                        }
                    }

                    ArrayAdapter<String> adapter_gift = new ArrayAdapter<String>(
                            getBaseContext(), android.R.layout.simple_spinner_item, spinnergift_array);
                    adapter_gift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_gift.setAdapter(adapter_gift);

                    spinner_animal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            spinnergift_array.clear();
                            spinnergift_array.add(" ");
                            final int animal_type;
                            if(Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(position)) == null) animal_type = 0;
                            else animal_type = Globals.ANIMAL_TO_TYPE.get(spinnerArray.get(position));
                            DatabaseReference ref2 = Util.databaseReference.child("users").child(Util.userID).child("items");
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapShot : dataSnapshot.getChildren()){
                                        String gift_type = snapShot.child("itemName").getValue(String.class);
                                        if(Globals.ITEM_TO_TYPE.get(gift_type) != null){
                                            if(Globals.ITEM_TO_TYPE.get(gift_type) <= animal_type){
                                                spinnergift_array.add(gift_type);
                                            }
                                        }
                                    }
                                    ((BaseAdapter) spinner_gift.getAdapter()).notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // your code here
                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
            //

            returnIntent.putExtra("giftName", (String)spinner_gift.getSelectedItem());
            returnIntent.putExtra("animalName", (String)spinner_animal.getSelectedItem());
            returnIntent.putExtra("message", editText_message.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    public void populate_itemarray(int type){

    }
}

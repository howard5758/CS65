package cs65.edu.dartmouth.cs.gifto;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_chooser);

        spinner_animal = findViewById(R.id.spinner_animal);
        spinner_gift = findViewById(R.id.spinner_gift);
        editText_message = findViewById(R.id.editText_message);

        final List<String> spinnerArray =  new ArrayList<String>();

        // figure out which animals user can use to deliver gift
        // uses "animals" info from firebase
        DatabaseReference ref = Util.databaseReference.child("users").child(Util.userID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapShot : dataSnapshot.child("animals").getChildren()){
                    spinnerArray.add(snapShot.child("animalName").getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getBaseContext(), android.R.layout.simple_spinner_item, spinnerArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_animal.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // which types of gifts the user can send
        final List<String> spinnergift_array =  new ArrayList<String>();
        spinnergift_array.add("Gift Type 1");
        spinnergift_array.add("Gift Type 2");

        ArrayAdapter<String> adapter_gift = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnergift_array);
        adapter_gift.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_gift.setAdapter(adapter_gift);

    }

    public void onClick(View view) {
        Intent returnIntent = new Intent();

        // send gift info to mapview, so it can put the new gift on the map and in the database
        if(view == findViewById(R.id.button_send)) {
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
}

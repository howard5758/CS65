package cs65.edu.dartmouth.cs.gifto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ping-Jung on 2/25/2018.
 *
 * Screen to purchase items and view gifts and animals you own
 */

public class purchase_screen extends AppCompatActivity {

    Button buy, cancel;
    ImageView image;
    TextView description, price;
    MySQLiteHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_screen);

        buy = findViewById(R.id.buy);
        cancel = findViewById(R.id.cancel);
        helper = new MySQLiteHelper(this);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        final String object = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        image = findViewById(R.id.item_picture);

        // some ui stuff based on different type of input
        switch (type) {

            case "gifts":

                image.setImageResource(Util.getImageIdFromName(object));
                if(object.equals("")){
                    String text = "Received a message!";
                    description.setText(text);
                }
                else{
                    String text = "Received a " + object;
                    description.setText(text);
                }
                buy.setVisibility(View.GONE);
                break;

            case "goodies":

                String text = "Would you like an interesting "+object+"?";
                description.setText(text);
                text = Globals.ITEM_TO_PRICE.get(object) + " coins each!";
                price.setText(text);
                break;

            case "pets":

                Animal pet = helper.fetchAnimalByName(object);
                buy.setVisibility(View.GONE);
                String text1 = object + " has visited you " +
                        String.valueOf(pet.getNumVisits()) + " times. ";
                description.setText(text1);
                if(pet.getNumVisits() >= Globals.ANIMAL_TO_PROB.get(pet.getAnimalName()) * 10) {
                    if (Globals.ANIMAL_TO_GIFT.get(object) != null) {
                        String text2 = object + " sent you a " + Globals.ANIMAL_TO_GIFT.get(object);
                        price.setText(text2);
                    }
                }
                else{
                    String text3 = object + " has not sent you anything T_T";
                    price.setText(text3);
                }

                break;
        }

        image.setImageResource(Util.getImageIdFromName(object));

        // this is only visible in shop's purchase_screen
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // buy stuff
                InventoryItem money = helper.fetchinventoryItemByName("money");
                // remaining money more than price
                if (money.getItemAmount() >= Globals.ITEM_TO_PRICE.get(object)) {

                    InventoryItem temp = helper.fetchinventoryItemByName(object);
                    int prev = temp.getItemAmount();
                    temp.setItemName(object);
                    temp.setItemAmount(prev + 1);
                    helper.removeInventoryItem(object);
                    helper.insertInventory(temp, true);
                    prev = money.getItemAmount();
                    money.setItemAmount(prev - Globals.ITEM_TO_PRICE.get(object));
                    helper.removeInventoryItem("money");
                    helper.insertInventory(money, true);
                }
                else{
                    Toast.makeText(getBaseContext(), "Not Enough Money", Toast.LENGTH_SHORT).show();
                }

                String text = "You have "+ String.valueOf(helper.
                        fetchinventoryItemByName("money").getItemAmount())+" coins";
                Collection.money.setText(text);

                finish();
            }
        });
        // cancel
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}

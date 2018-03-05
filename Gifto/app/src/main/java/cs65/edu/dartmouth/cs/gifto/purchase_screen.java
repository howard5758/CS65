package cs65.edu.dartmouth.cs.gifto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class purchase_screen extends AppCompatActivity {

    Button buy, cancel;
    ImageView image;
    TextView description, price;
    MySQLiteHelper helper;
    InventoryItem item;
    Animal pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_screen);

        buy = (Button) findViewById(R.id.buy);
        cancel = (Button) findViewById(R.id.cancel);
        helper = new MySQLiteHelper(this);
        description = (TextView) findViewById(R.id.description);
        price = (TextView) findViewById(R.id.price);

        final String object = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        image = (ImageView) findViewById(R.id.item_picture);


        switch (type) {
            case "gifts":
                image.setImageResource(Util.getImageIdFromName(object));
                if(object.equals(" ")){
                    description.setText("Received NOTHING!!!");
                }
                else{
                    description.setText("Received a " + object);
                }
                buy.setVisibility(View.GONE);
                break;
            case "goodies":
                description.setText("Would you like an interesting "+object+"?");
                price.setText(Globals.ITEM_TO_PRICE.get(object) + " coins each!");
                break;
            case "pets":
                Animal pet = helper.fetchAnimalByName(object);
                buy.setVisibility(View.GONE);
                description.setText(object + " has visited you " + String.valueOf(pet.getNumVisits()) + " times. ");
                if(pet.getNumVisits() >= Globals.ANIMAL_TO_PROB.get(pet.getAnimalName()) * 10) {
                    price.setText(object + " sent you a " + Globals.ANIMAL_TO_GIFT.get(object));
                }
                else{
                    price.setText(object + " has not sent you anything T_T");
                }

                break;
        }

        image.setImageResource(Util.getImageIdFromName(object));



        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InventoryItem money = helper.fetchinventoryItemByName("money");
                if (money.getItemAmount() >= Globals.ITEM_TO_PRICE.get(object)) {

                    InventoryItem temp = helper.fetchinventoryItemByName(object);
                    int prev = temp.getItemAmount();
                    Log.d("master", String.valueOf(prev));
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

                //Garden.money_text.setText(String.valueOf(money.getItemAmount()));
                Collection.money.setText("You have "+ String.valueOf(helper.fetchinventoryItemByName("money").getItemAmount())+" coins");


                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}

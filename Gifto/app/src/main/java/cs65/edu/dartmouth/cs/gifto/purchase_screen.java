package cs65.edu.dartmouth.cs.gifto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class purchase_screen extends AppCompatActivity {

    Button buy, cancel;
    ImageView image;
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
        String object = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        image = (ImageView) findViewById(R.id.item_picture);

        switch (type) {
            case "goodies":
                item = (InventoryItem) getIntent().getSerializableExtra("actual_object");
                break;
            case "pets":

                buy.setVisibility(View.GONE);

                pet = (Animal) getIntent().getSerializableExtra("actual_object");
                break;
        }

        switch (object) {
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



        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InventoryItem money = helper.fetchinventoryItemByName("money");
                if (money.getItemAmount() >= 30) {

                    InventoryItem temp = helper.fetchinventoryItemByName(item.getItemName());
                    int prev = temp.getItemAmount();
                    Log.d("master", String.valueOf(prev));
                    item.setItemAmount(prev + 1);
                    helper.removeInventoryItem(item.getItemName());
                    helper.insertInventory(item);

                    prev = money.getItemAmount();
                    money.setItemAmount(prev - 30);
                    helper.removeInventoryItem("money");
                    helper.insertInventory(money);
                }
                else{
                    Toast.makeText(getBaseContext(), "Not Enough Money", Toast.LENGTH_SHORT).show();
                }

                Garden.money_text.setText(String.valueOf(money.getItemAmount()));



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

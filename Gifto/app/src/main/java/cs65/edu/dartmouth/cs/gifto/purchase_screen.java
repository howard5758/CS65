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
        final String object = getIntent().getStringExtra("name");
        String type = getIntent().getStringExtra("type");
        image = (ImageView) findViewById(R.id.item_picture);

        switch (type) {
            case "goodies":
                break;
            case "pets":

                buy.setVisibility(View.GONE);

                break;
        }

        image.setImageResource(Util.getImageIdFromName(object));



        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InventoryItem money = helper.fetchinventoryItemByName("money");
                if (money.getItemAmount() >= 30) {

                    InventoryItem temp = helper.fetchinventoryItemByName(object);
                    int prev = temp.getItemAmount();
                    Log.d("master", String.valueOf(prev));
                    temp.setItemName(object);
                    temp.setItemAmount(prev + 1);
                    helper.removeInventoryItem(object);
                    helper.insertInventory(temp);

                    prev = money.getItemAmount();
                    money.setItemAmount(prev - 30);
                    helper.removeInventoryItem("money");
                    helper.insertInventory(money);
                }
                else{
                    Toast.makeText(getBaseContext(), "Not Enough Money", Toast.LENGTH_SHORT).show();
                }

                //Garden.money_text.setText(String.valueOf(money.getItemAmount()));
                Collection.title.setText("GOODIES" + String.valueOf(money.getItemAmount()));


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

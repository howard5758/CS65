package cs65.edu.dartmouth.cs.gifto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class purchase_screen extends AppCompatActivity {

    Button buy, cancel;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_screen);

        String object = getIntent().getStringExtra("object");
        image = (ImageView) findViewById(R.id.item_picture);

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

        buy = (Button) findViewById(R.id.buy);
        cancel = (Button) findViewById(R.id.cancel);

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

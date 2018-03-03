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

        // edit by Jess: I've made a Util function that takes String names
        // and returns the appropriate R.drawables
        // I saw the same long case statement in a few places in the code,
        // and thought this would make things easier
        image.setImageResource(Util.getImageIdFromName(object));

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

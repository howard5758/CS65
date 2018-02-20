package cs65.edu.dartmouth.cs.gifto;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Oliver on 2/11/2018.
 *
 */

public class Garden extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.garden, container, false);

        Button button_food = view.findViewById(R.id.button_food);
        Button button_gifts = view.findViewById(R.id.button_gift_collections);
        Button button_animals = view.findViewById(R.id.button_pet_collections);

        // Food button
        button_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("food", true);
                startActivity(intent);
            }
        });

        // Gifts button
        button_gifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("gifts", true);
                startActivity(intent);
            }
        });

        // Animals button
        button_animals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("animals", true);
                startActivity(intent);
            }
        });

        return view;

    }
}

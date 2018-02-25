package cs65.edu.dartmouth.cs.gifto;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Oliver on 2/11/2018.
 *
 */

public class Garden extends Fragment {
    String background;
    String place1, place2, place3, place4;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        background = Globals.DEFAULT_BACKGROUND;
        place1 = Globals.EMPTY;
        place2 = Globals.DEFAULT_PLACE2;
        place3 = Globals.DEFAULT_PLACE3;
        place4 = Globals.EMPTY;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.garden, container, false);

        Button button_food = view.findViewById(R.id.button_food);
        Button button_gifts = view.findViewById(R.id.button_gift_collections);
        Button button_animals = view.findViewById(R.id.button_pet_collections);

        Button button_place1 = view.findViewById(R.id.button_place1);
        Button button_place2 = view.findViewById(R.id.button_place2);
        Button button_place3 = view.findViewById(R.id.button_place3);
        Button button_place4 = view.findViewById(R.id.button_place4);
        Button button_expand_left = view.findViewById(R.id.expand_left);
        Button button_expand_right = view.findViewById(R.id.expand_right);

        // Food button
        button_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("goodies", true);
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

        // Expand on the left side button, if no left side of garden, hint the users they can expand
        // their garden
        button_expand_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Currently no left side",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Expand on the right side button, if no right side of garden, hint the users they can expand
        // their garden
        button_expand_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Currently no right side",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "choose food from your fridge",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "choose from your storage",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "choose from your storage",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "choose from your toys",
                        Toast.LENGTH_SHORT).show();
            }
        });

        view = setBackground(view);

        return view;

    }

    public View setBackground(View view){
        if (background == Globals.DEFAULT_BACKGROUND){
            ImageView bg = (ImageView) view.findViewById(R.id.bg);
            bg.setImageResource(R.drawable.plain_bg);
        }
        if (place2 == Globals.DEFAULT_PLACE2){
            ImageView p2 = (ImageView) view.findViewById(R.id.place2);
            p2.setImageResource(R.drawable.tree);
        }
        if (place3 == Globals.DEFAULT_PLACE3){
            ImageView p3 = (ImageView) view.findViewById(R.id.place3);
            p3.setImageResource(R.drawable.pile_leaves);
        }

        return view;
    }
}


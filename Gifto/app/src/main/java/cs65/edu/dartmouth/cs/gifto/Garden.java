package cs65.edu.dartmouth.cs.gifto;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Oliver on 2/11/2018.
 *
 */

public class Garden extends Fragment {
    String background;
    String place1, place2, place3, place4;
    public static ImageView loc1, loc2, loc3, loc4;
    public static ImageView pet1, pet2, pet3, pet4;
    public static MySQLiteHelper helper;
    public static TextView item1_name, item2_name, item3_name, item4_name, animal1_name, animal2_name, animal3_name, animal4_name;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        background = Globals.DEFAULT_BACKGROUND;
        place1 = Globals.EMPTY;
        place2 = Globals.EMPTY;
        place3 = Globals.EMPTY;
        place4 = Globals.EMPTY;
        helper = new MySQLiteHelper(getActivity());


    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.garden, container, false);

        loc1 = (ImageView)view.findViewById(R.id.place1);
        loc2 = (ImageView)view.findViewById(R.id.place2);
        loc3 = (ImageView)view.findViewById(R.id.place3);
        loc4 = (ImageView)view.findViewById(R.id.place4);
        pet1 = (ImageView)view.findViewById(R.id.animal1);
        pet2 = (ImageView)view.findViewById(R.id.animal2);
        pet3 = (ImageView)view.findViewById(R.id.animal3);
        pet4 = (ImageView)view.findViewById(R.id.animal4);
        item1_name = (TextView)view.findViewById(R.id.item1_name);
        item2_name = (TextView)view.findViewById(R.id.item2_name);
        item3_name = (TextView)view.findViewById(R.id.item3_name);
        item4_name = (TextView)view.findViewById(R.id.item4_name);
        animal1_name = (TextView)view.findViewById(R.id.animal1_name);
        animal2_name = (TextView)view.findViewById(R.id.animal2_name);
        animal3_name = (TextView)view.findViewById(R.id.animal3_name);
        animal4_name = (TextView)view.findViewById(R.id.animal4_name);

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
                intent.putExtra("pets", true);
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
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("selection", true);
                intent.putExtra("loc_type", 1);
                startActivity(intent);
                Toast.makeText(getActivity(), "choose food from your fridge",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("selection", true);
                intent.putExtra("loc_type", 2);
                startActivity(intent);
                Toast.makeText(getActivity(), "choose from your storage",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("selection", true);
                intent.putExtra("loc_type", 3);
                startActivity(intent);
                Toast.makeText(getActivity(), "choose from your storage",
                        Toast.LENGTH_SHORT).show();
            }
        });

        button_place4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Collection.class);
                intent.putExtra("selection", true);
                intent.putExtra("loc_type", 4);
                startActivity(intent);
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
            bg.setImageResource(R.drawable.bg_plain);
        }
//        if (place2 == Globals.DEFAULT_PLACE2){
//            ImageView p2 = (ImageView) view.findViewById(R.id.place2);
//            p2.setImageResource(R.drawable.tree);
//        }
//        if (place3 == Globals.DEFAULT_PLACE3){
//            ImageView p3 = (ImageView) view.findViewById(R.id.place3);
//            p3.setImageResource(R.drawable.pile_leaves);
//        }

        return view;
    }

    public static void check_animals(){
        ArrayList<InventoryItem> items = helper.fetchAllInventoryItems();
        ArrayList<Animal> animals = helper.fetchAllAnimals();
        for(Animal a : animals){
            if(a.getPresent() == 1){
                Garden.pet1.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                Garden.animal1_name.setText(a.getAnimalName());
            }
            else if(a.getPresent() == 2){
                Garden.pet2.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                Garden.animal2_name.setText(a.getAnimalName());
            }
            else if(a.getPresent() == 3){
                Garden.pet3.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                Garden.animal3_name.setText(a.getAnimalName());
            }
            else if(a.getPresent() == 4){
                Garden.pet4.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                Garden.animal4_name.setText(a.getAnimalName());
            }
        }
        for(InventoryItem i : items){

            Log.d("master", i.getItemName());
            if(i.getPresent() == 1){
                Log.d("master", "present!!");
                loc1.setImageResource(Util.getImageIdFromName(i.getItemName()));
                Garden.item1_name.setText(i.getItemName());
                if(Garden.animal1_name.getText().equals("")) {
                    ArrayList<String> related_animals = Globals.ITEM_TO_ANIMAL_LIST.get(i.getItemName());
                    Random random = new Random();
                    String target = related_animals.get(random.nextInt(related_animals.size()));
                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp.getPresent()==-1){
                            Animal pet = new Animal();
                            pet.setAnimalName(target);
                            pet.setPresent(1);
                            helper.removeAnimal(target);
                            helper.insertAnimal(pet, true);
                            Garden.pet1.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal1_name.setText(target);
                        }
                    }
                }
            }
            else if(i.getPresent() == 2){
                Log.d("master", "present!!");
                loc2.setImageResource(Util.getImageIdFromName(i.getItemName()));
                Garden.item2_name.setText(i.getItemName());
                if(Garden.animal2_name.getText().equals("")) {
                    ArrayList<String> related_animals = Globals.ITEM_TO_ANIMAL_LIST.get(i.getItemName());
                    Random random = new Random();
                    String target = related_animals.get(random.nextInt(related_animals.size()));
                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp.getPresent()==-1){
                            Animal pet = new Animal();
                            pet.setAnimalName(target);
                            pet.setPresent(2);
                            helper.removeAnimal(target);
                            helper.insertAnimal(pet, true);
                            Garden.pet2.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal2_name.setText(target);
                        }
                    }
                }
            }
            else if(i.getPresent() == 3){
                Log.d("master", "present!!");
                loc3.setImageResource(Util.getImageIdFromName(i.getItemName()));
                Garden.item3_name.setText(i.getItemName());
                if(Garden.animal3_name.getText().equals("")) {
                    ArrayList<String> related_animals = Globals.ITEM_TO_ANIMAL_LIST.get(i.getItemName());
                    Random random = new Random();
                    String target = related_animals.get(random.nextInt(related_animals.size()));
                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp.getPresent()==-1){
                            Animal pet = new Animal();
                            pet.setAnimalName(target);
                            pet.setPresent(3);
                            helper.removeAnimal(target);
                            helper.insertAnimal(pet, true);
                            Garden.pet3.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal3_name.setText(target);
                        }
                    }
                }
            }
            else if(i.getPresent() == 4){
                Log.d("master", "present!!");
                loc4.setImageResource(Util.getImageIdFromName(i.getItemName()));
                Garden.item4_name.setText(i.getItemName());
                if(Garden.animal4_name.getText().equals("")) {
                    ArrayList<String> related_animals = Globals.ITEM_TO_ANIMAL_LIST.get(i.getItemName());
                    Random random = new Random();
                    String target = related_animals.get(random.nextInt(related_animals.size()));
                    Log.d("master", "hi"+target);
                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp.getPresent()==-1){
                            Animal pet = new Animal();
                            pet.setAnimalName(target);
                            pet.setPresent(4);
                            helper.removeAnimal(target);
                            helper.insertAnimal(pet, true);
                            Garden.pet4.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal4_name.setText(target);
                        }
                    }
                }
            }
        }
        Iterator<InventoryItem> iter = items.iterator();

        while (iter.hasNext()) {
            InventoryItem i = iter.next();

            if (i.getItemAmount()<0 && !i.getItemName().equals("money")){

                i.setItemAmount(0);
                helper.insertInventory(i, true);
                helper.removeInventoryItem(i.getItemName());
            }

        }
    }
}


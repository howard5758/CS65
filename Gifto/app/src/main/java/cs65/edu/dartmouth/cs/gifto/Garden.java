package cs65.edu.dartmouth.cs.gifto;

import android.content.Context;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
//    private static Context context = null;
    private static FragmentManager fm;
    String background;
    String place1, place2, place3, place4;
    public static ImageView loc1, loc2, loc3, loc4;
    public static ImageView pet1, pet2, pet3, pet4;
    public static MySQLiteHelper helper;
    public static TextView item1_name, item2_name, item3_name, item4_name, animal1_name, animal2_name, animal3_name, animal4_name;

    public static Button button_food, button_gifts, button_animals, button_place1, button_place2, button_place3, button_place4;

    public static boolean check;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        fm = getActivity().getFragmentManager();
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

        button_food = view.findViewById(R.id.button_food);
        button_gifts = view.findViewById(R.id.button_gift_collections);
        button_animals = view.findViewById(R.id.button_pet_collections);

        button_place1 = view.findViewById(R.id.button_place1);
        button_place2 = view.findViewById(R.id.button_place2);
        button_place3 = view.findViewById(R.id.button_place3);
        button_place4 = view.findViewById(R.id.button_place4);


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

        return view;

    }

//    public View setBackground(View view){
//        if (background == Globals.DEFAULT_BACKGROUND){
//            ImageView bg = (ImageView) view.findViewById(R.id.bg);
//            bg.setImageResource(R.drawable.bg_plain);
////            bg.setImageResource(R.drawable.load_page);
//        }
//
//        return view;
//    }

    public static void check_animals(){
        ArrayList<InventoryItem> items = helper.fetchAllInventoryItems();
        ArrayList<Animal> animals = helper.fetchAllAnimals();
        for(Animal a : animals){
            if(a.getPresent() == 1){
                if(Util.nightTime && Globals.ANIMAL_TO_NIGHT.containsKey(a.getAnimalName())){

                    String night_name = Globals.ANIMAL_TO_NIGHT.get(a.getAnimalName());

                    Garden.pet1.setImageResource(Util.getImageIdFromName(night_name));
                    Garden.animal1_name.setText(night_name);

                    a.setPresent(-1);
                    helper.removeAnimal(a.getAnimalName());
                    helper.insertAnimal(a, true);

                    Animal night = helper.fetchAnimalByName(night_name);
                    if(night == null){
                        night = new Animal();
                    }
                    night.setAnimalName(night_name);
                    night.setPresent(1);
                    helper.removeAnimal(night_name);
                    helper.insertAnimal(night, true);
                }
                else{
                    Garden.pet1.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                    Garden.animal1_name.setText(a.getAnimalName());
                }
            }
            else if(a.getPresent() == 2){
                if(Util.nightTime && Globals.ANIMAL_TO_NIGHT.containsKey(a.getAnimalName())){
                    String night_name = Globals.ANIMAL_TO_NIGHT.get(a.getAnimalName());

                    Garden.pet2.setImageResource(Util.getImageIdFromName(night_name));
                    Garden.animal2_name.setText(night_name);

                    a.setPresent(-1);
                    helper.removeAnimal(a.getAnimalName());
                    helper.insertAnimal(a, true);

                    Animal night = helper.fetchAnimalByName(night_name);
                    if(night == null){
                        night = new Animal();
                    }
                    night.setAnimalName(night_name);
                    night.setPresent(2);
                    helper.removeAnimal(night_name);
                    helper.insertAnimal(night, true);
                }
                else{
                    Garden.pet2.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                    Garden.animal2_name.setText(a.getAnimalName());
                }
            }
            else if(a.getPresent() == 3){
                if(Util.nightTime && Globals.ANIMAL_TO_NIGHT.containsKey(a.getAnimalName())){
                    String night_name = Globals.ANIMAL_TO_NIGHT.get(a.getAnimalName());

                    Garden.pet3.setImageResource(Util.getImageIdFromName(night_name));
                    Garden.animal3_name.setText(night_name);

                    a.setPresent(-1);
                    helper.removeAnimal(a.getAnimalName());
                    helper.insertAnimal(a, true);

                    Animal night = helper.fetchAnimalByName(night_name);
                    if(night == null){
                        night = new Animal();
                    }
                    night.setAnimalName(night_name);
                    night.setPresent(3);
                    helper.removeAnimal(night_name);
                    helper.insertAnimal(night, true);
                }
                else{
                    Garden.pet3.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                    Garden.animal3_name.setText(a.getAnimalName());
                }
            }
            else if(a.getPresent() == 4){
                if(Util.nightTime && Globals.ANIMAL_TO_NIGHT.containsKey(a.getAnimalName())){
                    String night_name = Globals.ANIMAL_TO_NIGHT.get(a.getAnimalName());

                    Garden.pet4.setImageResource(Util.getImageIdFromName(night_name));
                    Garden.animal4_name.setText(night_name);

                    a.setPresent(-1);
                    helper.removeAnimal(a.getAnimalName());
                    helper.insertAnimal(a, true);

                    Animal night = helper.fetchAnimalByName(night_name);
                    if(night == null){
                        night = new Animal();
                    }
                    night.setAnimalName(night_name);
                    night.setPresent(4);
                    helper.removeAnimal(night_name);
                    helper.insertAnimal(night, true);
                }
                else{
                    Garden.pet4.setImageResource(Util.getImageIdFromName(a.getAnimalName()));
                    Garden.animal4_name.setText(a.getAnimalName());
                }
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

                    //check same animal
                    check = false;
                    Animal day = helper.fetchAnimalByName(target);
                    Animal night = helper.fetchAnimalByName(Globals.ANIMAL_TO_NIGHT.get(target));
                    if(day == null)
                        day = new Animal();
                    if(night == null)
                        night = new Animal();
                    if(day.getPresent()==-1 && night.getPresent()==-1)
                        check = true;
                    //
                    // night
                    if(Util.nightTime)
                        target = Globals.ANIMAL_TO_NIGHT.get(target);

                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp == null){
                            tempp = new Animal();
                        }

                        if(check){
                            tempp.setAnimalName(target);
                            tempp.setPresent(1);
                            tempp.setNumVisits(tempp.getNumVisits() + 1);
                            helper.removeAnimal(target);
                            helper.insertAnimal(tempp, true);
                            Garden.pet1.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal1_name.setText(target);

                            int animal_money = (int)Math.floor(Math.random()*30);
                            InventoryItem temp_money = helper.fetchinventoryItemByName("money");
                            temp_money.setItemAmount(temp_money.getItemAmount() + animal_money);
                            helper.removeInventoryItem("money");
                            helper.insertInventory(temp_money, true);

                            Bundle args = new Bundle();
                            args.putString("animal_key", target);
                            args.putInt("money_key", animal_money);
                            fm.beginTransaction();
                            MyDialogFragment myDialogFragment = new MyDialogFragment();
                            myDialogFragment.setArguments(args);
                            myDialogFragment.show(fm, "animal");

                            if(tempp.getNumVisits() == Globals.ANIMAL_TO_PROB.get(target)*10){
                                String gift_name = Globals.ANIMAL_TO_GIFT.get(target);
                                InventoryItem gift_item = helper.fetchinventoryItemByName(gift_name);
                                if(gift_item == null) {
                                    gift_item = new InventoryItem();
                                    gift_item.setItemName(gift_name);
                                }
                                gift_item.setItemName(gift_name);
                                gift_item.setItemAmount(gift_item.getItemAmount() + 1);
                                helper.removeInventoryItem(gift_name);
                                helper.insertInventory(gift_item, true);
                            }
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

                    //check same animal
                    check = false;
                    Animal day = helper.fetchAnimalByName(target);
                    Animal night = helper.fetchAnimalByName(Globals.ANIMAL_TO_NIGHT.get(target));
                    if(day == null)
                        day = new Animal();
                    if(night == null)
                        night = new Animal();
                    if(day.getPresent()==-1 && night.getPresent()==-1)
                        check = true;
                    //
                    // night
                    if(Util.nightTime)
                        target = Globals.ANIMAL_TO_NIGHT.get(target);

                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp == null){
                            tempp = new Animal();
                        }
                        if(check){
                            tempp.setAnimalName(target);
                            tempp.setPresent(2);
                            tempp.setNumVisits(tempp.getNumVisits() + 1);
                            helper.removeAnimal(target);
                            helper.removeAnimal(target);
                            helper.insertAnimal(tempp, true);
                            Garden.pet2.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal2_name.setText(target);

                            int animal_money = (int)Math.floor(Math.random()*30);
                            InventoryItem temp_money = helper.fetchinventoryItemByName("money");
                            temp_money.setItemAmount(temp_money.getItemAmount() + animal_money);
                            helper.removeInventoryItem("money");
                            helper.insertInventory(temp_money, true);

                            Bundle args = new Bundle();
                            args.putString("animal_key", target);
                            args.putInt("money_key", animal_money);
                            fm.beginTransaction();
                            MyDialogFragment myDialogFragment = new MyDialogFragment();
                            myDialogFragment.setArguments(args);
                            myDialogFragment.show(fm, "animal");


                            if(tempp.getNumVisits() == Globals.ANIMAL_TO_PROB.get(target)*10){
                                String gift_name = Globals.ANIMAL_TO_GIFT.get(target);
                                InventoryItem gift_item = helper.fetchinventoryItemByName(gift_name);
                                if(gift_item == null) {
                                    gift_item = new InventoryItem();
                                    gift_item.setItemName(gift_name);
                                }
                                gift_item.setItemName(gift_name);
                                gift_item.setItemAmount(gift_item.getItemAmount() + 1);
                                helper.removeInventoryItem(gift_name);
                                helper.insertInventory(gift_item, true);
                            }
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

                    //check same animal
                    check = false;
                    Animal day = helper.fetchAnimalByName(target);
                    Animal night = helper.fetchAnimalByName(Globals.ANIMAL_TO_NIGHT.get(target));
                    if(day == null)
                        day = new Animal();
                    if(night == null)
                        night = new Animal();
                    if(day.getPresent()==-1 && night.getPresent()==-1)
                        check = true;
                    //
                    // night
                    if(Util.nightTime)
                        target = Globals.ANIMAL_TO_NIGHT.get(target);

                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp == null){
                            tempp = new Animal();
                        }
                        if(check){
                            tempp.setAnimalName(target);
                            tempp.setPresent(3);
                            tempp.setNumVisits(tempp.getNumVisits() + 1);
                            helper.removeAnimal(target);
                            helper.removeAnimal(target);
                            helper.insertAnimal(tempp, true);
                            Garden.pet3.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal3_name.setText(target);

                            int animal_money = (int)Math.floor(Math.random()*30);
                            InventoryItem temp_money = helper.fetchinventoryItemByName("money");
                            temp_money.setItemAmount(temp_money.getItemAmount() + animal_money);
                            helper.removeInventoryItem("money");
                            helper.insertInventory(temp_money, true);

                            Bundle args = new Bundle();
                            args.putString("animal_key", target);
                            args.putInt("money_key", animal_money);
                            fm.beginTransaction();
                            MyDialogFragment myDialogFragment = new MyDialogFragment();
                            myDialogFragment.setArguments(args);
                            myDialogFragment.show(fm, "animal");


                            if(tempp.getNumVisits() == Globals.ANIMAL_TO_PROB.get(target)*10){
                                String gift_name = Globals.ANIMAL_TO_GIFT.get(target);
                                InventoryItem gift_item = helper.fetchinventoryItemByName(gift_name);
                                if(gift_item == null) {
                                    gift_item = new InventoryItem();
                                    gift_item.setItemName(gift_name);
                                }
                                gift_item.setItemName(gift_name);
                                gift_item.setItemAmount(gift_item.getItemAmount() + 1);
                                helper.removeInventoryItem(gift_name);
                                helper.insertInventory(gift_item, true);
                            }
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

                    //check same animal
                    check = false;
                    Animal day = helper.fetchAnimalByName(target);
                    Animal night = helper.fetchAnimalByName(Globals.ANIMAL_TO_NIGHT.get(target));
                    if(day == null)
                        day = new Animal();
                    if(night == null)
                        night = new Animal();
                    if(day.getPresent()==-1 && night.getPresent()==-1)
                        check = true;
                    //
                    // night
                    if(Util.nightTime)
                        target = Globals.ANIMAL_TO_NIGHT.get(target);

                    Log.d("master", "hi"+target);
                    double prob = Globals.ANIMAL_TO_PROB.get(target);
                    Log.d("master", String.valueOf(prob));
                    if (Math.random() <= prob) {
                        Log.d("master", "animal!!");
                        Animal tempp = helper.fetchAnimalByName(target);
                        if(tempp == null){
                            tempp = new Animal();
                        }
                        if(check){
                            tempp.setAnimalName(target);
                            tempp.setPresent(4);
                            tempp.setNumVisits(tempp.getNumVisits() + 1);
                            helper.removeAnimal(target);
                            helper.removeAnimal(target);
                            helper.insertAnimal(tempp, true);
                            Garden.pet4.setImageResource(Util.getImageIdFromName(target));
                            Garden.animal4_name.setText(target);

                            int animal_money = (int)Math.floor(Math.random()*30);
                            InventoryItem temp_money = helper.fetchinventoryItemByName("money");
                            temp_money.setItemAmount(temp_money.getItemAmount() + animal_money);
                            helper.removeInventoryItem("money");
                            helper.insertInventory(temp_money, true);

                            Bundle args = new Bundle();
                            args.putString("animal_key", target);
                            args.putInt("money_key", animal_money);
                            fm.beginTransaction();
                            MyDialogFragment myDialogFragment = new MyDialogFragment();
                            myDialogFragment.setArguments(args);
                            myDialogFragment.show(fm, "animal");


                            if(tempp.getNumVisits() == Globals.ANIMAL_TO_PROB.get(target)*10){
                                String gift_name = Globals.ANIMAL_TO_GIFT.get(target);
                                InventoryItem gift_item = helper.fetchinventoryItemByName(gift_name);
                                if(gift_item == null) {
                                    gift_item = new InventoryItem();
                                    gift_item.setItemName(gift_name);
                                }
                                gift_item.setItemName(gift_name);
                                gift_item.setItemAmount(gift_item.getItemAmount() + 1);
                                helper.removeInventoryItem(gift_name);
                                helper.insertInventory(gift_item, true);
                            }
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
                helper.removeInventoryItem(i.getItemName());
                helper.insertInventory(i, true);

            }

        }
    }

    public static void shake(){
        ArrayList<Animal> all_animals = helper.fetchAllAnimals();
        Log.d("master", String.valueOf(all_animals.size()));
        for(Animal a : all_animals){
            if(a.getPresent() == 1 && Globals.NIGHT_TO_ANIMAL.containsKey(a.getAnimalName())){

                Garden.pet1.setImageResource(Util.getImageIdFromName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName())));
                Garden.animal1_name.setText(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));

                a.setPresent(-1);
                helper.removeAnimal(a.getAnimalName());
                helper.insertAnimal(a, true);

                Animal day = helper.fetchAnimalByName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                if(day == null){
                    day = new Animal();
                }
                day.setAnimalName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                day.setPresent(1);
                helper.removeAnimal(day.getAnimalName());
                helper.insertAnimal(day, true);
            }
            else if(a.getPresent() == 2 && Globals.NIGHT_TO_ANIMAL.containsKey(a.getAnimalName())){

                Garden.pet2.setImageResource(Util.getImageIdFromName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName())));
                Garden.animal2_name.setText(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));

                a.setPresent(-1);
                helper.removeAnimal(a.getAnimalName());
                helper.insertAnimal(a, true);

                Animal day = helper.fetchAnimalByName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                if(day == null){
                    day = new Animal();
                }
                day.setAnimalName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                day.setPresent(2);
                helper.removeAnimal(day.getAnimalName());
                helper.insertAnimal(day, true);
            }
            else if(a.getPresent() == 3 && Globals.NIGHT_TO_ANIMAL.containsKey(a.getAnimalName())){

                Garden.pet3.setImageResource(Util.getImageIdFromName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName())));
                Garden.animal3_name.setText(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));

                a.setPresent(-1);
                helper.removeAnimal(a.getAnimalName());
                helper.insertAnimal(a, true);

                Animal day = helper.fetchAnimalByName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                if(day == null){
                    day = new Animal();
                }
                day.setAnimalName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                day.setPresent(3);
                helper.removeAnimal(day.getAnimalName());
                helper.insertAnimal(day, true);
            }
            else if(a.getPresent() == 4 && Globals.NIGHT_TO_ANIMAL.containsKey(a.getAnimalName())){

                Garden.pet4.setImageResource(Util.getImageIdFromName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName())));
                Garden.animal4_name.setText(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));

                a.setPresent(-1);
                helper.removeAnimal(a.getAnimalName());
                helper.insertAnimal(a, true);

                Animal day = helper.fetchAnimalByName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                if(day == null){
                    day = new Animal();
                }
                day.setAnimalName(Globals.NIGHT_TO_ANIMAL.get(a.getAnimalName()));
                day.setPresent(4);
                helper.removeAnimal(day.getAnimalName());
                helper.insertAnimal(day, true);
            }
        }
    }
}


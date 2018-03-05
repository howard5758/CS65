package cs65.edu.dartmouth.cs.gifto;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import java.util.Calendar;
/**
 * Created by kiron on 3/5/18.
 */

public class MyDialogFragment extends DialogFragment {


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        String animal = getArguments().getString("animal_key");
        int animal_money = getArguments().getInt("money_key");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String msg = animal+" brought you "+animal_money+" coins!";
        builder.setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                })
                .setNegativeButton("GO TO SHOP", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), Collection.class);
                        intent.putExtra("goodies", true);
                        startActivity(intent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

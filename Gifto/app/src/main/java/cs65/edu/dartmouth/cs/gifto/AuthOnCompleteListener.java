package cs65.edu.dartmouth.cs.gifto;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by Oliver on 2/24/2018.
 *
 * Helper class to make sure authorization goes smoothly
 * Taken from MyRuns
 */

public class AuthOnCompleteListener implements OnCompleteListener {
    private Context context;
    AuthOnCompleteListener(Context context){
        this.context = context;
    }

    public void onComplete(@NonNull Task task){
        if(task.isSuccessful())
            Util.showActivity(context, MainActivity.class);
        else {
            try {
                Util.showDialog(context, task.getException().getMessage());
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}

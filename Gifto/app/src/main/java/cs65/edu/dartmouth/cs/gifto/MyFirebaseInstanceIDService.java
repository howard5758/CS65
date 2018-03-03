package cs65.edu.dartmouth.cs.gifto;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Created by olivermct on 3/3/18.
 *
 * Also for interacting with messaging...?
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    String TAG = "firebaseIDService";

    public String retrieveToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    public void sendRegistrationToServer(String token) {
        Util.token = token;
        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
    }
}

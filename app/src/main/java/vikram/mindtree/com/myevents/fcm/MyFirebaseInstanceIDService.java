package vikram.mindtree.com.myevents.fcm;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import vikram.mindtree.com.myevents.Constants;
import vikram.mindtree.com.myevents.NetworkIntentService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        storeToken(refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, NetworkIntentService.class);
        intent.putExtra("url", Constants.DEVICE);
        intent.putExtra("method", Constants.POST_METHOD);
        //Post body
        //Register data
        intent.putExtra("regId", token);
        intent.putExtra("from", "reg-token");

        startService(intent);
    }

    private void storeToken(String token) {
        SharedPrefManager.getInstance(this).storeToken(token);
    }
}

package mx.com.cesarcorona.coffeetime.services;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mx.com.cesarcorona.coffeetime.pojo.User;

/**
 * Created by ccabrera on 29/08/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    public static String USERS_REFERENCE = "usuarios";
    public static String PREFERENCES_KEY="firebase";
    public static String KEY_TOKEN = "token";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        //save on firebase a
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USERS_REFERENCE);
        if(FirebaseAuth.getInstance().getCurrentUser()!= null){
            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),token);
            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCES_KEY,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_TOKEN,token);
            editor.apply();
            editor.commit();
        }

    }
}
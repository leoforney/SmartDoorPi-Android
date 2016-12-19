package tk.leoforney.doorreader;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Dynamic Signals on 12/17/2016.
 */

public final class AuthListener implements FirebaseAuth.AuthStateListener {

    private final static String TAG = AuthListener.class.getName();

    private static AuthListener instance;

    private AuthListener() {}

    public static synchronized AuthListener getInstance() {
        if (instance == null) {
            instance = new AuthListener();
        }
        return instance;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "Auth changed! " + firebaseAuth.getCurrentUser().toString());
        } else {
            Log.d(TAG, "Not signed in!");
        }
    }
}

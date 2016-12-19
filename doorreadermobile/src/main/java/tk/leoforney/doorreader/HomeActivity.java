package tk.leoforney.doorreader;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class HomeActivity extends AppCompatActivity {

    BottomBar bottomBar;

    public static String PREF_KEY = "DOORREADER";

    SharedPreferences pref;

    public static FirebaseAuth auth;

    final static String TAG = "HomeActivity";

    GoogleApiClient mGoogleApiClient;

    private final static int RC_SIGN_IN = 264;

    static AuthListener listener;

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        listener = AuthListener.getInstance();

        auth.addAuthStateListener(listener);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("471050035692-b7d7fm359nmoe0m2e0nsv6alfsm2leem.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Connection failed!");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        if (auth.getCurrentUser() == null) signIn();

        bottomBar = BottomBar.attach(this, savedInstanceState);

        pref = getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        bottomBar.setItems(R.menu.bottombar_items);

        bottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int i) {
                switch (i) {
                    case R.id.status:
                        switchFragments(new StatusFragment());
                        break;
                    case R.id.log:
                        switchFragments(new LogFragment());
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int i) {

            }
        });


    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.configureDoors:
                Intent activityIntent = new Intent(getApplicationContext(), ConfigureActivity.class);
                startActivity(activityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topbar_menu_items, menu);
        return true;
    }


    private void switchFragments(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, fragment).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "Signed in as: " + acct.getDisplayName());
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            auth.signInWithCredential(credential);
        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "Failed");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        switch (bottomBar.getCurrentTabPosition()) {
            case 0:
                switchFragments(new StatusFragment());
                break;
            case 1:
                switchFragments(new LogFragment());
                break;
        }

        getSharedPreferences("ChangeListPref", MODE_PRIVATE).edit().remove("changeList").apply();


    }

}

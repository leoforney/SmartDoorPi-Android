package tk.leoforney.doorreader;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class HomeActivity extends AppCompatActivity {

    BottomBar bottomBar;

    public static String PREF_KEY = "DOORREADER";

    SharedPreferences pref;

    public static FirebaseAuth auth;

    final static String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            auth.signInWithEmailAndPassword("client@leoforney.tk", "daryleo1");
        }

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


    private void switchFragments(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, fragment).commit();
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

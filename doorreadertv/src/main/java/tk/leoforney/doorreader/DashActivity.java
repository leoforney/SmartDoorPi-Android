package tk.leoforney.doorreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class DashActivity extends Activity {

    static DataSnapshot dataSnapshot;
    static FirebaseAuth mAuth;

    static List<String> changeList;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_dash);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Log.d("DashActivity", "Logged out");
            mAuth.signInWithEmailAndPassword("client@leoforney.tk", "daryleo1");
        } else {
            Log.d("DashActivity", "Logged in");
        }


        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://strpidoors.firebaseio.com/");

                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            changeList = dataSnapshot.child("changeList").getValue(new GenericTypeIndicator<List<String>>() {
                            });
                            if (changeList != null) {
                                Collections.reverse(changeList);
                            }

                            DashActivity.dataSnapshot = dataSnapshot;

                            StatusFragment.dataChanged();

                            LogFragment.dataChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

    }
}

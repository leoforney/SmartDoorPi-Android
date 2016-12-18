package tk.leoforney.doorreader;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class StatusFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = StatusFragment.class.getName();

    static DatabaseReference mDatabase;
    static FirebaseAuth mAuth;

    Button safeTimeButton;
    RecyclerView recyclerView;

    RelativeLayout progressBar;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Switch notificationSwitch;

    StatusAdapter adapter;

    public static String PREF_KEY = "DOORREADER";

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_status, container, false);
    }

    @Override
    public void onResume() {

        //TODO: OnDataChange not called

        super.onResume();

        this.setRetainInstance(true);

        context = getActivity();

        View v = getView();

        notificationSwitch = (Switch) v.findViewById(R.id.notificationSwitch);
        recyclerView = (RecyclerView) v.findViewById(R.id.doorRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        adapter = new StatusAdapter();

        List<Door> emptyList = new ArrayList<>();
        adapter.doorList = emptyList;

        recyclerView.setAdapter(adapter);

        pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();

        notificationSwitch.setOnCheckedChangeListener(this);

        boolean NotificationsEnabled = pref.getBoolean("Notifications", true);

        notificationSwitch.setChecked(NotificationsEnabled);

        safeTimeButton = (Button) v.findViewById(R.id.safeTimeButton);
        try {
            if (context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName.equals("1.0-cheaty")) {
                Log.d(TAG, "Cheaty!!!!");
                safeTimeButton.setVisibility(View.VISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        safeTimeButton.setOnClickListener(this);

        progressBar = (RelativeLayout) v.findViewById(R.id.loadingPanel);

        SetViewVisibility(false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getToken();

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://strpidoors.firebaseio.com");
                    mDatabase.child("doors").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (progressBar.getVisibility() != View.VISIBLE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            SetViewVisibility(true);

                            GenericTypeIndicator<List<Door>> type = new GenericTypeIndicator<List<Door>>() {
                            };

                            List<Door> doorList = dataSnapshot.getValue(type);

                            for (Door door : doorList) {
                                Log.d(TAG, door.codeName);
                            }

                            adapter.setDoorList(doorList);

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

    }

    public void SetViewVisibility(boolean Visible) {
        int Visibility = View.VISIBLE;
        if (Visible) {
            Visibility = View.VISIBLE;
        }
        if (!Visible) {
            Visibility = View.GONE;
        }

        recyclerView.setVisibility(Visibility);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.notificationSwitch) {
            editor.putBoolean("Notifications", isChecked);
            editor.commit();
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("notifications");
            }
            if (!isChecked) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications");
            }
            Log.d(TAG, "Switch changed: " + pref.getBoolean("Notifications", false) + ", " + isChecked);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.safeTimeButton:
                TimeDialog dialog = new TimeDialog();
                dialog.setStartingValue(getStartingBoolean());
                dialog.show(getFragmentManager(), "timedialog");
                break;
        }
    }


    static boolean returnValue = false;

    private static boolean getStartingBoolean() {
        if (mDatabase != null) {
            mDatabase.child("safetime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    returnValue = dataSnapshot.getValue(Boolean.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        return returnValue;
    }

    public static void handleBoolean(boolean b) {
        Log.d(TAG, String.valueOf(b));
        if (HomeActivity.auth.getCurrentUser() != null) {
            mDatabase.child("safetime").setValue(b);
        }
    }
}

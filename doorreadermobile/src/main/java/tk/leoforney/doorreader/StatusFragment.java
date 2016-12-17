package tk.leoforney.doorreader;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class StatusFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = StatusFragment.class.getName();

    static DatabaseReference mDatabase;
    static FirebaseAuth mAuth;

    TextView FrontDoorView;
    TextView PatioDoorView;
    TextView GarageDoorView;
    TextView FrontLeftDoorView;
    TextView FrontRightDoorView;

    Button safeTimeButton;

    RelativeLayout progressBar;

    boolean FrontDoor;
    boolean PatioDoor;
    boolean GarageDoor;
    boolean FrontLeftDoor;
    boolean FrontRightDoor;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    Switch notificationSwitch;

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

        Log.d(TAG, "Resume called");

        context = getActivity();

        View v = getView();

        notificationSwitch = (Switch) v.findViewById(R.id.notificationSwitch);

        pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();

        notificationSwitch.setOnCheckedChangeListener(this);

        boolean NotificationsEnabled = pref.getBoolean("Notifications", true);

        notificationSwitch.setChecked(NotificationsEnabled);

        FrontDoorView = (TextView) v.findViewById(R.id.FrontDoorView);
        PatioDoorView = (TextView) v.findViewById(R.id.PatioDoorView);
        GarageDoorView = (TextView) v.findViewById(R.id.GarageDoorView);
        FrontLeftDoorView = (TextView) v.findViewById(R.id.FrontLeftDoorView);
        FrontRightDoorView = (TextView) v.findViewById(R.id.FrontRightDoorView);

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

                            HashMap<String, Boolean> doorMap = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Boolean>>() {
                            });

                            FrontDoor = doorMap.get("FrontDoor");
                            ActUponByBooleanAndView(FrontDoor, FrontDoorView);
                            PatioDoor = doorMap.get("PatioDoor");
                            ActUponByBooleanAndView(PatioDoor, PatioDoorView);
                            GarageDoor = doorMap.get("GarageDoor");
                            ActUponByBooleanAndView(GarageDoor, GarageDoorView);
                            FrontLeftDoor = doorMap.get("FrontLeftDoor");
                            ActUponByBooleanAndView(FrontLeftDoor, FrontLeftDoorView);
                            FrontRightDoor = doorMap.get("FrontRightDoor");
                            ActUponByBooleanAndView(FrontRightDoor, FrontRightDoorView);

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

        FrontDoorView.setVisibility(Visibility);
        PatioDoorView.setVisibility(Visibility);
        GarageDoorView.setVisibility(Visibility);
        FrontLeftDoorView.setVisibility(Visibility);
        FrontRightDoorView.setVisibility(Visibility);

    }

    public void ActUponByBooleanAndView(boolean isOpen, TextView view) {
        if (isAdded()) {
            if (isOpen) {
                view.setBackgroundColor(getResources().getColor(R.color.open));
            }
            if (!isOpen) {
                view.setBackgroundColor(getResources().getColor(R.color.closed));
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.notificationSwitch) {
            editor.putBoolean("Notifications", isChecked);
            editor.commit();
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("notifications");
            } if (!isChecked) {
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

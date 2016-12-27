package tk.leoforney.doorreader;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

    FirebaseRecyclerAdapter adapter;

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

        context = getActivity();

        View v = getView();

        notificationSwitch = (Switch) v.findViewById(R.id.notificationSwitch);
        recyclerView = (RecyclerView) v.findViewById(R.id.doorRecyclerView);
        progressBar = (RelativeLayout) v.findViewById(R.id.loadingPanel);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);

        /**
         * Notification Switch section
         */
        pref = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        editor = pref.edit();
        notificationSwitch.setOnCheckedChangeListener(this);
        boolean NotificationsEnabled = pref.getBoolean("Notifications", true);
        notificationSwitch.setChecked(NotificationsEnabled);

        /**
         * Safetime
         */
        safeTimeButton = (Button) v.findViewById(R.id.safeTimeButton);
        try {
            if (context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName.equals("1.0-cheaty")) {
                Log.d(TAG, "Cheaty!");
                safeTimeButton.setVisibility(View.VISIBLE);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        safeTimeButton.setOnClickListener(this);

        // TODO: Fix that weird bug where the app crashes

        SetViewVisibility(false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getToken();

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://strpidoors.firebaseio.com").child("doors");
                    adapter = new FirebaseRecyclerAdapter<Door, DoorViewHolder>(Door.class, R.layout.door_item, DoorViewHolder.class, reference) {

                        @Override
                        protected void populateViewHolder(DoorViewHolder viewHolder, Door model, int position) {
                            viewHolder.doorTextView.setText(model.name);
                            viewHolder.actUponByBooleanAndView(model.current);
                        }
                    };
                    recyclerView.setAdapter(adapter);

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (progressBar.getVisibility() != View.VISIBLE) {
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            SetViewVisibility(true);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });


                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.cleanup();
    }

    public static class DoorViewHolder extends RecyclerView.ViewHolder {

        TextView doorTextView;

        public DoorViewHolder(View itemView) {
            super(itemView);

            doorTextView = (TextView) itemView.findViewById(R.id.doorTextView);
        }

        private void actUponByBooleanAndView(boolean isOpen) {
            if (isOpen) {
                doorTextView.setBackgroundColor(doorTextView.getContext().getResources().getColor(R.color.open));
            }
            if (!isOpen) {
                doorTextView.setBackgroundColor(doorTextView.getContext().getResources().getColor(R.color.closed));
            }

        }
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
        if (FirebaseDatabase.getInstance().getReference() != null) {
            FirebaseDatabase.getInstance().getReference().child("safetime").addValueEventListener(new ValueEventListener() {
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
            FirebaseDatabase.getInstance().getReference().child("safetime").setValue(b);
        }
    }
}

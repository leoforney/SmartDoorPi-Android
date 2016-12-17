package tk.leoforney.doorreader;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;

public class StatusFragment extends Fragment {

    private static final String TAG = StatusFragment.class.getName();

    static TextView FrontDoorView;
    static TextView PatioDoorView;
    static TextView GarageDoorView;
    static TextView FrontLeftDoorView;
    static TextView FrontRightDoorView;

    static RelativeLayout progressBar;

    static boolean FrontDoor;
    static boolean PatioDoor;
    static boolean GarageDoor;
    static boolean FrontLeftDoor;
    static boolean FrontRightDoor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_door_status, container, false);
    }

    @Override
    public void onResume() {

        //TODO: OnDataChange not called

        super.onResume();

        Log.d(TAG, "Resume called");

        View v = getView();

        FrontDoorView = (TextView) v.findViewById(R.id.FrontDoorView);
        PatioDoorView = (TextView) v.findViewById(R.id.PatioDoorView);
        GarageDoorView = (TextView) v.findViewById(R.id.GarageDoorView);
        FrontLeftDoorView = (TextView) v.findViewById(R.id.FrontLeftDoorView);
        FrontRightDoorView = (TextView) v.findViewById(R.id.FrontRightDoorView);

        progressBar = (RelativeLayout) v.findViewById(R.id.loadingPanel);

        SetViewVisibility(false);

    }

    public static void SetViewVisibility(boolean Visible) {
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

    public static void ActUponByBooleanAndView(boolean isOpen, TextView view) {
        if (isOpen) {
            view.setBackgroundColor(Color.parseColor("#d32f2f"));
        }
        if (!isOpen) {
            view.setBackgroundColor(Color.parseColor("#388E3C"));
        }

    }

    public static void dataChanged() {
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }


        SetViewVisibility(true);

        HashMap<String, Boolean> doorMap = DashActivity.dataSnapshot.child("doors").getValue(new GenericTypeIndicator<HashMap<String, Boolean>>() {
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

}

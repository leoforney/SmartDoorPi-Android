package tk.leoforney.doorreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.pi4j.io.gpio.RaspiPin;

import java.util.ArrayList;
import java.util.List;

public class ConfigureFragment extends Fragment implements View.OnClickListener {

    final static String TAG = ConfigureFragment.class.getName();

    private RecyclerView rv;

    Context context;

    FirebaseRecyclerAdapter adapter;

    FloatingActionButton fab;

    CoordinatorLayout coordinatorLayout;

    Button NewDoorButton;

    DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_configure, container, false);
    }

    @Override
    public void onResume() {

        super.onResume();

        View v = getView();
        context = getActivity();

        rv = (RecyclerView) v.findViewById(R.id.configureRecyclerView);
        fab = (FloatingActionButton) v.findViewById(R.id.save_fab_configure);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator_configure);
        NewDoorButton = (Button) v.findViewById(R.id.addDoorButton);

        fab.setOnClickListener(this);
        NewDoorButton.setOnClickListener(this);

        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                   ref = FirebaseDatabase.getInstance().getReference().child("doors");
                    adapter = new FirebaseRecyclerAdapter<Door, ConfigureItemHolder>(Door.class, R.layout.door_configure_item, ConfigureItemHolder.class, ref) {

                        @Override
                        protected void populateViewHolder(ConfigureItemHolder viewHolder, Door model, int position) {
                            viewHolder.nameEditText.setText(model.removeLastChar(model.name));
                            viewHolder.door = model;

                            selectSpinnerItemByValue(viewHolder.gpioSpinner, model.doorPin);
                        }
                    };
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                    SwipeableRecyclerViewTouchListener swipeTouchListener =
                            new SwipeableRecyclerViewTouchListener(rv,
                                    new SwipeableRecyclerViewTouchListener.SwipeListener() {

                                        @Override
                                        public boolean canSwipeLeft(int position) {
                                            return true;
                                        }

                                        @Override
                                        public boolean canSwipeRight(int position) {
                                            return true;
                                        }

                                        @Override
                                        public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {

                                            final List<Door> doorList = new ArrayList<>();
                                            View child;
                                            for (int i = 0; i < rv.getChildCount(); i++) {
                                                child = rv.getChildAt(i);
                                                ConfigureItemHolder holder = (ConfigureItemHolder) rv.getChildViewHolder(child);
                                                doorList.add(holder.door);
                                            }

                                            for (int position : reverseSortedPositions) {
                                                doorList.remove(position);
                                            }
                                            ref.setValue(doorList);
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                            final List<Door> doorList = new ArrayList<>();
                                            View child;
                                            for (int i = 0; i < rv.getChildCount(); i++) {
                                                child = rv.getChildAt(i);
                                                ConfigureItemHolder holder = (ConfigureItemHolder) rv.getChildViewHolder(child);
                                                doorList.add(holder.door);
                                            }

                                            for (int position : reverseSortedPositions) {
                                                doorList.remove(position);
                                            }
                                            ref.setValue(doorList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });

                    rv.addOnItemTouchListener(swipeTouchListener);

                }
            }
        });


    }

    public static void selectSpinnerItemByValue(Spinner spnr, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(value)) {
                spnr.setSelection(position);
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        List<Door> doorList = new ArrayList<>();
        View child;
        for (int i = 0; i < rv.getChildCount(); i++) {
            child = rv.getChildAt(i);
            ConfigureItemHolder holder = (ConfigureItemHolder) rv.getChildViewHolder(child);
            Log.d(TAG, "Door " + holder.door.name + "@" + holder.door.doorPin);
            doorList.add(holder.door);
        }
        switch (view.getId()) {
            case R.id.save_fab_configure:
                if (ref != null) {
                    ref.setValue(doorList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(coordinatorLayout, "Successfully saved!", Snackbar.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(coordinatorLayout, "Oh no! Something went wrong!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                break;
            case R.id.addDoorButton:
                if (ref != null) {
                    Door newDoor = new Door();
                    newDoor.setCanonicalName("");
                    newDoor.doorPin = RaspiPin.GPIO_00.getName();
                    newDoor.current = false;
                    newDoor.previous = false;
                    doorList.add(newDoor);
                    ref.setValue(doorList);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }


    public static class ConfigureItemHolder extends RecyclerView.ViewHolder implements TextWatcher, AdapterView.OnItemSelectedListener {
        private EditText nameEditText;
        private Spinner gpioSpinner;

        private Door door;

        public ConfigureItemHolder(View itemView) {
            super(itemView);

            nameEditText = (EditText) itemView.findViewById(R.id.doorNameEditText);
            gpioSpinner = (Spinner) itemView.findViewById(R.id.gpioSpinner);

            nameEditText.addTextChangedListener(this);
            gpioSpinner.setOnItemSelectedListener(this);

            List<String> names = new ArrayList<>();

            for (int i = 0; i < RaspiPin.allPins().length; i++) {
                names.add(i, "GPIO " + i);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(), R.layout.gpio_spinner_item, names);
            gpioSpinner.setAdapter(adapter);

        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (door != null) {
                door.setCanonicalName(charSequence.toString());
                Log.d(TAG, "Name changed! " + door.name + " C: " + door.codeName);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            door.doorPin = (String) ((TextView) view).getText();
            Log.d(TAG, "Door Pin changed! " + door.doorPin);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

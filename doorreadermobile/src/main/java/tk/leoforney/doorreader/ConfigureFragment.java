package tk.leoforney.doorreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pi4j.io.gpio.RaspiPin;

import java.util.ArrayList;
import java.util.List;

public class ConfigureFragment extends Fragment {

    final static String TAG = ConfigureFragment.class.getName();

    private RecyclerView rv;

    Context context;

    FirebaseRecyclerAdapter adapter;

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

        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("doors");
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

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}

package tk.leoforney.doorreader;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;

public class LogFragment extends Fragment {

    final static String TAG = LogFragment.class.getName();

    private RecyclerView rv;

    RVAdapter adapter;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onResume() {

        super.onResume();

        //TODO: Retain fragment after rotation.

        context = getActivity();

        View v = getView();

        rv = (RecyclerView) v.findViewById(R.id.rv);

        final LinearLayoutManager llm = new LinearLayoutManager(context);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        adapter = new RVAdapter();
        rv.setAdapter(adapter);

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://strpidoors.firebaseio.com");
                    database.child("changeList").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<String> changeList = dataSnapshot.getValue(new GenericTypeIndicator<List<String>>() {
                            });
                            if (changeList != null) {
                                Collections.reverse(changeList);

                                adapter.setChangeList(changeList);

                                adapter.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }

}
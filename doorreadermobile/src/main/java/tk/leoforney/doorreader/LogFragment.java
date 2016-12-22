package tk.leoforney.doorreader;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LogFragment extends Fragment {

    final static String TAG = LogFragment.class.getName();

    private RecyclerView rv;

    Context context;

    FirebaseRecyclerAdapter adapter;

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
        rv.setLayoutManager(new LinearLayoutManager(context));
        rv.setHasFixedSize(true);

        HomeActivity.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReferenceFromUrl("https://strpidoors.firebaseio.com").child("changeList");

                    adapter = new FirebaseRecyclerAdapter<String, LogItemHolder>(String.class, R.layout.log_change_item, LogItemHolder.class, database) {
                        @Override
                        protected void populateViewHolder(LogItemHolder viewHolder, String model, int position) {
                            viewHolder.doorTextView.setText(model);
                        }
                    };

                    rv.setAdapter(adapter);

                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }


    public static class LogItemHolder extends RecyclerView.ViewHolder {
        private TextView doorTextView;

        public LogItemHolder(View itemView) {
            super(itemView);
            doorTextView = (TextView) itemView.findViewById(R.id.changeTextView);
        }
    }
}
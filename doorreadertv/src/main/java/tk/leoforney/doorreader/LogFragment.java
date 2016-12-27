package tk.leoforney.doorreader;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LogFragment extends Fragment {

    private RecyclerView rv;

    static RVAdapter adapter;

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

        rv.requestFocus();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }

    public static void dataChanged() {
        if (DashActivity.changeList != null) {
            adapter.setChangeList(DashActivity.changeList);
            adapter.notifyDataSetChanged();
        }
    }

}
package tk.leoforney.doorreader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Leo on 7/3/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.DoorViewHolder> {

    public static class DoorViewHolder extends RecyclerView.ViewHolder {

        TextView doorTextView;

        DoorViewHolder(View itemView) {
            super(itemView);

            doorTextView = (TextView) itemView.findViewById(R.id.changeTextView);
        }
    }

    List<String> changeList;

    RVAdapter(){

    }

    public void setChangeList(List<String> changeList) {
        this.changeList = changeList;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DoorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        DoorViewHolder dvh = new DoorViewHolder(v);
        return dvh;
    }

    @Override
    public void onBindViewHolder(DoorViewHolder doorViewHolder, int i) {
        if (changeList != null) {
            doorViewHolder.doorTextView.setText(changeList.get(i));
        }

    }

    @Override
    public int getItemCount() {
        if (changeList != null) {
            return changeList.size();
        } else {
            return 0;
        }
    }
}
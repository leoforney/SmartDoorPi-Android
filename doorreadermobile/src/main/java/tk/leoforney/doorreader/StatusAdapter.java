package tk.leoforney.doorreader;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Leo on 7/3/2016.
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.DoorViewHolder> {

    public static class DoorViewHolder extends RecyclerView.ViewHolder {

        TextView doorTextView;

        DoorViewHolder(View itemView) {
            super(itemView);

            doorTextView = (TextView) itemView.findViewById(R.id.doorTextView);
        }
    }

    List<Door> doorList;

    StatusAdapter() {
    }

    public void setDoorList(List<Door> doorList) {
        this.doorList = doorList;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DoorViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.door_item, viewGroup, false);
        return new DoorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DoorViewHolder doorViewHolder, int i) {
        doorViewHolder.doorTextView.setText(doorList.get(i).name);
        ActUponByBooleanAndView(doorList.get(i).current, doorViewHolder.doorTextView);

    }

    @Override
    public int getItemCount() {
        if (doorList != null) {
            return doorList.size();
        } else {
            return 0;
        }
    }

    private void ActUponByBooleanAndView(boolean isOpen, TextView view) {
        if (isOpen) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.open));
        }
        if (!isOpen) {
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.closed));
        }

    }

}
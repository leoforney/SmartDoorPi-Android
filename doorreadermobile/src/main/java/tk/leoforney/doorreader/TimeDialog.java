package tk.leoforney.doorreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by Leo on 8/13/2016.
 */
public class TimeDialog extends DialogFragment implements CompoundButton.OnCheckedChangeListener {

    static Context context;
    static Switch safeTimeSwitch;
    boolean StartingValue = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View myView = View.inflate(context, R.layout.dialog_time, new RelativeLayout(context));
        builder.setView(myView);
        safeTimeSwitch = (Switch) myView.findViewById(R.id.safeTimeSwitch);
        safeTimeSwitch.setChecked(StartingValue);
        safeTimeSwitch.setOnCheckedChangeListener(this);
        return builder.create();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.safeTimeSwitch:
                StatusFragment.handleBoolean(isChecked);
                break;
        }
    }

    public void setStartingValue(boolean value) {
        this.StartingValue = value;
    }
}

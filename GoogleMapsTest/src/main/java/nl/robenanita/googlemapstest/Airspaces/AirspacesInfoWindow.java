package nl.robenanita.googlemapstest.Airspaces;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class AirspacesInfoWindow {
    public AirspacesInfoWindow(Context context)
    {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.airspace_info_window);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    private Context context;
    private Dialog dialog;

    public void ShowAirspacesInfoWindow(Airspaces airspaces)
    {
        if (airspaces != null) {
            TextView infoTxt = (TextView) dialog.findViewById(R.id.airspaces_info_text);
            infoTxt.setText(airspaces.getAirpspacesInfoString());
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dialogWidth = (int)(displayMetrics.widthPixels * 0.85);
        int dialogHeight = (int)(displayMetrics.heightPixels * 0.85);
        dialog.getWindow().setLayout(dialogWidth, dialogHeight);

        dialog.show();
    }
}

package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Rob Verhoef on 25-3-14.
 */
public class AddWayPointPopup extends PopupWindow{
    public Boolean Result;
    public Boolean Search;
    public String WaypointName;
    public LatLng Location;

    EditText textEdit;
    public AddWayPointPopup(Context context, View layout)
    {
        super(context);
        Result = false;
        Search = false;


        Button SaveBtn = (Button) layout.findViewById(R.id.addWayPointSaveBtn);
        Button CancelBtn = (Button) layout.findViewById(R.id.addWayPointCancelBtn);
        Button SearchBtn = (Button) layout.findViewById(R.id.searchWaypointBtn);
        textEdit = (EditText) layout.findViewById(R.id.addWayPointNameEdit);

        SaveBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!"".equals(textEdit.getText().toString()))
                {
                    WaypointName = textEdit.getText().toString();
                    Result = true;
                    Search = false;
                    dismiss();
                }
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result = false;
                Search = false;
                dismiss();
            }
        });

        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result = false;
                Search = true;
                dismiss();
            }
        });
    }
}
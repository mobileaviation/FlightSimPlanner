package nl.robenanita.googlemapstest.Airspaces;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 1-9-2017.
 */

public class AirspaceAdapter extends BaseAdapter {

    public AirspaceAdapter(Airspaces airspaces)
    {
        this.airspaces = airspaces;
    }

    private Airspaces airspaces;

    @Override
    public int getCount() {
        return airspaces.size();
    }

    @Override
    public Object getItem(int i) {
        return airspaces.get(i);
    }

    public Airspace getAirspace(int i)
    {
        return airspaces.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.airspace_item, viewGroup, false);

        TextView airspaceCategory = (TextView) view.findViewById(R.id.airspaceCategory);
        TextView airspaceTopLevelTxt = (TextView) view.findViewById(R.id.airspaceTopLevelTxt);
        TextView airspacebottomLevelTxt = (TextView) view.findViewById(R.id.airspacebottomLevelTxt);
        TextView airspaceNameTxt = (TextView) view.findViewById(R.id.airspaceNameTxt);

        Airspace airspace = getAirspace(i);

        airspaceCategory.setText(airspace.Category.toString());
        airspaceTopLevelTxt.setText(SetString(airspace.getAltLimit_Top(), airspace.AltLimit_Top_Unit, airspace.AltLimit_Top_Ref));
        airspaceTopLevelTxt.setText(SetString(airspace.getAltLimit_Bottom(), airspace.AltLimit_Bottom_Unit, airspace.AltLimit_Bottom_Ref));
        airspaceNameTxt.setText(airspace.Name);

        return view;
    }

    private String SetString(Integer altitude, AltitudeUnit unit, AltitudeReference reference )
    {
        if (altitude==0) return "GND";
        if (unit==AltitudeUnit.FL) return "FL " + altitude.toString();
        if (unit==AltitudeUnit.F) return altitude.toString() + " " + reference.toString();
        return altitude.toString() + " " + unit.toString();
    }
}

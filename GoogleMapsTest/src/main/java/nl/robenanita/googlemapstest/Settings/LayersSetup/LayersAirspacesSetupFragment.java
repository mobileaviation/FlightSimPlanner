package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LayersAirspacesSetupFragment extends Fragment {

    NavigationActivity n;
    Property p;

    public LayersAirspacesSetupFragment() {
        // Required empty public constructor

    }

    private void getProperty()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(n);
        propertiesDataSource.open();
        p = propertiesDataSource.getMapSetup("AIRSPACES");

        if (p==null)
        {
            p = new Property();
            p.value1 = "setup";
            p.name = "AIRSPACES";
            p.value2 = Boolean.toString(true);
            propertiesDataSource.InsertProperty(p);
        }
        propertiesDataSource.close();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = (LinearLayout) inflater.inflate(R.layout.fragment_layers_airspaces_setup, container, false);
        n = (NavigationActivity)container.getContext();
        getProperty();

        final CheckBox enabledBox = (CheckBox) view.findViewById(R.id.airspacesEnabledBtn);
        enabledBox.setChecked(Boolean.valueOf(p.value2));

        enabledBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                n.mapController.ShowSkylinesMap(enabledBox.isChecked());
                p.value2 = Boolean.toString(enabledBox.isChecked());
                SaveToDatabase(n);
            }
        });

        n.mapController.ShowSkylinesMap(Boolean.valueOf(p.value2));
        return view;
    }

    public void SaveToDatabase(Context context)
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open();
        propertiesDataSource.updateProperty(p);
        propertiesDataSource.close();
    }


}

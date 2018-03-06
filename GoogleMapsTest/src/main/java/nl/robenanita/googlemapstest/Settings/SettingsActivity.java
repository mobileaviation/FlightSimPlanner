package nl.robenanita.googlemapstest.Settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andreabaccega.widget.FormEditText;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.Airport.AirportType;
import nl.robenanita.googlemapstest.Helpers;
import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Airport.Runway;
import nl.robenanita.googlemapstest.database.MarkerProperties;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.database.RunwaysDataSource;
import nl.robenanita.googlemapstest.search.SearchAirportsPopup;

public class SettingsActivity extends ActionBarActivity {
    private String ServerIPAddress;
    private Integer ServerPort;
    private Airport airport;
    private Runway runway;
    private FormEditText ipAddressTxt;
    private TextView homeAirportTxt;
    private TextView portTxt;
    private Button saveBtn;
    private Button cancelBtn;
    private Button searchAirportBtn;
    private RadioButton simRadioBtn;
    private RadioButton simV2RadioBtn;
    private RadioButton gpsRadioBtn;
    private CheckBox showInstrumentsChkBox;
    private NumberPicker rw;
    private String[] rws;
    private SearchAirportsPopup searchAirportsPopup;
    private MarkerProperties markerProperties;
    private Property bufferProperty;
    private TextView bufferSizeTxt;
    private CheckBox bufferVisibleBox;
    private EditText chartUrlEdit;
    private Property chartUrlProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
        propertiesDataSource.open(true);
        propertiesDataSource.FillProperties();
        markerProperties = propertiesDataSource.getMarkersProperties();
        bufferProperty = propertiesDataSource.GetProperty("BUFFER");
        propertiesDataSource.close(true);

        setupMarkersVisible();

        ServerIPAddress = propertiesDataSource.IpAddress.value1;
        ServerPort = Helpers.parseIntWithDefault(propertiesDataSource.IpAddress.value2, 5000);

        airport = propertiesDataSource.InitAirport;
        runway = propertiesDataSource.InitRunway;
        setupRunwaysSpinner();

        bufferSizeTxt = (TextView)findViewById(R.id.bufferSizeEdit);
        bufferVisibleBox = (CheckBox) findViewById(R.id.bufferVisibleBox);
        bufferSizeTxt.setText(bufferProperty.value1);
        bufferVisibleBox.setChecked(Boolean.parseBoolean(bufferProperty.value2));

        ipAddressTxt = (FormEditText)findViewById(R.id.ipAddressEdit);
        //setupIpAddressEdit();
        ipAddressTxt.setText(ServerIPAddress);

        homeAirportTxt = (TextView) findViewById(R.id.homeAirportEdit);
        homeAirportTxt.setText(airport.name);

        portTxt = (TextView) findViewById(R.id.portEdit);
        portTxt.setText(ServerPort.toString());

        cancelBtn = (Button) findViewById(R.id.cancelSettingsBtn);
        saveBtn = (Button) findViewById(R.id.saveSettingsBtn);
        searchAirportBtn = (Button) findViewById(R.id.searchHomeAirportBtn);

        gpsRadioBtn = (RadioButton) findViewById(R.id.gpsRadioBtn);
        simRadioBtn = (RadioButton) findViewById(R.id.simRadioBtn);
        simV2RadioBtn = (RadioButton) findViewById(R.id.simv2RadioBtn);

        NavigationActivity.ConnectionType c = propertiesDataSource.getConnectionType();
        switch (c) {
            case gps:
            {
                gpsRadioBtn.setChecked(true);
                simV2RadioBtn.setChecked(false);
                simRadioBtn.setChecked(false);
                break;
            }
            case sim:
            {
                gpsRadioBtn.setChecked(false);
                simV2RadioBtn.setChecked(false);
                simRadioBtn.setChecked(true);
                break;
            }
            case simv2:
            {
                gpsRadioBtn.setChecked(false);
                simV2RadioBtn.setChecked(true);
                simRadioBtn.setChecked(false);
                break;
            }
        }

        showInstrumentsChkBox = (CheckBox) findViewById(R.id.showInstrumentsChkBox);
        showInstrumentsChkBox.setChecked((propertiesDataSource.getInstrumentsVisible()));

        chartUrlEdit = (EditText) findViewById(R.id.airportChartsUrlEdit);
        if (propertiesDataSource.ChartsUrl != null) chartUrlEdit.setText(propertiesDataSource.ChartsUrl.value1);
        else chartUrlEdit.setText("http://*****");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                setResult(400,intent);
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveSettings()) {
                    Intent intent = new Intent();
                    setResult(401, intent);
                    finish();
                }
            }
        });

        searchAirportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowSearchAirportPopup();
            }
        });
    }

    private boolean saveSettings()
    {
        boolean valid = false;

        if (ipAddressTxt.testValidity()) {
            valid = true;
            PropertiesDataSource propertiesDataSource = new PropertiesDataSource(this);
            propertiesDataSource.open(true);
            propertiesDataSource.FillProperties();

            NavigationActivity.ConnectionType c = NavigationActivity.ConnectionType.gps;;
            if (simRadioBtn.isChecked()) c = NavigationActivity.ConnectionType.sim;
            if (simV2RadioBtn.isChecked()) c = NavigationActivity.ConnectionType.simv2;
            if (gpsRadioBtn.isChecked()) c = NavigationActivity.ConnectionType.gps;
            propertiesDataSource.updateConnectionType(c);
            propertiesDataSource.IpAddress.value1 = ipAddressTxt.getText().toString();
            propertiesDataSource.IpAddress.value2 = portTxt.getText().toString();

            propertiesDataSource.updateProperty(propertiesDataSource.IpAddress);

            propertiesDataSource.updateInstrumentVisible(showInstrumentsChkBox.isChecked());

            Integer r = rw.getValue();
            Integer r1 = 0;
            Integer i = 0;

            Boolean runwaycheck = (airport.runways==null)? false : (airport.runways.size()>0);

            if (runwaycheck) {
                while (i <= airport.runways.size()) {
                    if (r == r1) {
                        runway = airport.runways.get(i);
                        runway.active = "le";
                    }
                    r1++;
                    if (r == r1) {
                        runway = airport.runways.get(i);
                        runway.active = "he";
                    }
                    r1++;
                    i++;
                }
            } else {
                runway = new Runway();
                runway.id = -1;
                runway.le_latitude_deg = airport.latitude_deg;
                runway.le_longitude_deg = airport.longitude_deg;
                runway.le_heading_degT = 0;
            }


            propertiesDataSource.InitAirport = airport;
            propertiesDataSource.InitRunway = runway;

            propertiesDataSource.UpdateInitAirport();

            markersCheckBoxesToProperties();
            propertiesDataSource.storePropertiesInDB(markerProperties);

            bufferProperty.value1 = bufferSizeTxt.getText().toString();
            bufferProperty.value2 = (bufferVisibleBox.isChecked() ? "true" : "false");
            propertiesDataSource.updateProperty(bufferProperty);

            if (propertiesDataSource.ChartsUrl == null)
            {
                propertiesDataSource.ChartsUrl = new Property();
                propertiesDataSource.ChartsUrl.name = "CHARTSURL";
                propertiesDataSource.ChartsUrl.value1 = chartUrlEdit.getText().toString();
                propertiesDataSource.ChartsUrl.value2 = "";
                propertiesDataSource.InsertProperty(propertiesDataSource.ChartsUrl);
            }
            else
            {
                propertiesDataSource.ChartsUrl.value1 = chartUrlEdit.getText().toString();
                propertiesDataSource.updateProperty(propertiesDataSource.ChartsUrl);
            }

            propertiesDataSource.close(true);

        }

        return valid;
    }

    private void setupIpAddressEdit()
    {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) +
                            source.subSequence(start, end) +
                            destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\." +
                            "(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };
        ipAddressTxt.setFilters(filters);
    }

    private void setupRunwaysSpinner()
    {
        rw = (NumberPicker) findViewById(R.id.runwayPicker);
        if (airport.runways != null) {
            //if (runway.id > 0) {
                rw.setEnabled(true);
                rws = new String[airport.runways.size() * 2];
                int i = 0;
                int active = 0;
                for (Runway r : airport.runways) {
                    if ((r == runway) && (runway.active.equals("le"))) active = i;
                    rws[i] = r.le_ident + " : " + r.surface;
                    i++;
                    if ((r == runway) && (runway.active.equals("he"))) active = i;
                    rws[i] = r.he_ident + " : " + r.surface;
                    i++;
                }

                rw.setWrapSelectorWheel(false);

                int max = rws.length - 1;
                int maxV = rw.getMaxValue();
                if (max > maxV) {
                    rw.setMinValue(0);
                    rw.setValue(0);
                    rw.setDisplayedValues(rws);
                    rw.setMaxValue(max);
                } else {
                    rw.setMinValue(0);
                    rw.setValue(0);
                    rw.setMaxValue(max);
                    rw.setDisplayedValues(rws);
                }

                rw.setValue(active);
//            } else {
//                rw.setEnabled(false);
//            }
        }
        else
        {
            rw.setEnabled(false);
            rws = new String[1];
            rws[0] = "Unknown!";
            rw.setMinValue(0);
            rw.setValue(0);
            rw.setMaxValue(0);
            rw.setDisplayedValues(rws);
            //rw.setMaxValue(0);
        }
    }

    private void markersCheckBoxesToProperties()
    {
        CheckBox largeAirportChkBox = (CheckBox)findViewById(R.id.largeAirportChkBox);
        CheckBox mediumAirportChkBox = (CheckBox)findViewById(R.id.mediumAirportChkBox);
        CheckBox smallAirportChkBox = (CheckBox)findViewById(R.id.smallAirportChkBox);
        CheckBox heliportAirportChkBox = (CheckBox)findViewById(R.id.heliportAirportChkBox);
        CheckBox seaBaseChkBox = (CheckBox)findViewById(R.id.seaBaseChkBox);
        CheckBox balloonBaseChkBox = (CheckBox)findViewById(R.id.balloonBaseChkBox);

        markerProperties.getMarkerPropertyByAirportType(AirportType.large_airport).visible = largeAirportChkBox.isChecked();
        markerProperties.getMarkerPropertyByAirportType(AirportType.medium_airport).visible = mediumAirportChkBox.isChecked();
        markerProperties.getMarkerPropertyByAirportType(AirportType.small_airport).visible = smallAirportChkBox.isChecked();
        markerProperties.getMarkerPropertyByAirportType(AirportType.heliport).visible = heliportAirportChkBox.isChecked();
        markerProperties.getMarkerPropertyByAirportType(AirportType.seaplane_base).visible = seaBaseChkBox.isChecked();
        markerProperties.getMarkerPropertyByAirportType(AirportType.balloonport).visible = balloonBaseChkBox.isChecked();
    }

    private void setupMarkersVisible()
    {
        CheckBox largeAirportChkBox = (CheckBox)findViewById(R.id.largeAirportChkBox);
        CheckBox mediumAirportChkBox = (CheckBox)findViewById(R.id.mediumAirportChkBox);
        CheckBox smallAirportChkBox = (CheckBox)findViewById(R.id.smallAirportChkBox);
        CheckBox heliportAirportChkBox = (CheckBox)findViewById(R.id.heliportAirportChkBox);
        CheckBox seaBaseChkBox = (CheckBox)findViewById(R.id.seaBaseChkBox);
        CheckBox balloonBaseChkBox = (CheckBox)findViewById(R.id.balloonBaseChkBox);

        largeAirportChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.large_airport).visible);
        mediumAirportChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.medium_airport).visible);
        smallAirportChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.small_airport).visible);
        heliportAirportChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.heliport).visible);
        seaBaseChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.seaplane_base).visible);
        balloonBaseChkBox.setChecked(markerProperties.getMarkerPropertyByAirportType(AirportType.balloonport).visible);

    }

    private void ShowSearchAirportPopup()
    {
        int popupWidth = 800;
        int popupHeight = 350;

        LinearLayout viewGroup = null;
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View Layout = layoutInflater.inflate(R.layout.searchairports_popup, viewGroup);

        searchAirportsPopup = new SearchAirportsPopup(this, Layout);

        searchAirportsPopup.setContentView(Layout);
        searchAirportsPopup.setWidth(popupWidth);
        searchAirportsPopup.setHeight(popupHeight);
        searchAirportsPopup.setFocusable(true);

        searchAirportsPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (searchAirportsPopup.Result)
                {
                    airport = searchAirportsPopup.SelectedAirport;
                    homeAirportTxt.setText(airport.name);
                    RunwaysDataSource runwaysDataSource = new RunwaysDataSource(getBaseContext());
                    runwaysDataSource.open();
                    airport.runways = runwaysDataSource.loadRunwaysByAirport(airport);
                    runwaysDataSource.close();
                    setupRunwaysSpinner();
                }
            }
        });

        searchAirportsPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
    }
}

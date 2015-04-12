package nl.robenanita.googlemapstest.Weather;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nl.robenanita.googlemapstest.Airport;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Xs;
import nl.robenanita.googlemapstest.database.AirportDataSource;

public class WeatherActivity extends ActionBarActivity {
    private String TAG = "GooglemapsTest";
    private ArrayList<Metar> metars;
    private ArrayList<Taf> tafs;
    private Metar selectedMetar;
    private Taf selectedTaf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost) findViewById(R.id.weatherTabhost);
        tabHost.setup();

        TabHost.TabSpec metarTab = tabHost.newTabSpec("Metars");
        TabHost.TabSpec tafTab = tabHost.newTabSpec("Tafs");

        metarTab.setIndicator("Metars");
        metarTab.setContent(R.id.metarTabLayout);
//
        tafTab.setIndicator("Tafs");
        tafTab.setContent(R.id.tafTabLayout);

        tabHost.addTab(metarTab);
        tabHost.addTab(tafTab);

        Intent intent = getIntent();
        Integer airport_id = intent.getIntExtra("airport_id", -1);
        if (airport_id > -1) {
            Airport airport;
            AirportDataSource airportDataSource = new AirportDataSource(this);
            airportDataSource.open(-1);
            airport = airportDataSource.GetAirportByID(airport_id);
            airportDataSource.close();
            LatLng airportLocation = new LatLng(airport.latitude_deg, airport.longitude_deg);

            ProgressBar b = (ProgressBar) findViewById(R.id.metarprogressBar);
            b.setVisibility(View.VISIBLE);
            ProgressBar t = (ProgressBar) findViewById(R.id.tafprogressBar);
            t.setVisibility(View.VISIBLE);

            WeatherWebService weatherWebService = new WeatherWebService(this);
            setWeatherServiceListeners(weatherWebService);
            weatherWebService.GetMetar100MilesRadiusFromLocation(airportLocation);
            weatherWebService.GetTaf100MilesRadiusFromLocation(airportLocation);
        }
    }

    private void setWeatherServiceListeners(WeatherWebService weatherWebService) {
        weatherWebService.setOnDataAvailable(new WeatherWebService.OnDataAvailable() {
            @Override
            public void OnMetarsAvailable(ArrayList<Metar> metars) {
                SetupMetarListView(metars);
            }

            @Override
            public void OnTafsAvailable(ArrayList<Taf> tafs) {
                SetupTafListView(tafs);
            }
        });

    }

    private void test()
    {

    }

    public void SetMetarProgress(Integer progress, String Message)
    {
        Log.i(TAG, "Metar Progress: " + progress.toString() + " : " + Message);
    }

    public void SetTafProgress(Integer progress, String Message)
    {
        Log.i(TAG, "Taf Progress: " + progress.toString() + " : " + Message);
    }

    public void SetupTafListView(ArrayList<Taf> tafs)
    {
        Collections.sort(tafs, new Comparator<Taf>() {
            @Override
            public int compare(Taf taf, Taf taf2) {
                return Math.round(taf.distance_to_org_m - taf2.distance_to_org_m);
            }
        });

        this.tafs = tafs;

        final ListView tafListView = (ListView) findViewById(R.id.tafList);
        TafAdapter adapter = new TafAdapter(tafs);
        tafListView.setAdapter(adapter);

        ProgressBar b = (ProgressBar) findViewById(R.id.tafprogressBar);
        b.setVisibility(View.INVISIBLE);

        tafListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TafAdapter adapter1 = (TafAdapter) adapterView.getAdapter();
                selectedTaf = adapter1.getTaf(i);
                setTafTxtViews();
            }
        });
    }

    public void SetupMetarListView(ArrayList<Metar> metars)
    {
        Collections.sort(metars, new Comparator<Metar>() {
            @Override
            public int compare(Metar metar, Metar metar2) {
                return Math.round(metar.distance_to_org_m - metar2.distance_to_org_m);
            }
        });

        this.metars = metars;

        final ListView metarsListView = (ListView) findViewById(R.id.metarsListView);
        MetarAdapter adapter = new MetarAdapter(metars);
        metarsListView.setAdapter(adapter);

        metarsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //metarsListView.setSelection(i);
                MetarAdapter adapter1 = (MetarAdapter) adapterView.getAdapter();
                selectedMetar = adapter1.getMetar(i);
                setMetarTxtViews();
            }
        });

        ProgressBar b = (ProgressBar) findViewById(R.id.metarprogressBar);
        b.setVisibility(View.INVISIBLE);
    }

    public void setTafTxtViews()
    {
        TextView tafRawTxt = (TextView) findViewById(R.id.tafRawTxt);
        TextView tafStationTxt = (TextView) findViewById(R.id.tafStationTxt);
        TextView tafIssueTimeTxt = (TextView) findViewById(R.id.tafIssueTimeTxt);
        TextView tafBulletinTimeTxt = (TextView) findViewById(R.id.tafBulletinTimeTxt);
        TextView tafValidFromTxt = (TextView) findViewById(R.id.tafValidFromTxt);
        TextView tafValidToTxt = (TextView) findViewById(R.id.tafValidToTxt);
        TextView tafElevationTxt = (TextView) findViewById(R.id.tafElevationTxt);
        TextView tafRemarksTxt = (TextView) findViewById(R.id.tafRemarksTxt);
        TextView tafForecastTxt = (TextView) findViewById(R.id.tafForecastTxt);

        tafRawTxt.setText(selectedTaf.raw_text);
        tafStationTxt.setText((selectedTaf.airport == null) ?
                        ((selectedTaf.station_id == null) ? "?" : selectedTaf.station_id)
                        :
                        selectedTaf.station_id  + " : "  + selectedTaf.airport.name
        );

        tafIssueTimeTxt.setText((selectedTaf.issue_time != null) ? selectedTaf.issue_time : "??");
        tafBulletinTimeTxt.setText((selectedTaf.bulletin_time != null) ? selectedTaf.bulletin_time : "??");
        tafValidFromTxt.setText((selectedTaf.valid_time_from != null) ? selectedTaf.valid_time_from : "??");
        tafValidToTxt.setText((selectedTaf.valid_time_to != null) ? selectedTaf.valid_time_to : "??");
        tafElevationTxt.setText(Float.toString(selectedTaf.elevation_m));
        tafRemarksTxt.setText((selectedTaf.remarks != null) ? selectedTaf.remarks : "??");

        String forecast = "";
        for (Taf.forecast_class f : selectedTaf.forecast)
        {
            forecast +=  "Valid from:\t\t\t" + Xs.StrIsNull(f.fcst_time_from, "?") +
                    " to:" + Xs.StrIsNull(f.fcst_time_to, "?") + "\n";
            forecast += "Change Indication:\t" + Xs.StrIsNull(f.change_indicator, "?") + "\n";
            forecast += "Wind:\t\t\t\t\t" + Xs.IntegerIsNull(f.wind_speed_kt, 0) + "kt " +
                    Xs.IntegerIsNull(f.wind_dir_degrees, 0) + " degree, " +
                        "gusts to: " + Xs.IntegerIsNull(f.wind_gust_kt, 0) + "\n";

            forecast += "Visibility:\t\t\t\t" + Xs.FloatIsNull(f.visibility_statute_mi, 0f) + "SM \n";

            String al = String.format("%.2f", Xs.FloatIsNull(f.altim_in_hg, 0f)) + " inHg " +
                    String.format("%.0f", 33.8638815f * Xs.FloatIsNull(f.altim_in_hg, 0f)) + " mbar";
            forecast += "Altimeter Setting:\t" + al + "\n";

            String sky = "";
            if (f.sky_condition != null)
                for (Taf.forecast_class.sky_condition_class s : f.sky_condition)
                {
                    sky = sky + s.cloud_base_ft_agl + ", " + s.sky_cover + " : ";
                }
            forecast += "Sky Condition:\t\t\t" + sky + "\n";
            forecast += "-----------------------------------------------------------\n";

        }

        tafForecastTxt.setText(forecast);

    }

    public void setMetarTxtViews()
    {
        TextView metarStationIDTxt = (TextView) findViewById(R.id.metarStationIDTxt);
        TextView metarTimeTxt = (TextView) findViewById(R.id.metarTimeTxt);
        TextView metarTempTxt = (TextView) findViewById(R.id.metarTempTxt);
        TextView metarWindDirTxt = (TextView) findViewById(R.id.metarWindDirTxt);
        TextView metarWindSpeedTxt = (TextView) findViewById(R.id.metarWindSpeedTxt);
        TextView metarAltimeterTxt = (TextView) findViewById(R.id.metarAltimeterTxt);
        TextView metarFlightCatTxt = (TextView) findViewById(R.id.metarFlightCatTxt);
        TextView metarVisibilityTxt = (TextView) findViewById(R.id.metarVisibilityTxt);
        TextView metarElevationTxt = (TextView) findViewById(R.id.metarElevationTxt);
        TextView metarSkyConTxt = (TextView) findViewById(R.id.metarSkyConTxt);
        TextView metarRawTxt = (TextView) findViewById(R.id.metarRawTxt);

        metarStationIDTxt.setText((selectedMetar.airport == null) ?
                ((selectedMetar.station_id == null) ? "?" : selectedMetar.station_id)
                        :
                        selectedMetar.station_id  + " : "  + selectedMetar.airport.name
        );

        metarRawTxt.setText((selectedMetar.raw_text != null) ? selectedMetar.raw_text : "??");
        metarTimeTxt.setText((selectedMetar.observation_time == null) ? "?" : selectedMetar.observation_time);
        metarTempTxt.setText(Float.toString(selectedMetar.temp_c));
        metarWindDirTxt.setText((selectedMetar.wind_dir_degrees == null) ? "?" : selectedMetar.wind_dir_degrees.toString());
        metarWindSpeedTxt.setText((selectedMetar.wind_speed_kt == null) ? "?" : selectedMetar.wind_speed_kt.toString());


        String al = String.format("%.2f", Xs.FloatIsNull(selectedMetar.altim_in_hg, 0f)) + " inHg " +
                String.format("%.0f", 33.8638815f * Xs.FloatIsNull(selectedMetar.altim_in_hg, 0f)) + " mbar";

        metarAltimeterTxt.setText(al);

        metarFlightCatTxt.setText((selectedMetar.flight_category == null) ? "" : selectedMetar.flight_category );
        metarVisibilityTxt.setText(Float.toString(selectedMetar.visibility_statute_mi));
        metarElevationTxt.setText(Float.toString(selectedMetar.elevation_m));
        String sky = "";
        if (selectedMetar.sky_condition != null)
            for (Metar.sky_condition_class s : selectedMetar.sky_condition)
            {
                sky = sky + s.cloud_base_ft_agl + ", " + s.sky_cover + " : ";
            }
        metarSkyConTxt.setText(sky);
    }
}

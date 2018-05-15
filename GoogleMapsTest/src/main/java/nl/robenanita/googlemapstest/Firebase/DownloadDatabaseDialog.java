package nl.robenanita.googlemapstest.Firebase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import nl.robenanita.googlemapstest.R;


public class DownloadDatabaseDialog extends DialogFragment {
    private String TAG = "GooglemapsTest";

    public DownloadDatabaseDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_download_database, container, false);
        getDialog().setTitle("Download Navigation Databases");
        Button downloadButton = (Button) rootView.findViewById(R.id.downloadDatabasesBtn);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    FBTableDownloadProgress progress;

    FBAirportsDataSource fbAirportsDataSource;
    FBNavaidsDataSource fbNavaidsDataSource;
    FBFixesDataSource fbFixesDataSource;
    FBTilesDataSource fbTilesDataSource;
    FBCountriesDataSource fbCountriesDataSource;
    FBFirDataSource fbFirDataSource;
    FBRegionsDataSource fbRegionsDataSource;

    private void startDownload()
    {
        Log.i(TAG, "Start download from Firebase");
        progress = new FBTableDownloadProgress() {
            @Override
            public void onProgress(Integer count, Integer downloaded, FBTableType tableType) {
                onProgressBarUpdate(count, downloaded, tableType);
            }
        };

        fbAirportsDataSource = new FBAirportsDataSource(getContext());
        fbAirportsDataSource.Open();
        fbNavaidsDataSource = new FBNavaidsDataSource(getContext());
        fbNavaidsDataSource.Open();
        fbFixesDataSource = new FBFixesDataSource(getContext());
        fbFixesDataSource.Open();
        fbTilesDataSource = new FBTilesDataSource(getContext());
        fbTilesDataSource.Open();
        fbCountriesDataSource = new FBCountriesDataSource(getContext());
        fbCountriesDataSource.Open();
        fbFirDataSource = new FBFirDataSource(getContext());
        fbFirDataSource.Open();
        fbRegionsDataSource = new FBRegionsDataSource(getContext());
        fbRegionsDataSource.Open();

        // First get Statistics
        FBStatistics statistics = new FBStatistics();
        statistics.OnStatisticsEvent = new FBStatistics.StatisticsEventListerner() {
            @Override
            public void OnStatistics(FBStatistics statistics) {
                Log.i(TAG, "Recieved statistics from Firebase");

                Boolean clearTable = true;
                fbAirportsDataSource.progress = progress;
                fbAirportsDataSource.ReadFBAirportData(statistics.AirportsCount, clearTable);
                fbNavaidsDataSource.progress = progress;
                fbNavaidsDataSource.ReadFBNavaidData(statistics.NavaidsCount, clearTable);
                fbCountriesDataSource.progress = progress;
                fbCountriesDataSource.ReadFBCountryData(statistics.CountriesCount, clearTable);
                fbTilesDataSource.progress = progress;
                fbTilesDataSource.ReadFBTilesData(statistics.MBTilesCount, clearTable);
                fbFixesDataSource.progress = progress;
                fbFixesDataSource.ReadFBFixesData(statistics.FixesCount, clearTable);
                fbRegionsDataSource.progress = progress;
                fbRegionsDataSource.ReadFBRegionsData(statistics.RegionsCount, clearTable);
                fbFirDataSource.progress = progress;
                fbFirDataSource.ReadFBFirsData(statistics.FirsCount, clearTable);
            }
        };
        statistics.FillStatistics();
    }

    private void onProgressBarUpdate(Integer count, Integer downloaded, FBTableType type)
    {
        ProgressBar progressBar = null;
        if (type==FBTableType.airports) progressBar = (ProgressBar)getView().findViewById(R.id.airportsProgress);
        if (type==FBTableType.navaids) progressBar = (ProgressBar)getView().findViewById(R.id.navaidsProgress);
        if (type==FBTableType.countries) progressBar = (ProgressBar)getView().findViewById(R.id.countriesProgress);
        if (type==FBTableType.regions) progressBar = (ProgressBar)getView().findViewById(R.id.regionsProgress);
        if (type==FBTableType.mbtiles) progressBar = (ProgressBar)getView().findViewById(R.id.chartsProgress);
        if (type==FBTableType.firs) progressBar = (ProgressBar)getView().findViewById(R.id.firsProgress);
        if (type==FBTableType.fixes) progressBar = (ProgressBar)getView().findViewById(R.id.fixesProgress);
        if (type==FBTableType.airspaces) progressBar = (ProgressBar)getView().findViewById(R.id.airspacesProgress);
        if (progressBar != null) {
            progressBar.setMax(count);
            progressBar.setProgress(downloaded);
        }
    }

}

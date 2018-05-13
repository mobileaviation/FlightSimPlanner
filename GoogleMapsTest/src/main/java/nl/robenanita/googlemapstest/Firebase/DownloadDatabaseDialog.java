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

        // First get Statistics
        FBStatistics statistics = new FBStatistics();
        statistics.OnStatisticsEvent = new FBStatistics.StatisticsEventListerner() {
            @Override
            public void OnStatistics(FBStatistics statistics) {
                Log.i(TAG, "Recieved statistics from Firebase");

                Boolean clearTable = true;
                fbAirportsDataSource.progress = progress;
                fbAirportsDataSource.ReadFBAirportData(statistics.AirportsCount, clearTable);
            }
        };
        statistics.FillStatistics();
    }

    private void onProgressBarUpdate(Integer count, Integer downloaded, FBTableType type)
    {
        ProgressBar progressBar = (ProgressBar)getView().findViewById(R.id.airportsProgress);
        progressBar.setMax(count);
        progressBar.setProgress(downloaded);
    }

}

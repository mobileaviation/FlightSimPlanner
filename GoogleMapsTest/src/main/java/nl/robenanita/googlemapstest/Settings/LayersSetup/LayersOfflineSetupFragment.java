package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Wms.Offline.Downloader;

/**
 * A simple {@link Fragment} subclass.
 */
public class LayersOfflineSetupFragment extends Fragment {

    private String TAG = "LayersOfflineSetupFragment";

    private Route flightPlan;

    public LayersOfflineSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline_setup, container, false);
    }

    public void DownloadRouteTiles()
    {
        Context context = this.getActivity();
        if (flightPlan.buffer != null)
        {
            String p = context.getFilesDir().getPath() + "/";
            Downloader d = new Downloader(context, flightPlan.buffer, p, "", "Openstreet");
            d.SetOnDownloadProgress(new Downloader.OnDownloadProgress() {
                @Override
                public void OnProgress(Integer progress) {
                    Log.i(TAG, "Download Progress: " + progress);
                }
            });
            d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    ///data/data/com.mobileaviationtools.flightsimplannerpro/files//Openstreet/Openstreet-13-4243-2697.png

}

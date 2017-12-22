package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Wms.Offline.Downloader;
import nl.robenanita.googlemapstest.Wms.TileProviderType;

/**
 * A simple {@link Fragment} subclass.
 */
public class LayersOfflineSetupFragment extends Fragment {

    private String TAG = "LayersOfflineSetupFragment";

    private Route route;
    private Button downloadBtn;

    public LayersOfflineSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_layers_offline_setup, container, false);

        downloadBtn = (Button) view.findViewById(R.id.downloadBtn);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                NavigationActivity a = (NavigationActivity)getActivity();
                Log.i(TAG, "test message");
            }
        });

        return view;
    }

    public void DownloadRouteTiles()
    {
        Context context = this.getActivity();
        if (route.buffer != null)
        {
            String p = context.getFilesDir().getPath() + "/";
            String map = TileProviderType.offline_openstreet.toString();
            Downloader d = new Downloader(context, route.buffer, p, "", map);
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

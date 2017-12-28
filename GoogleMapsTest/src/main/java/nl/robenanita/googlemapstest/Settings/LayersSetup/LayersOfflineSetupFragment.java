package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vividsolutions.jts.geom.Geometry;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.Route.Route;
import nl.robenanita.googlemapstest.Wms.Offline.CleanOfflineTiles;
import nl.robenanita.googlemapstest.Wms.Offline.Downloader;
import nl.robenanita.googlemapstest.Wms.OfflineMapTypes;
import nl.robenanita.googlemapstest.Wms.TileProviderType;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;

/**
 * A simple {@link Fragment} subclass.
 */
public class LayersOfflineSetupFragment extends Fragment {

    private String TAG = "LayersOfflineSetupFragment";

    private Button downloadBtn;
    private CheckBox offLineVisibleCheckbox;
    private Button cleanUpBtn;
    private OfflineMapTypes offlineMapTypes;
    private Boolean offlineMapVisible;

    public LayersOfflineSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_layers_offline_setup, container, false);

        setupOfflineFragment();

        downloadBtn = (Button) view.findViewById(R.id.offlineDownloadBtn);
        cleanUpBtn = (Button) view.findViewById(R.id.cleanOfflineTilesBtn);
        offLineVisibleCheckbox = (CheckBox) view.findViewById(R.id.offlineEnabledBtn);
        RadioGroup offlineTypeGroup = (RadioGroup)view.findViewById(R.id.offlineChartSelection);

        offlineTypeGroup.check(offlineMapTypes.toButtonId());
        offlineTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton b = (RadioButton)view.findViewById(i);
                offlineMapTypes = (OfflineMapTypes)b.getTag();
                Log.i(TAG, "Radiobutton selected: " + offlineMapTypes.toString());
                NavigationActivity a = (NavigationActivity)getActivity();
                a.mapController.ShowOfflineMap(offlineMapVisible, offlineMapTypes);
                updateProperty();
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Download Tiles!")
                        .setMessage("Are you sure you want to download these map tiles." + System.lineSeparator() +
                            "It can take up a large amount of internal storage?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                DownloadRouteTiles();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        offLineVisibleCheckbox.setChecked(offlineMapVisible);
        offLineVisibleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                NavigationActivity a = (NavigationActivity)getActivity();
                offlineMapVisible = b;
                a.mapController.ShowOfflineMap(b, offlineMapTypes);
                updateProperty();
            }
        });

        cleanUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Cleanup offline tiles!")
                        .setMessage("Are you sure you want remove all offline tiles" + System.lineSeparator() +
                                "of type: " + offlineMapTypes.toString())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                String path = getActivity().getFilesDir().getPath();
                                CleanOfflineTiles cleanOfflineTiles = new CleanOfflineTiles(path, offlineMapTypes);
                                cleanOfflineTiles.CleanTiles();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        return view;
    }

    private void setupOfflineFragment()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(getContext());
        propertiesDataSource.open(true);
        Property offlineProperty = propertiesDataSource.getMapSetup("OFFLINEMAPS");
        if (offlineProperty==null)
        {
            offlineProperty = Property.NewProperty("OFFLINEMAPS", "offline_openstreet", "false");
            propertiesDataSource.InsertProperty(offlineProperty);
        }
        propertiesDataSource.close(true);

        offlineMapTypes = OfflineMapTypes.valueOf(offlineProperty.value1);
        offlineMapVisible = Boolean.valueOf(offlineProperty.value2);


    }

    private void updateProperty()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(getContext());
        propertiesDataSource.open(true);
        Property offlineProperty = propertiesDataSource.GetProperty("OFFLINEMAPS");
        offlineProperty.value1 = offlineMapTypes.toString();
        offlineProperty.value2 = offlineMapVisible.toString();
        propertiesDataSource.updateProperty(offlineProperty);
        propertiesDataSource.close(true);

    }

    public void DownloadRouteTiles()
    {
        Context context = this.getActivity();
        Geometry b = ((NavigationActivity)context).bufferArea.GetBuffer();
        LinearLayout progressLayout = (LinearLayout)this.getActivity().findViewById(R.id.progressLayout);
        progressLayout.setVisibility(View.VISIBLE);

        if (b != null)
        {
            String p = context.getFilesDir().getPath();
            String map = offlineMapTypes.toString();
            String url = offlineMapTypes.toUrl();
            Downloader d = new Downloader(context, b, p, url, map);

            d.SetOnDownloadProgress(new Downloader.OnDownloadProgress() {
                @Override
                public void OnProgress(Downloader.DownloadProgress progresstype, Integer progress, String message) {
                    Log.i(TAG, message + " : " + progress);
                    NavigationActivity navigationActivity = (NavigationActivity)getActivity();
                    LinearLayout progressLayout = (LinearLayout)navigationActivity.findViewById(R.id.progressLayout);
                    ProgressBar progressBar = (ProgressBar)navigationActivity.findViewById(R.id.navigationProgressBar);
                    TextView progressText = (TextView)navigationActivity.findViewById(R.id.navigationProgressText);

                    switch (progresstype)
                    {
                        case preprocessing:
                        {
                            progressBar.setProgress(progress);
                            progressText.setText(message);
                            break;
                        }
                        case downloading:
                        {
                            progressBar.setProgress(progress);
                            progressText.setText(message);
                            break;
                        }
                        case finished:
                        {
                            progressLayout.setVisibility(View.INVISIBLE);
                            break;
                        }
                    }
                }
            });

            d.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    ///data/data/com.mobileaviationtools.flightsimplannerpro/files//Openstreet/Openstreet-13-4243-2697.png

}

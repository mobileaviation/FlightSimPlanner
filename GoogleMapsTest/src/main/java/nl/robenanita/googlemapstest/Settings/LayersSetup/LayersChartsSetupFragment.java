package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.Charts.AirportCharts;
import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.MBTiles.MBTileType;
import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.AirportChartsDataSource;
import nl.robenanita.googlemapstest.database.MBTilesDataSource;
import nl.robenanita.googlemapstest.database.MBTilesLocalDataSource;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;

public class LayersChartsSetupFragment extends Fragment {

    public LayersChartsSetupFragment() {
        // Required empty public constructor
    }

    private View view;
    private NavigationActivity n;
    private String TAG = "GooglemapsTest";
    private String downloadedToDir;
    private DownloadManager dm;
    private Button refreshServerChartsBtn;
    private Button refreshLocalChartsBtn;
    private ArrayList<MBTile> maps;
    private ListView chartsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_layers_charts_setup, container, false);
        n = (NavigationActivity)container.getContext();
        downloadedToDir = n.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        Log.i(TAG, "Download Folder: " + downloadedToDir);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chartsListView = (ListView) view.findViewById(R.id.airportChartsSetupList);
        refreshServerChartsBtn = (Button)view.findViewById(R.id.refreshServerChartsBtn);
        refreshLocalChartsBtn = (Button)view.findViewById(R.id.refreshLocalChartsBtn);

        refreshServerChartsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Get chartslist from server ?")
                        .setMessage("Do you want to retrieve the chartslist from the server?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refreshRemoteCharts();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        refreshLocalChartsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("Get MBTILES from Downloads?")
                        .setMessage("Retrieve all local downloaded MBTILES files, " + System.lineSeparator() +
                        "Push the download button next to the map to make it available for use.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refreshLocalCharts();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        maps = new ArrayList<>();

        getChartData();

        setupListView();

//        MBTile test = new MBTile(n);
//        test._id = 10;
//        test.name = "VACLFOR.mbtiles";
//        test.type = MBTileType.fsp;
//        test.mbtileslink = "http://192.168.2.8:81/VACEHLE.mbtiles";
//        test.version = 1804;
//        test.endValidity = new Date();
//
//        maps.add(test);
//        chartsListView.invalidate();

    }

    private void refreshLocalCharts() {
        LocalMBCharts localMBCharts = new LocalMBCharts(n);

        setLocalMBCharts(localMBCharts, MBTileType.local);
        localMBCharts.GetLocalFileList();
    }

    private void setupListView()
    {
        ChartsSetupAdapter chartsSetupAdapter = new ChartsSetupAdapter(maps, n);
        chartsSetupAdapter.SetOnEvent(new ChartEvent() {
            @Override
            public void OnStartDownload(MBTile tile) {

            }

            @Override
            public void OnChecked(Boolean checked, MBTile tile) {
                n.mapController.SetupMBTileMap(tile, checked);
            }
        });

        chartsListView.setAdapter(chartsSetupAdapter);
    }


    private void getChartData()
    {
        MBTilesDataSource tilesDataSource = new MBTilesDataSource(n);
        tilesDataSource.open();
        tilesDataSource.GetMBTilesByType(MBTileType.ofm, maps);
        tilesDataSource.close();
        MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(n);
        mbTilesLocalDataSource.open();
        mbTilesLocalDataSource.getAllLocalTiles(maps, MBTileType.fsp);
        mbTilesLocalDataSource.getAllLocalTiles(maps, MBTileType.local);
        mbTilesLocalDataSource.close();
    }


    private void refreshRemoteCharts()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(n);
        propertiesDataSource.open(true);
        propertiesDataSource.FillProperties();
        String ip = propertiesDataSource.IpAddress.value1;
        Integer port = Integer.parseInt(propertiesDataSource.IpAddress.value2);
        LocalMBCharts localMBCharts = new LocalMBCharts(ip,port, n);

        setLocalMBCharts(localMBCharts, MBTileType.fsp);

        localMBCharts.GetRemoteFilelist();
    }

    private void setLocalMBCharts(LocalMBCharts localMBCharts, final MBTileType chartType)
    {
        localMBCharts.SetOnMBFilesList(new LocalMBCharts.OnMBFileList() {
            @Override
            public void filesList(List<MBTile> files, Boolean success, String message) {
                if (success) {
                    MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(getContext());
                    mbTilesLocalDataSource.open();
                    for (MBTile tile : files) {

                        MBTile checkTile = mbTilesLocalDataSource.getTileByName(tile);
                        if (checkTile != null) tile.visible_order = checkTile.visible_order;
                        tile.InsertUpdateDB(mbTilesLocalDataSource);

                    }

                    mbTilesLocalDataSource.removeUnknown(files, true, chartType);
                    mbTilesLocalDataSource.close();

                    maps = new ArrayList<>();
                    getChartData();
                    setupListView();

                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
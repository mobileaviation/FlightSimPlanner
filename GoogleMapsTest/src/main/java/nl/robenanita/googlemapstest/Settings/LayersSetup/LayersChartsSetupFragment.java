package nl.robenanita.googlemapstest.Settings.LayersSetup;


import android.app.DownloadManager;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;

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
//
//        ListView chartsListView = (ListView) view.findViewById(R.id.airportChartsSetupList);
//        AirportChartsDataSource airportChartsDataSource = new AirportChartsDataSource(getContext());
//        airportChartsDataSource.open();
//        AirportCharts charts = airportChartsDataSource.GetAllCharts();
//        airportChartsDataSource.close();
//
//        ChartsSetupAdapter chartsSetupAdapter = new ChartsSetupAdapter(charts);
//        chartsListView.setAdapter(chartsSetupAdapter);
//
//        chartsListView.setClickable(true);
//        chartsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ChartsSetupAdapter adapter = (ChartsSetupAdapter) adapterView.getAdapter();
//                AirportChart chart = adapter.GetAirportChart(i);
//
//                downloadTask = new DownloadImageTask();
//                downloadTask.execute(chart.thumbnail_url);
//
//                n.mapController.SetAirportChart(chart);
//            }
//        });

        ListView chartsListView = (ListView) view.findViewById(R.id.airportChartsSetupList);

        MBTilesDataSource tilesDataSource = new MBTilesDataSource(n);
        tilesDataSource.open();
        ArrayList<MBTile> maps = new ArrayList<>();
        tilesDataSource.GetMBTilesByType(MBTileType.ofm, maps);
        tilesDataSource.close();

        ChartsSetupAdapter chartsSetupAdapter = new ChartsSetupAdapter(maps, n);
        chartsSetupAdapter.SetOnEvent(new ChartEvent() {
            @Override
            public void OnStartDownload(MBTile tile) {
                tile.StartDownload();
            }

            @Override
            public void OnChecked(Boolean checked, MBTile tile) {
                n.mapController.SetupMBTileMap(tile, checked);
            }
        });
        chartsListView.setAdapter(chartsSetupAdapter);

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
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(n);
        propertiesDataSource.open(true);
        propertiesDataSource.FillProperties();
        String ip = propertiesDataSource.IpAddress.value1;
        Integer port = Integer.parseInt(propertiesDataSource.IpAddress.value2);
        LocalMBCharts localMBCharts = new LocalMBCharts(ip,port);

        localMBCharts.SetOnMBFilesList(new LocalMBCharts.OnMBFileList() {
            @Override
            public void filesList(List<MBTile> files, Boolean success) {

            }
        });

        localMBCharts.GetFilelist();

    }

//    private DownloadImageTask downloadTask;
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            super.onPostExecute(bitmap);
//
//            ImageView imageView = (ImageView)view.findViewById(R.id.chartImageView);
//            imageView.setImageBitmap(bitmap);
//        }
//    }
}
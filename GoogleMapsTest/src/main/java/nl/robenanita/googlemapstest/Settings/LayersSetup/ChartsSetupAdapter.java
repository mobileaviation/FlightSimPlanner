package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.app.DownloadManager;
import android.database.Cursor;
import android.graphics.Color;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.Charts.AirportCharts;
import nl.robenanita.googlemapstest.Continent;
import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.MBTiles.MBTileType;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 10-8-2017.
 */

public class ChartsSetupAdapter extends BaseAdapter {
    private ArrayList<MBTile> charts;
    private String TAG = "GooglemapsTest";
    private StartDownloadEvent onStartDownload;
    public void SetOnStartDownload(StartDownloadEvent startDownloadEvent)
    {
        onStartDownload = startDownloadEvent;
    }

    public ChartsSetupAdapter(ArrayList<MBTile> charts) {
        this.charts = charts;
    }

    @Override
    public int getCount() {
        return charts.size();
    }

    @Override
    public Object getItem(int i) {
        return getItem(i);
    }

    public MBTile GetChart(int i) {
        return this.charts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.adapter_chartssetup, viewGroup, false);

        final MBTile chart = GetChart(i);

        ImageButton downloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        downloadChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i( TAG,"Start download the: " + chart.mbtileslink + " file");
                if (onStartDownload != null) onStartDownload.OnStartDownload(chart);
            }
        });

        TextView textView = (TextView) view.findViewById(R.id.chartSetupTxt);

        textView.setText(chart.name);

        ImageView imageView = (ImageView) view.findViewById(R.id.chartSetupImage);
        if (chart.type == MBTileType.ofm) imageView.setImageResource(R.drawable.ofm_charts_header);
        if (chart.type == MBTileType.fsp) imageView.setImageResource(R.drawable.fsp_charts_header);

        Boolean downloaded = chart.CheckDownloadedTile();
        Log.i(TAG, "Downloaded: " + chart.name + ((downloaded) ? " found" : " not found"));

        LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.chartSetupLayout);

        if ( (i & 1) == 0 ) {
            chartLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

}

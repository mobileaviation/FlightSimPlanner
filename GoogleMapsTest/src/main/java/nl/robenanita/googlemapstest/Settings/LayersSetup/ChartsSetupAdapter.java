package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private ChartEvent onEvent;
    private Context context;

    public void SetOnEvent(ChartEvent event)
    {
        onEvent = event;
    }

    public ChartsSetupAdapter(ArrayList<MBTile> charts, Context context) {
        this.charts = charts;
        this.context = context;
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
        chart.CheckVisibleStatus();

        ImageButton downloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        downloadChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i( TAG,"Start download the: " + chart.mbtileslink + " file");
                if (onEvent != null) onEvent.OnStartDownload(chart);
            }
        });

        //TODO
//        ImageButton deleteChartBtn;
//        deleteChartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // delete the file
//                //check the file
//                checkFiles checkFiles = new checkFiles(activateChartCheckBox, context);
//                checkFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chart);
//            }
//        });

        TextView textView = (TextView) view.findViewById(R.id.chartSetupTxt);

        textView.setText(chart.name);

        ImageView imageView = (ImageView) view.findViewById(R.id.chartSetupImage);
        if (chart.type == MBTileType.ofm) imageView.setImageResource(R.drawable.ofm_charts_header);
        if (chart.type == MBTileType.fsp) imageView.setImageResource(R.drawable.fsp_charts_header);


        CheckBox activateChartCheckBox = (CheckBox) view.findViewById(R.id.activateChartCheckBox);
        activateChartCheckBox.setTag(chart);
        if (chart.visible_order>-1) activateChartCheckBox.setChecked(true);
        activateChartCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MBTile chechedChart = (MBTile) compoundButton.getTag();
                if (onEvent != null) onEvent.OnChecked(b, chechedChart);
            }
        });

        checkFiles checkFiles = new checkFiles(activateChartCheckBox, context);
        checkFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chart);

        LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.chartSetupLayout);

        if ( (i & 1) == 0 ) {
            chartLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    public class checkFiles extends AsyncTask<MBTile, String, Boolean>
    {
        public checkFiles(CheckBox activateChartCheckBox, Context context)
        {
            this.activateChartCheckBox = activateChartCheckBox;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activateChartCheckBox.setEnabled(false);
            activateChartCheckBox.setBackgroundColor(ContextCompat.getColor(context, R.color.light_orange));
        }

        @Override
        protected Boolean doInBackground(MBTile... mbTiles) {
            tile = mbTiles[0];
            return tile.CheckFile();
        }

        private CheckBox activateChartCheckBox;
        private MBTile tile;
        private Context context;

        @Override
        protected void onPostExecute(Boolean localFilePresent) {
            super.onPostExecute(localFilePresent);

            activateChartCheckBox.setEnabled(localFilePresent);
            activateChartCheckBox.setBackgroundColor(ContextCompat.getColor(context,
                    ((localFilePresent) ? R.color.light_green : R.color.light_red)));

            Log.i(TAG, "MBTiles file for: " + tile.name + " is " + ((localFilePresent) ? "present" : "not present"));
        }
    }

}

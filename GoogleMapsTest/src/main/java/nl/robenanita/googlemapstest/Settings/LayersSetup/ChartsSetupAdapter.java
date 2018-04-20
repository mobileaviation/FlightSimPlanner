package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

        final CheckBox activateChartCheckBox = (CheckBox) view.findViewById(R.id.activateChartCheckBox);
        final ImageButton deleteDownloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        deleteDownloadChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chart.LocalFileExists()) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Download " + chart.name + " ?")
                            .setMessage("Are you sure you want to download: " + chart.name + " ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Log.i(TAG, "Start download the: " + chart.mbtileslink + " file");
                                    if (onEvent != null) onEvent.OnStartDownload(chart);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();

                }
                else
                {
                    // delete file only if not visible
                    if (!(chart.visible_order>-1))
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Delete " + chart.name + " ?")
                                .setMessage("Are you sure you want to delete: " + chart.name + " ?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (chart.DeleteLocalFile())
                                        {
                                            Log.i(TAG, "File deleted: " + chart.getLocalFilename());
                                            checkFiles checkFiles = new checkFiles(activateChartCheckBox, deleteDownloadChartButton, context);
                                            checkFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chart);
                                        }
                                        else
                                        {
                                            Log.e(TAG, "Trouble deleting file: " + chart.getLocalFilename());
                                        }
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                    else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("Disable chart before delete")
                                .setMessage("You first must disable the chart before i can be deleted!")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton("Yes", null)
                                .show();
                    }
                }

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



        activateChartCheckBox.setTag(chart);
        activateChartCheckBox.setChecked(((chart.visible_order>-1)));
        activateChartCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MBTile chechedChart = (MBTile) compoundButton.getTag();
                if (onEvent != null) onEvent.OnChecked(b, chechedChart);
            }
        });

        if (!chart.CheckfileRunning) {
            checkFiles checkFiles = new checkFiles(activateChartCheckBox, deleteDownloadChartButton, context);
            checkFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, chart);
        }

        LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.chartSetupLayout);

        if ( (i & 1) == 0 ) {
            chartLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    public class checkFiles extends AsyncTask<MBTile, String, Boolean>
    {
        public checkFiles(CheckBox activateChartCheckBox, ImageButton deleteDownloadBtn, Context context)
        {
            this.activateChartCheckBox = activateChartCheckBox;
            this.deleteDownloadBtn = deleteDownloadBtn;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activateChartCheckBox.setEnabled(false);
            activateChartCheckBox.setBackgroundColor(ContextCompat.getColor(context, R.color.light_orange));
            deleteDownloadBtn.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(MBTile... mbTiles) {
            tile = mbTiles[0];
            return tile.CheckFile();
        }

        private CheckBox activateChartCheckBox;
        private ImageButton deleteDownloadBtn;
        private MBTile tile;
        private Context context;

        @Override
        protected void onPostExecute(Boolean localFilePresent) {
            super.onPostExecute(localFilePresent);

            activateChartCheckBox.setEnabled(localFilePresent);
            activateChartCheckBox.setBackgroundColor(ContextCompat.getColor(context,
                    ((localFilePresent) ? R.color.light_green : R.color.light_red)));

            deleteDownloadBtn.setEnabled(true);
            deleteDownloadBtn.setBackground((Drawable)context.getResources().
                    getDrawable(localFilePresent ? R.drawable.delete_download_btn : R.drawable.download_btn));

            Log.i(TAG, "MBTiles file for: " + tile.name + " is " + ((localFilePresent) ? "present" : "not present"));

        }
    }

}

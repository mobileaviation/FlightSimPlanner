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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.Charts.AirportCharts;
import nl.robenanita.googlemapstest.Classes.FileDownloader;
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
    //private View adapterView;

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
        //adapterView = view;

        final MBTile chart = GetChart(i);
        chart.tileView = view;
        chart.CheckVisibleStatus();

        final CheckBox activateChartCheckBox = (CheckBox) view.findViewById(R.id.activateChartCheckBox);
        final ImageButton deleteDownloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        final ProgressBar tileDownloadProgress = (ProgressBar) view.findViewById(R.id.tileDownloadProgress);
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
                                    StartDownload(chart, deleteDownloadChartButton, tileDownloadProgress);
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
                                            checkFile(chart);
                                            setupControls(-1, chart);
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

        setupControls(i, chart);
//        TextView textView = (TextView) view.findViewById(R.id.chartSetupTxt);
//
//        textView.setText(chart.name);
//
//        ImageView imageView = (ImageView) view.findViewById(R.id.chartSetupImage);
//        if (chart.type == MBTileType.ofm) imageView.setImageResource(R.drawable.ofm_charts_header);
//        if (chart.type == MBTileType.fsp) imageView.setImageResource(R.drawable.fsp_charts_header);
//        if (chart.type == MBTileType.local) imageView.setImageResource(R.drawable.local_charts_header);
//
//
//
//        activateChartCheckBox.setTag(chart);
//        activateChartCheckBox.setChecked(((chart.visible_order>-1)));
//        activateChartCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                MBTile chechedChart = (MBTile) compoundButton.getTag();
//                if (onEvent != null) onEvent.OnChecked(b, chechedChart);
//            }
//        });
//
//        if (!chart.CheckfileRunning) {
//            checkFile(chart, adapterView);
//        }
//
        LinearLayout chartLayout = (LinearLayout) view.findViewById(R.id.chartSetupLayout);

        if ( (i & 1) == 0 ) {
            chartLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }

        return view;
    }

    private void setupControls(int i, MBTile chart)
    {
        TextView textView = (TextView) chart.tileView.findViewById(R.id.chartSetupTxt);
        CheckBox activateChartCheckBox = (CheckBox) chart.tileView.findViewById(R.id.activateChartCheckBox);
        //ImageButton deleteDownloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        //ProgressBar tileDownloadProgress = (ProgressBar) view.findViewById(R.id.tileDownloadProgress);

        textView.setText(chart.name);

        ImageView imageView = (ImageView) chart.tileView.findViewById(R.id.chartSetupImage);
        if (chart.type == MBTileType.ofm) imageView.setImageResource(R.drawable.ofm_charts_header);
        if (chart.type == MBTileType.fsp) imageView.setImageResource(R.drawable.fsp_charts_header);
        if (chart.type == MBTileType.local) imageView.setImageResource(R.drawable.local_charts_header);



        activateChartCheckBox.setTag(chart);
        activateChartCheckBox.setChecked(((chart.visible_order>-1)));
        activateChartCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                MBTile chechedChart = (MBTile) compoundButton.getTag();
                if (onEvent != null) onEvent.OnChecked(b, chechedChart);
            }
        });

//        if (!chart.CheckfileRunning) {
        checkFile(chart);
//        }
    }

    public void checkFile(MBTile chart)
    {
        final CheckBox activateChartCheckBox = (CheckBox) chart.tileView.findViewById(R.id.activateChartCheckBox);
        final ImageButton deleteDownloadChartButton = (ImageButton) chart.tileView.findViewById(R.id.downloadChartButton);

        boolean localFilePresent = chart.LocalFileExists();
        activateChartCheckBox.setEnabled(localFilePresent);
        activateChartCheckBox.setBackgroundColor(ContextCompat.getColor(context,
                ((localFilePresent) ? R.color.light_green : R.color.light_red)));

        deleteDownloadChartButton.setEnabled(true);
        deleteDownloadChartButton.setBackground((Drawable)context.getResources().
                getDrawable(localFilePresent ? R.drawable.delete_download_btn : R.drawable.download_btn));

        Log.i(TAG, "MBTiles file for: " + chart.name + " is " + ((localFilePresent) ? "present" : "not present"));
    }

    public void StartDownload(final MBTile chart, final ImageButton downloadBtn, final ProgressBar progressBar) {
        File D = new File(chart.getLocalChartsDirectory());
        if (!D.exists()) D.mkdir();

        FileDownloader downloader = new FileDownloader(context);
        downloader.SetOnDownloadInfo(new FileDownloader.DownloadInfo() {
            @Override
            public void OnProgress(String url, File result_file, Integer progress) {
                progressBar.setProgress(progress);
                Log.i(TAG, "Progress: " + progress + " downloading from: " + url);
            }

            @Override
            public void OnError(String url, File result_file, String message) {
                downloadBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Log.i(TAG, "Error: " + message + " downloading from: " + url);
            }

            @Override
            public void OnFinished(String url, File result_file) {
                downloadBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                chart.local_file = result_file.getAbsolutePath();
                chart.UpdateLocalFile();
                checkFile(chart);
                setupControls(-1, chart);
                Log.i(TAG, "Finished: downloading from: " + url + " To local: " + result_file.getAbsolutePath());
            }
        });

        downloadBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        downloader.SetLocaDir(chart.getLocalChartsDirectory());
        downloader.SetUrl(chart.mbtileslink);
        downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}

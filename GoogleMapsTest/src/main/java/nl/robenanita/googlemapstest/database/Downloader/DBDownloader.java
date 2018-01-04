package nl.robenanita.googlemapstest.database.Downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.DBHelper;

/**
 * Created by Rob Verhoef on 4-1-2018.
 */

public class DBDownloader extends AsyncTask {
    public DBDownloader(Context context)
    {
        this.context = context;
        dbBaseUrl = context.getString(R.string.url_databases);
    }

    private Context context;
    private String dbBaseUrl;
    private String TAG = "DBDownloader";
    private DownloadManager dm;

    public String version;
    public String navDb;
    public String airspacesDb;

    public Long navDbDownloadId;
    public Long airspaceDbDownloadId;

    private void processNavJson()
    {
        InputStream stream = null;
        try {
            stream = new URL(dbBaseUrl + "nav.json").openStream();
            String json = IOUtils.toString(stream, "UTF-8");
            JSONObject object = new JSONObject(json);

            this.version = object.getString("version");
            this.navDb = dbBaseUrl + object.getString("nav");
            this.airspacesDb = dbBaseUrl + object.getString("airspaces");

            Log.i(TAG, "JSON: " + json);
        }
        catch (Exception ee)
        {
            Log.i(TAG, "Downloader Eception: " + ee.getMessage());
        }
        finally {
            if (stream != null) IOUtils.closeQuietly(stream);
        }
    }

    public void Download()
    {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        processNavJson();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        String downloadTo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request nvRequest = new DownloadManager.Request(Uri.parse(navDb));
        DownloadManager.Request airsRequest = new DownloadManager.Request(Uri.parse(airspacesDb));

        navDbDownloadId = dm.enqueue(nvRequest);
        airspaceDbDownloadId = dm.enqueue(airsRequest);

        super.onPostExecute(o);
    }

    public ArrayList<String> getFiles()
    {
        ArrayList<String> files = new ArrayList<>();
        DownloadManager.Query q = new DownloadManager.Query();
        Cursor c = dm.query(q);

        if(c.moveToFirst()) {
            do {
                String downloadFilePath = null;
                String downloadFileLocalUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                if (downloadFileLocalUri != null) {
                    File mFile = new File(Uri.parse(downloadFileLocalUri).getPath());
                    downloadFilePath = mFile.getAbsolutePath();
                }

                files.add(downloadFilePath);
            } while (c.moveToNext());
        } else {
            Log.i("DOWNLOAD LISTENER", "empty cursor :(");
        }

        c.close();
        return files;
    }
}

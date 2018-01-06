package nl.robenanita.googlemapstest.database.Downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.cardemulation.CardEmulation;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Property;
import nl.robenanita.googlemapstest.R;
import nl.robenanita.googlemapstest.database.DBFilesHelper;
import nl.robenanita.googlemapstest.database.DBHelper;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;

/**
 * Created by Rob Verhoef on 4-1-2018.
 */

public class DBDownloader extends AsyncTask {
    public DBDownloader(Context context)
    {
        this.context = context;
        dbBaseUrl = context.getString(R.string.url_databases);
        downloadedToDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        Log.i(TAG, "Download Folder: " + downloadedToDir);
    }

    private Context context;
    private String dbBaseUrl;
    private String TAG = "DBDownloader";
    private DownloadManager dm;
    private String downloadedToDir;

    public String version;
    public String navDb;
    public String airspacesDb;

    public File navDBbFile;
    public File airspacesDbFile;

    public Long navDbDownloadId;
    public Long airspaceDbDownloadId;

    public String Message;

    private void processNavJson()
    {
        InputStream stream = null;
        try {
            stream = new URL(dbBaseUrl + "nav.json").openStream();
            String json = IOUtils.toString(stream, "UTF-8");
            JSONObject object = new JSONObject(json);

            this.version = object.getString("version");
            this.navDb = object.getString("nav");
            this.airspacesDb = object.getString("airspaces");

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

        Property ver = checkVersion();

        if ((ver.value1.equals(version))&&(ver.value2.equals("true")))
        {
            Message = Message = "You have the lasest version Databases installed";
            setMessage(Message);
            super.onPostExecute(o);
            return;
        }

        if ((ver.value1.equals(version))&&(ver.value2.equals("false"))&&(checkLocalFiles()))
        {
            Message = Message = "You have download, but not installed the lasest version databases yet, please restart FlightSim-Planner to install";
            setMessage(Message);
            super.onPostExecute(o);
            return;
        }

        if ((!ver.value1.equals(version))) {
            doDownload(ver);
            super.onPostExecute(o);
            return;
        }

        if ((ver.value1.equals(version))&&(ver.value2.equals("false"))&&(!checkLocalFiles()))
        {
            doDownload(ver);
            super.onPostExecute(o);
            return;
        }

        super.onPostExecute(o);
    }

    private void doDownload(Property ver)
    {
        updateVersion(ver);
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request nvRequest = new DownloadManager.Request(Uri.parse(dbBaseUrl + navDb));
        nvRequest.setTitle("Download Navigation Database V" + version);
        nvRequest.setDescription("Download Navigation Database V" + version);
        nvRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, navDb);

        DownloadManager.Request airsRequest = new DownloadManager.Request(Uri.parse(dbBaseUrl + airspacesDb));
        airsRequest.setTitle("Download Airspaces Database V" + version);
        airsRequest.setDescription("Download Airspaces Database V" + version);
        airsRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, airspacesDb);

        navDbDownloadId = dm.enqueue(nvRequest);
        airspaceDbDownloadId = dm.enqueue(airsRequest);

        Message = "Download updated databases is started..";
        setMessage(Message);
    }

    private Property checkVersion()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        String name = "INSTALLED_DB_VERSION";

        Property ver = propertiesDataSource.GetProperty(name);
        if (ver == null)
        {
            ver = new Property();
            ver.name = name;
            ver.value1 = version;
            ver.value2 = "false";
            propertiesDataSource.InsertProperty(ver);
            ver = propertiesDataSource.GetProperty(name);
        }

        propertiesDataSource.close(true);

        return ver;
    }

    private void updateVersion(Property ver)
    {
        ver.value1 = version;
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        propertiesDataSource.updateProperty(ver);
    }

    private Boolean checkLocalFiles()
    {
        return new File(downloadedToDir+"airnav.db").exists() &&
                new File(downloadedToDir + "all-airspaces.db").exists();
    }

    public void CheckAndUpdateDatabases()
    {
        if (checkLocalFiles())
        {
            Log.i(TAG, "Found Local downloaded databases");
            Property ver = checkVersion();
            version = ver.value1;
            String databasePath = context.getDatabasePath("airnav.db").getParent() + "/";
            //String databasePath = DBFilesHelper.DatabasePath(context);

            navDBbFile = new File(downloadedToDir + "airnav.db");
            airspacesDbFile = new File(downloadedToDir + "all-airspaces.db");

            if (navDBbFile.exists() && airspacesDbFile.exists())
            {
                Log.i(TAG, "Start deleting old databases and move the new ones");
                Log.i(TAG, "Database Path: " + databasePath);
                File oldNavDbFile = new File(databasePath + "airnav.db");
                if (oldNavDbFile.exists()) context.deleteDatabase("airnav.db");

                File oldAirspaceDbFile = new File(databasePath + "all-airspaces.db");
                if (oldAirspaceDbFile.exists()) context.deleteDatabase("all-airspaces.db");

                try {
                    DBFilesHelper.Copy(context, navDBbFile.getPath().toString(), oldNavDbFile.getPath().toString());
                    navDBbFile.delete();
                    DBFilesHelper.Copy(context, airspacesDbFile.getPath().toString(), oldAirspaceDbFile.getPath().toString());
                    airspacesDbFile.delete();

                    ver.value2 = "true";
                    updateVersion(ver);

                    Message = "Database update to version: " + version + " is complete!";
                    setMessage(Message);

                    Log.i(TAG, "Database update to version: " + version + " is complete!");
                }
                catch (Exception ee)
                {
                    Message = "Error copying the database files :" + ee.getMessage();
                    Log.e(TAG, Message);
                    setMessage(Message);
                }
            }
        }
    }

    public ArrayList<String> getFiles()
    {
        ArrayList<String> files = new ArrayList<>();
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(navDbDownloadId);
        q.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = dm.query(q);

        if (c.moveToFirst())
        {
            navDb = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            Log.i(TAG, "Found download: " + navDbDownloadId + " To: " + navDb);
            navDBbFile = new File(Uri.parse(navDb).getPath());
            String p = navDBbFile.getParent() + "/airnav.db";
            navDBbFile.renameTo(new File(p));
            //dm.remove(navDbDownloadId);
        }
        c.close();

        q.setFilterById(airspaceDbDownloadId);
        q.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c1 = dm.query(q);

        if (c1.moveToFirst())
        {
            airspacesDb = c1.getString(c1.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            Log.i(TAG, "Found download: " + airspaceDbDownloadId + " To: " + airspacesDb);
            airspacesDbFile = new File(Uri.parse(airspacesDb).getPath());
            String p = airspacesDbFile.getParent() + "/all-airspaces.db";
            airspacesDbFile.renameTo(new File(p));
            //dm.remove(airspaceDbDownloadId);
        }
        c1.close();

        if ((navDBbFile!=null) && (airspacesDbFile!=null)) {
            Message = "Download Completed, please restart FlightSim-Planner to install new Databases!";
            setMessage(Message);
        }

        return files;
    }

    private void setMessage(String message)
    {
        if (onMessage != null)
            onMessage.Message(message);
    }
    private OnMessage onMessage;
    public void SetOnMessage(OnMessage d) { onMessage = d; }
    public interface OnMessage
    {
        public void Message(String message);
    }
}

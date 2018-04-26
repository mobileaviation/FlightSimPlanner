package nl.robenanita.googlemapstest.MBTiles;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import nl.robenanita.googlemapstest.database.DBFilesHelper;
import nl.robenanita.googlemapstest.database.DBHelper;
import nl.robenanita.googlemapstest.database.MBTilesLocalDataSource;

public class MBTile {
    public MBTile(Context context)
    {
        this.context = context;
        localChartsDir = this.context.getApplicationInfo().dataDir + "/charts/";
        visible_order = -1;
        CheckfileRunning = false;
    }

    private String TAG = "MBTile";

    private Context context;
    private String localChartsDir;
    private DownloadManager dm;
    private Long dbDownloadId;

    public Integer _id;
    public String name;
    public String region;
    public MBTileType type;
    public void setType(String type_str)
    {
        if (type_str.equals("ofm")) type = MBTileType.ofm;
        if (type_str.equals("fsp")) type = MBTileType.fsp;
        if (type_str.equals("local")) type = MBTileType.local;
    }
    public String mbtileslink;
    public String xmllink;
    public Integer version;
    public Integer visible_order;

    public Date startValidity;
    public void setStartValidity(Integer timestamp) { startValidity = getDate(timestamp);}
    public Date endValidity;
    public void setEndValidity(Integer timestamp) { endValidity = getDate(timestamp);}
    public Boolean LocalFileExists()
    {
        return new File(getLocalFilename()).exists();
    }

    public Boolean DeleteLocalFile()
    {
        File file = new File(getLocalFilename());
        file.delete();
        String downloadedFile = CheckDownloadedTile();
        File dlFile = new File(downloadedFile);
        if (dlFile.exists()) dlFile.delete();
        return !LocalFileExists() && !dlFile.exists();
    }

    private Boolean CheckValidity()
    {
        File file = new File(getLocalFilename());
        Date lastModified = new Date(file.lastModified());
        return endValidity.before(new Date());
    }

    public String getLocalFilename()
    {
        File remoteFile = new File(mbtileslink);
        return localChartsDir + remoteFile.getName();
    }

    public void StartDownload()
    {
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request nvRequest = new DownloadManager.Request(Uri.parse(mbtileslink));
        nvRequest.setTitle("Downloading " + name + " from " + type.toString());
        nvRequest.setDescription("Downloading " + name + " from " + type.toString());
        nvRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,
                new File(mbtileslink).getName());
        dbDownloadId = dm.enqueue(nvRequest);
    }

    public String CheckDownloadedTile()
    {
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = dm.query(q);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String file = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String filename = new File(Uri.parse(file).getPath()).getName();

                if (filename.equals(new File(mbtileslink).getName())) return file;
                c.moveToNext();
            }
        }

        return null;
    }

    public Boolean CheckfileRunning;
    public Boolean CheckFile()
    {
        if (!LocalFileExists())
        {
            String downloadedFile = CheckDownloadedTile();
            if (downloadedFile != null)
            {
                try {
                    CheckfileRunning = true;
                    Log.i(TAG, "Found downloaded file: " + downloadedFile);
                    File downl_file = new File(Uri.parse(downloadedFile).getPath());
                    File local_file = new File(getLocalFilename());

                    if (downl_file.exists()) {

                        if (local_file.exists()) local_file.delete();
                        if (!(new File(localChartsDir)).exists())
                            (new File(localChartsDir)).mkdir();
                        DBFilesHelper.Copy1(context, downl_file.toString(), local_file.toString());
                        if (local_file.exists()) downl_file.delete();

                        Log.i(TAG, "Copied downloaded file to: " + local_file.toString());

                        CheckfileRunning = false;
                        return local_file.exists();
                    }
                    else
                    {
                        CheckfileRunning = false;
                        return false;

                    }

                } catch (Exception e) {
                    CheckfileRunning = false;
                    e.printStackTrace();
                    return false;
                }
            }
            else
                return false;
        }
        else
            return true;
    }

    private Date getDate(Integer timestamp) { return new Date((long)timestamp*1000); }

    public void UpdateVisibility()
    {
        MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(context);
        mbTilesLocalDataSource.open();
        mbTilesLocalDataSource.updateVisibility(this);
        mbTilesLocalDataSource.close();
    }


    public void InsertUpdateDB()
    {
        MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(context);
        mbTilesLocalDataSource.open();
        mbTilesLocalDataSource.insertUpdateTile(this);
        mbTilesLocalDataSource.close();
    }

    public void CheckVisibleStatus()
    {
        MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(context);
        mbTilesLocalDataSource.open();
        visible_order = mbTilesLocalDataSource.checkVisibleStatusTile(this);
        mbTilesLocalDataSource.close();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MBTile)
        {
            MBTile testTile = (MBTile) object;
            return this.getLocalFilename().equals(testTile.getLocalFilename());
        }
        else return false;
    }
}

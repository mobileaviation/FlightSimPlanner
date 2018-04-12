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

public class MBTile {
    public MBTile(Context context)
    {
        this.context = context;
        localChartsDir = this.context.getApplicationInfo().dataDir + "/charts/";
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
    }
    public String mbtileslink;
    public String xmllink;
    public Integer version;
    public Date startValidity;
    public void setStartValidity(Integer timestamp) { startValidity = getDate(timestamp);}
    public Date endValidity;
    public void setEndValidity(Integer timestamp) { endValidity = getDate(timestamp);}
    private Boolean LocalFileExists()
    {
        return new File(getLocalFilename()).exists();
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

    public Boolean CheckFile()
    {
        if (!LocalFileExists())
        {
            String downloadedFile = CheckDownloadedTile();
            if (downloadedFile != null)
            {
                try {
                    Log.i(TAG, "Found downloaded file: " + downloadedFile);
                    File downl_file = new File(Uri.parse(downloadedFile).getPath());
                    File local_file = new File(getLocalFilename());

                    if (local_file.exists()) local_file.delete();
                    if (!(new File(localChartsDir)).exists()) (new File(localChartsDir)).mkdir();
                    DBFilesHelper.Copy1(context, downl_file.toString(), local_file.toString());
                    if (local_file.exists()) downl_file.delete();

                    Log.i(TAG, "Copied downloaded file to: " + local_file.toString());

                    return local_file.exists();

                } catch (Exception e) {
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
}

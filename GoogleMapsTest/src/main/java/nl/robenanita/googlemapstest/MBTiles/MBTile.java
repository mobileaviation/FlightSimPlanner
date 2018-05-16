package nl.robenanita.googlemapstest.MBTiles;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.robenanita.googlemapstest.database.DBFilesHelper;
import nl.robenanita.googlemapstest.database.DBHelper;
import nl.robenanita.googlemapstest.database.MBTilesDBHelper;
import nl.robenanita.googlemapstest.database.MBTilesLocalDataSource;

public class MBTile {
    @Override
    protected void finalize() throws Throwable {
        //mbTilesLocalDataSource.close();
        super.finalize();
    }

    public MBTile(Context context)
    {
        this.context = context;
        mbTilesLocalDataSource = new MBTilesLocalDataSource(context);
        mbTilesLocalDataSource.open();
        localChartsDir = this.context.getApplicationInfo().dataDir + "/files/";
        visible_order = -1;
        CheckfileRunning = false;
        available = false;
    }

    private MBTilesLocalDataSource mbTilesLocalDataSource;
    private String TAG = "MBTile";

    private Context context;
    private String localChartsDir;
    private DownloadManager dm;
    private Long dbDownloadId;

    public Boolean available;
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
    public String local_file;

    public Date startValidity;
    public void setStartValidity(Integer timestamp) { startValidity = getDate(timestamp);}
    public Date endValidity;
    public void setEndValidity(Integer timestamp) { endValidity = getDate(timestamp);}
    public Boolean LocalFileExists()
    {
        return (local_file!=null)? new File(local_file).exists() : false;
    }

    public Boolean DeleteLocalFile()
    {
        File file = new File(local_file);
        file.delete();
//        String downloadedFile = CheckDownloadedTile();
//        File dlFile = new File(downloadedFile);
//        if (dlFile.exists()) dlFile.delete();
        return !LocalFileExists();// && !dlFile.exists();
    }

    private Boolean CheckValidity()
    {
        File file = new File(local_file);
        Date lastModified = new Date(file.lastModified());
        return endValidity.before(new Date());
    }

    public String getLocalFilename()
    {
        return local_file;
    }

    public void StartDownload()
    {
//        File D = new File(localChartsDir);
//        if (!D.exists()) D.mkdir();

        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request nvRequest = new DownloadManager.Request(Uri.parse(mbtileslink));
        nvRequest.setTitle("Downloading " + name + " from " + type.toString());
        nvRequest.setDescription("Downloading " + name + " from " + type.toString());
        nvRequest.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS,
                "charts/" + (new File(mbtileslink).getName()));
//        String df = localChartsDir + new File(mbtileslink).getName();
//        nvRequest.setDestinationUri(Uri.parse("file:/" + df));
        nvRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        nvRequest.setMimeType("application/octet-stream");
        dbDownloadId = dm.enqueue(nvRequest);
    }

    public String CheckDownloadedTile()
    {
        ArrayList<File> foundFiles = new ArrayList<>();
        dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c = dm.query(q);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                String fileStr = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                File file = new File(Uri.parse(fileStr).getPath());
                File orgFile = new File(mbtileslink);

                String orgFilenameNoExt = orgFile.getName().replaceFirst("[.][^.]+$", "");

                if (file.getName().contains(orgFilenameNoExt))
                {
                    if (file.exists() && !foundFiles.contains(file))
                        foundFiles.add(file);
                }

                c.moveToNext();
            }

            Collections.sort(foundFiles, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    return ((file.lastModified() - t1.lastModified())<0) ? 0 : 1;
                }
            });
        }

        return (foundFiles.size()>0)? foundFiles.get(0).getPath() : null;
    }

    public Boolean CheckfileRunning;
    public Boolean CheckFile()
    {
        if (!available) {
            local_file = CheckDownloadedTile();
            UpdateLocalFile();

            CheckfileRunning = false;
            return (local_file != null);
        }
        else
        {
            if (local_file != null)
            {
                if (new File(local_file).exists())
                {
                    CheckfileRunning = false;
                    return true;
                }
                else
                {
                    available = false;
                    UpdateLocalFile();
                    CheckfileRunning = false;
                    return false;
                }
            }
            else
            {
                available = false;
                CheckfileRunning = false;
                return CheckFile();
            }

        }

//        if (!LocalFileExists())
//        {
//            String downloadedFile = CheckDownloadedTile();
//            if (downloadedFile != null)
//            {
//                try {
//                    CheckfileRunning = true;
//                    Log.i(TAG, "Found downloaded file: " + downloadedFile);
//                    File downl_file = new File(Uri.parse(downloadedFile).getPath());
//                    File local_file = new File(getLocalFilename());
//
//                    if (downl_file.exists()) {
//
//                        MBTilesDBHelper helper = new MBTilesDBHelper(context, local_file.getName());
//                        if (local_file.exists()) local_file.delete();
//                        helper.getWritableDatabase();
////                        if (!(new File(localChartsDir)).exists())
////                            (new File(localChartsDir)).mkdir();
//                        DBFilesHelper.Copy(context, downl_file.toString(), local_file.toString());
//                        if (local_file.exists()) downl_file.delete();
//
//                        Log.i(TAG, "Copied downloaded file to: " + local_file.toString());
//
//                        CheckfileRunning = false;
//                        return local_file.exists();
//                    }
//                    else
//                    {
//                        CheckfileRunning = false;
//                        return false;
//
//                    }
//
//                } catch (Exception e) {
//                    CheckfileRunning = false;
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//            else
//                return false;
//        }
//        else
//            return true;
    }

    private Date getDate(Integer timestamp) { return new Date((long)timestamp*1000); }

    public void UpdateVisibility()
    {
        mbTilesLocalDataSource.updateVisibility(this);
    }

    private void UpdateLocalFile()
    {
        if (local_file != null) {
            mbTilesLocalDataSource.updateLocalFile(this);
            mbTilesLocalDataSource.updateAvailable(this);
        }
    }

    public void InsertUpdateDB()
    {
        mbTilesLocalDataSource.insertUpdateTile(this);
    }

    public void InsertUpdateDB(MBTilesLocalDataSource mbTilesLocalDataSource)
    {
        mbTilesLocalDataSource.insertUpdateTile(this);
    }

    public void CheckVisibleStatus()
    {
        mbTilesLocalDataSource.open();
        visible_order = mbTilesLocalDataSource.checkVisibleStatusTile(this);
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

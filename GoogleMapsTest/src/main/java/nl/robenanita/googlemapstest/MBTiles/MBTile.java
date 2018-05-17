package nl.robenanita.googlemapstest.MBTiles;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import nl.robenanita.googlemapstest.Classes.FileDownloader;
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
        localChartsDir = this.context.getApplicationInfo().dataDir + "/charts/";
        visible_order = -1;
        CheckfileRunning = false;
        available = false;
    }

    private MBTilesLocalDataSource mbTilesLocalDataSource;
    private String TAG = "MBTile";

    private Context context;
    private String localChartsDir;
    public String getLocalChartsDirectory() { return  localChartsDir; }
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
    public void GenerateLocalFile()
    {
        File f = new File(Uri.parse(mbtileslink).getPath());
        local_file = localChartsDir + f.getName();
    }

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

    public Boolean CheckfileRunning;
    public Boolean CheckFile()
    {
        if (!available) {
            //local_file = CheckDownloadedTile();
            UpdateLocalFile();

            CheckfileRunning = false;
            return (new File(local_file).exists());
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

package nl.robenanita.googlemapstest.Wms.Offline;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.Wms.OfflineMapTypes;

/**
 * Created by Rob Verhoef on 26-12-2017.
 */

public class CleanOfflineTiles {
    public CleanOfflineTiles(String path, OfflineMapTypes type)
    {
        this.path = path;
        this.type = type.toString();
    }

    private String path;
    private String type;
    private String TAG = "CleanOfflineTiles";

    public void CleanTiles()
    {
        ArrayList<File> f = getFiles();
        for (File file: f)
        {
            if (!file.isDirectory())
                Log.i(TAG, "Delete File: " + file.getName());
                file.delete();
        }

    }

    private ArrayList<File> getFiles()
    {
        File directory = new File(path);
        File[] files = directory.listFiles();
        ArrayList<File> filteredFiles = new ArrayList<File>();

        for(File f: files)
            if (f.getName().contains(type)) {
                Log.i(TAG, f.getName());
                filteredFiles.add(f);
            }

        return filteredFiles;
    }
}

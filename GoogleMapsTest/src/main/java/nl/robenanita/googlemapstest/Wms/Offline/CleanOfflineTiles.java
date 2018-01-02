package nl.robenanita.googlemapstest.Wms.Offline;

import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
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

    public String GetSize()
    {
        Long size = Long.valueOf(0);
        ArrayList<File> files = getFiles();
        for (File f: files)
        {
            size = size + f.length();
        }
        return  readableFileSize(size);
    }

    private String readableFileSize(long size) {
        if(size <= 0) return "0 MB";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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

package nl.robenanita.googlemapstest.database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 24-7-2017.
 */

public class DBFilesHelper {
    public static String DatabasePath(Context context)
    {
        String p = context.getFilesDir().getPath() + "/"
                + "databases" + "/";
        File f = new File(p);
        if (!f.exists()) f.mkdir();
        return p;
    }

    public static boolean CopyNavigationDatabase(Context context, String name)
    {
        String dest = DatabasePath(context);
        try {
            CopyFromAssetsToStorage(context, name, dest + name);
            Log.e("DatabaseFile", name + " copied to: " + dest);
            return true;
        } catch (IOException e) {
            Log.e("DatabaseFile", "Error copying " + name + " file");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean CopyFromAssetDatabaseTo(Context context, String name, String dest)
    {
        //String dest = DatabasePath(context);
        try {
            CopyFromAssetsToStorage(context, name, dest + name);
            Log.e("DatabaseFile", name + " copied to: " + dest);
            return true;
        } catch (IOException e) {
            Log.e("DatabaseFile", "Error copying " + name + " file");
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> CopyDatabases(Context context, Boolean copy)
    {
        ArrayList<String> databases = new ArrayList<String>();
        String dest = DatabasePath(context);
        try {
            String [] list = context.getAssets().list("");
            for (String f : list) {
                if (f.contains("airspaces.db.sqlite")) {
                    databases.add(f);
                    if (copy) CopyFromAssetsToStorage(context, f, dest + f);
                    Log.e("DatabaseFile", f+ " copied to: " + dest);
                }
            }
        } catch (IOException e1) {
            Log.e("Filedir", "Error copying airspaces files");
            e1.printStackTrace();
        }
        return databases;
    }

    public static String CopyAirspaceMap(Context context)
    {
//        Log.i("Maptest", "Airspace Files Copied to: " + dest);
//        mapView.addVectorMap("Airspaces", dest + "Airspaces.sqlite", dest + "Airspaces.map");

        String p = context.getFilesDir().getPath() + "/";
        try {
            CopyFromAssetsToStorage(context, "Airspaces.map", p + "Airspaces.map");
            CopyFromAssetsToStorage(context, "Airspaces.sqlite", p + "Airspaces.sqlite");
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }

        return p;
    }

    private static void CopyFromAssetsToStorage(Context context, String SourceFile, String DestinationFile) throws IOException {
        InputStream IS = context.getAssets().open(SourceFile);
        OutputStream OS = new FileOutputStream(DestinationFile);
        CopyStream(IS, OS);
        OS.flush();
        OS.close();
        IS.close();
    }

    public static void Copy(Context context, String SourceFile, String DestinationFile) throws IOException {
        InputStream IS = new FileInputStream(SourceFile);
        OutputStream OS = new FileOutputStream(DestinationFile);
        CopyStream(IS, OS);
        OS.flush();
        OS.close();
        IS.close();
    }

    private static void CopyStream(InputStream In, OutputStream Out) throws IOException {
        byte[] buffer = new byte[5120];
        int lenght = In.read(buffer);
        while (lenght >0){
            Out.write(buffer, 0, lenght);
            lenght = In.read(buffer);
        }

    }
}

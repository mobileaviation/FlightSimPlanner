package nl.robenanita.googlemapstest.openaip;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 29-9-2015.
 */
public class Airspaces extends ArrayList<Airspace> {
    private String TAG = "GooglemapsTest";

    public Airspaces()
    {

    }

    public void Add(Airspace airspace)
    {
        this.add(airspace);
    }

    public void OpenAipFile(Context context, String filename)
    {
        //String _filename = Environment.getExternalStorageDirectory().toString()+"/Download/" + filename;
        String _filename = "/sdcard/Download/" + filename;
        String XML = readFromFile(context, _filename);
        Log.i(TAG, XML);
    }

    private String readFromFile(Context context, String fileName) {
        if (context == null) {
            return null;
        }

        String ret = "";

        try {
            FileInputStream inputStream = new FileInputStream (new File(fileName));

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int size = inputStream.available();
                char[] buffer = new char[size];

                inputStreamReader.read(buffer);

                inputStream.close();
                ret = new String(buffer);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

}

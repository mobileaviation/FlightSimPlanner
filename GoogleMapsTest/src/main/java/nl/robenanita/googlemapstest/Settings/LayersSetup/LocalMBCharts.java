package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.MBTiles.MBTileType;
import nl.robenanita.googlemapstest.SimConnection.WebApiApache;

public class LocalMBCharts {
    private String TAG = "GooglemapsTest";

    public interface OnMBFileList
    {
        public void filesList(List<MBTile> files, Boolean success);
    }

    public LocalMBCharts(String Ip, int Port, Context context)
    {
        this.context = context;
        baseUrl = "http://" + Ip + ":" + Integer.toString(Port);
    }

    private String baseUrl;
    private Context context;

    private OnMBFileList onMBFilesList;
    public void SetOnMBFilesList(OnMBFileList onMBFileList) {this.onMBFilesList = onMBFileList;}

    public void GetFilelist()
    {
        getFiles files = new getFiles(baseUrl);
        files.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void parseFilesListJson(String json)
    {
        if (!json.startsWith("error")) {
            try
            {
                JSONArray array = new JSONArray(json);
                ArrayList<MBTile> tileList = new ArrayList<>();

                for (int i = 0; i<array.length(); i++)
                {
                    JSONObject object = array.getJSONObject(i);
                    MBTile tile = new MBTile(context);
                    tile.name = object.getString("Filename");
                    tile.mbtileslink = baseUrl + "/" + tile.name;
                    tile.type = MBTileType.fsp;
                    tile.version = 1804;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    tile.startValidity = format.parse(object.getString("CreationTime"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(tile.startValidity);
                    cal.add(Calendar.DATE, 30);
                    tile.endValidity = cal.getTime();
                    tileList.add(tile);
                }

                if (onMBFilesList != null) onMBFilesList.filesList(tileList, true);
            }
            catch (Exception ee) {
                if (onMBFilesList != null) onMBFilesList.filesList(null, false);
            }
        }
        else
        {
            if (onMBFilesList != null) onMBFilesList.filesList(null, false);
        }
    }

    public class getFiles extends AsyncTask<String, String, String>
    {
        public getFiles(String url)
        {
            this.url = url;
        }

        private String url;

        @Override
        protected void onPostExecute(String s) {
            parseFilesListJson(s);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                WebApiApache webApiApache = new WebApiApache();
                String resp = webApiApache.Get(url + "/v1/fsuipc/files?filter=*.mbtiles");
                return resp;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return "error; " + ex.getMessage();
            }
        }
    }
}

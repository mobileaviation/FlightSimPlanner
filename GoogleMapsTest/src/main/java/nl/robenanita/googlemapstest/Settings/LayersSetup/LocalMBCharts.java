package nl.robenanita.googlemapstest.Settings.LayersSetup;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.SimConnection.WebApiApache;

public class LocalMBCharts {
    private String TAG = "GooglemapsTest";

    public interface OnMBFileList
    {
        public void filesList(List<MBTile> files, Boolean success);
    }

    public LocalMBCharts(String Ip, int Port)
    {
        baseUrl = "http://" + Ip + ":" + Integer.toString(Port) + "/v1/fsuipc/files?filter=*.mbtiles";
    }

    private String baseUrl;

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
                Log.i(TAG, "Test");
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
                String resp = webApiApache.Get(url);
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

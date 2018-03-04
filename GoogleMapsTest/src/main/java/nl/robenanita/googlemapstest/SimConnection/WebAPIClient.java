package nl.robenanita.googlemapstest.SimConnection;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import nl.robenanita.googlemapstest.Offset;

/**
 * Created by Rob Verhoef on 4-3-2018.
 */

public class WebAPIClient {
    public static final String  OPENENDPOINT = "v1/fsuipc/open";
    public static final String  CLOSEENDPOINT = "v1/fsuipc/close";
    public static final String  STATUSENDPOINT = "v1/fsuipc/status";
    public static final String  VALUESENDPOINT = "v1/fsuipc/offsets";

    public WebAPIClient(String IP, int Port)
    {
        _url = "http://" + IP + ":" + Integer.toString(Port) + "/";
        _isConnected = false;
    }

    private String _url;
    private URL url;

    private boolean _isConnected;
    public  boolean isConnected()
    {
        return false;
    }

    public boolean OpenFSUIPC()
    {
        try {
            url = new URL(_url + OPENENDPOINT);
            connectTask connectTask = new connectTask();
            connectTask.url = url;
            connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ProcessFSUIPCOffsets(List<Offset> offsets)
    {
        JSONArray json_offsets = new JSONArray();
        //json_offsets.put(offsets);
        try {
            url = new URL(_url + VALUESENDPOINT);

                for (int i = 0; i < offsets.size(); i++)
                {
                    JSONObject j = new JSONObject();
                    j.put("Address", Integer.toString(offsets.get(i).Address));
                    j.put("DataGroup", offsets.get(i).DatagroupName);
                    j.put("DataType", offsets.get(i).Datatype.toString());
                    j.put("Value", "-");

                    json_offsets.put(i, j);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        String json = json_offsets.toString();

        processTask p = new processTask();
        p.processJson = json;
        p.url = url;
        p.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return true;
    }

    private class processTask extends AsyncTask<String, Void, String>
    {
        public String processJson;
        public URL url;

        @Override
        protected void onPostExecute(String s) {

            if (mFSUIPCProcessedListener != null) mFSUIPCProcessedListener.FSUIPCAction(s, true);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            String resp = WebAPIHelpers.Post(url, processJson.toString());
            return resp;
        }
    }

    private class connectTask extends AsyncTask<String, Void, String>
    {
        public URL url;

        @Override
        protected void onPostExecute(String s) {
            // {"Message":"Connection Opened","Simulator":"FSX","Aircraft":"Aircreation582SL Blue"}
            try {
                JSONObject json = new JSONObject(s);
                if (json.getString("Message").equals("Connection Opened")) {
                    String message = json.getString("Message") + " Sim: " + json.getString("Simulator") + " Aircraft: " + json.getString("Aircraft");
                    if (mFSUIPCOpenedListener != null) mFSUIPCOpenedListener.FSUIPCAction(message, true);
                }
                else
                {
                    if (mFSUIPCOpenedListener != null) mFSUIPCOpenedListener.FSUIPCAction("Connection Error", false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject json = new JSONObject();
                json.put("name", "Flightsim Planner");

                String resp = WebAPIHelpers.Post(url, json.toString());

                return resp;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private FSUIPCConnection.OnFSUIPCAction mFSUIPCOpenedListener = null;
    public void SetFSUIPCOpenListener(FSUIPCConnection.OnFSUIPCAction listener) {mFSUIPCOpenedListener = listener;}
    private FSUIPCConnection.OnFSUIPCAction mFSUIPCClosedListener = null;
    public void SetFSUIPCCloseListener(FSUIPCConnection.OnFSUIPCAction listener) {mFSUIPCClosedListener = listener;}
    private FSUIPCConnection.OnFSUIPCAction mFSUIPCProcessedListener = null;
    public void SetFSUIPCProcessListener(FSUIPCConnection.OnFSUIPCAction listener) {mFSUIPCProcessedListener = listener;}
}

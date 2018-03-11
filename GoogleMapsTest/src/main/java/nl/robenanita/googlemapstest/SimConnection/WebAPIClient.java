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

    public boolean CloseFSUIPC()
    {
        try {
            url = new URL(_url + CLOSEENDPOINT);
            closeTask closeTask = new closeTask();
            closeTask.url = url;
            closeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            return true;
        }
        catch (Exception e)
        {
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
            boolean success = !s.startsWith("error");
            if (mFSUIPCProcessedListener != null) mFSUIPCProcessedListener.FSUIPCAction(s, success);
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                WebAPIHelpers webAPIHelpers = new WebAPIHelpers();
                SimConnectResponse resp = webAPIHelpers.Post(url, processJson.toString());
                if (resp.HttpResultCode != HttpURLConnection.HTTP_OK)
                    resp.Response = "error: " + resp.Response;

                return resp.Response;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "error; " + e.getMessage();
            }
        }
    }

    private class connectTask extends AsyncTask<String, Void, String>
    {
        public URL url;

        @Override
        protected void onPostExecute(String s) {
            // {"Message":"Connection Opened","Simulator":"FSX","Aircraft":"Aircreation582SL Blue"}
            if (!s.startsWith("error")) {
                try {
                    JSONObject json = new JSONObject(s);
                    if (json.getString("Message").equals("Connection Opened")) {
                        String message = json.getString("Message") + " Sim: " + json.getString("Simulator") + " Aircraft: " + json.getString("Aircraft");
                        String version = json.getString("Version");
                        if (mFSUIPCOpenedListener != null)
                            mFSUIPCOpenedListener.FSUIPCOpen(message, true, version);
                    } else {
                        if (mFSUIPCOpenedListener != null)
                            mFSUIPCOpenedListener.FSUIPCOpen("Error: " + s, false, "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                mFSUIPCOpenedListener.FSUIPCOpen("Error: " + s, false, "");
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject json = new JSONObject();
                json.put("name", "Flightsim Planner");

                WebAPIHelpers webAPIHelpers = new WebAPIHelpers();
                SimConnectResponse resp = webAPIHelpers.Post(url, json.toString());
                if (resp.HttpResultCode != HttpURLConnection.HTTP_OK)
                    resp.Response = "error: " + resp.Response;

                return resp.Response;
            } catch (Exception e) {
                e.printStackTrace();
                return "error; " + e.getMessage();
            }
        }
    }

    private class closeTask extends AsyncTask<String, Void, String>
    {
        public URL url;

        @Override
        protected void onPostExecute(String s) {
            if (!s.startsWith("error")) {
                try {
                    if (s.contains("Connection Closed")) {
                        String message = "Connection Closed";
                        if (mFSUIPCClosedListener != null)
                            mFSUIPCClosedListener.FSUIPCAction(message, true);
                    } else {
                        if (mFSUIPCClosedListener != null)
                            mFSUIPCClosedListener.FSUIPCAction("Error: " + s, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                mFSUIPCClosedListener.FSUIPCAction("Error: " + s, false);
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONObject json = new JSONObject();
                json.put("name", "Flightsim Planner");

                WebAPIHelpers webAPIHelpers = new WebAPIHelpers();
                SimConnectResponse resp = webAPIHelpers.Post(url, json.toString());
                if (resp.HttpResultCode != HttpURLConnection.HTTP_OK)
                    resp.Response = "error: " + resp.Response;

                return resp.Response;
            } catch (Exception e) {
                e.printStackTrace();
                return "error; " + e.getMessage();
            }
        }
    }

    private FSUIPCConnection.OnFSUIPCOpen mFSUIPCOpenedListener = null;
    public void SetFSUIPCOpenListener(FSUIPCConnection.OnFSUIPCOpen listener) {mFSUIPCOpenedListener = listener;}
    private FSUIPCConnection.OnFSUIPCAction mFSUIPCClosedListener = null;
    public void SetFSUIPCCloseListener(FSUIPCConnection.OnFSUIPCAction listener) {mFSUIPCClosedListener = listener;}
    private FSUIPCConnection.OnFSUIPCAction mFSUIPCProcessedListener = null;
    public void SetFSUIPCProcessListener(FSUIPCConnection.OnFSUIPCAction listener) {mFSUIPCProcessedListener = listener;}
}

package nl.robenanita.googlemapstest.SimConnection;

import android.os.AsyncTask;

import org.json.JSONObject;

/**
 * Created by Rob Verhoef on 31-3-2018.
 */

public class VersionCheck {
    public interface OnVersionCheck
    {
        void Checked(String ServerVersion, Boolean success);
    }

    public VersionCheck(String version, String ip, int port)
    {
        this.version = new Version(version);
        this.ip = ip;
        this.port = port;
    }

    public OnVersionCheck onVersionCheck;

    private Version version;
    private String ip;
    private int port;

    private class checkAsync extends AsyncTask<String, String, String>
    {
        public Version version;
        public String ip;
        public int port;

        @Override
        protected void onPostExecute(String s) {
            if (!s.startsWith("error")) {
                try {
                    JSONObject json = new JSONObject(s);
                    int major = json.getInt("Major");
                    int minor = json.getInt("Minor");
                    int revision = json.getInt("Revision");
                    int build = json.getInt("Build");

                    boolean checked = version.check(major,minor,revision,build);
                    if (onVersionCheck != null) onVersionCheck.Checked(String.format("%d.%d.%d.%d",major,minor,revision,build), checked);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else {
                if (onVersionCheck != null) onVersionCheck.Checked(s, false);
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String url = "http://" + ip + ":" + Integer.toString(port) + "/v1/fsuipc/version";
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

    public void StartVersionCheck()
    {
        checkAsync checkAsync = new checkAsync();
        checkAsync.ip = ip;
        checkAsync.port = port;
        checkAsync.version = version;

        checkAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

package nl.robenanita.googlemapstest.Classes;

import android.os.AsyncTask;

import nl.robenanita.googlemapstest.Helpers;

/**
 * Created by Rob Verhoef on 4-9-2017.
 */

public class NetworkCheck extends AsyncTask {
    public NetworkCheck()
    {
        Url = null;
    }

    public NetworkCheck(String Url)
    {
        this.Url = Url;
    }

    private String Url;
    private boolean result;
    public interface OnResult
    {
        public void Checked(Boolean result);
    }
    private OnResult onResult;
    public void SetOnResult(OnResult onResult){
        this.onResult = onResult;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        result = (Url==null) ? Helpers.CheckInternetAvailability():
                Helpers.checkInternetAvailabilityAdress(Url);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (onResult != null) onResult.Checked(result);
        super.onPostExecute(o);
    }
}

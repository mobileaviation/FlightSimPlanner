package nl.robenanita.googlemapstest.SimConnection;

/**
 * Created by Rob Verhoef on 17-3-2018.
 */

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class WebApiApache {

    private String TAG = "WebApiApache";

    public WebApiApache()
    {
        HttpParams httpParameters = new BasicHttpParams();
// Set the timeout in milliseconds until a connection is established.
// The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
// Set the default socket timeout (SO_TIMEOUT)
// in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        client = new DefaultHttpClient(httpParameters);

    }

    private HttpClient client;

    public String Post(String url, String body)
    {
        String response = "";
        HttpPost request = new HttpPost(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        try {
            StringEntity requestEntity = new StringEntity(body,"utf-8");
            request.setEntity(requestEntity);
            HttpResponse serverResponse = client.execute(request);
            HttpEntity responseEntity = serverResponse.getEntity();

            if (serverResponse.getStatusLine().getStatusCode()!=200) response = "error:";
            if (responseEntity.getContentLength()>0) {
                response = response + EntityUtils.toString(responseEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response = "error: " + e.getMessage();
        }
        finally {
            client.getConnectionManager().shutdown();
            return response;
        }
    }

    public String Get(String url)
    {
        String response = "";
        HttpUriRequest request = new HttpGet(url);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        try {
            HttpResponse serverResponse = client.execute(request);
            if (serverResponse.getStatusLine().getStatusCode()!=200) response = "error:";

            HttpEntity responseEntity = serverResponse.getEntity();
            if (responseEntity.getContentLength()>0)
            {
                response = response + EntityUtils.toString(responseEntity);
            }

            Log.d(TAG, "Get: " + response);
        } catch (IOException e) {
            response = "error: " + e.getMessage();
            e.printStackTrace();
        }
        return response;
    }

}

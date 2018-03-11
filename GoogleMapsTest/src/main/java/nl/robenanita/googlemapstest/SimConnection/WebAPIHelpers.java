package nl.robenanita.googlemapstest.SimConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rob Verhoef on 4-3-2018.
 */

public class WebAPIHelpers {
    public static SimConnectResponse Post(URL url, String json)
    {
        SimConnectResponse resp = new SimConnectResponse();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestMethod("POST");
            //connection.setConnectTimeout(1000);

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            StringBuilder sb = new StringBuilder();
            int HttpResult = connection.getResponseCode();
            resp.HttpResultCode = HttpResult;
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                resp.Response = sb.toString();
                return resp;
            } else {
                resp.Response = connection.getResponseMessage();
                return resp;
            }
        }
        catch (Exception ex)
        {
            resp.HttpResultCode = HttpURLConnection.HTTP_BAD_REQUEST;
            resp.Response = ex.getMessage();
            return resp;
        }
    }

}

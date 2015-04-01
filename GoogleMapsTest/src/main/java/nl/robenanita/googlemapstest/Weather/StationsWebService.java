package nl.robenanita.googlemapstest.Weather;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 1-4-2015.
 */
public class StationsWebService {
    public StationsWebService()
    {
        stations = new ArrayList<Station>();
    }

    final static String TAG = "GooglemapsTest";

    public ArrayList<Station> stations;
    private LatLng orgLocation;

    public void GetStationsByLocationRadius(LatLng location, Integer radius)
    {
        //http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=100;5.5,52.46
        String command = "http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=#RAD#;#LON#,#LAT#";
        command = command.replace("#LON#", Double.toString(location.longitude));
        command = command.replace("#LAT#", Double.toString(location.latitude));
        command = command.replace("#RAD#", Integer.toString(radius));

        ReadStationsAsync readStationsAsync = new ReadStationsAsync();
        readStationsAsync.command = command;
        readStationsAsync.type = Type.station;
        readStationsAsync.onlyRawData = false;
        orgLocation = location;

        Log.i(TAG, "Metar command: " + command);

        readStationsAsync.execute();
    }

    private class ReadStationsAsync extends AsyncTask<String, Integer, Void>
    {
        public String command;
        public String Icao;
        public Type type;
        public String Xml;
        public Boolean onlyRawData;

        @Override
        protected Void doInBackground(String... strings) {
            Xml = readFromUrl(command);

            if (type == Type.station)
            {
                processStationsXml(Xml);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (type == Type.station) {
                Log.i(TAG, "Finished Reading stations xml");
                onDataAvailable.OnStationsAvailable(stations);
            }


            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        private void processStationsXml(String Xml)
        {
            Log.i(TAG, "Stations XML: " + Xml);
            Station station = null;


            XmlPullParser parser = android.util.Xml.newPullParser();
            try {
                parser.setInput(new StringReader(Xml));
                int eventType = parser.getEventType();
                String name = null;

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.TEXT:
                        {

                            if (name != null) {
                                try {
                                    if (name.equals("station_id")) station.station_id = parser.getText();
                                    if (name.equals("METAR")) station.AddSiteType("METAR");
                                    if (name.equals("TAF")) station.AddSiteType("TAF");
                                    name = "";
                                }
                                catch (Exception ee)
                                {
                                    Log.i(TAG, "Parse Metar Value XML Error: " + ee.getMessage());
                                }
                            }

                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            name = parser.getName();

                            if (name.equals("Station")) station = new Station();

                            break;
                        }
                        case XmlPullParser.END_TAG:
                        {
                            if (parser.getName().equals("Station")) {
                                stations.add(station);
                                publishProgress(stations.size());
                            }
                            break;
                        }

                    }

                    eventType = parser.next();
                }
                Log.i(TAG, "End XML Parsing : " + stations.size());
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Station XML Parse error: " + ee.getMessage());
            }
        }

        private void processMobileAviationToolsStationsJson(String Json)
        {
            try {
                JSONObject jObj = new JSONObject(Json);
                JSONArray dataObj = jObj.getJSONArray("airports");

                for (int i=0; i<dataObj.length(); i++)
                {
                    Station station = new Station();
                    station.station_id = dataObj.getJSONObject(i).getString("ident");
                    station.latitude = dataObj.getJSONObject(i).getDouble("latitude_deg");
                    station.longitude = dataObj.getJSONObject(i).getDouble("longitude_deg");
                    stations.add(station);
                }
            }
            catch(JSONException ee)
            {
                Log.e(TAG, "MobileAviationTools Json Error: " + ee.getMessage());
            }
        }

        private String readFromUrl(String Url)
        {
            HttpGet request = new HttpGet(Url);
            HttpClient httpclient = new DefaultHttpClient();
            String Result = "";

            try
            {
                HttpResponse response = httpclient.execute(request);
                Result = inputStreamToString(response.getEntity().getContent()).toString();

            }
            catch (ClientProtocolException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return Result;
        }

        private StringBuilder inputStreamToString(InputStream is)
        {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            try
            {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                while ((rLine = rd.readLine()) != null)
                {
                    answer.append(rLine);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return answer;
        }
    }





    public OnDataAvailable onDataAvailable = null;
    public void setOnDataAvailable(final OnDataAvailable d) {onDataAvailable = d;}
    public interface OnDataAvailable{
        public void OnStationsAvailable(ArrayList<Station> stations);
    }
}

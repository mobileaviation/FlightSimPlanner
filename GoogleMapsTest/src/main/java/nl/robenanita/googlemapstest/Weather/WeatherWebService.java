package nl.robenanita.googlemapstest.Weather;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rob Verhoef on 2-5-2014.
 */
public class WeatherWebService {
    final static String TAG = "GooglemapsTest";
    final static String BaseUrl = "http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=#DATASOURCE#&requestType=retrieve&format=xml";

    public ArrayList<Metar> metars;
    public ArrayList<Taf> tafs;

    private LatLng orgLocation;


    public WeatherWebService() {
        metars = new ArrayList<Metar>();
        tafs = new ArrayList<Taf>();
    }

    public void GetMetarsByICAO(List<String> IcaoCode) {
        metars = new ArrayList<Metar>();
        // http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&mostRecentForEachStation=true&hoursBeforeNow=3&stationString=EHAM,EHRD&requestType=retrieve&format=xml

        String command = "metars&mostRecentForEachStation=true&hoursBeforeNow=3&stationString=";
        String codes = "";
        for (String code : IcaoCode)
        {
            codes = codes + code + ",";
        }
        command = command + codes.substring(0, codes.length()-1);
        command = BaseUrl.replace("#DATASOURCE#", command);

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = Type.metar;
        readWeatherXMLAsync.onlyRawData = true;

        Log.i(TAG, "Metar command: " + command);

        readWeatherXMLAsync.execute();
    }

    public void GetTafsByICAO(List<String> IcaoCode) {
        metars = new ArrayList<Metar>();
        // http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=metars&mostRecentForEachStation=true&hoursBeforeNow=3&stationString=EHAM,EHRD&requestType=retrieve&format=xml

        String command = "tafs&mostRecentForEachStation=true&hoursBeforeNow=3&stationString=";
        String codes = "";
        for (String code : IcaoCode)
        {
            codes = codes + code + ",";
        }
        command = command + codes.substring(0, codes.length()-1);
        command = BaseUrl.replace("#DATASOURCE#", command);

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = Type.taf;
        readWeatherXMLAsync.onlyRawData = true;

        Log.i(TAG, "Taf command: " + command);

        readWeatherXMLAsync.execute();
    }

    public void GetMetar100MilesRadiusFromLocation(LatLng location)
    {
        metars = new ArrayList<Metar>();
        String command = "&radialDistance=100;#LON#,#LAT#&hoursBeforeNow=1&mostRecentForEachStation=true";
        command = command.replace("#LON#", Double.toString(location.longitude));
        command = command.replace("#LAT#", Double.toString(location.latitude));

        command = BaseUrl.replace("#DATASOURCE#", "metars") + command;

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = Type.metar;
        readWeatherXMLAsync.onlyRawData = false;
        orgLocation = location;

        Log.i(TAG, "Metar command: " + command);

        readWeatherXMLAsync.execute();
    }

    public void GetTaf100MilesRadiusFromLocation(LatLng location)
    {
        tafs = new ArrayList<Taf>();
        String command = "&radialDistance=100;#LON#,#LAT#&hoursBeforeNow=1&mostRecentForEachStation=true";
        command = command.replace("#LON#", Double.toString(location.longitude));
        command = command.replace("#LAT#", Double.toString(location.latitude));

        command = BaseUrl.replace("#DATASOURCE#", "tafs") + command;

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = Type.taf;
        readWeatherXMLAsync.onlyRawData = false;
        orgLocation = location;

        Log.i(TAG, "Taf command: " + command);

        readWeatherXMLAsync.execute();
    }

    private Float getFloat(String val)
    {
        return Float.parseFloat((val.equals("")) ? "0" : val);
    }

    private Integer getInteger(String val)
    {
        return Integer.parseInt((val.equals("")) ? "0" : val);
    }

    private class ReadWeatherXMLAsync extends AsyncTask<String, Integer, Void>
    {
        public String command;
        public String Icao;
        public Type type;
        public String Xml;
        public Boolean onlyRawData;

        @Override
        protected Void doInBackground(String... strings) {
            Xml = readFromUrl(command);

            if (type == Type.metar)
            {
                processMetarXml(Xml);
            }
            if (type == Type.taf)
            {
                processTafXml(Xml);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (type == Type.metar) {
                Log.i(TAG, "Finished Reading metar XML");
                onDataAvailable.OnMetarsAvailable(metars);
            }
            if (type == Type.taf)
            {
                Log.i(TAG, "Finished Reading taf XML");
                onDataAvailable.OnTafsAvailable(tafs);
            }


            super.onPostExecute(aVoid);
        }



        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        private void processTafXml(String Xml)
        {
            Log.i(TAG, "Taf XML: " + Xml);
            XmlPullParser parser = android.util.Xml.newPullParser();
            try {
                parser.setInput(new StringReader(Xml));
                int eventType = parser.getEventType();
                Taf taf = null;
                Taf.forecast_class forecast = null;
                String name = null;

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.TEXT: {
                            if (taf != null) {
                                if (name != null) {
                                    try {
                                        // TAF data Fields --------------------
                                        if (name.equals("raw_text"))
                                            taf.raw_text = parser.getText();
                                        if (name.equals("station_id"))
                                            taf.setStation_id(parser.getText());
                                        if (!onlyRawData) {

                                            if (name.equals("issue_time"))
                                                taf.issue_time = parser.getText();
                                            if (name.equals("bulletin_time"))
                                                taf.bulletin_time = parser.getText();
                                            if (name.equals("valid_time_from"))
                                                taf.valid_time_from = parser.getText();
                                            if (name.equals("valid_time_to"))
                                                taf.valid_time_to = parser.getText();
                                            if (name.equals("latitude"))
                                                taf.latitude = getFloat(parser.getText());
                                            if (name.equals("longitude"))
                                                taf.longitude = getFloat(parser.getText());
                                            if (name.equals("elevation_m"))
                                                taf.elevation_m = getFloat(parser.getText());
                                            if (name.equals("remarks"))
                                                taf.remarks = parser.getText();
                                            // TAF data Fields --------------------

                                            // forecast data Fields ---------------
                                            if (forecast != null) {
                                                if (name.equals("fcst_time_from"))
                                                    forecast.fcst_time_from = parser.getText();
                                                if (name.equals("fcst_time_to"))
                                                    forecast.fcst_time_to = parser.getText();
                                                if (name.equals("change_indicator"))
                                                    forecast.change_indicator = parser.getText();
                                                if (name.equals("wx_string"))
                                                    forecast.wx_string = parser.getText();
                                                if (name.equals("not_decoded"))
                                                    forecast.not_decoded = parser.getText();
                                                if (name.equals("visibility_statute_mi"))
                                                    forecast.visibility_statute_mi = getFloat(parser.getText());
                                                if (name.equals("altim_in_hg"))
                                                    forecast.altim_in_hg = getFloat(parser.getText());
                                                if (name.equals("probability"))
                                                    forecast.probability = getInteger(parser.getText());
                                                if (name.equals("wind_dir_degrees"))
                                                    forecast.wind_dir_degrees = getInteger(parser.getText());
                                                if (name.equals("wind_speed_kt"))
                                                    forecast.wind_speed_kt = getInteger(parser.getText());
                                                if (name.equals("wind_gust_kt"))
                                                    forecast.wind_gust_kt = getInteger(parser.getText());
                                                if (name.equals("wind_shear_hgt_ft_agl"))
                                                    forecast.wind_shear_hgt_ft_agl = getInteger(parser.getText());
                                                if (name.equals("wind_shear_dir_degrees"))
                                                    forecast.wind_shear_dir_degrees = getInteger(parser.getText());
                                                if (name.equals("wind_shear_speed_kt"))
                                                    forecast.wind_shear_speed_kt = getInteger(parser.getText());
                                                if (name.equals("vert_vis_ft"))
                                                    forecast.vert_vis_ft = getInteger(parser.getText());

                                            }
                                            // forecast data Fields ---------------
                                        }

                                        name = "";
                                    } catch (Exception ee) {
                                        Log.i(TAG, "Parse Taf Value XML Error: " + ee.getMessage());
                                    }
                                }
                            }
                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            name = null;
                            name = parser.getName();

                            if (name.equals("TAF")) taf = new Taf();
                            if(taf != null)
                                if (name.equals("forecast")) forecast = taf.getNewForecastClass();

                            if (!onlyRawData)
                                if(taf != null)
                                    if (name.equals("sky_condition")) {
                                        forecast.AddSkyCondition(parser.getAttributeValue(null, "cloud_base_ft_agl"),
                                                parser.getAttributeValue(null, "sky_cover"));
                                    }

                            break;
                        }
                        case XmlPullParser.END_TAG:
                        {
                            if (parser.getName().equals("TAF")) {
                                if (!onlyRawData) taf.caculateDistance(orgLocation);
                                tafs.add(taf);
                                publishProgress(tafs.size());
                                taf = null;
                            }

                            if (parser.getName().equals("forecast"))
                            {
                                taf.forecast.add(forecast);
                                forecast = null;
                            }

                            break;
                        }
                    }
                    eventType = parser.next();
                }
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Taf XML Parse error: " + ee.getMessage());
            }
        }

        private void processMetarXml(String Xml)
        {
            Log.i(TAG, "Metar XML: " + Xml);

            XmlPullParser parser = android.util.Xml.newPullParser();
            try {
                parser.setInput(new StringReader(Xml));
                int eventType = parser.getEventType();
                Metar metar = null;
                String name = null;

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.TEXT:
                        {
                            if (metar != null) {
                                if (name != null) {
                                    try {
                                        if (name.equals("raw_text")) metar.raw_text = parser.getText();
                                        if (name.equals("station_id"))metar.setStation_id(parser.getText());
                                        if (!onlyRawData) {

                                            //metar.station_id = parser.getText();
                                            if (name.equals("observation_time"))
                                                metar.observation_time = parser.getText();
                                            if (name.equals("latitude"))
                                                metar.latitude = getFloat(parser.getText());
                                            if (name.equals("longitude"))
                                                metar.longitude = getFloat(parser.getText());
                                            if (name.equals("temp_c"))
                                                metar.temp_c = getFloat(parser.getText());
                                            if (name.equals("dewpoint_c"))
                                                metar.dewpoint_c = getFloat(parser.getText());
                                            if (name.equals("wind_dir_degrees"))
                                                metar.wind_dir_degrees = getInteger(parser.getText());
                                            if (name.equals("wind_speed_kt"))
                                                metar.wind_speed_kt = getInteger(parser.getText());
                                            if (name.equals("wind_gust_kt"))
                                                metar.wind_gust_kt = getInteger(parser.getText());
                                            if (name.equals("visibility_statute_mi"))
                                                metar.visibility_statute_mi = getFloat(parser.getText());
                                            if (name.equals("altim_in_hg"))
                                                metar.altim_in_hg = getFloat(parser.getText());
                                            if (name.equals("elevation_m"))
                                                getFloat(parser.getText());
                                            if (name.equals("flight_category"))
                                                metar.flight_category = parser.getText();
                                            if (name.equals("metar_type"))
                                                metar.metar_type = parser.getText();
                                            if (name.equals("three_hr_pressure_tendency_mb"))
                                                metar.three_hr_pressure_tendency_mb = getFloat(parser.getText());
                                            if (name.equals("maxT_c"))
                                                metar.maxT_c = getFloat(parser.getText());
                                            if (name.equals("minT_c"))
                                                metar.minT_c = getFloat(parser.getText());
                                            if (name.equals("maxT24hr_c"))
                                                metar.maxT24hr_c = getFloat(parser.getText());
                                            if (name.equals("minT24hr_c"))
                                                metar.minT24hr_c = getFloat(parser.getText());
                                            if (name.equals("precip_in"))
                                                metar.precip_in = getFloat(parser.getText());
                                            if (name.equals("pcp3hr_in"))
                                                metar.pcp3hr_in = getFloat(parser.getText());
                                            if (name.equals("pcp6hr_in"))
                                                metar.pcp6hr_in = getFloat(parser.getText());
                                            if (name.equals("pcp24hr_in"))
                                                metar.pcp24hr_in = getFloat(parser.getText());
                                            if (name.equals("snow_in"))
                                                metar.snow_in = getFloat(parser.getText());
                                            if (name.equals("elevation_m"))
                                                metar.elevation_m = getFloat(parser.getText());
                                            if (name.equals("sea_level_pressure_mb"))
                                                metar.sea_level_pressure_mb = getFloat(parser.getText());
                                            if (name.equals("quality_control_flags"))
                                                metar.quality_control_flags = parser.getText();
                                            if (name.equals("wx_string"))
                                                metar.wx_string = parser.getText();
                                            if (name.equals("station_id"))
                                                metar.station_id = parser.getText();
                                            if (name.equals("vert_vis_ft"))
                                                metar.vert_vis_ft = getInteger(parser.getText());
                                        }
                                        name = "";
                                    }
                                    catch (Exception ee)
                                    {
                                        Log.i(TAG, "Parse Metar Value XML Error: " + ee.getMessage());
                                    }
                                }
                            }
                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            name = null;
                            name = parser.getName();
                            if (name.equals("METAR")) metar = new Metar();
                            if (!onlyRawData)
                                if(metar != null)
                                    if (name.equals("sky_condition")) {
                                        metar.AddSkyCondition(parser.getAttributeValue(null, "cloud_base_ft_agl"),
                                                parser.getAttributeValue(null, "sky_cover"));
                                    }
                            break;
                        }
                        case XmlPullParser.END_TAG:
                        {
                            if (parser.getName().equals("METAR")) {
                                if (!onlyRawData) metar.caculateDistance(orgLocation);
                                metars.add(metar);
                                publishProgress(metars.size());
                                metar = null;
                            }
                            break;
                        }

                    }

                    eventType = parser.next();
                }
                Log.i(TAG, "End XML Parsing : " + metars.size());
                int i=0;
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Metar XML Parse error: " + ee.getMessage());
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
        public void OnMetarsAvailable(ArrayList<Metar> metars);
        public void OnTafsAvailable(ArrayList<Taf> tafs);
    }

}

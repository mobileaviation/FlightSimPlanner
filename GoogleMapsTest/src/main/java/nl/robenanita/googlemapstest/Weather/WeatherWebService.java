package nl.robenanita.googlemapstest.Weather;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.robenanita.googlemapstest.AirportsInfoFragment;

/**
 * Created by Rob Verhoef on 2-5-2014.
 */
public class WeatherWebService {
    final static String TAG = "GooglemapsTest";
    final static String BaseUrl = "http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=#DATASOURCE#&requestType=retrieve&format=xml";

    public ArrayList<Metar> metars;
    public ArrayList<Taf> tafs;
    public ArrayList<Notam> notams;
    public ArrayList<Station> stations;

    private WeatherActivity weatherActivity;
    private AirportsInfoFragment airportsInfoFragment;
    private LatLng orgLocation;

    public enum WeatherType {
        metar,
        taf,
        openaviation_notam,
        vatme_notam,
        stations
    }

    public WeatherWebService(WeatherActivity weatherActivity) {
        this.weatherActivity = weatherActivity;
        metars = new ArrayList<Metar>();
        tafs = new ArrayList<Taf>();
        notams = new ArrayList<Notam>();
        stations = new ArrayList<Station>();
    }

    public WeatherWebService(AirportsInfoFragment airportsInfoFragment) {
        this.airportsInfoFragment = airportsInfoFragment;
        metars = new ArrayList<Metar>();
        tafs = new ArrayList<Taf>();
        notams = new ArrayList<Notam>();
        stations = new ArrayList<Station>();
    }

    public void GetNotamsByICAOs(List<String> Icaos)
    {
        // http://api.vateud.net/notams/EHLE,EHAM,EHTE.xml
        // http://info.vatme.net/notams/#ICAO#.xml
        notams = new ArrayList<Notam>();

        String command = "http://api.vateud.net/notams/#ICAO#.xml";

        String codes = "";
        for (String code : Icaos)
        {
            codes = codes + code + ",";
        }
        codes = codes.substring(0, codes.length()-1);
        command = command.replace("#ICAO#", codes);

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = WeatherType.vatme_notam;
        readWeatherXMLAsync.onlyRawData = true;

        Log.i(TAG, "Vatme Notam command: " + command);

        readWeatherXMLAsync.execute();
    }

    public void GetNotamByICAO(String IcaoCode) {
        metars = new ArrayList<Metar>();

        String command = "https://api.openaviationdata.com/v1/notam?icao=#ICAO#&key=Cr5jWc5oRDIomdWggEx0WiPG1cDofJSD";

        command = command.replace("#ICAO#", IcaoCode);

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = WeatherType.openaviation_notam;
        readWeatherXMLAsync.onlyRawData = true;
        readWeatherXMLAsync.Icao = IcaoCode;

        Log.i(TAG, "OpenAviation Notam command: " + command);

        readWeatherXMLAsync.execute();
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
        readWeatherXMLAsync.type = WeatherType.metar;
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
        readWeatherXMLAsync.type = WeatherType.taf;
        readWeatherXMLAsync.onlyRawData = true;

        Log.i(TAG, "Taf command: " + command);

        readWeatherXMLAsync.execute();
    }

    public void GetStationsByLocationRadius(LatLng location, Integer radius)
    {
        //http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=100;5.5,52.46
        String command = "http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=stations&requestType=retrieve&format=xml&radialDistance=#RAD#;#LON#,#LAT#";
        command = command.replace("#LON#", Double.toString(location.longitude));
        command = command.replace("#LAT#", Double.toString(location.latitude));
        command = command.replace("#RAD#", Integer.toString(radius));

        ReadWeatherXMLAsync readWeatherXMLAsync = new ReadWeatherXMLAsync();
        readWeatherXMLAsync.command = command;
        readWeatherXMLAsync.type = WeatherType.stations;
        readWeatherXMLAsync.onlyRawData = false;
        orgLocation = location;

        Log.i(TAG, "Metar command: " + command);

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
        readWeatherXMLAsync.type = WeatherType.metar;
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
        readWeatherXMLAsync.type = WeatherType.taf;
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
        public WeatherType type;
        public String Xml;
        public Boolean onlyRawData;

        @Override
        protected Void doInBackground(String... strings) {
            Xml = readFromUrl(command);

            if (type == WeatherType.metar)
            {
                processMetarXml(Xml);
            }
            if (type == WeatherType.taf)
            {
                processTafXml(Xml);
            }
            if (type == WeatherType.openaviation_notam)
            {
                processOpenAviationNotamJson(Xml);
            }
            if (type == WeatherType.vatme_notam)
            {
                processVatmeNotamsXml(Xml);
            }
            if (type == WeatherType.stations)
            {
                processStationsXml(Xml);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (type == WeatherType.metar) {
                Log.i(TAG, "Finished Reading metar XML");
                onDataAvailable.OnMetarsAvailable(metars);
                //if (weatherActivity!=null) weatherActivity.SetupMetarListView(metars);
                //if (airportsInfoFragment!=null) airportsInfoFragment.setupMetarsView(metars);
            }
            if (type == WeatherType.taf)
            {
                Log.i(TAG, "Finished Reading taf XML");
                onDataAvailable.OnTafsAvailable(tafs);
                //if (weatherActivity!=null) weatherActivity.SetupTafListView(tafs);
                //if (airportsInfoFragment!=null) airportsInfoFragment.setupTafsView(tafs);
            }
            if (type == WeatherType.openaviation_notam) {
                Log.i(TAG, "Finished Reading openaviation notam json");
                //weatherActivity.SetupTafListView(tafs);
            }
            if (type == WeatherType.vatme_notam) {
                Log.i(TAG, "Finished Reading vatme notams xml");
                onDataAvailable.OnNotamsAvailable(notams);
                //if (airportsInfoFragment!=null) airportsInfoFragment.setupNotamsView(notams);
            }
            if (type == WeatherType.stations) {
                Log.i(TAG, "Finished Reading stations xml");
                onDataAvailable.OnStationsAvailable(stations);
                //if (airportsInfoFragment!=null) airportsInfoFragment.setupStationsView(stations);
            }


            super.onPostExecute(aVoid);
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            if (type == WeatherType.metar)
                if (weatherActivity != null) weatherActivity.SetMetarProgress(values[0], "");
            if (type == WeatherType.taf)
                if (weatherActivity != null) weatherActivity.SetTafProgress(values[0], "");
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

        private void processOpenAviationNotamJson(String Json)
        {
            try {
                JSONObject jObj = new JSONObject(Json);
                JSONObject dataObj = jObj.getJSONObject("data");
                JSONObject IcaoObj = dataObj.getJSONObject(Icao);

                Iterator<String> k = IcaoObj.keys();
                while(k.hasNext())
                {
                    Notam notam = new Notam(weatherActivity);
                    String name = k.next();
                    Log.e(TAG, "Icao Object: " + name);
                    JSONObject o = IcaoObj.getJSONObject(name);
                    notam.raw_text = o.getString("text");
                    notam.setStation_id(Icao);
                    notam.SetNotamType(o.getString("type"));
                    notam.SetEndDate(o.getString("end"));
                    notam.SetStartDate(o.getString("start"));
                    notam.SetQualifier(o.getString("qualifier"));
                    notam.source = o.getString("source");
                    notams.add(notam);
                }

            } catch (JSONException e) {
                Log.e(TAG, "Notam Json read Error: " + e.toString());
            }
        }

        private void processVatmeNotamsXml(String Xml)
        {
            XmlPullParser parser = android.util.Xml.newPullParser();
            try {
                parser.setInput(new StringReader(Xml));
                int eventType = parser.getEventType();
                Notam notam = null;
                String name = null;

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.TEXT: {
                            if (notam != null) {
                                if (name != null) {
                                    try {

                                        if (name.equals("raw")) notam.SetRawText(parser.getText());
                                        if (name.equals("message")) notam.SetMessage(parser.getText());
                                        if (name.equals("icao")) notam.setStation_id(parser.getText());

                                    } catch (Exception ee) {
                                        Log.e(TAG, "Parse Notam Value XML Error: " + ee.getMessage());
                                    }
                                    name = "";
                                }
                            }
                            break;
                        }
                        case XmlPullParser.START_TAG: {
                            name = parser.getName();
                            if (name != null)
                                if (name.equals("object")) notam = new Notam(null);

                            break;
                        }
                        case XmlPullParser.END_TAG:
                        {
                            if (parser.getName().equals("object")) {
                                notams.add(notam);
                                publishProgress(notams.size());
                                notam = null;
                            }
                            break;
                        }

                    }

                    eventType = parser.next();
                }
                Integer s = notams.size();
                Log.i(TAG, "Succesfully fetched " + s.toString() + " notams");
            }
            catch (Exception ee)
            {
                Log.e(TAG, "Notams XML Parse error: " + ee.getMessage());
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
        public void OnNotamsAvailable(ArrayList<Notam> notams);
        public void OnStationsAvailable(ArrayList<Station> stations);
    }

}

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

/**
 * Created by Rob Verhoef on 1-4-2015.
 */
public class NotamsWebService {
    public NotamsWebService()
    {
        notams = new ArrayList<Notam>();
    }



    final static String TAG = "GooglemapsTest";

    public ArrayList<Notam> notams;
    private LatLng orgLocation;

    public void GetNotamsByICAOs(List<String> Icaos)
    {
        // http://api.vateud.net/notams/EHLE,EHAM,EHTE.xml
        // http://info.vatme.net/notams/#ICAO#.xml
        String command = "http://api.vateud.net/notams/#ICAO#.xml";

        String codes = "";
        for (String code : Icaos)
        {
            codes = codes + code + ",";
        }
        codes = codes.substring(0, codes.length()-1);
        command = command.replace("#ICAO#", codes);

        ReadNotamsAsync readNotamsLAsync = new ReadNotamsAsync();
        readNotamsLAsync.command = command;
        readNotamsLAsync.type = Type.vatme_notam;
        readNotamsLAsync.onlyRawData = true;

        Log.i(TAG, "Vatme Notam command: " + command);

        readNotamsLAsync.execute();
    }

    public void GetNotamByICAO(String IcaoCode) {
        String command = "https://api.openaviationdata.com/v1/notam?icao=#ICAO#&key=Cr5jWc5oRDIomdWggEx0WiPG1cDofJSD";

        command = command.replace("#ICAO#", IcaoCode);

        ReadNotamsAsync readNotamsLAsync = new ReadNotamsAsync();
        readNotamsLAsync.command = command;
        readNotamsLAsync.type = Type.openaviation_notam;
        readNotamsLAsync.onlyRawData = true;
        readNotamsLAsync.Icao = IcaoCode;

        Log.i(TAG, "OpenAviation Notam command: " + command);

        readNotamsLAsync.execute();
    }

    public void GetNotamsFromFAAByICAO(String IcaoCode)
    {
        String command = "https://pilotweb.nas.faa.gov/PilotWeb/notamRetrievalByICAOAction.do?method=displayByICAOs&reportType=RAW&formatType=DOMESTIC&retrieveLocId=#ICAO#&actionType=notamRetrievalByICAOs";
        command = command.replace("#ICAO#", IcaoCode);

        ReadNotamsAsync readNotamsLAsync = new ReadNotamsAsync();
        readNotamsLAsync.command = command;
        readNotamsLAsync.type = Type.faa_notam;
        readNotamsLAsync.Icao = IcaoCode;

        Log.i(TAG, "FAA Notam command: " + command);

        readNotamsLAsync.execute();
    }

    private class ReadNotamsAsync extends AsyncTask<String, Integer, Void>
    {
        public String command;
        public String Icao;
        public Type type;
        public String Xml;
        public Boolean onlyRawData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (type == Type.openaviation_notam) {
                Log.i(TAG, "Finished Reading openaviation notam json");
                onDataAvailable.OnNotamsAvailable(notams);
            }
            if (type == Type.vatme_notam) {
                Log.i(TAG, "Finished Reading vatme notams xml");
                onDataAvailable.OnNotamsAvailable(notams);
            }
            if (type == Type.faa_notam) {
                Log.i(TAG, "Finished Reading Faa notams html");
                onDataAvailable.OnNotamsAvailable(notams);
            }

            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {
            Xml = readFromUrl(command);

            if (type == Type.openaviation_notam)
            {
                processOpenAviationNotamJson(Xml);
            }
            if (type == Type.vatme_notam)
            {
                processVatmeNotamsXml(Xml);
            }
            if (type == Type.faa_notam)
            {
                processFaaNotamXml(Xml);
            }

            return null;
        }

        private void processFaaNotamXml(String Html)
        {
            Integer index = 0;
            Integer i = 0;
            Integer j = 0;

            while(j>-1)
            {

                try {
                    i = Html.indexOf("<PRE>", index);
                    j = Html.indexOf("</PRE>", index);

                    Notam notam = new Notam();
                    notam.SetRawFAAText(Html.substring(i+5, j));
                    notam.setStation_id(Icao);
                    notams.add(notam);

                    index = j + 4;
                } catch (Exception e) {
                    e.printStackTrace();
                    j = -1;
                }

            }

            Log.i(TAG, "Found " + notams.size() + " raw notams");
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
                    Notam notam = new Notam();
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
                                if (name.equals("object")) notam = new Notam();

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
        public void OnNotamsAvailable(ArrayList<Notam> notams);
    }

}

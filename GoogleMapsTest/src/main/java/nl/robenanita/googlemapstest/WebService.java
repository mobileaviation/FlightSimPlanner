package nl.robenanita.googlemapstest;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rob Verhoef on 18-1-14.
 */
public class WebService {
    private static String NAMSPACE = "http://robenanita.nl/";
    private static String URL = "http://www.robenanita.nl/airnav/AirNavService.asmx";
    private static String SOAP_ACTION = "http://robenanita.nl/";
    final static String TAG = "GooglemapsTest";

    public static ArrayList<Country> GetCountries()
    {
        String Methode = "GetCountries";
        ArrayList<Country> countries = new ArrayList<Country>();

        SoapObject request = new SoapObject(NAMSPACE, Methode);
        SoapSerializationEnvelope envelope = GetEnvelope(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try
        {
            androidHttpTransport.call(SOAP_ACTION+Methode, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            for(int i=0; i<response.getPropertyCount(); i++)
            {
                SoapObject data = (SoapObject) response.getProperty(i);
                Country c = new Country();
                c.id = Integer.parseInt(data.getProperty("id").toString());
                c.code = data.getProperty("code").toString();
                c.continent = data.getProperty("continent").toString();
                c.keywords = data.getProperty("keywords").toString();
                c.name = data.getProperty("name").toString();
                c.wikipedia_link = data.getProperty("wikipedia_link").toString();

                countries.add(c);
            }
        }
        catch (Exception ee)
        {
            ee.printStackTrace();
        }
        return countries;
    }

    public static Map<Integer, Airport> GetAirportsByCountryCode(String Code){
        String Methode = "GetAirportByCountryCode";
        Map<Integer, Airport> airports = new HashMap<Integer, Airport>();

        SoapObject request = new SoapObject(NAMSPACE, Methode);
        request.addProperty(GetProperty("CountryCode", Code));
        SoapSerializationEnvelope envelope = GetEnvelope(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try
        {
            androidHttpTransport.call(SOAP_ACTION+Methode, envelope);
            SoapObject response = (SoapObject) envelope.getResponse();
            airports = new HashMap<Integer, Airport>();
            for(int i=0; i<response.getPropertyCount(); i++){
                SoapObject data = (SoapObject) response.getProperty(i);
                Airport mdata = new Airport();
                mdata.id = Integer.parseInt(data.getProperty("id").toString());
                mdata.name = data.getProperty("name").toString();
                mdata.latitude_deg = Double.parseDouble(data.getProperty("latitude_deg").toString());
                mdata.type = Airport.ParseAirportType(data.getProperty("type").toString());
                mdata.longitude_deg = Double.parseDouble(data.getProperty("longitude_deg").toString());
                mdata.elevation_ft = Double.parseDouble(data.getProperty("elevation_ft").toString());
                mdata.continent = data.getProperty("continent").toString();
                mdata.iso_country = data.getProperty("iso_country").toString();
                mdata.iso_region = data.getProperty("iso_region").toString();
                mdata.municipality = data.getProperty("municipality").toString();
                mdata.scheduled_service = data.getProperty("scheduled_service").toString();
                mdata.gps_code = data.getProperty("gps_code").toString();
                mdata.iata_code = data.getProperty("iata_code").toString();
                mdata.local_code = data.getProperty("local_code").toString();
                mdata.home_link = data.getProperty("home_link").toString();
                mdata.wikipedia_link = data.getProperty("wikipedia_link").toString();
                mdata.keywords = data.getProperty("keywords").toString();
                mdata.version = 1;
                mdata.modified = new Date(System.currentTimeMillis());

                airports.put(mdata.id, mdata);
            }
        }
        catch(Exception ee) {
            ee.printStackTrace();
        }

        return airports;
    }

    private static PropertyInfo GetProperty(String name, String value) {
        PropertyInfo PI = new PropertyInfo();
        PI.setName(name);
        PI.setValue(value);
        PI.setType(String.class);
        return PI;
    }

    private static SoapSerializationEnvelope GetEnvelope(SoapObject request){
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        return envelope;
    }

}

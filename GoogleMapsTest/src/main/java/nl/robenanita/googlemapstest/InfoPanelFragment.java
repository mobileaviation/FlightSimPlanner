package nl.robenanita.googlemapstest;


import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.flightplan.Leg;
import nl.robenanita.googlemapstest.flightplan.Waypoint;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class InfoPanelFragment extends Fragment {
    public InfoPanelFragment() {
        // Required empty public constructor
    }

    private String TAG = "GooglemapsTest";
    private Location curlocation;
    private View view;

    public void setLocation(Location location)
    {
        TextView latTxt = (TextView) view.findViewById(R.id.latitudeTxt);
        TextView lonTxt = (TextView) view.findViewById(R.id.longitudeTxt);
        TextView headingTxt = (TextView) view.findViewById(R.id.headingTxt);
        TextView gsTxt = (TextView) view.findViewById(R.id.groundspeedTxt);
        TextView altitudeTxt = (TextView) view.findViewById(R.id.infoAltitudeTxt);

        String latStr = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
        String lonStr = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);

        latTxt.setText(latStr);
        lonTxt.setText(lonStr);

        headingTxt.setText(
                String.format("%03d dg", Math.round(location.getBearing())));
        // Calculate m/s to knots
        gsTxt.setText(
                String.format("%d kt", Math.round(location.getSpeed()*1.9438444924574f)));
        // Calculate Meters to Feet
        altitudeTxt.setText(
                String.format("%04d ft", Math.round(location.getAltitude() * 3.2808399d)));

        curlocation = location;
    }

    public void setTrack(Track track)
    {
        TextView toTxt = (TextView) view.findViewById(R.id.toWaypointTxt);
        TextView distanceTxt = (TextView) view.findViewById(R.id.distanceToTxt);
        TextView hdgTxt = (TextView) view.findViewById(R.id.hdgToTxt);

        if (track != null) {
            toTxt.setText("To:" + track.getIdent());
            distanceTxt.setText("Dis:" + track.getDistanceNM().toString() + " NM");
            hdgTxt.setText("Hdg:" + track.getBearing().toString());
        } else
        {
            distanceTxt.setText("Dis:.....");
            hdgTxt.setText("Hdg:....");
        }
    }

    public void setActiveLeg(Leg activeLeg)
    {
        Waypoint m_to = activeLeg.getToWaypoint();

        String _to = m_to.name;
        String _dst = String.format("%.0f NM", activeLeg.getDistanceNM());
        String _trk = String.format("%03d", activeLeg.getTrueTrack());
        String _hdg = String.format("%03d", activeLeg.getCourseTo());
        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        String _eta = (m_to.eto == null) ? "NA" : ft.format(m_to.eto);
        Calendar c = new GregorianCalendar(2013,1,1);
        c.add(Calendar.SECOND, activeLeg.getSecondsToGo());
        ft = new SimpleDateFormat("HH:mm:ss");
        String _ete = ft.format(c.getTime());

        TextView toTxt = (TextView) view.findViewById(R.id.toWaypointTxt);
        TextView distanceTxt = (TextView) view.findViewById(R.id.distanceToTxt);
        TextView hdgTxt = (TextView) view.findViewById(R.id.hdgToTxt);

        toTxt.setText("To:" +_to);
        distanceTxt.setText("Dis:" + _dst + " NM");
        hdgTxt.setText("Hdg:" + _hdg);
    }

    public void LoadAdd()
    {
        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(view.getContext());
        propertiesDataSource.open(true);
        boolean ads = propertiesDataSource.checkNoAdvertisements();
        propertiesDataSource.close(true);


        if (!ads) {
            AdView MainAd = (AdView) view.findViewById(R.id.adMainNavigation);
            AdRequest leftRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("36A5D52DC49CF3A84F6BD03381312CE1")
                    .addTestDevice("70F82DF4BD716E85F87B34C81B78C5ED")
                    .build();
            MainAd.loadAd(leftRequest);
        }
        else
        {
            LinearLayout adsNavigationLayout = (LinearLayout) view.findViewById(R.id.adsNavigationLayout);
            adsNavigationLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        view = inflater.inflate(R.layout.fragment_info_panel, container, false);
        setTrack(null);

//        setupDirectToBtn();

        return view;
    }

//    private View.OnClickListener onDirectToBtnClicked = null;
//    public void setOnDirectToBtnClicked( final View.OnClickListener d) {onDirectToBtnClicked = d; }
}

package nl.robenanita.googlemapstest;

import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import nl.robenanita.googlemapstest.database.LocationTrackingDataSource;

/**
 * Created by Rob Verhoef on 11-8-2014.
 */
public class TrackItemAdapter extends BaseAdapter {
    private ArrayList<LocationTrackingDataSource.TrackPoint> trackPoints;
    private int evenColor = Color.TRANSPARENT;
    private int unevenColor = 0xffe0e0e0;
    private int selectedColor = Color.parseColor("#7ecce8");

    private static int selectedIndex = -1;
    public void setSelectedIndex(int selectedIndex) {
        TrackItemAdapter.selectedIndex = selectedIndex;
    }

    public TrackItemAdapter(ArrayList<LocationTrackingDataSource.TrackPoint> trackPoints)
    {
        this.trackPoints = trackPoints;
    }

    @Override
    public int getCount() {
        return trackPoints.size();
    }

    @Override
    public Object getItem(int i) {
        return trackPoints.get(i);
    }
    public Location getTrackpoint(int i)
    {
        LocationTrackingDataSource.TrackPoint t = trackPoints.get(i);
        Location l = new Location("trackPoint");
        l.setLongitude(t.longitude);
        l.setLatitude(t.latitude);
        l.setAltitude(t.altitude);
        l.setBearing(t.true_heading);
        l.setSpeed(t.ground_speed * 0.514444444f);
        l.setTime(t.time.getTime());
        return l;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.track_item, viewGroup, false);

        LinearLayout l = (LinearLayout) view.findViewById(R.id.track_item_layout);
        if (isEven(i))
            l.setBackgroundColor((i==selectedIndex) ? selectedColor : evenColor);
        else
            l.setBackgroundColor((i==selectedIndex) ? selectedColor : unevenColor);

        TextView trackItemTimeTxt = (TextView)view.findViewById(R.id.trackItemTimeTxt);
        TextView trackItemLatTxt = (TextView)view.findViewById(R.id.trackItemLatTxt);
        TextView trackItemLonTxt = (TextView)view.findViewById(R.id.trackItemLonTxt);
        TextView trackItemHeightTxt = (TextView)view.findViewById(R.id.trackItemHeightTxt);
        TextView trackItemSpeedTxt = (TextView)view.findViewById(R.id.trackItemSpeedTxt);

        CompassNeedle compassNeedle = (CompassNeedle)view.findViewById(R.id.compassArrowView);

        LocationTrackingDataSource.TrackPoint t = trackPoints.get(i);

        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
        trackItemTimeTxt.setText(hm.format(t.time));

        compassNeedle.setHeading(t.true_heading);

        trackItemLatTxt.setText(Location.convert(t.latitude, Location.FORMAT_MINUTES));
        trackItemLonTxt.setText(Location.convert(t.longitude, Location.FORMAT_MINUTES));

        trackItemSpeedTxt.setText(String.format("%dkt", Math.round(t.ground_speed)));
        trackItemHeightTxt.setText(String.format("%dft", Math.round(t.altitude)));

        return view;
    }

    boolean isEven(double num) { return ((num % 2) == 0) ; }
}

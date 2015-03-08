package nl.robenanita.googlemapstest;

import java.util.ArrayList;

/**
 * Created by Rob Verhoef on 22-9-2014.
 */
public class RunwaysList extends ArrayList<Runway> {

    public Double getLeft_deg()
    {
        Double l = this.get(0).he_longitude_deg;

        for (Runway r : this)
        {
            if (r.he_longitude_deg<l) l = r.he_longitude_deg;
            if (r.le_longitude_deg<l) l = r.le_longitude_deg;
        }

        return l;
    }
    public Double getCenterX_deg() {
        Double l = this.get(0).he_longitude_deg;
        double r = this.get(0).he_longitude_deg;

        for (Runway ru : this)
        {
            if (ru.he_longitude_deg<l) l = ru.he_longitude_deg;
            if (ru.le_longitude_deg<l) l = ru.le_longitude_deg;

            if (ru.he_longitude_deg>r) r = ru.he_longitude_deg;
            if (ru.le_longitude_deg>r) r = ru.le_longitude_deg;
        }

        return l + ((r - l)/2);
    }

    public Double getTop_deg()
    {
        Double l = this.get(0).he_latitude_deg;

        for (Runway r : this)
        {
            if (r.he_latitude_deg>l) l = r.he_latitude_deg;
            if (r.le_latitude_deg>l) l = r.le_latitude_deg;
        }

        return l;
    }
    public Double getCenterY_deg() {
        Double l = this.get(0).he_latitude_deg;
        double r = this.get(0).he_latitude_deg;

        for (Runway ru : this)
        {
            if (ru.he_latitude_deg<l) l = ru.he_latitude_deg;
            if (ru.le_latitude_deg<l) l = ru.le_latitude_deg;

            if (ru.he_latitude_deg>r) r = ru.he_latitude_deg;
            if (ru.le_latitude_deg>r) r = ru.le_latitude_deg;
        }

        return l + ((r - l)/2);
    }
}

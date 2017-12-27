package nl.robenanita.googlemapstest.Wms;

/**
 * Created by Rob Verhoef on 26-12-2017.
 */

public enum OfflineMapTypes {
    offline_openstreet,
    offline_aafsectional;

    @Override
    public String toString() {
        return super.toString();
    }

    public String toUrl(){
        switch (this)
        {
            case offline_openstreet: return "http://a.tile.openstreetmap.org/{zoom}/{x}/{y}.png";
            case offline_aafsectional: return "http://wms.chartbundle.com/tms/1.0.0/sec/{zoom}/{x}/{y}.png?origin=nw";
            default: return "";
        }
    }
}

package nl.robenanita.googlemapstest.Wms;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 26-12-2017.
 */

public enum OfflineMapTypes {
    offline_openstreet,
    offline_aafsectional,
    offline_germandsf;

    @Override
    public String toString() {
        return super.toString();
    }

    public String toUrl(){
        switch (this)
        {
            case offline_openstreet: return "http://a.tile.openstreetmap.org/{zoom}/{x}/{y}.png";
            case offline_aafsectional: return "http://wms.chartbundle.com/tms/1.0.0/sec/{zoom}/{x}/{y}.png?origin=nw";
            case offline_germandsf: return "https://secais.dfs.de/static-maps/icao500/tiles/{zoom}/{x}/{y}.png";
            default: return "";
        }
    }

    public Integer toButtonId(){
        switch (this)
        {
            case offline_openstreet: return R.id.offlineOpenstreetBtn;
            case offline_aafsectional: return R.id.offlineAafSectionalBtn;
            case offline_germandsf: return R.id.offlineGermanDSFBtn;
            default: return -1;
        }
    }

}

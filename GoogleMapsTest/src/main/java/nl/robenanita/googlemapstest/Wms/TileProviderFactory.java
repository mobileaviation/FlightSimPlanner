package nl.robenanita.googlemapstest.Wms;

import android.content.Context;

import com.google.android.gms.maps.model.TileProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by Rob Verhoef on 2-10-2014.
 */
public class TileProviderFactory {


    private static String TAG = "GooglemapsTest";

    // return a geoserver wms tile layer
    private static TileProvider getTileProvider(final TileProviderType tileProviderType,
                                                final String layer, final String style) {
        TileProvider tileProvider = new WMSTileProvider(256,256) {

            @Override
            public synchronized URL getTileUrl(int x, int y, int zoom) {
                double[] bbox = getBoundingBox(x, y, zoom);
                String s = "";

                if (tileProviderType == TileProviderType.aaf_chartbundle)
                    s = String.format(Locale.US, TileProviderFormats.CHARTBUNDLE_FORMAT, bbox[MINX],
                            bbox[MINY], bbox[MAXX], bbox[MAXY]);

                if (tileProviderType == TileProviderType.skylines)
                    s = String.format(Locale.US, TileProviderFormats.SKYLINES_FORMAT, bbox[MINX],
                            bbox[MINY], bbox[MAXX], bbox[MAXY]);

                if (tileProviderType == TileProviderType.canadaweather)
                    s = String.format(Locale.US, TileProviderFormats.CANADAWEATHER_FORMAT, bbox[MINX],
                            bbox[MINY], bbox[MAXX], bbox[MAXY]);

                URL url = null;
                try {
                    s = s.replace("#LAYER#", layer);
                    s = s.replace("#STYLE#", style);
                    //Log.i(TAG, "WeatherURL:" + s);
                    url = new URL(s);
                } catch (MalformedURLException e) {
                    throw new AssertionError(e);
                } return url;
            } };

        return tileProvider;
    }

    public static TileProvider getSkylinesProvider(TileProviderFormats.skylinesLayer layer, Context context)
    {
        WMSCachedTileProvider tileProvider = new WMSCachedTileProvider(TileProviderType.skylines, layer.toString(), "", context);
        return tileProvider;

        //return TileProviderFactory.getTileProvider(TileProviderType.skylines, layer.toString(), "");
    }

    public static TileProvider getFAAProvider(TileProviderFormats.chartBundleLayer layer, Context context)
    {
        //return TileProviderFactory.getTileProvider(TileProviderType.aaf_chartbundle, layer.toString(), "");
        WMSCachedTileProvider tileProvider = new WMSCachedTileProvider(TileProviderType.aaf_chartbundle, layer.toString(), "", context);
        return tileProvider;
    }

    public static TileProvider getCanadaWeatherProvider(TileProviderFormats.weathermapLayer layer,
                                                        TileProviderFormats.canadamapStyle style)
    {
        return TileProviderFactory.getTileProvider(TileProviderType.canadaweather, layer.toString(),
                style.toString());
    }

    public static TileProvider getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer layer,
                                                      int opacity)
    {
        TileProvider tileProvider = new OpenWeatherMapUrlTileProvider(TileProviderFormats.OPENWEATHERTILE_FORMAT,
                layer.toString(), opacity);
        return tileProvider;
    }
}

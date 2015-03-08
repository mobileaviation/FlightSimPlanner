package nl.robenanita.googlemapstest.Wms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rob Verhoef on 21-10-2014.
 */
public class WMSCachedTileProvider implements TileProvider {
    private TileProviderType tileProviderType;
    private static String TAG = "GooglemapsTest";
    private String layer;
    private String style;
    private Context context;

    final private Long cacheTimeOut = Long.valueOf(14 * 24); // in hours

    public WMSCachedTileProvider(TileProviderType tileProviderType,
                                 String layer, String style,
                                 Context context)
    {
        this.tileProviderType = tileProviderType;
        this.layer = layer;
        this.style = style;
        this.context = context;
    }

    @Override
    public Tile getTile(int x, int y, int zoom)
    {
        URL tileUrl = getTileUrl(x, y, zoom);


        // https://maps.skylines.aero/mapserver/?service=WMS&version=1.3.0&request=GetMap&layers=Airspace&transparent=true&bbox=313086.067936,6418264.392684,469629.101904,6574807.426652&width=256&height=256&crs=EPSG:3857&format=image/png&styles=
        String cachedFileName = tileProviderType.toString() + layer.toString() + Integer.toString(x) + "_"
                + Integer.toString(y) + "_"
                + Integer.toString(zoom) + ".png";

        //Log.i(TAG, "Cached Filename:" + cachedFileName);

        Tile tile = null;
        ByteArrayOutputStream stream = null;
        Bitmap image = null;
        Boolean cached = false;
        Boolean timedOut = false;

        try
        {

            File f = new File(context.getFilesDir().getPath() + "/" + cachedFileName);
            if (f.exists()) {
                //Log.i(TAG, "Loading from cache: " + f.getPath() + "/" +f.getName() + " Modified: " + Long.toString(f.lastModified()));
                Date today = new Date();
                long diff = TimeUnit.HOURS.convert(today.getTime() - f.lastModified(), TimeUnit.MILLISECONDS);

                image = BitmapFactory.decodeStream(context.openFileInput(cachedFileName));

                timedOut = (diff > cacheTimeOut);
                //Log.i(TAG, "HoursDif " + diff + " TimedOut: " + timedOut.toString());
                cached = true;
            }
            else
            {
                image = BitmapFactory.decodeStream(tileUrl.openConnection().getInputStream());
                //Log.i(TAG, "Loading from url: " + tileUrl.toString());
                cached = false;
            }

            stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);

            if (!cached)
            {
                FileOutputStream fos = context.openFileOutput(cachedFileName, Context.MODE_PRIVATE);
                fos.write(stream.toByteArray());
                fos.close();
            }

            byte[] byteArray = stream.toByteArray();

            tile = new Tile(256, 256, byteArray);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(stream != null)
            {
                try
                {
                    stream.close();
//                    Todo: Only delete when connected to the Internet
                    if (timedOut) context.deleteFile(cachedFileName);
                }
                catch(IOException e) {}
            }
        }

        return tile;
    }

    private URL getTileUrl(int x, int y, int zoom)
    {
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
        try
        {
            s = s.replace("#LAYER#", layer);
            s = s.replace("#STYLE#", style);
            //Log.i(TAG, s);
            url = new URL(s);
        }
            catch (MalformedURLException e)
        {
                throw new AssertionError(e);
        }
        return url;
    }

    // Web Mercator n/w corner of the map.
    private static final double[] TILE_ORIGIN = {-20037508.34789244, 20037508.34789244};
    //array indexes for that data
    private static final int ORIG_X = 0;
    private static final int ORIG_Y = 1; // "

    // Size of square world map in meters, using WebMerc projection.
    private static final double MAP_SIZE = 20037508.34789244 * 2;

    // array indexes for array to hold bounding boxes.
    protected static final int MINX = 0;
    protected static final int MAXX = 1;
    protected static final int MINY = 2;
    protected static final int MAXY = 3;

    // Return a web Mercator bounding box given tile x/y indexes and a zoom
    // level.
    protected double[] getBoundingBox(int x, int y, int zoom) {
        double tileSize = MAP_SIZE / Math.pow(2, zoom);
        double minx = TILE_ORIGIN[ORIG_X] + x * tileSize;
        double maxx = TILE_ORIGIN[ORIG_X] + (x + 1) * tileSize;
        double miny = TILE_ORIGIN[ORIG_Y] - (y + 1) * tileSize;
        double maxy = TILE_ORIGIN[ORIG_Y] - y * tileSize;
        double[] bbox = new double[4];
        bbox[MINX] = minx;
        bbox[MINY] = miny;
        bbox[MAXX] = maxx;
        bbox[MAXY] = maxy;
        return bbox;
    }

}


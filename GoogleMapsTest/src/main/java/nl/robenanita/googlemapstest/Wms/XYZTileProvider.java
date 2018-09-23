package nl.robenanita.googlemapstest.Wms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rob Verhoef on 14-10-2014.
 */
public class XYZTileProvider implements TileProvider {
    private String url;
    private Paint opacityPaint = new Paint();
    private static String TAG = "GooglemapsTest";
    private TileProviderType tileProviderType;
    private String layer;
    private String style;
    private Context context;

    final private Long cacheTimeOut = Long.valueOf(14 * 24); // in hours


    /**
     * This constructor assumes the <code>url</code> parameter contains three placeholders for the x- and y-positions of
     * the tile as well as the zoom level of the tile. The placeholders are assumed to be <code>{x}</code>,
     * <code>{y}</code>, and <code>{zoom}</code>. An example
     * for an OpenWeatherMap URL would be: http://tile.openweathermap.org/map/precipitation/{zoom}/{x}/{y}.png
     *
     * @param url The tile server's endpoint URL, from which to retrieve {@link Tile} images
     */
    public XYZTileProvider(TileProviderType tileProviderType, String url, String layer, int opacity , Context context)
    {

        this.url = url.replace("#LAYER#", layer);
        setOpacity(opacity);
        this.tileProviderType = tileProviderType;
        this.layer = layer;
        this.style = style;
        this.context = context;
    }

    /**
     * Sets the desired opacity of map {@link Tile}s, as a percentage where 0% is invisible and 100% is completely opaque.
     * @param opacity The desired opacity of map {@link Tile}s (as a percentage between 0 and 100, inclusive)
     */
    public void setOpacity(int opacity)
    {
        int alpha = (int)Math.round(opacity * 2.55);    // 2.55 = 255 * 0.01
        opacityPaint.setAlpha(alpha);
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
//                InputStream content = null;
//                HttpGet httpGet = new HttpGet(url);
//                HttpClient httpclient = new DefaultHttpClient();
//                // Execute HTTP Get Request
//                HttpResponse response = httpclient.execute(httpGet);
//                content = response.getEntity().getContent();
//                image = BitmapFactory.decodeStream(content);

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

    /**
     * Helper method that returns the {@link URL} of the tile image for the given x/y/zoom location.
     *
     * <p>This method assumes the URL string provided in the constructor contains three placeholders for the x-
     * and y-positions as well as the zoom level of the desired tile; <code>{x}</code>, <code>{y}</code>, and
     * <code>{zoom}</code>. An example for an OpenWeatherMap URL would be:
     * http://tile.openweathermap.org/map/precipitation/{zoom}/{x}/{y}.png</p>
     *
     * @param x The x-position of the tile
     * @param y The y-position of the tile
     * @param zoom The zoom level of the tile
     *
     * @return The {@link URL} of the desired tile image
     */
    private URL getTileUrl(int x, int y, int zoom)
    {
        return TileProviderFormats.getTileUrl(url, x, y, zoom);

//        String tileUrl = url
//                .replace("{x}", Integer.toString(x))
//                .replace("{y}", Integer.toString(y))
//                .replace("{zoom}", Integer.toString(zoom));
//
//        try
//        {
//            return new URL(tileUrl);
//        }
//        catch(MalformedURLException e)
//        {
//            throw new AssertionError(e);
//        }
    }

    /**
     * Helper method that adjusts the given {@link Bitmap}'s opacity to the opacity previously set via
     * {@link #setOpacity(int)}. Stolen from Elysium's comment at StackOverflow.
     *
     * @param bitmap The {@link Bitmap} whose opacity to adjust
     * @return A new {@link Bitmap} with an adjusted opacity
     *
     //* @see http://stackoverflow.com/questions/14322236/making-tileoverlays-transparent#comment19934097_14329560
     */
    private Bitmap adjustOpacity(Bitmap bitmap)
    {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);

        return adjustedBitmap;
    }
}

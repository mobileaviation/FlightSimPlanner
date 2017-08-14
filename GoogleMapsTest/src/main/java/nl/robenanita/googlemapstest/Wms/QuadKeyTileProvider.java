package nl.robenanita.googlemapstest.Wms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rob Verhoef on 14-7-2017.
 */

public class QuadKeyTileProvider implements TileProvider {
    private String url;
    private Paint opacityPaint = new Paint();
    private static String TAG = "GooglemapsTest";
    private String manifest;
    private Context context;
    private String layer;

    final private Long cacheTimeOut = Long.valueOf(14 * 24); // in hours

    private class readManifest extends AsyncTask<String, Integer, Void>
    {
        public String Url;
        public String Xml;

        @Override
        protected Void doInBackground(String... strings) {
            URL _manifestUrl;
            try {
                _manifestUrl = new URL(Url);
                InputStream inputStream = _manifestUrl.openConnection().getInputStream();
                Xml = readFromInputStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public QuadKeyTileProvider(String url, String manifestUrl, TileProviderFormats.airportLayer layer,
                               int opacity, Context context  )
    {
        // set boundary
        // download manifest

//        readManifest readManifest = new readManifest();
//        readManifest.Url = manifestUrl;
//        readManifest.execute();

        this.url = url.replace("#LAYER#", layer.toString());
        setOpacity(opacity);

        this.context = context;
        this.layer = layer.toString();
    }

    public QuadKeyTileProvider(String url, String layer, int opacity, Context context)
    {
        this.url = url;
        this.context = context;
        this.layer = layer;
        setOpacity(opacity);
    }

    private String readFromInputStream(InputStream inputStream)
    {
        if(inputStream != null)
        {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }
        else
        {
            return "";
        }
    }

    public void setOpacity(int opacity)
    {
        int alpha = (int)Math.round(opacity * 2.55);    // 2.55 = 255 * 0.01
        opacityPaint.setAlpha(alpha);
    }

    private String TileToQuadKey (int x, int y, int zoom){
        String quad = "";
        for (int i = zoom; i > 0; i--){
            int mask = 1 << (i - 1);
            int cell = 0;
            if ((x & mask) != 0)
                cell++;
            if ((y & mask) != 0)
                cell += 2;
            quad += cell;
        }
        return quad;
    }

    private URL getTileUrl(String quad)
    {
        String tileUrl = url.replace("#QUADKEY#", quad );

        try
        {
            return new URL(tileUrl);
        }
        catch(MalformedURLException e)
        {
            throw new AssertionError(e);
        }
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        String quad = TileToQuadKey(x,y,zoom);
        URL tileUrl = getTileUrl(quad);

        String cachedFileName = "Airport_"+layer.toString() + "_" + quad + ".png";

        //Log.i(TAG, "Airport URL: " + tileUrl.toString());
        // http://tile.openweathermap.org/map/clouds/6/33/20.png

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
            //e.printStackTrace();
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

    private Bitmap adjustOpacity(Bitmap bitmap)
    {
        Bitmap adjustedBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(adjustedBitmap);
        canvas.drawBitmap(bitmap, 0, 0, opacityPaint);

        return adjustedBitmap;
    }
}

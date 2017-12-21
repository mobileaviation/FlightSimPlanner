package nl.robenanita.googlemapstest.Wms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Rob Verhoef on 21-12-2017.
 */

public class XYZOfflineTileProvider implements TileProvider {
    public XYZOfflineTileProvider(TileProviderType tileProviderType,
                                 Context context)
    {
        this.tileProviderType = tileProviderType;
        this.context = context;
    }

    private TileProviderType tileProviderType;
    private Context context;

    @Override
    public Tile getTile(int x, int y, int zoom) {
        String fileName = tileProviderType.toString() + Integer.toString(x) + "_"
                + Integer.toString(y) + "_"
                + Integer.toString(zoom) + ".png";

        Tile tile = null;
        ByteArrayOutputStream stream = null;
        Bitmap image = null;

        try
        {
            File f = new File(context.getFilesDir().getPath() + "/" + fileName);
            if (f.exists()) {
                image = BitmapFactory.decodeStream(context.openFileInput(fileName));
                stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                tile = new Tile(256, 256, byteArray);
            }
        }
        catch (IOException e)
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
                }
                catch(IOException e) {}
            }
        }

        return tile;
    }
}

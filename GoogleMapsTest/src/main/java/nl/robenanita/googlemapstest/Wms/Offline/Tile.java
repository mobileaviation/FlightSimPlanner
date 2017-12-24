package nl.robenanita.googlemapstest.Wms.Offline;

import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Rob Verhoef on 23-11-2016.
 */

public class Tile {
    public int x;
    public int y;
    public int z;
    public Coordinate tile;

    private String TAG = "TileDownload";

    public Tile()
    {

    }
    public Tile(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tile = new Coordinate(this.x, this.y, this.z);
    }

    public void getTileNumber(final double lat, final double lon, final int zoom)
    {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) );
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI)  / 2 * (1<<zoom));

        if (xtile < 0) xtile = 0;
        if (xtile >= (1<<zoom)) xtile = ((1<<zoom) -1 );
        if (ytile < 0) ytile = 0;
        if (ytile >= (1<<zoom)) ytile = ((1<<zoom) - 1 );

        this.x = xtile;
        this.y = ytile;
        this.z = zoom;

        this.tile = new Coordinate(this.x, this.y, this.z);
    }

    public String Url()
    {
        return "http://tile.openstreetmap.org/" + z + "/" + x + "/" + y + ".png";
    }

    public void DownloadFile(String localPath, String baseName)
    {
        try {
            String f = localPath + "/" + baseName + "-" + z + "-" + x + "-" + y + ".png";
            Log.i(TAG, "Download from: " + Url() + " to: " + f );
            FileUtils.copyURLToFile(new URL(Url()), new File(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

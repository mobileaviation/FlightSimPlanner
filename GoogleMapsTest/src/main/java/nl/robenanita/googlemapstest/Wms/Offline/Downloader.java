package nl.robenanita.googlemapstest.Wms.Offline;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * Created by Rob Verhoef on 23-11-2016.
 */

public class Downloader extends AsyncTask {
    public Downloader(Context context, Geometry buffer, String LocalPath, String Url, String Mapname)
    {
        this.buffer = buffer;
        this.context = context;
        localPath = LocalPath;
        url = Url;
        mapname = Mapname;
    }

    private Geometry buffer;
    private Context context;
    private String localPath;
    private String mapname;
    private String url;
    private String TAG = "Downloader";

    @Override
    protected Object doInBackground(Object[] params) {
        Tile tt = new Tile();

        BoundingBox bb = new BoundingBox();

        Coordinate[] testCoords = buffer.getEnvelope().getCoordinates();


        int tileCount = 0;
        for (int z = 6; z <= 13; z++) {
            tt.getTileNumber(testCoords[1].y, testCoords[0].x, z);
            int xBegin = tt.x;
            int yBegin = tt.y;
            tt.getTileNumber(testCoords[3].y, testCoords[2].x, z);
            int xEnd = tt.x;
            int yEnd = tt.y;

            for (int x = xBegin; x<=xEnd; x++)
            {
                for (int y = yBegin; y<=yEnd; y++)
                {
                    bb.tile2boundingBox(x,y,z);
                    if (buffer.intersects(bb.bbbox))
                    {
                        tileCount++;
                    }
                }

            }

        }

        int downloadTileCount = 0;
        for (int z = 6; z <= 13; z++) {
            tt.getTileNumber(testCoords[1].y, testCoords[0].x, z);
            int xBegin = tt.x;
            int yBegin = tt.y;
            tt.getTileNumber(testCoords[3].y, testCoords[2].x, z);
            int xEnd = tt.x;
            int yEnd = tt.y;

            for (int x = xBegin; x<=xEnd; x++) {
                for (int y = yBegin; y <= yEnd; y++) {
                    bb.tile2boundingBox(x, y, z);
                    if (buffer.intersects(bb.bbbox)) {
                        Tile tile = new Tile(x, y, z);

                        float percentage = ((float) downloadTileCount / (float) tileCount) * 100;

                        Log.i(TAG, tile.Url() + "    : Percentage: " + Math.round(percentage));
                        publishProgress(Math.round(percentage));

                        tile.DownloadFile(localPath, mapname);
                        downloadTileCount++;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        Integer p = (Integer)values[0];
        onDownloadProgress.OnProgress(p);
        super.onProgressUpdate(values);
    }

    private OnDownloadProgress onDownloadProgress;
    public void SetOnDownloadProgress(OnDownloadProgress d) { onDownloadProgress = d; }
    public interface OnDownloadProgress
    {
        public void OnProgress(Integer progress);
    }
}

package nl.robenanita.googlemapstest.Wms.Offline;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Created by Rob Verhoef on 23-11-2016.
 */

public class BoundingBox {
    public double north;
    public double south;
    public double east;
    public double west;

    public Geometry bbbox;

    public void tile2boundingBox(final int x, final int y, final int z)
    {
        Coordinate[] coordinates = new Coordinate[5];
        this.north = tile2lat(y, z);
        this.south = tile2lat(y + 1, z);
        this.west = tile2lon(x, z);
        this.east = tile2lon(x + 1, z);

        coordinates[0] = new Coordinate(this.west, this.north);
        coordinates[1] = new Coordinate(this.east, this.north);
        coordinates[2] = new Coordinate(this.south, this.east);
        coordinates[3] = new Coordinate(this.west, this.south);
        coordinates[4] = new Coordinate(this.west, this.north);

        bbbox = new GeometryFactory().createPolygon(coordinates);
    }

    private double tile2lon(int x, int z)
    {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    private double tile2lat(int y, int z)
    {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

}

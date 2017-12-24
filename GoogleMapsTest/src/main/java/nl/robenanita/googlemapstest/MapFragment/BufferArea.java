package nl.robenanita.googlemapstest.MapFragment;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import nl.robenanita.googlemapstest.Property;

/**
 * Created by Rob Verhoef on 24-12-2017.
 */

public class BufferArea {
    public BufferArea(Context context)
    {

    }

    private Geometry buffer;
    private Polygon bufferPolygon;
    private PolygonOptions bufferPolygonOptions;

    public void CreateBuffer(Coordinate[] coordinates, Property bufferProperty)
    {
        Geometry g = new GeometryFactory().createLineString(coordinates);
        BufferOp bufOp = new BufferOp(g);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));
    }

    public void DrawBuffer(GoogleMap map)
    {
        if (bufferPolygon != null) bufferPolygon.remove();
        bufferPolygonOptions = new PolygonOptions();
        bufferPolygonOptions.fillColor(Color.RED);
        bufferPolygonOptions.strokeWidth(1);

        for (Coordinate c: buffer.getCoordinates()) {
            bufferPolygonOptions.add(new LatLng(c.y, c.x));
        }

        map.addPolygon(bufferPolygonOptions);
    }
}

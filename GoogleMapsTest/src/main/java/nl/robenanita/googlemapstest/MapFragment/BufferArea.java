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
        this.context = context;
    }

    private Geometry buffer;
    public Geometry GetBuffer() { return buffer; }

    private Polygon bufferPolygon;
    private PolygonOptions bufferPolygonOptions;
    private Property bufferProperty;
    private Context context;

    public void CreateBuffer(LatLng pos, Property bufferProperty)
    {
        this.bufferProperty = bufferProperty;
        Geometry g1 = new GeometryFactory().createPoint(new Coordinate(pos.longitude,
                pos.latitude));
        BufferOp bufOp = new BufferOp(g1);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));
    }

    public void CreateBuffer(Coordinate[] coordinates, Property bufferProperty)
    {
        this.bufferProperty = bufferProperty;
        Geometry g = new GeometryFactory().createLineString(coordinates);
        BufferOp bufOp = new BufferOp(g);
        bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
        buffer = bufOp.getResultGeometry(Double.parseDouble(bufferProperty.value1));
    }

    public void CreateBuffer(Coordinate[] coordinates)
    {
        CreateBuffer(coordinates, this.bufferProperty);
    }

    public void DrawBuffer(GoogleMap map)
    {
        if (bufferPolygon != null) bufferPolygon.remove();

        bufferPolygonOptions = new PolygonOptions();
        bufferPolygonOptions.fillColor(Color.alpha(255));
        bufferPolygonOptions.strokeColor(Color.RED);
        bufferPolygonOptions.strokeWidth(2);
        bufferPolygonOptions.zIndex(1050);

        for (Coordinate c: buffer.getCoordinates()) {
            bufferPolygonOptions.add(new LatLng(c.y, c.x));
        }

        if (bufferProperty.value2.equals("true"))
            bufferPolygon = map.addPolygon(bufferPolygonOptions);
    }
}

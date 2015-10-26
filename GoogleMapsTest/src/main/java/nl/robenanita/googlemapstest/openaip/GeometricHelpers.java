package nl.robenanita.googlemapstest.openaip;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.util.GeometricShapeFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import nl.robenanita.googlemapstest.Helpers;

/**
 * Created by Rob Verhoef on 11-10-2015.
 */
public class GeometricHelpers {
    public static Geometry drawFullArc(LatLng start, LatLng end, LatLng center)
    {
        // Get Location class from LatLng Class
        Location _center = Helpers.getLocation(center);
        Location _begin = Helpers.getLocation(start);
        Location _end = Helpers.getLocation(end);

        // Get the distance to begin or end point. (distance in meters)
        // Multiply by 2 for diameter
        Float distance = _center.distanceTo(_end) * 2;
        // Calculate Lateral and longitudinal degrees from the distance in meters
        Double latTraveledDeg = (1 / 110.54) * (distance / 1000);
        Double longTraveledDeg = (1 / (111.320 * Math.cos(Math.toRadians(center.latitude)))) * (distance/1000);

        // find the first angle to the begin point
        // The 0 degree line for the arc is horizontal and the first point is left
        // So recalculate the angles
        // angles should all be positive

        Float arcBegin = _center.bearingTo(_begin);
        if (arcBegin<0) arcBegin = 360 - (arcBegin + 360); else arcBegin = 360 - arcBegin;
        Float arcEnd = _center.bearingTo(_end);
        if (arcEnd<0) arcEnd = 360 - (arcEnd + 360); else arcEnd = 360 - arcEnd;
        arcBegin = arcBegin + 90;
        arcEnd = arcEnd + 90;
        Float arcSize = arcBegin - arcEnd;
        // if the size is positive this is an counterclockwise arc (positive)
        // if the size is negative this is an clockwise arc

        GeometricShapeFactory geometricShapeFactory = new GeometricShapeFactory();
        geometricShapeFactory.setCentre(new Coordinate(center.longitude, center.latitude));
        geometricShapeFactory.setHeight(latTraveledDeg);
        geometricShapeFactory.setWidth(longTraveledDeg);
        geometricShapeFactory.setNumPoints(15);

        // because the arc is drawn counter clockwise the arcEnd is actually the startpoint

        if (arcSize>0)
            return geometricShapeFactory.createArcPolygon(Math.toRadians(arcEnd) , Math.toRadians(arcSize));
        else
            return geometricShapeFactory.createArcPolygon(Math.toRadians(arcBegin) , Math.toRadians(arcSize * -1));
    }

    public static ArrayList<Coordinate> drawCircle(LatLng center, Double radius)
    {
        // Get the distance to begin or end point. (distance in meters)
        // Multiply by 2 for diameter
        Double distance = (radius * 1853) * 2;
        // Calculate Lateral and longitudinal degrees from the distance in meters
        Double latTraveledDeg = (1 / 110.54) * (distance / 1000);
        Double longTraveledDeg = (1 / (111.320 * Math.cos(Math.toRadians(center.latitude)))) * (distance/1000);

        GeometricShapeFactory geometricShapeFactory = new GeometricShapeFactory();
        geometricShapeFactory.setCentre(new Coordinate(center.longitude, center.latitude));
        geometricShapeFactory.setHeight(latTraveledDeg);
        geometricShapeFactory.setWidth(longTraveledDeg);
        geometricShapeFactory.setNumPoints(15);

        com.vividsolutions.jts.geom.Polygon coordinates;
        coordinates = geometricShapeFactory.createEllipse();

        ArrayList<Coordinate> list = new ArrayList<Coordinate>(Arrays.asList(coordinates.getCoordinates()));
        return list;
    }

    public static ArrayList<Coordinate> drawArc(LatLng start, LatLng end, LatLng center, Boolean positive)
    {
        // Get Location class from LatLng Class
        Location _center = Helpers.getLocation(center);
        Location _begin = Helpers.getLocation(start);
        Location _end = Helpers.getLocation(end);

        // Get the distance to begin or end point. (distance in meters)
        // Multiply by 2 for diameter
        Float distance = _center.distanceTo(_end) * 2;
        // Calculate Lateral and longitudinal degrees from the distance in meters
        Double latTraveledDeg = (1 / 110.54) * (distance / 1000);
        Double longTraveledDeg = (1 / (111.320 * Math.cos(Math.toRadians(center.latitude)))) * (distance/1000);

        // find the first angle to the begin point
        // The 0 degree line for the arc is horizontal and the first point is left
        // So recalculate the angles
        // angles should all be positive

        Float arcBegin = _center.bearingTo(_begin) - 90;
        Float arcEnd = _center.bearingTo(_end) - 90;
        Float arcSize =  arcBegin - arcEnd;
        if (arcSize>0) arcSize = arcSize - 360;
        if (arcBegin<0) arcBegin = 360 - (arcBegin + 360); else arcBegin = 360 - arcBegin;
        if (arcEnd<0) arcEnd = 360 - (arcEnd + 360); else arcEnd = 360 - arcEnd;


        // if the size is positive this is an counterclockwise arc (positive)
        // if the size is negative this is an clockwise arc

        GeometricShapeFactory geometricShapeFactory = new GeometricShapeFactory();
        geometricShapeFactory.setCentre(new Coordinate(center.longitude, center.latitude));
        geometricShapeFactory.setHeight(latTraveledDeg);
        geometricShapeFactory.setWidth(longTraveledDeg);
        geometricShapeFactory.setNumPoints(15);

        // because the arc is drawn counter clockwise the arcEnd is actually the startpoint
        Coordinate[] coordinates;
        if (arcSize>0)
            coordinates =  geometricShapeFactory.createArc(Math.toRadians(arcBegin) , Math.toRadians(arcSize)).getCoordinates();
        else
            coordinates = geometricShapeFactory.createArc(Math.toRadians(arcEnd), Math.toRadians(arcSize * -1)).getCoordinates();

        ArrayList<Coordinate> list = new ArrayList<Coordinate>(Arrays.asList(coordinates));
        if (positive) Collections.reverse(list);
        return list;
    }
}

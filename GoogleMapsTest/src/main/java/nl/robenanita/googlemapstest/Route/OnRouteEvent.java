package nl.robenanita.googlemapstest.Route;

/**
 * Created by Rob Verhoef on 1-8-2017.
 */

public interface OnRouteEvent {
    public void onVariationClicked(Waypoint waypoint, Route flightPlan);
    public void onDeviationClicked(Waypoint waypoint, Route flightPlan);
    public void onTakeoffClicked(Waypoint waypoint, Route flightPlan);
    public void onAtoClicked(Waypoint waypoint, Route flightPlan);
    public void onDeleteClickedClicked(Waypoint waypoint, Route flightPlan);
    public void onClosePlanClicked(Route flightPlan);
    public void onWaypointClicked(Waypoint waypoint);
    public void onWaypointMoved(Route flightPlan);
}

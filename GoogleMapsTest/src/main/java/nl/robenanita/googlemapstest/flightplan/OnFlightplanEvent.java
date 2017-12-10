package nl.robenanita.googlemapstest.flightplan;

/**
 * Created by Rob Verhoef on 1-8-2017.
 */

public interface OnFlightplanEvent {
    public void onVariationClicked(Waypoint waypoint, FlightPlan flightPlan);
    public void onDeviationClicked(Waypoint waypoint, FlightPlan flightPlan);
    public void onTakeoffClicked(Waypoint waypoint, FlightPlan flightPlan);
    public void onAtoClicked(Waypoint waypoint, FlightPlan flightPlan);
    public void onDeleteClickedClicked(Waypoint waypoint, FlightPlan flightPlan);
    public void onClosePlanClicked(FlightPlan flightPlan);
    public void onWaypointClicked(Waypoint waypoint);
    public void onWaypointMoved(FlightPlan flightPlan);
}

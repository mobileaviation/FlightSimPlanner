package nl.robenanita.googlemapstest.Settings.LayersSetup;

import nl.robenanita.googlemapstest.MBTiles.MBTile;

public interface ChartEvent {
    public void OnStartDownload(MBTile tile);
    public void OnChecked(Boolean checked, MBTile tile);
}

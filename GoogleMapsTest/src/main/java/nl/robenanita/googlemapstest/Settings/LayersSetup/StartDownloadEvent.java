package nl.robenanita.googlemapstest.Settings.LayersSetup;

import nl.robenanita.googlemapstest.MBTiles.MBTile;

public interface StartDownloadEvent {
    public void OnStartDownload(MBTile tile);
}

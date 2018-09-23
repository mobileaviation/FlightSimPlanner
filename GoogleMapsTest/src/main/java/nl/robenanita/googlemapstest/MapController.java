package nl.robenanita.googlemapstest;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import nl.robenanita.googlemapstest.Charts.AirportChart;
import nl.robenanita.googlemapstest.MBTiles.MBTile;
import nl.robenanita.googlemapstest.Settings.LayersSetup.MapStyle;
import nl.robenanita.googlemapstest.Wms.MapBoxOfflineTileProvider;
import nl.robenanita.googlemapstest.Wms.OfflineMapTypes;
import nl.robenanita.googlemapstest.Wms.TileProviderFactory;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;
import nl.robenanita.googlemapstest.database.ChartBundleProperties;
import nl.robenanita.googlemapstest.database.DBFilesHelper;
import nl.robenanita.googlemapstest.database.MBTilesLocalDataSource;
import nl.robenanita.googlemapstest.database.MapTypeProperties;
import nl.robenanita.googlemapstest.database.PropertiesDataSource;
import nl.robenanita.googlemapstest.database.WeatherProperties;

/**
 * Created by Rob Verhoef on 18-10-2014.
 */
public class MapController
{
    public MapController(GoogleMap map, Context context)
    {
        this.map = map;
        this.context = context;
        mbTilesIndex = 100;
        mbTileProviderOverlays = new HashMap<>();
    }

    private GoogleMap map;
    private Context context;
    private String TAG = "GooglemapsTest";
    private Integer mbTilesIndex;

    private TileOverlay chartBundleSecTileOverlay;
    private TileOverlay chartBundleWacTileOverlay;
    private TileOverlay chartBundleTacTileOverlay;
    private TileOverlay chartBundleEnrHTileOverlay;
    private TileOverlay chartBundleEnrLTileOverlay;
    private TileOverlay chartBundleEnrATileOverlay;
    private ChartBundleProperties chartBundleProperties;
    private WeatherProperties weatherProperties;
    private MapTypeProperties mapTypeProperties;

    public TileProviderFormats.chartBundleLayer getSelectedChartBundle()
    {
        return chartBundleProperties.GetSelected();
    }
    public boolean getChartBundleEnabled()
    {
        return chartBundleProperties.getEnabled();
    }

    public boolean getVisibleWeatherChart(TileProviderFormats.weathermapLayer layer)
    {
        return weatherProperties.GetValue(layer);
    }
    public Integer getGoogleMapType()
    {
        if (mapTypeProperties != null)
            return mapTypeProperties.GetSelected();
        else return 1;
    }

    private TileOverlay skylinesOverlay;            // Airspaces

    private TileOverlay openWeathermapCloudsOverlay;
    private TileOverlay openWeathermapPertOverlay;
    private TileOverlay openWeathermapPressOverlay;

    private TileOverlay canadaWeatherWindOverlay;
    private TileOverlay canadaWeatherPressOverlay;
    private TileOverlay canadaWeatherUSRadarOverlay;

    private TileOverlay airportTestOverlay;

    private TileOverlay offlineOverlay;

    public void setUpTileProvider()
    {
        TileProvider tp1 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.sec_4326, context);
        chartBundleSecTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp1));
        chartBundleSecTileOverlay.setZIndex(90);
        TileProvider tp2 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.wac_4326, context);
        chartBundleWacTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp2));
        chartBundleWacTileOverlay.setZIndex(90);
        TileProvider tp3 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.tac_4326, context);
        chartBundleTacTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp3));
        chartBundleTacTileOverlay.setZIndex(90);
        TileProvider tp4 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enrh_4326, context);
        chartBundleEnrHTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp4));
        chartBundleEnrHTileOverlay.setZIndex(90);
        TileProvider tp5 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enrl_4326, context);
        chartBundleEnrLTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp5));
        chartBundleEnrLTileOverlay.setZIndex(90);
        TileProvider tp6 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enra_4326, context);
        chartBundleEnrATileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp6));
        chartBundleEnrATileOverlay.setZIndex(90);

        chartBundleProperties = new ChartBundleProperties();
        chartBundleProperties.ClearProperties();
        chartBundleProperties.LoadFromDatabase(context);

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        Property airspacesProperty = propertiesDataSource.getMapSetup("AIRSPACES");
        Property offlineProperty = propertiesDataSource.getMapSetup("OFFLINEMAPS");
        propertiesDataSource.close(true);
        boolean airspacesVisible = (airspacesProperty!=null) ? Boolean.valueOf(airspacesProperty.value2) : true;
        if (offlineProperty==null) offlineProperty = Property.NewProperty("OFFLINEMAPS", "offline_openstreet", "false");

        ShowOfflineMap(Boolean.valueOf(offlineProperty.value2), OfflineMapTypes.valueOf(offlineProperty.value1));

        TileProvider SkylinesTileProvider = TileProviderFactory.getSkylinesProvider(TileProviderFormats.skylinesLayer.Airspace, context);
        skylinesOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(SkylinesTileProvider));
        skylinesOverlay.setZIndex(105);

        TileProvider tp7 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.precipitation, 50, context);
        openWeathermapPertOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp7));
        openWeathermapPertOverlay.setZIndex(110);
        TileProvider tp8 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.clouds, 50, context);
        openWeathermapCloudsOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp8));
        openWeathermapCloudsOverlay.setZIndex(110);
        TileProvider tp9 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.pressure_cntr, 80, context);
        openWeathermapPressOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp9));
        openWeathermapPressOverlay.setZIndex(110);
        TileProvider tp10 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.ETA_UU, TileProviderFormats.canadamapStyle.WINDARROW);
        canadaWeatherWindOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp10));
        canadaWeatherWindOverlay.setZIndex(110);
        TileProvider tp11 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.ETA_PN, TileProviderFormats.canadamapStyle.PRESSURE4_LINE);
        canadaWeatherPressOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp11));
        canadaWeatherPressOverlay.setZIndex(110);
        TileProvider tp12 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.RADAR_RDBR, TileProviderFormats.canadamapStyle.RADAR);
        canadaWeatherUSRadarOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp12));
        canadaWeatherUSRadarOverlay.setZIndex(110);



        weatherProperties = new WeatherProperties();
        weatherProperties.ClearProperties();
        weatherProperties.LoadFromDatabase(context);

        setWeatherMap();

        SetChartBundle();

        skylinesOverlay.setVisible(airspacesVisible);

        setupMBTiles();
    }

    private class MBTilesOverlay
    {
        public MBTilesOverlay(String mbTilesFile)
        {
            Provider = new MapBoxOfflineTileProvider(new File(mbTilesFile));
        }

        public TileOverlay Overlay;
        public Integer Index;
        public MapBoxOfflineTileProvider Provider;

        public void Remove()
        {
            Overlay.remove();
            Provider.close();
        }
    }

    private void setupMBTiles()
    {
        MBTilesLocalDataSource mbTilesLocalDataSource = new MBTilesLocalDataSource(context);
        mbTilesLocalDataSource.open();
        ArrayList<MBTile> tiles = mbTilesLocalDataSource.getVisibleTiles();
        mbTilesLocalDataSource.close();

        for (MBTile tile : tiles)
        {
            if (tile.getLocalFilename()!= null) {
                String localFilename = tile.getLocalFilename();
                if (new File(localFilename).exists()) {
                    MBTilesOverlay tileOverlay = setupMBTilesMap(localFilename);
                    tileOverlay.Index = tile.visible_order;
                    mbTilesIndex = tile.visible_order + 1;
                    mbTileProviderOverlays.put(localFilename, tileOverlay);
                }
            }
        }
    }

    private MBTilesOverlay setupMBTilesMap(String mbTilesFile)
    {
        TileOverlayOptions opts = new TileOverlayOptions();
        MBTilesOverlay mbTilesOverlay = new MBTilesOverlay(mbTilesFile);
        opts.tileProvider(mbTilesOverlay.Provider);
        mbTilesOverlay.Overlay = map.addTileOverlay(opts);
        mbTilesOverlay.Overlay.setZIndex(90);
        return mbTilesOverlay;
    }

    private Map<String, MBTilesOverlay> mbTileProviderOverlays;
    public void SetupMBTileMap(MBTile map, Boolean checked)
    {
        String localFilename = map.getLocalFilename();
        if (mbTileProviderOverlays.containsKey(localFilename))
        {
            MBTilesOverlay tileOverlay = mbTileProviderOverlays.get(localFilename);
            if (!checked)
            {
                tileOverlay.Remove();
                map.visible_order = -1;
                map.InsertUpdateDB();
                mbTileProviderOverlays.remove(localFilename);
            }
        }
        else
        {
            if (checked) {
                MBTilesOverlay tileOverlay = setupMBTilesMap(localFilename);
                tileOverlay.Index = mbTilesIndex;
                map.visible_order = mbTilesIndex;
                map.InsertUpdateDB();
                mbTilesIndex++;
                mbTileProviderOverlays.put(localFilename, tileOverlay);
            }
        }
    }


    private TileOverlayOptions chartoverlayOptions;
    public void SetAirportChart(AirportChart airportChart)
    {
        TileProvider tp13 = TileProviderFactory.getTileAirportChartProvider(airportChart, 100, context);

        if (airportTestOverlay == null)
        {
            chartoverlayOptions = new TileOverlayOptions().tileProvider(tp13);
            airportTestOverlay = map.addTileOverlay(chartoverlayOptions);
            airportTestOverlay.setZIndex(120);
            airportTestOverlay.setVisible(true);
        }
        else
        {
            airportTestOverlay.remove();
            chartoverlayOptions = new TileOverlayOptions().tileProvider(tp13);
            airportTestOverlay = map.addTileOverlay(chartoverlayOptions);
            airportTestOverlay.setZIndex(120);
            airportTestOverlay.setVisible(true);
        }
    }

    private void SetChartBundle()
    {
        if (chartBundleProperties.getEnabled()) {
            chartBundleSecTileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.sec_4326));
            chartBundleWacTileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.wac_4326));
            chartBundleTacTileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.tac_4326));
            chartBundleEnrHTileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.enrh_4326));
            chartBundleEnrLTileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.enrl_4326));
            chartBundleEnrATileOverlay.setVisible(chartBundleProperties.GetValue(TileProviderFormats.chartBundleLayer.enra_4326));
        }
        else
        {
            chartBundleSecTileOverlay.setVisible(false);
            chartBundleWacTileOverlay.setVisible(false);
            chartBundleTacTileOverlay.setVisible(false);
            chartBundleEnrHTileOverlay.setVisible(false);
            chartBundleEnrLTileOverlay.setVisible(false);
            chartBundleEnrATileOverlay.setVisible(false);
        }
    }

    public void setBaseMapType(int mapType)
    {
        mapTypeProperties = new MapTypeProperties();
        mapTypeProperties.ClearProperties();
        mapTypeProperties.LoadFromDatabase(context);
        int t = mapTypeProperties.GetSelected();
        if (t<1000)
        {
            map.setMapStyle(null);
            map.setMapType(t);
        } else
        {
            // Set a userdefined style
            map.setMapType(MapStyle.MAP_TYPE_NORMAL);
            MapStyleOptions style;
            style = MapStyleOptions.loadRawResourceStyle(context, R.raw.icao_style2);
            map.setMapStyle(style);
        }
    }

    public void setMapType(int mapType)
    {
        mapTypeProperties.ClearProperties();
        mapTypeProperties.SetValue(mapType, true);

        if (mapType<1000) {
            map.setMapStyle(null);
            map.setMapType(mapType);
        } else
        {
            // Set a userdefined style
            map.setMapType(MapStyle.MAP_TYPE_NORMAL);
            MapStyleOptions style;
            style = MapStyleOptions.loadRawResourceStyle(context, R.raw.icao_style2);
            map.setMapStyle(style);
        }
        mapTypeProperties.SaveToDatabase(context);
    }

    public void ShowWeatherMap(Boolean show, TileProviderFormats.weathermapLayer layer)
    {
        weatherProperties.SetValue(layer, show);

        setWeatherMap();

        weatherProperties.SaveToDatabase(context);
    }

    private void setWeatherMap()
    {
        canadaWeatherPressOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.ETA_PN));
        canadaWeatherWindOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.ETA_UU));
        canadaWeatherUSRadarOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.RADAR_RDBR));
        openWeathermapCloudsOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.clouds));
        openWeathermapPressOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.pressure_cntr));
        openWeathermapPertOverlay.setVisible(weatherProperties.GetValue(TileProviderFormats.weathermapLayer.precipitation));
    }

    public void ShowUSMap(TileProviderFormats.chartBundleLayer layer, Boolean enabled){
        chartBundleProperties.setEnabled(enabled);

        if (enabled)
        {
            boolean v = false;
            v = (layer== TileProviderFormats.chartBundleLayer.sec_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.sec_4326, v);
            v = (layer== TileProviderFormats.chartBundleLayer.wac_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.wac_4326, v);
            v = (layer== TileProviderFormats.chartBundleLayer.tac_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.tac_4326, v);
            v = (layer== TileProviderFormats.chartBundleLayer.enrh_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.enrh_4326, v);
            v = (layer== TileProviderFormats.chartBundleLayer.enrl_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.enrl_4326, v);
            v = (layer== TileProviderFormats.chartBundleLayer.enra_4326);
            chartBundleProperties.SetValue(TileProviderFormats.chartBundleLayer.enra_4326, v);

        }

        chartBundleProperties.SaveToDatabase(context);
        SetChartBundle();

    }

    public void ShowSkylinesMap(Boolean enabled)
    {
        skylinesOverlay.setVisible(enabled);
    }

    public void ShowOfflineMap(Boolean enabled, OfflineMapTypes offlineMapTypes) {
        if (offlineOverlay!=null)
        {
            offlineOverlay.remove();
            offlineOverlay = null;
        }

        TileProvider offlineTileProvider =
                TileProviderFactory.getTileOfflineProvider(context, offlineMapTypes);
        offlineOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(offlineTileProvider));
        offlineOverlay.setZIndex(100);
        offlineOverlay.setVisible(enabled);
    }
}

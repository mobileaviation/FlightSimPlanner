package nl.robenanita.googlemapstest;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import nl.robenanita.googlemapstest.Wms.TileProviderFactory;
import nl.robenanita.googlemapstest.Wms.TileProviderFormats;
import nl.robenanita.googlemapstest.database.ChartBundleProperties;
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
    }

    private GoogleMap map;
    private Context context;
    private String TAG = "GooglemapsTest";

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
        return mapTypeProperties.GetSelected();
    }

    private TileOverlay skylinesOverlay;            // Airspaces

    private TileOverlay openWeathermapCloudsOverlay;
    private TileOverlay openWeathermapPertOverlay;
    private TileOverlay openWeathermapPressOverlay;

    private TileOverlay canadaWeatherWindOverlay;
    private TileOverlay canadaWeatherPressOverlay;
    private TileOverlay canadaWeatherUSRadarOverlay;

    public void setUpTileProvider()
    {
        TileProvider tp1 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.sec_4326, context);
        chartBundleSecTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp1));
        TileProvider tp2 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.wac_4326, context);
        chartBundleWacTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp2));
        TileProvider tp3 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.tac_4326, context);
        chartBundleTacTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp3));
        TileProvider tp4 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enrh_4326, context);
        chartBundleEnrHTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp4));
        TileProvider tp5 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enrl_4326, context);
        chartBundleEnrLTileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp5));
        TileProvider tp6 = TileProviderFactory.getFAAProvider(TileProviderFormats.chartBundleLayer.enra_4326, context);
        chartBundleEnrATileOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp6));

        chartBundleProperties = new ChartBundleProperties();
        chartBundleProperties.ClearProperties();
        chartBundleProperties.LoadFromDatabase(context);

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(context);
        propertiesDataSource.open(true);
        Property p = propertiesDataSource.getMapSetup("AIRSPACES");
        propertiesDataSource.close(true);
        boolean airspacesVisible = (p!=null) ? Boolean.valueOf(p.value2) : true;

        TileProvider SkylinesTileProvider = TileProviderFactory.getSkylinesProvider(TileProviderFormats.skylinesLayer.Airspace, context);
        skylinesOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(SkylinesTileProvider));

        TileProvider tp7 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.precipitation, 50);
        openWeathermapPertOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp7));
        TileProvider tp8 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.clouds, 50);
        openWeathermapCloudsOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp8));
        TileProvider tp9 =
                TileProviderFactory.getTileOpenWeatherMapProvider(TileProviderFormats.weathermapLayer.pressure_cntr, 80);
        openWeathermapPressOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp9));

        TileProvider tp10 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.ETA_UU, TileProviderFormats.canadamapStyle.WINDARROW);
        canadaWeatherWindOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp10));
        TileProvider tp11 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.ETA_PN, TileProviderFormats.canadamapStyle.PRESSURE4_LINE);
        canadaWeatherPressOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp11));
        TileProvider tp12 =
                TileProviderFactory.getCanadaWeatherProvider(TileProviderFormats.weathermapLayer.RADAR_RDBR, TileProviderFormats.canadamapStyle.RADAR);
        canadaWeatherUSRadarOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(tp12));

        weatherProperties = new WeatherProperties();
        weatherProperties.ClearProperties();
        weatherProperties.LoadFromDatabase(context);

        setWeatherMap();

        SetChartBundle();

        skylinesOverlay.setVisible(airspacesVisible);
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
        map.setMapType(mapTypeProperties.GetSelected());
    }

    public void setMapType(int mapType)
    {
        mapTypeProperties.ClearProperties();
        mapTypeProperties.SetValue(mapType, true);
        map.setMapType(mapType);
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


            //SetChartBundle();

        }

        chartBundleProperties.SaveToDatabase(context);
        SetChartBundle();

    }

    public void ShowSkylinesMap(Boolean enabled)
    {
        skylinesOverlay.setVisible(enabled);
    }

}

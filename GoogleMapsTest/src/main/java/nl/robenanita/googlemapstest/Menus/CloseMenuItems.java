package nl.robenanita.googlemapstest.Menus;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Rob Verhoef on 21-2-2018.
 */

public class CloseMenuItems {
    public static ArrayList<MenuItemType> Items()
    {
        return new ArrayList<>(Arrays.asList(
                MenuItemType.directTo,
                MenuItemType.routeActivate,
//                MenuItemType.connectDisconnect,
//                MenuItemType.tracking,
                MenuItemType.routeCreate,
                MenuItemType.maptype,
                MenuItemType.search,
                MenuItemType.loadTrack,
                MenuItemType.settings,
                MenuItemType.loadCharts,
                MenuItemType.isNew
//                MenuItemType.airspacesTracking,
//                MenuItemType.appLocking
        ));
    }
}

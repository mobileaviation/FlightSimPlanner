package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by Rob Verhoef on 6-2-2018.
 */

public class FSPMenuDrawer {
    public FSPMenuDrawer()
    {

    }

    private Activity activity;
    private Drawer drawer;

    public Drawer getMenuDrawer(Activity activity)
    {
        this.activity = activity;


        drawer = new DrawerBuilder()
                .withActivity(this.activity)
                .withActionBarDrawerToggle(false)
                .withActionBarDrawerToggleAnimated(true)
                .withSliderBackgroundColor(Color.DKGRAY)
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(createheader(activity))
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        MenuItemType item = (MenuItemType) drawerItem.getTag();
                        if (item != null)
                        {
                            if (onMenuTtemClicked != null) onMenuTtemClicked.MenuItemClicked(item, drawerItem);
                        }
                        //drawer.closeDrawer();
                        return true;
                    }
                })
                .build();
        createMenuItems();
        return drawer;
    }

    private AccountHeader createheader(Activity activity)
    {
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header1)
                .build();
        return headerResult;
    }

    public void SetTrackingItemIcon(boolean active)
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(10);
        item.withIcon((active) ? R.drawable.trackactive : R.drawable.trackinactive);
        drawer.removeItem(10);
        drawer.addItemAtPosition(item, 4);
    }

    public void SetConnectDisConnectIcon(boolean connected)
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(9);
        item.withIcon((connected) ? R.drawable.connected : R.drawable.disconnected);
        drawer.removeItem(9);
        drawer.addItemAtPosition(item, 3);
    }

    private void createMenuItems()
    {
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(6).withName(R.string.action_directTo)
                .withIcon(R.drawable.dirtecttobtn)
                .withTag(MenuItemType.directTo)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(8).withName(R.string.action_flightplan_activate)
                .withIcon(R.drawable.flightplanactivate)
                .withTag(MenuItemType.routeActivate)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(9).withName(R.string.action_connect_disconnect)
                .withIcon(R.drawable.disconnected)
                .withTag(MenuItemType.connectDisconnect)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(10).withName(R.string.action_tracking_active)
                .withIcon(R.drawable.trackactive)
                .withTag(MenuItemType.tracking)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(7).withName(R.string.action_flightplan)
                .withIcon(R.drawable.flightplanbutton)
                .withTag(MenuItemType.routeCreate)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new DividerDrawerItem());

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(11).withName(R.string.action_maptype)
                .withIcon(R.drawable.maptype)
                .withTextColor(Color.LTGRAY)
                .withTag(MenuItemType.maptype)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(5).withName(R.string.action_searchairports)
                .withIcon(R.drawable.aerodromesearch)
                .withTextColor(Color.LTGRAY)
                .withTag(MenuItemType.search)
                .withSelectable(false));
        drawer.addItem(new DividerDrawerItem());

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.action_loadtrack)
                .withTextColor(Color.LTGRAY)
                .withTag(MenuItemType.loadTrack)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(2)
                .withName(R.string.action_settings)
                .withTag(MenuItemType.settings)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(3)
                .withName(R.string.action_load_test_chart)
                .withTag(MenuItemType.loadCharts)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(4)
                .withName(R.string.action_isnew)
                .withTag(MenuItemType.isNew)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));

        drawer.addItem(new DividerDrawerItem());
    }

    public enum MenuItemType
    {
        directTo,
        routeActivate,
        connectDisconnect,
        tracking,
        routeCreate,
        maptype,
        search,
        loadTrack,
        settings,
        loadCharts,
        isNew
    }

    public void setOnMenuTtemClicked(OnMenuTtemClicked menuTtemClicked)
    {
        this.onMenuTtemClicked = menuTtemClicked;
    }
    private OnMenuTtemClicked onMenuTtemClicked;
    public interface OnMenuTtemClicked
    {
        public void MenuItemClicked(MenuItemType menuItemType, IDrawerItem drawerItem);
    }

}

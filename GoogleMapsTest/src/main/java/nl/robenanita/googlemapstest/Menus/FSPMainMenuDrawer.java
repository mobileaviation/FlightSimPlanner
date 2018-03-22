package nl.robenanita.googlemapstest.Menus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialize.util.UIUtils;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 6-2-2018.
 */

public class FSPMainMenuDrawer {
    public FSPMainMenuDrawer()
    {

    }

    private Activity activity;
    private Drawer drawer;
    private MiniDrawer miniDrawer;
    private Crossfader crossfader;

    public Drawer getDrawer() {return drawer;}

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
                            if (onMenuTtemClicked != null) onMenuTtemClicked.MenuItemClicked(item, drawerItem,
                                    CloseMenuItems.Items().contains(item));
                        }
                        //drawer.closeDrawer();
                        return true;
                    }
                })
                .build();


        createMenuItems();

        return drawer;
    }

    public void CloseMenu()
    {
        drawer.closeDrawer();
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
        item.withName((active) ? R.string.action_tracking_locked : R.string.action_tracking_free);
        drawer.removeItem(10);
        drawer.addItemAtPosition(item, 4);
    }

    public void SetConnectDisConnectIcon(boolean connected)
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(9);
        item.withIcon((connected) ? R.drawable.connected : R.drawable.disconnected);
        item.withName((connected) ? R.string.action_connected : R.string.action_disconnected);
        item.withEnabled(true);
        drawer.removeItem(9);
        drawer.addItemAtPosition(item, 3);
    }

    public void SetConnectingIcon()
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(9);
        item.withIcon(R.drawable.connecting);
        item.withName(R.string.action_connecting);
        item.withDisabledTextColor(Color.LTGRAY);
        item.withEnabled(false);
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
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(9).withName(R.string.action_disconnected)
                .withIcon(R.drawable.disconnected)
                .withTag(MenuItemType.connectDisconnect)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(10).withName(R.string.action_tracking_locked)
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
    }

    public void setOnMenuTtemClicked(OnMenuTtemClicked menuTtemClicked)
    {
        this.onMenuTtemClicked = menuTtemClicked;
    }
    private OnMenuTtemClicked onMenuTtemClicked;
    public interface OnMenuTtemClicked
    {
        public void MenuItemClicked(MenuItemType menuItemType, IDrawerItem drawerItem, Boolean closeMenu);
    }

}




package nl.robenanita.googlemapstest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

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
                .build();
        createMenuItems();
        return drawer;
    }

    private void createMenuItems()
    {
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(6).withName(R.string.action_directTo)
                .withIcon(R.drawable.dirtecttobtn)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(8).withName(R.string.action_flightplan_activate)
                .withIcon(R.drawable.flightplanactivate)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(9).withName(R.string.action_connect_disconnect)
                .withIcon(R.drawable.disconnected)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(10).withName(R.string.action_tracking_active)
                .withIcon(R.drawable.trackactive)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(7).withName(R.string.action_flightplan)
                .withIcon(R.drawable.flightplanbutton)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new DividerDrawerItem());

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(11).withName(R.string.action_maptype)
                .withIcon(R.drawable.maptype)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(5).withName(R.string.action_searchairports)
                .withIcon(R.drawable.aerodromesearch)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new DividerDrawerItem());

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.action_loadtrack)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(2)
                .withName(R.string.action_settings)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(3)
                .withName(R.string.action_load_test_chart)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(4)
                .withName(R.string.action_isnew)
                .withTextColor(Color.LTGRAY));
        drawer.addItem(new DividerDrawerItem());
    }

}

package nl.robenanita.googlemapstest.Menus;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 15-2-2018.
 */

public class FSPSecondairyMenuDrawer {

    private Activity activity;
    private Drawer drawer;

    public Drawer addSecondairyMenuDrawer(FSPMainMenuDrawer drawer, Activity activity)
    {
        this.activity = activity;


        this.drawer = new DrawerBuilder()
                .withActivity(this.activity)
                .withActionBarDrawerToggle(false)
                .withActionBarDrawerToggleAnimated(true)
                .withSliderBackgroundColor(Color.DKGRAY)
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(createheader(activity))
                .withDrawerGravity(Gravity.END)
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
                .append(drawer.getDrawer());
        createMenuItems();
        return drawer.getDrawer();
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

    public void SetAirspaceItemIcon(boolean active)
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(20);
        item.withIcon((active) ? R.drawable.airspace_locked : R.drawable.airspace_unlocked);
        item.withName((active) ? R.string.action_airspace_check_active : R.string.action_airspace_check_disabled);
        drawer.removeItem(20);
        drawer.addItemAtPosition(item, 1);
    }

    public void SetApplockedIcon(boolean locked)
    {
        PrimaryDrawerItem item = (PrimaryDrawerItem)drawer.getDrawerItem(21);
        item.withIcon((locked) ? R.drawable.locked : R.drawable.unlocked);
        item.withName((locked) ? R.string.action_app_locked : R.string.action_app_free);
        drawer.removeItem(21);
        drawer.addItemAtPosition(item, 2);
    }

    private void createMenuItems() {

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(20).withName(R.string.action_airspace_check_active)
                .withTag(MenuItemType.airspacesTracking)
                .withTextColor(Color.LTGRAY)
                .withIcon(R.drawable.airspace_locked)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(21).withName(R.string.action_app_free)
                .withTag(MenuItemType.appLocking)
                .withIcon(R.drawable.unlocked)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));

        drawer.addItem(new DividerDrawerItem());

        drawer.addItem(new PrimaryDrawerItem().withIdentifier(22)
                .withName(R.string.action_loadtrack)
                .withTextColor(Color.LTGRAY)
                .withTag(MenuItemType.loadTrack)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(23)
                .withName(R.string.action_settings)
                .withTag(MenuItemType.settings)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(24)
                .withName(R.string.action_load_test_chart)
                .withTag(MenuItemType.loadCharts)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
        drawer.addItem(new PrimaryDrawerItem().withIdentifier(25)
                .withName(R.string.action_isnew)
                .withTag(MenuItemType.isNew)
                .withTextColor(Color.LTGRAY)
                .withSelectable(false));
    }

    public void setOnMenuTtemClicked(FSPMainMenuDrawer.OnMenuTtemClicked menuTtemClicked)
    {
        this.onMenuTtemClicked = menuTtemClicked;
    }
    private FSPMainMenuDrawer.OnMenuTtemClicked onMenuTtemClicked;
    public interface OnMenuTtemClicked
    {
        public void MenuItemClicked(MenuItemType menuItemType, IDrawerItem drawerItem, Boolean closeMenu);
    }
}

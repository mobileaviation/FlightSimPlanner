package nl.robenanita.googlemapstest.Menus;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

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
                .withDisplayBelowStatusBar(false)
                .withDrawerGravity(Gravity.END)
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
                .append(drawer.getDrawer());
        return drawer.getDrawer();
    }

    public void setOnMenuTtemClicked(FSPMainMenuDrawer.OnMenuTtemClicked menuTtemClicked)
    {
        this.onMenuTtemClicked = menuTtemClicked;
    }
    private FSPMainMenuDrawer.OnMenuTtemClicked onMenuTtemClicked;
    public interface OnMenuTtemClicked
    {
        public void MenuItemClicked(MenuItemType menuItemType, IDrawerItem drawerItem);
    }
}

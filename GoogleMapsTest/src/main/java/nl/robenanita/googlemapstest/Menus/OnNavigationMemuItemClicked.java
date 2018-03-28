package nl.robenanita.googlemapstest.Menus;

import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.view.View;

/**
 * Created by Rob Verhoef on 24-3-2018.
 */

public interface OnNavigationMemuItemClicked {
    public Boolean OnMenuItemClicked(View button, MenuItemType itemType);
}

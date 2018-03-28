package nl.robenanita.googlemapstest.Menus;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.mikepenz.crossfader.util.UIUtils;

import nl.robenanita.googlemapstest.R;


public class NavigationButtonFragment extends Fragment {


    public NavigationButtonFragment() {
        // Required empty public constructor
    }

    public static NavigationButtonFragment newInstance(String param1, String param2) {
        NavigationButtonFragment fragment = new NavigationButtonFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuVisible = true;
        if (getArguments() != null) {

        }
    }

    private View.OnClickListener onButtonClick;
    private OnNavigationMemuItemClicked onNavigationMemuItemClicked;
    public void SetOnButtonClicked(OnNavigationMemuItemClicked onNavigationMemuItemClicked)
    {
        this.onNavigationMemuItemClicked = onNavigationMemuItemClicked;
    }

    private Boolean menuVisible;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_navigation_button, container, false);

        setupOpenCloseMenuButton();
        setupButtons();

        return view;
    }

    private void setupOpenCloseMenuButton()
    {
        final ImageButton openclose_btn = (ImageButton)view.findViewById(R.id.openCloseMenuBtn);
        final View layout = view.findViewById(R.id.button_layout);
        final ValueAnimator closeslideAnimator = ValueAnimator
                .ofInt(50, 0)
                .setDuration(500);
        final ValueAnimator openslideAnimator = ValueAnimator
                .ofInt(0, 50)
                .setDuration(500);
        openclose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimatorSet set = new AnimatorSet();
                set.play(menuVisible ? closeslideAnimator : openslideAnimator);
                set.setInterpolator(new AccelerateDecelerateInterpolator());
                set.start();
                openclose_btn.setBackground((Drawable)getResources().
                        getDrawable(menuVisible ? R.drawable.open_menu : R.drawable.close_menu));

                menuVisible = !menuVisible;

            }
        });

        closeslideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                layout.getLayoutParams().width = Math.round(UIUtils.convertDpToPixel(value.intValue(),getActivity()));
                layout.requestLayout();
            }
        });

        openslideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                layout.getLayoutParams().width = Math.round(UIUtils.convertDpToPixel(value.intValue(),getActivity()));
                layout.requestLayout();
            }
        });
   }


   private ImageButton dirtect_toBtn;
    private ImageButton flightplan_activateBtn;
    private ImageButton connect_disconnectBtn;
    private ImageButton trackactiveBtn;
    private ImageButton new_flightplanBtn;
    private ImageButton map_typeBtn;
    private ImageButton aerodrome_searchBtn;
    private ImageButton airspace_lockingBtn;
    private ImageButton appLockingBtn;
    private ImageButton moreBtn;
    private MenuItem settingsItem;
    private MenuItem prevtrackItem;
    private MenuItem loadChartsItem;
    private MenuItem isnewItem;

   private void setupButtons()
   {
       onButtonClick = new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ImageButton button = (ImageButton)view;
               MenuItemType itemType = (MenuItemType)button.getTag();
               if (itemType==MenuItemType.more){
                   showMoreMenu();
               }
               else
                    if (onNavigationMemuItemClicked != null) onNavigationMemuItemClicked.OnMenuItemClicked(view,itemType);
           }
       };

       dirtect_toBtn = (ImageButton) view.findViewById(R.id.dirtect_to);
       dirtect_toBtn.setTag(MenuItemType.directTo);
       dirtect_toBtn.setOnClickListener(onButtonClick);
       flightplan_activateBtn = (ImageButton) view.findViewById(R.id.flightplan_activate);
       flightplan_activateBtn.setTag(MenuItemType.routeActivate);
       flightplan_activateBtn.setOnClickListener(onButtonClick);
       connect_disconnectBtn = (ImageButton) view.findViewById(R.id.connect_disconnect);
       connect_disconnectBtn.setTag(MenuItemType.connectDisconnect);
       connect_disconnectBtn.setOnClickListener(onButtonClick);
       trackactiveBtn = (ImageButton) view.findViewById(R.id.trackactive);
       trackactiveBtn.setTag(MenuItemType.tracking);
       trackactiveBtn.setOnClickListener(onButtonClick);
       new_flightplanBtn = (ImageButton) view.findViewById(R.id.new_flightplan);
       new_flightplanBtn.setTag(MenuItemType.routeCreate);
       new_flightplanBtn.setOnClickListener(onButtonClick);
       map_typeBtn = (ImageButton) view.findViewById(R.id.map_type);
       map_typeBtn.setTag(MenuItemType.maptype);
       map_typeBtn.setOnClickListener(onButtonClick);
       aerodrome_searchBtn = (ImageButton) view.findViewById(R.id.aerodrome_search);
       aerodrome_searchBtn.setTag(MenuItemType.search);
       aerodrome_searchBtn.setOnClickListener(onButtonClick);
       airspace_lockingBtn = (ImageButton) view.findViewById(R.id.airspace_locking);
       airspace_lockingBtn.setTag(MenuItemType.airspacesTracking);
       airspace_lockingBtn.setOnClickListener(onButtonClick);
       appLockingBtn = (ImageButton) view.findViewById(R.id.appLocking);
       appLockingBtn.setTag(MenuItemType.appLocking);
       appLockingBtn.setOnClickListener(onButtonClick);
       moreBtn = (ImageButton) view.findViewById(R.id.settings);
       moreBtn.setTag(MenuItemType.more);
       moreBtn.setOnClickListener(onButtonClick);
   }

   private void showMoreMenu()
   {
       PopupMenu popup = new PopupMenu(getActivity(), moreBtn);
       popup.getMenuInflater().inflate(R.menu.nav_menu, popup.getMenu());

       popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {

               MenuItemType itemType = null;
               if(menuItem.getItemId()==R.id.action_loadtrack_menuitem) itemType = MenuItemType.loadTrack;
               if(menuItem.getItemId()==R.id.action_load_chart_menuitem) itemType = MenuItemType.loadCharts;
               if(menuItem.getItemId()==R.id.action_settings_menuitem) itemType = MenuItemType.settings;
               if(menuItem.getItemId()==R.id.action_isnew_menuitem) itemType = MenuItemType.isNew;
               if (itemType != null)
                    if (onNavigationMemuItemClicked != null) onNavigationMemuItemClicked.OnMenuItemClicked(null,itemType);

               return false;
           }
       });

       popup.show();
   }

   public void SetTrackingItemIcon(Boolean enabled)
   {
       trackactiveBtn.setBackground((Drawable)getResources().
               getDrawable(enabled ? R.drawable.trackactive : R.drawable.trackinactive));
   }

   public void SetAirspaceItemIcon(Boolean enabled)
   {
       airspace_lockingBtn.setBackground((Drawable)getResources().
               getDrawable(enabled ? R.drawable.airspace_locked : R.drawable.airspace_unlocked));
   }

   public void SetApplockedIcon(Boolean enabled)
   {
       appLockingBtn.setBackground((Drawable)getResources().
               getDrawable(enabled ? R.drawable.locked : R.drawable.unlocked));
   }

    public void SetConnectDisConnectIcon(boolean connected)
    {
        connect_disconnectBtn.setBackground((Drawable)getResources().
                getDrawable(connected ? R.drawable.connected : R.drawable.disconnected));
    }

    public void SetConnectingIcon()
    {
        connect_disconnectBtn.setBackground((Drawable)getResources().
                getDrawable(R.drawable.connecting));
    }

}

package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import nl.robenanita.googlemapstest.Airport.Airport;
import nl.robenanita.googlemapstest.search.SearchPopup;

/**
 * Created by Rob Verhoef on 10-5-2014.
 */
public class DirectToPopup extends PopupWindow {
    private Airport alternateAirport;
    public Airport selectedAirport;
    public Navaid selectedNavaid;
    public Fix selectedFix;
    public Boolean Result;

    EditText selectedTxt;
    Button saveBtn;

    private View view;

    public DirectToPopup(Context context, View layout, Airport alternateAirport, final NavigationActivity navigationActivity)
    {
        Result = false;
        this.view = layout;

        this.alternateAirport = alternateAirport;
        this.selectedAirport = alternateAirport;

        saveBtn = (Button) layout.findViewById(R.id.selectDirectToBtn);
        Button cancelBtn = (Button) layout.findViewById(R.id.cancelDirectToBtn);
        final Button searchBtn = (Button) layout.findViewById(R.id.directToSearchBtn);

        TextView directToTitleTxt = (TextView) layout.findViewById(R.id.directToTitleTxt);
        selectedTxt = (EditText) layout.findViewById(R.id.directToAirportTxt);

        if (alternateAirport == null)
        {
            directToTitleTxt.setText("Search for an airport, navaid or fix to go to:");
        }
        else
        {
            directToTitleTxt.setText("Select the Alternate Airport or Search for an airport, navaid or fix to go to:");
            selectedTxt.setText("Airport: " + this.selectedAirport.name);
            saveBtn.setEnabled(true);
        }


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result = true;
                dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Result = false;
                dismiss();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int popupWidth = 700;
                int popupHeight = 500;

                LinearLayout viewGroup = (LinearLayout) view.findViewById(R.id.searchPopup);
                LayoutInflater layoutInflater = (LayoutInflater) navigationActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View Layout = layoutInflater.inflate(R.layout.searchpopup, viewGroup);

                final SearchPopup searchPopup = new SearchPopup(navigationActivity, Layout, navigationActivity.curPosition);

                searchPopup.setContentView(Layout);
                searchPopup.setWidth(popupWidth);
                searchPopup.setHeight(popupHeight);
                searchPopup.setFocusable(true);

                searchPopup.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        if (searchPopup.airport != null) {
                            selectedAirport = searchPopup.airport;
                            selectedNavaid = null;
                            selectedFix = null;
                            selectedTxt.setText("Airport: " + selectedAirport.name);
                            saveBtn.setEnabled(true);
                        }
                        if (searchPopup.navaid != null) {
                            selectedNavaid = searchPopup.navaid;
                            selectedAirport = null;
                            selectedFix = null;
                            selectedTxt.setText("Navaid: " + selectedNavaid.name);
                            saveBtn.setEnabled(true);
                        }
                        if (searchPopup.fix != null) {
                            selectedFix = searchPopup.fix;
                            selectedAirport = null;
                            selectedNavaid = null;
                            selectedTxt.setText("Fix: " + selectedFix.name);
                            saveBtn.setEnabled(true);
                        }
                    }
                });

                searchPopup.showAtLocation(Layout, Gravity.TOP, 0, 10);
            }
        });


    }

}

package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;

import nl.robenanita.googlemapstest.database.PropertiesDataSource;

/**
 * Created by Rob Verhoef on 28-11-2014.
 */
public class IsNewPopup extends PopupWindow {

    public IsNewPopup(Context context, final View layout) {
        super(context);

        Button closeBtn = (Button) layout.findViewById(R.id.closeIsNewBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        WebView isNewView = (WebView) layout.findViewById(R.id.isNewwebView);
        isNewView.loadUrl("file:///android_asset/index.html");

        PropertiesDataSource propertiesDataSource = new PropertiesDataSource(layout.getContext());
        propertiesDataSource.open();
        Property p = propertiesDataSource.getMapSetup("SHOWISNEW");
        propertiesDataSource.close();

        CheckBox doNotShowAgainChkBox = (CheckBox) layout.findViewById(R.id.doNotShowAgaincheckBox);
        doNotShowAgainChkBox.setChecked(!Boolean.parseBoolean(p.value2));

        doNotShowAgainChkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PropertiesDataSource propertiesDataSource = new PropertiesDataSource(layout.getContext());
                propertiesDataSource.open();
                Property p = propertiesDataSource.getMapSetup("SHOWISNEW");
                p.value2 = Boolean.toString(!b);
                propertiesDataSource.updateProperty(p);
                propertiesDataSource.close();
            }
        });

    }
}

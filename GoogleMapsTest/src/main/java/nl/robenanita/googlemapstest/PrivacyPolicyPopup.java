package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.PopupWindow;

public class PrivacyPolicyPopup extends PopupWindow {

    public PrivacyPolicyPopup(Context context, final View layout) {
        super(context);

        Button closeBtn = (Button) layout.findViewById(R.id.closePrivacyBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        WebView isNewView = (WebView) layout.findViewById(R.id.privacyWebView);
        isNewView.loadUrl("file:///android_asset/privacy_statement.html");
    }
}

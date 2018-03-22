package nl.robenanita.googlemapstest.Charts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnDrawListener;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;

import java.io.File;

import nl.robenanita.googlemapstest.NavigationActivity;
import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 17-3-2016.
 */
public class PDFCharts {
    public PDFCharts(NavigationActivity navigationActivity)
    {
        this.navigationActivity = navigationActivity;

        //pdfView = (PDFView) navigationActivity.findViewById(R.id.pdfView);
    }

    private NavigationActivity navigationActivity;
    private PDFView pdfView;
    private final String SAMPLE_FILE = "EH-AD-2.EHLE-VAC.pdf";

    public boolean LoadTestPDF()
    {
        File file = new File("/storage/sdcard0/Download", SAMPLE_FILE);
        PDFView.Configurator configurator = pdfView.fromFile(file);
        configurator.onDraw(new OnDrawListener() {
            @Override
            public void onLayerDrawn(Canvas canvas, float v, float v1, int i) {
                Bitmap overlayBitmap = Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas overlayCanvas = new Canvas(overlayBitmap);

                //canvas.setBitmap(overlayBitmap);
                //GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                //groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(overlayBitmap));
                //groundOverlayOptions.positionFromBounds(navigationActivity.map.getProjection().getVisibleRegion().latLngBounds);
                //navigationActivity.map.addGroundOverlay(groundOverlayOptions);
                //pdfView.setVisibility(View.INVISIBLE);
            }
        });

        configurator.load();



        return true;
    }

}

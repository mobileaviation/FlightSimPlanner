package nl.robenanita.googlemapstest.Instruments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.content.res.Resources;

import nl.robenanita.googlemapstest.R;

/**
 * Created by Rob Verhoef on 26-8-2014.
 */
public class HorizonView extends View {
    public HorizonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bank = 0;
        pitch = 0;
        big = false;
        setupView(big);
    }

    float bank;
    float pitch;
    boolean big;

    int hi, os, hos, hbg, hf;
    Bitmap horizonInsideBitmap;
    Bitmap outsideBitmap;
    Bitmap horizonBgBitmap;
    Bitmap horizonOutsideBitmap;
    Bitmap horizonFixedBitmap;

    private void setupView(boolean big)
    {
        Resources res = getResources();

        if (big)
        {
            hi = R.drawable.horizoninsidebig;
            os = R.drawable.instrumentoutsidebig;
            hos = R.drawable.horizonoutsidebig;
            hf = R.drawable.horizonfixedbig;
            hbg = R.drawable.horizonbgbig;
        }else
        {
            hi = R.drawable.horizoninside;
            os = R.drawable.instrumentoutside;
            hos = R.drawable.horizonoutside;
            hf = R.drawable.horizonfixed;
            hbg = R.drawable.horizonbg;
        }

        horizonInsideBitmap = BitmapFactory.decodeResource(res, hi);
        outsideBitmap = BitmapFactory.decodeResource(res, os);
        horizonOutsideBitmap = BitmapFactory.decodeResource(res, hos);
        horizonFixedBitmap = BitmapFactory.decodeResource(res, hf);
        horizonBgBitmap = BitmapFactory.decodeResource(res, hbg);
    }

    public void setHorizon(float bank, float pitch)
    {
        this.bank = bank;
        this.pitch = pitch;
        invalidate();
        requestLayout();
    }

    public void setBig(boolean big)
    {
        this.big = big;
        setupView(big);
        invalidate();
        requestLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        float b = bank;
        float p = -pitch;

//        // Create blank bitmap of equal size
//        Bitmap totalInsideBitmap = Bitmap.createBitmap(horizonOutsideBitmap.getWidth(), horizonOutsideBitmap.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas totalCanvas = new Canvas(totalInsideBitmap);
//        totalCanvas.drawBitmap(horizonOutsideBitmap, 0, 0, null);
//        totalCanvas.drawBitmap(horizonInsideBitmap, 0, (float)p, null);

        Bitmap rotateBitmap = Bitmap.createBitmap(horizonInsideBitmap.getWidth(), horizonInsideBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas rotateCanvas = new Canvas(rotateBitmap);

        Bitmap rotateOutsideBitmap = Bitmap.createBitmap(horizonOutsideBitmap.getWidth(), horizonOutsideBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas rotateOutsideCanvas = new Canvas(rotateOutsideBitmap);

        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(b, rotateBitmap.getWidth()/2, rotateBitmap.getHeight()/2);
        rotateCanvas.drawBitmap(horizonInsideBitmap, rotateMatrix, null);

        Matrix rotateOutsideMatrix = new Matrix();
        rotateOutsideMatrix.setRotate(b, rotateOutsideBitmap.getWidth()/2, rotateOutsideBitmap.getHeight()/2);
        rotateOutsideCanvas.drawBitmap(horizonOutsideBitmap, rotateOutsideMatrix, null);

        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(horizonBgBitmap,(float)midx-(horizonBgBitmap.getWidth()/2), (float)midy-(horizonBgBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateBitmap, (float)midx-(rotateBitmap.getWidth()/2), ((float)midy-(rotateBitmap.getHeight()/2)) + p, null);
        canvas.drawBitmap(horizonFixedBitmap, (float)midx-(horizonFixedBitmap.getWidth()/2), (float)midy-(horizonFixedBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateOutsideBitmap, (float)midx-(rotateOutsideBitmap.getWidth()/2), (float)midy-(rotateOutsideBitmap.getHeight()/2), null);
        canvas.drawBitmap(outsideBitmap, 0, 0, null);
    }
}

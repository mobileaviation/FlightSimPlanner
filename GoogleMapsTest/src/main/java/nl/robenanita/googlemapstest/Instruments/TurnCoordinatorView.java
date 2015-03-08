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
public class TurnCoordinatorView extends View {
    public TurnCoordinatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        big = false;
        ballPos = 0;
        turn = 0;
        setupView(big);
    }

    float ballPos;
    float turn;
    boolean big;

    Context context;

    int tc, os, p, ba;

    Bitmap turnCoordinatorBitmap;
    Bitmap outsideBitmap;
    Bitmap planeBitmap;
    Bitmap ballBitmap;

    private void setupView(boolean big)
    {
        Resources res = getResources();

        if (big)
        {
            tc = R.drawable.turncoordinatorbig;
            os = R.drawable.instrumentoutsidebig;
            p = R.drawable.turncoordinatorplanebig;
            ba = R.drawable.turncoordinatorballbig;
        }
        else
        {
            tc = R.drawable.turncoordinator;
            os = R.drawable.instrumentoutside;
            p = R.drawable.turncoordinatorplane;
            ba = R.drawable.turncoordinatorball;
        }

        turnCoordinatorBitmap = BitmapFactory.decodeResource(res, tc);
        outsideBitmap = BitmapFactory.decodeResource(res, os);
        planeBitmap = BitmapFactory.decodeResource(res, p);
        ballBitmap = BitmapFactory.decodeResource(res, ba);
    }

    public void setBig(boolean big)
    {
        this.big = big;
        setupView(big);
        invalidate();
        requestLayout();
    }

    public void setTurnCoordinator(float turn, float ballPos)
    {
        this.ballPos = ballPos;
        this.turn = turn;
        invalidate();
        requestLayout();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        double b = (ballPos/127) * 16;
        double t = (turn/512) * 18;
        // Create blank bitmap of equal size
        Bitmap rotateBitmap = Bitmap.createBitmap(planeBitmap.getWidth(), planeBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas planeCanvas = new Canvas(rotateBitmap);
        // Create rotation matrix
        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate((float)t, planeBitmap.getWidth()/2, planeBitmap.getHeight()/2);
        planeCanvas.drawBitmap(planeBitmap, rotateMatrix, null);


        int midx = outsideBitmap.getWidth()/2;
        int midy = outsideBitmap.getHeight()/2;

        canvas.drawBitmap(outsideBitmap, 0, 0, null);
        canvas.drawBitmap(turnCoordinatorBitmap, (float)midx-(rotateBitmap.getWidth()/2), (float)midy-(rotateBitmap.getHeight()/2), null);
        canvas.drawBitmap(ballBitmap, (float)midx-(rotateBitmap.getWidth()/2) + (float)b, (float)midy-(rotateBitmap.getHeight()/2), null);
        canvas.drawBitmap(rotateBitmap, (float)midx-(planeBitmap.getWidth()/2), (float)midy-(planeBitmap.getHeight()/2), null);
    }
}


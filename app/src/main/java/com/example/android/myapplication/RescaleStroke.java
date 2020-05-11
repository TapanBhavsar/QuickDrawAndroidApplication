package com.example.android.myapplication;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.Log;

public class RescaleStroke {
    private Bitmap mModelBitmap;
    private static final int mBitmapWidth = 28;
    private static final int mBitmapHeight = 28;

    private static final String TAG = "RescaleStroke";
    private static final float PRECISION = 0.002f;


    RescaleStroke() {
        mModelBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
    }

    public void setPathOnBitmap(Path path) {
        final PathMeasure pathMeasure = new PathMeasure(path, false /* forceClosed */);

        final float pathLength = pathMeasure.getLength();
        final int numPoints = (int) (pathLength / PRECISION) + 1;

//        mX = new float[numPoints];
//        mY = new float[numPoints];

        final float[] position = new float[2];
        for (int i = 0; i < numPoints; ++i) {
            final float distance = (i * pathLength) / (numPoints - 1);
            pathMeasure.getPosTan(distance, position, null /* tangent */);

//            Log.i(TAG, "x: " + position[0] + ", y: " + position[1]);
//            mX[i] = position[0];
//            mY[i] = position[1];
        }
    }

}

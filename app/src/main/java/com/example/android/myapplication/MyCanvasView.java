package com.example.android.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MyCanvasView extends View {

    private Paint mPaint;
    private Path mPath;
    private int mDrawColor;
    private int mBackgroundColor;
    private Canvas mExtraCanvas;
    private Bitmap mExtraBitmap;
    private Rect mFrame;

    private Paint mModelPaint;
    private Path mModelPath;
    private int mModelDrawColor;
    private int mModelBackgroundColor;
    protected Canvas mModelCanvas;
    protected Bitmap mModelBitmap;
    private static final int mBitmapWidth = 28;
    private static final int mBitmapHeight = 28;

    private float mRatioX;
    private float mRatioY;

    private classifier mClassifier;

    private static final String TAG = "MyCanvasView";

    MyCanvasView(Context context) {
        this(context, null);
    }

    public MyCanvasView(Context context, AttributeSet attributeSet) {
        super(context);

        mBackgroundColor = ResourcesCompat.getColor(getResources(),
                R.color.opaque_orange, null);
        mDrawColor = ResourcesCompat.getColor(getResources(),
                R.color.opaque_yellow, null);

        mModelBackgroundColor = ResourcesCompat.getColor(getResources(),
                R.color.black, null);
        mModelDrawColor = ResourcesCompat.getColor(getResources(),
                R.color.white, null);
        // Holds the path we are currently drawing.
        mPath = new Path();
        // Set up the paint with which to draw.
        mPaint = new Paint();

        mPaint.setColor(mDrawColor);
        // Smoothes out edges of what is drawn without affecting shape.
        mPaint.setAntiAlias(true);
        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE); // default: FILL
        mPaint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        mPaint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        mPaint.setStrokeWidth(12); // default: Hairline-width (really thin)

        mModelPath = new Path();
        mModelPaint = new Paint();

        mModelPaint.setColor(mModelDrawColor);
        // Smoothes out edges of what is drawn without affecting shape.
        mModelPaint.setAntiAlias(true);
        // Dithering affects how colors with higher-precision device
        // than the are down-sampled.
        mModelPaint.setDither(true);
        mModelPaint.setStyle(Paint.Style.STROKE); // default: FILL
        mModelPaint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        mModelPaint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        mModelPaint.setStrokeWidth(1); // default: Hairline-width (really thin)

        mModelBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        mModelCanvas = new Canvas(mModelBitmap);
        mModelCanvas.drawColor(mModelBackgroundColor);
    }

    public void initModel(Activity activity) {
        try {
            mClassifier = new classifier(activity);
        } catch (IOException e) {
            Log.e("init error", "init(): Failed to create Classifier", e);
        }
    }



    @Override
    protected void onSizeChanged(int width, int height,
                                 int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        // Create bitmap, create canvas with bitmap, fill canvas with color.
        mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mExtraCanvas = new Canvas(mExtraBitmap);
        mExtraCanvas.drawColor(mBackgroundColor);

        mRatioX = mBitmapWidth/((float) width);
        mRatioY = mBitmapHeight/((float) height);

        // Calculate the rect a frame around the picture.
        int inset = 40;
        mFrame = new Rect(inset, inset, width - inset, height - inset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // Draw the bitmap that has the saved path.
        canvas.drawBitmap(mExtraBitmap, 0, 0, null);

        // Draw a frame around the picture.
        canvas.drawRect(mFrame, mPaint);

        mModelCanvas.drawBitmap(mModelBitmap, 0, 0, null);
    }

    // Variables for the latest x,y values,
    // which are the starting point for the next path.
    private float mX, mY;
    private float mModelX, mModelY;
    // Don't draw every single pixel.
    // If the finger has has moved less than this distance, don't draw.
    private static final float TOUCH_TOLERANCE = 4;
    private static final float MODEL_TOUCH_TOLERANCE = 1;


    // The following methods factor out what happens for different touch events,
    // as determined by the onTouchEvent() switch statement.
    // This keeps the switch statement
    // concise and and easier to change what happens for each event.

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void modelTouchStart(float x, float y) {
        mModelPath.moveTo(x, y);
        mModelX = x;
        mModelY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
            // Draw the path in the extra bitmap to save it.
            mExtraCanvas.drawPath(mPath, mPaint);
        }
    }

    private void modelTouchMove(float x, float y) {
        float dx = Math.abs(x - mModelX);
        float dy = Math.abs(y - mModelY);
        if (dx >= MODEL_TOUCH_TOLERANCE || dy >= MODEL_TOUCH_TOLERANCE) {
            mModelPath.quadTo(mModelX, mModelY, (x + mModelX) / 2, (y + mModelY) / 2);
            mModelX = x;
            mModelY = y;
            mModelCanvas.drawPath(mModelPath, mModelPaint);
        }
    }

    private void touchUp() {
        // Reset the path so it doesn't get drawn again.
        mPath.reset();
    }

    private void modelTouchUp() {
        // Reset the path so it doesn't get drawn again.
        Log.i(TAG, "classification class number: " + mClassifier.classify(mModelBitmap));
        mModelPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        float newX = (x*mRatioX);
        float newY = (y*mRatioY);

        // Invalidate() is inside the case statements because there are many
        // other types of motion events passed into this listener,
        // and we don't want to invalidate the view for those.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                modelTouchStart(newX,newY);
                // No need to invalidate because we are not drawing anything.
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                modelTouchMove(newX, newY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                modelTouchUp();
                break;
            default:
                // do nothing
        }
        return true;
    }
    // Get the width of the screen
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    // Get the height of the screen
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

//    public void showBitmapPixels() {
//        int A, R, G, B;
//        int pixel;
//
//        for (int x = 0; x < mModelBitmap.getWidth(); ++x) {
//            for (int y = 0; y < mModelBitmap.getHeight(); ++y) {
//                // get one pixel color
//                pixel = mModelBitmap.getPixel(x, y);
//                // retrieve color of all channels
////                A = Color.alpha(pixel);
//                R = Color.red(pixel);
//                G = Color.green(pixel);
//                B = Color.blue(pixel);
//                // take conversion up to one single value
////                Log.i(TAG, "location:(" + x + "," + y + ") pixel value: " + ((R+G+B)/3.0));
//                Log.i(TAG,""+ R);
//                // set new pixel color to output bitmap
//            }
//        }
//    }
//    write backup function which can resized draw stroke pixels into 28x28 bitmap
}

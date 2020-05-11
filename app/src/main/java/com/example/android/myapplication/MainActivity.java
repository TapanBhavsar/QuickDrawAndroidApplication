package com.example.android.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyCanvasView myCanvasView;
        // No XML file; just one custom view created programmatically.
        myCanvasView = new MyCanvasView(this);
        myCanvasView.initModel(this);
        // Request the full available screen for layout.
        myCanvasView.setSystemUiVisibility(SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(myCanvasView);
    }
}

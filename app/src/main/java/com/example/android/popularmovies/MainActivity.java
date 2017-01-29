package com.example.android.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView upperLeft;
    private TextView upperRight;
    private TextView lowerLeft;
    private TextView lowerRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upperLeft = (TextView) findViewById(R.id.tv_upperLeft);
        upperRight = (TextView) findViewById(R.id.tv_upperRight);
        lowerLeft = (TextView) findViewById(R.id.tv_lowerLeft);
        lowerRight = (TextView) findViewById(R.id.tv_lowerRight);
    }
}

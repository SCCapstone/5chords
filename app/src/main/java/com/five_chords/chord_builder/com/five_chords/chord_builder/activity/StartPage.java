package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.five_chords.chord_builder.R;

/**
 * The Start page Activity.
 * @version 1.0
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 **/
public class StartPage extends AppCompatActivity
{
    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        // Lock orientation in portrait mode with small screen devices
        if (!getResources().getBoolean(R.bool.isTablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Called to go to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}

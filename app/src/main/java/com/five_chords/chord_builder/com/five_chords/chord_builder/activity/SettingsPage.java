/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.five_chords.chord_builder.R;

public class SettingsPage extends Activity
{
    /**
     * Activity Creator
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
    }

    /**
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * The MainActivity call
     */
    public void backToMain(View view)
    {
        finish();
    }
}

/***************************************************************************************************
 * StartPage.java
 * This class sets up the Start Page and its buttons
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;

public class StartPage extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
    }
    /**
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * MainActivity Call
     */
    public void backToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }

}

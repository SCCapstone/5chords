/**
 * The Score page.
 * This class sets up the Score Page and it's buttons. The Score Page contains the score history views.
 * @version 1.0
 * @date 16 March 2016
 * @author: Drea,Steven,Zach,Theodore
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;


public class ScorePage extends AppCompatActivity
{
    @Override
    /**
     * Creation code for the Score Page.
     */
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);
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

    /**
     * Called to clear the score history.
     * @param view The calling Button view
     */
    public void clearScores(View view)
    {
        Score.resetScores(this);

        // TODO Invalidate Score History Fragment
    }
}

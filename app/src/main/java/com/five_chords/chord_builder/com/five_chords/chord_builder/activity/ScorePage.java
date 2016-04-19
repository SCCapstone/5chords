package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ScorePageFragment;

/**
 * The Score page Activity. ScorePage contains the history view for each chord as well as a slider to select
 * which chord types to see. ScorePage also contains a button to reset scores.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Theodore
 */
public class ScorePage extends AppCompatActivity
{
    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);
    }

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
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
        final Fragment fragment = getFragmentManager().findFragmentById(R.id.score_activity_score_fragment);

        Score.resetScores(this, new Runnable()
        {
            @Override
            public void run()
            {
                if (fragment != null && fragment instanceof ScorePageFragment)
                    ((ScorePageFragment)fragment).refreshDelayed();
            }
        });
    }
}

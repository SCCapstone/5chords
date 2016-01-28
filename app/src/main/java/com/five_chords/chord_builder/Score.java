
/*************************************************************************************************
 * Score.java
 * This class will display how many tries and how many user got it correct by "_/_" format.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class Score extends MainActivity
{
//    private static SharedPreferences chordScores;

    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** The array keeping track of the number of correct guesses for each chord */
    protected static int[] correctChords;// = new int[Resources.getSystem().getStringArray(R.array.chords).length];
    /** The array keeping track of the number of total guesses for each chord */
    protected static int[] totalChords;// = new int[Resources.getSystem().getStringArray(R.array.chords).length];

    RadioGroup chordClass;
    ArrayList<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);

        chordClass = (RadioGroup) findViewById(R.id.chordClass);

        // get all every TextView for displaying score
        RelativeLayout scorePage = (RelativeLayout) findViewById( R.id.scorePage );
        for( int i = 0; i < scorePage.getChildCount(); i++ )
            if( scorePage.getChildAt(i) instanceof TextView )
                textViews.add((TextView) scorePage.getChildAt(i));

        displayScores(0);

        // listen for change of chord class
        chordClass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.major)
                {
                    displayScores(0);
                } else if (checkedId == R.id.minor) {
                    displayScores(1);
                }

            }
        });

    }

    /***********************************************************************************************
     * Loads the score data for each chord and initializes the correctChords and totalChords arrays.
     * @param activity The calling Activity
     **********************************************************************************************/
    public void loadScores(Activity activity)
    {
        // Grab the array of chord names from the resources
        String[] chordNames = activity.getResources().getStringArray(R.array.chordNames);

        // Initialize correctChords and totalChords arrays
        correctChords = new int[chordNames.length];
        totalChords = new int[chordNames.length];

        // Load scores
        SharedPreferences savedChordScores = activity.getApplication().
                                getSharedPreferences(CHORD_SCORES_SAVE_FILENAME, Context.MODE_PRIVATE);
        for (int i = 0; i < chordNames.length; ++i)
        {
            correctChords[i] = savedChordScores.getInt("correct_" + chordNames[i], 0);
            totalChords[i] = savedChordScores.getInt("total_" + chordNames[i], 0);
        }
    }

    /***********************************************************************************************
     * Updates the score for the chord of the given index.
     * @param activity The calling Activity
     * @param chordIndex The index of the chord
     * @param correct Whether or not the chord guess was correct
     **********************************************************************************************/
    public void setScore(Activity activity, int chordIndex, boolean correct)
    {
        String[] chordNames = activity.getResources().getStringArray(R.array.chordNames);
        SharedPreferences savedChordScores = activity.getApplication().
                                    getSharedPreferences(CHORD_SCORES_SAVE_FILENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor scoreEditor = savedChordScores.edit();

        if (correct)
            scoreEditor.putInt("correct_" + chordNames[chordIndex], ++correctChords[chordIndex]);

        scoreEditor.putInt("total_" + chordNames[chordIndex], ++totalChords[chordIndex]);
        scoreEditor.apply();
    }
    /***********************************************************************************************
     * displayScores function
     * @param chords
     **********************************************************************************************/
    public void displayScores(int chords)
    {
        int chord = 12 * chords;
        String[] chordNames = getResources().getStringArray(R.array.chordNames);

        for (int i = chord; i < 12 + chord; i++)
        {
            textViews.get(i % 12).setText(chordNames[i] + " : " + correctChords[i] + "/" + totalChords[i]);
        }
    }
    /***********************************************************************************************
     * BacktoMain function
     * @param view
     **********************************************************************************************/
    public void BackToMain(View view)
    {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }

}

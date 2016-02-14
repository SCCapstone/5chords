/******************************************************************************************
 * Score.java
 * This class will display how many tries and how many user got it correct by "_/_" format.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Score// extends MainActivity
{
    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** The array of chord Scores */
    public static ScoreWrapper[] scores;

    public Score(Activity main)
    {
        loadScores(main);
    }

    /*********************************************************
     * Gets the SharedPreferences used to load and save scores
     * @param activity The calling Activity
     **/
    public static SharedPreferences getScoreLoader(Activity activity)
    {
        return activity.getSharedPreferences(CHORD_SCORES_SAVE_FILENAME, Context.MODE_PRIVATE);
    }

    /***********************************************************************************************
     * Loads the score data for each chord and initializes the correctChords and totalChords arrays.
     * @param main The calling Activity
     **/
    public void loadScores(Activity main)
    {
        // Grab the array of chord names from the resources
        String[] chordNames = main.getResources().getStringArray(R.array.chordNames);

        // Initialize score array
        scores = new ScoreWrapper[chordNames.length];

        // Load scores
        SharedPreferences savedChordScores = getScoreLoader(main);
        for (int i = 0; i < chordNames.length; ++i)
            scores[i] = loadScore(savedChordScores, chordNames[i]);
    }

    /*********************************************************************
     * Loads in a single score wrapper.
     * @param savedChordScores The SharedPreferences of saved chord scores
     * @param chordName The name of the chord whose score to load
     **/
    public ScoreWrapper loadScore(SharedPreferences savedChordScores, String chordName)
    {
        ScoreWrapper scoreWrapper = new ScoreWrapper(chordName);
        scoreWrapper.load(savedChordScores);
        return scoreWrapper;
    }

    /************************************************************
     * Updates the score for the chord of the given index.
     * @param activity The calling Activity
     * @param chordIndex The index of the chord
     * @param correct Whether or not the chord guess was correct
     **/
    public void setScore(Activity activity, int chordIndex, boolean correct)
    {
        ScoreWrapper scoreWrapper = scores[chordIndex];
        SharedPreferences savedChordScores = getScoreLoader(activity);

        // Edit the score
        scoreWrapper.numTotalGuesses++;
        if (correct)
            scoreWrapper.numCorrectGuesses++;

        // Save the new score
        scoreWrapper.save(savedChordScores);
    }

    /*****************************************
     * Wrapper class for a single chord score.
     */
    public static class ScoreWrapper
    {
        /** The chord name of this ScoreItem */
        public final String CHORD_NAME;
        /** The number of correct guesses */
        public int numCorrectGuesses;
        /** The number of total guesses */
        public int numTotalGuesses;

        /************************************************************
         * Constructs a new ScoreWrapper with default scores of zero.
         * @param name The name of the chord
         */
        public ScoreWrapper(String name)
        {
            CHORD_NAME = name;
        }

        /*******************************************************************
         * Loads this ScoreWrapper's score from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences from which to load
         **/
        public void load(SharedPreferences savedChordScores)
        {
            numCorrectGuesses = savedChordScores.getInt("correct_" + CHORD_NAME, 0);
            numTotalGuesses = savedChordScores.getInt("total_" + CHORD_NAME, 0);
        }

        /*****************************************************************
         * Saves this ScoreWrapper's score to the given SharedPreferences.
         * @param savedChordScores The SharedPreferences to which to save
         **/
        public void save(SharedPreferences savedChordScores)
        {
            SharedPreferences.Editor scoreEditor = savedChordScores.edit();
            scoreEditor.putInt("correct_" + CHORD_NAME, numCorrectGuesses);
            scoreEditor.putInt("total_" + CHORD_NAME, numTotalGuesses);
            scoreEditor.apply();
        }
    }

    /******************************
     * Activity for the Score page.
     **/
    public static class ScoreActivity extends AppCompatActivity
    {
        /*******************************
         * BacktoMain function
         * @param view The calling View
         **/
        public void BackToMain(View view)
        {
            Intent intent = new Intent(this, MainActivity.class );
            startActivity(intent);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_score_page);
        }
    }
}

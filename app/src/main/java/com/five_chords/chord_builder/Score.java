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

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Score// extends MainActivity
{
    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** The number of scores to keep in the history */
    public static final int NUM_SCORES_TO_KEEP = 100;

    /** The amount of time to wait between updating scores, in milliseconds */
    public static final long SCORE_UPDATE_INTERVAL = 24L * 3600L * 1000L;

    /** The array of chord Scores */
    public static CurrentScoreWrapper[] scores;

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

    public int getNumCorrectGuesses(int scoreIndex) { return scores[scoreIndex].numCorrectGuesses; }
    public int getNumTotalGuesses(int scoreIndex) { return scores[scoreIndex].numTotalGuesses; }

    /***********************************************************************************************
     * Loads the score data for each chord and initializes the correctChords and totalChords arrays.
     * @param main The calling Activity
     **/
    public void loadScores(Activity main)
    {
        // Grab the array of chord names from the resources
        String[] chordNames = main.getResources().getStringArray(R.array.chordNames);

        // Initialize score array
        scores = new CurrentScoreWrapper[chordNames.length];

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
    public CurrentScoreWrapper loadScore(SharedPreferences savedChordScores, String chordName)
    {
        CurrentScoreWrapper scoreWrapper = new CurrentScoreWrapper(chordName);
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
        CurrentScoreWrapper scoreWrapper = scores[chordIndex];
        SharedPreferences savedChordScores = getScoreLoader(activity);

        // Edit the score
        scoreWrapper.numTotalGuesses++;
        if (correct)
            scoreWrapper.numCorrectGuesses++;

        // Save the new score
        scoreWrapper.save(savedChordScores);
    }

    /************************************************************************************************
     * Wrapper class for a single chord score that represents a most up to date version of that score,
     * and contains a history of that score.
     */
    public static class CurrentScoreWrapper extends ScoreWrapper
    {
        /** Flag denoting whether or not thr history of this CurrentScoreWrapper has changed */
        private boolean historyChanged;

        /** The history of this score */
        private LinkedList<ScoreWrapper> scoreHistory;

        /******************************************************************
         * Constructs a new CurrentScoreWrapper with default scores of zero.
         * @param name The name of the chord
         */
        public CurrentScoreWrapper(String name)
        {
            super (name);
            historyChanged = false;
            scoreHistory = null;
        }

        /**
         * Gets the history of this CurrentScoreWrapper, loading it if needed.
         * @param activity The current Activity
         * @return The history of this CurrentScoreWrapper
         */
        public List<ScoreWrapper> getHistory(Activity activity)
        {
            // Need to load history
            if (scoreHistory == null)
                loadHistory(getScoreLoader(activity));

            return scoreHistory;
        }

        /*******************************************************************
         * Loads this CurrentScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences from which to load
         **/
        public void load(SharedPreferences savedChordScores)
        {
            // The current time
            long currentTime = new Date().getTime();

            // Load from index 0 (default)
            super.load(savedChordScores, 0);

            // TODO temporary random scores
            Random r = new Random();
            numCorrectGuesses = r.nextInt(100);
            numTotalGuesses = 100;

            // Check whether this CurrentScore Wrapper is old enough so that
            // a new wrapper should be added to the history
            if (currentTime - time > SCORE_UPDATE_INTERVAL)
            {
                historyChanged = true;
                time = currentTime;
            }
        }

        /*****************************************************************
         * Loads this CurrentScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences to which to save
         **/
        public void save(SharedPreferences savedChordScores)
        {
            SharedPreferences.Editor scoreEditor = savedChordScores.edit();

            // Save self
            super.save(scoreEditor, -1);

            // Save history if needed
            if (historyChanged)
            {
                historyChanged = false;

                // Load the history if needed
                if (scoreHistory == null)
                    loadHistory(savedChordScores);

                // Add this Score to the history
                scoreHistory.addFirst(this);

                // Discard last score if new history size is too large
                if (scoreHistory.size() > NUM_SCORES_TO_KEEP)
                    scoreHistory.removeLast();

                // Save history
                saveHistory(scoreEditor);
            }

            scoreEditor.apply();
        }

        /**
         * Loads the history of this CurrentScoreWrapper.
         * @param savedChordScores The SharedPreferences from which to load
         */
        private void loadHistory(SharedPreferences savedChordScores)
        {
            if (scoreHistory == null)
                scoreHistory = new LinkedList<>();
            else
                scoreHistory.clear();

            // Load the size of the history
            int size = savedChordScores.getInt(CHORD_NAME + "-n", 0);
            ScoreWrapper wrapper;

            for (int i = 0; i < size; ++i)
            {
                wrapper = new ScoreWrapper(CHORD_NAME);
                wrapper.load(savedChordScores, i);
                scoreHistory.add(wrapper);
            }
        }

        /**
         * Writes the history of this CurrentScoreWrapper without apllying changes.
         * @param scoreEditor The SharedPreferences.Editor to which to write
         */
        private void saveHistory(SharedPreferences.Editor scoreEditor)
        {
            // Save the size of the history
            scoreEditor.putInt(CHORD_NAME + "-n", scoreHistory.size());

            int i = 0;
            for (ScoreWrapper wrapper: scoreHistory)
                wrapper.save(scoreEditor, i++);
        }
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
        /** The last update time of this ScoreWrapper, in milliseconds */
        public long time;

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
         * @param index The index of this ScoreWrapper
         **/
        public void load(SharedPreferences savedChordScores, int index)
        {
            numCorrectGuesses = savedChordScores.getInt(CHORD_NAME + "-" + index + "-c", 0);
            numTotalGuesses = savedChordScores.getInt(CHORD_NAME + "-" + index + "-t", 0);
            time = savedChordScores.getLong(CHORD_NAME + "-" + index + "-h", 0L);
        }

        /*****************************************************************
         * Writes this ScoreWrapper's score to the given SharedPreferences.Editor without applying changes.
         * @param scoreEditor The SharedPreferences.Editor to which to write
         * @param index The index of this ScoreWrapper
         **/
        public void save(SharedPreferences.Editor scoreEditor, int index)
        {
            scoreEditor.putInt(CHORD_NAME + "-" + index + "-c", numCorrectGuesses);
            scoreEditor.putInt(CHORD_NAME + "-" + index + "-t", numTotalGuesses);
            scoreEditor.putLong(CHORD_NAME + "-" + index + "-h", time);
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

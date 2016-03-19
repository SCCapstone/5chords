/******************************************************************************************
 * Score.java
 * This class will display how many tries and how many user got it correct by "_/_" format.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Score
{
    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** Tags for the score history view */
    public static final String[] HISTORY_TAGS = new String[] {"Current", "1H", "2H", "6H", "12H", "1D",
                                                "2D", "4D", "1W", "2W", "1M", "2M", "6M", "1Y"};

    /** Update times for each history tag, in seconds */
    public static final long[] HISTORY_UPDATE_INTERVALS = new long[] {0L, 3600L, 2 * 3600L, 6 * 3600L,
                                                                        12 * 3600L, 24 * 3600L, 48 * 3600L,
                                                                        96 * 3600L, 168 * 3600L, 336 * 3600L,
                                                                        672 * 3600L, 1344 * 3600L, 4032 * 3600L, 8064 * 3600L};

    /** The array of chord Scores */
    public static Score[] scores;

    /** The chord name of this Score. */
    public final String CHORD_NAME;

    /** Records the overall value of this Score. */
    private ScoreValue overallValue;

    /** Records the current value of this Score. */
    private ScoreValue currentValue;

    /** The history of this Score split into discrete bins for scores of different age categories */
    private DiscreteScoreHistory discreteScoreHistory;

    /**
     * Constructs a new Score for the given chord.
     */
    public Score(String chordName)
    {
        CHORD_NAME = chordName;
        overallValue = new ScoreValue();
        currentValue = new ScoreValue();
        discreteScoreHistory = new DiscreteScoreHistory();
    }

    /**
     * Gets the current time to use for Score timestamps.
     * @return The current time to use for Score timestamps
     */
    private static long getScoreTime()
    {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Gets the SharedPreferences used to load and save scores
     * @param activity The calling Activity
     */
    private static SharedPreferences getScoreLoader(Activity activity)
    {
        return activity.getSharedPreferences(CHORD_SCORES_SAVE_FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Gets the Score object for the currently selected chord.
     * @return The Score object for the currently selected chord
     */
    public static Score getCurrentScore()
    {
        return scores[chordHandler.getSelectedChordIndex()];
    }

    /**
     * Called to reset the scores.
     * @param activity The current Activity
     */
    public static void resetScores(final Activity activity)
    {
        // Launch confirmation dialog
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Clear Scores")
                .setMessage("Are you sure you want clear all scores?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // Clear the history
                        SharedPreferences savedChordScores = getScoreLoader(activity);
                        SharedPreferences.Editor editor = savedChordScores.edit();
                        editor.clear();
                        editor.apply();

                        // Reload scores
                        loadScores(activity, true);

                        // Show confirmation toast
                        Toast.makeText(activity, "Scores cleared", Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Loads the score data for each chord.
     * @param main The calling Activity
     * @param overwrite Whether or not to overwrite the score history if it is already loaded
     */
    public static void loadScores(Activity main, boolean overwrite)
    {
        if (!overwrite && scores != null)
            return;

        // Grab the array of chord names from the resources
        final String[] chordNames = main.getResources().getStringArray(R.array.chordNames);

        // Initialize score array
        scores = new Score[chordNames.length];

        // Load scores
        final SharedPreferences savedChordScores = getScoreLoader(main);

        for (int i = 0; i < scores.length; ++i)
        {
            scores[i] = new Score(chordNames[i]);
            scores[i].load(savedChordScores);
        }
    }

    /**
     * Gets the current ScoreValue of this Score.
     * @return The current ScoreValue of this Score
     */
    public ScoreValue getCurrentValue()
    {
        return currentValue;
    }

    /**
     * Gets the overall ScoreValue of this Score.
     * @return The overall ScoreValue of this Score
     */
    public ScoreValue getOverallValue()
    {
        return overallValue;
    }

    /**
     * Gets the DiscreteScoreHistory of this Score.
     * @return The DiscreteScoreHistory of this Score
     */
    public DiscreteScoreHistory getHistory()
    {
        return discreteScoreHistory;
    }

    /**
     * Updates this Score with a new attempt.
     * @param activity The current Activity
     * @param correct Whether or not the attempt was successful
     */
    public void update(Activity activity, boolean correct)
    {
        // Set the current value time
        currentValue.time = getScoreTime();

        // Update the value
        ++currentValue.numTotalGuesses;
        ++overallValue.numTotalGuesses;
        if (correct)
        {
            ++currentValue.numCorrectGuesses;
            ++overallValue.numCorrectGuesses;
        }

        // Update the history
        discreteScoreHistory.setValueInHistory(currentValue, currentValue.time);

        // Save the changes
        save(getScoreLoader(activity));
    }

    /**
     * Loads this Score from the given SharedPreferences.
     * @param savedChordScores The SharedPreferences from which to load
     */
    private void load(SharedPreferences savedChordScores)
    {
        // Load the history
        discreteScoreHistory.load(savedChordScores, CHORD_NAME);

        // Load the overall value
        overallValue.load(savedChordScores, CHORD_NAME, -1);
    }

    /**
     * Saves this Score to the given SharedPreferences.
     * @param savedChordScores The SharedPreferences to which to save
     */
    public void save(SharedPreferences savedChordScores)
    {
        SharedPreferences.Editor editor = savedChordScores.edit();

        // Save the history
        discreteScoreHistory.save(editor, CHORD_NAME);

        // Save the overall value
        overallValue.save(editor, CHORD_NAME, -1);

        editor.apply();
    }

    /**
     * Wrapper class for a score history split into bins.
     */
    public static class DiscreteScoreHistory
    {
        /** The size of this DiscreteScoreHistory */
        public int size;

        /** The array of values in this DiscreteScoreHistory */
        public ScoreValue[] values;

        /**
         * Default Constructor.
         */
        public DiscreteScoreHistory()
        {
            size = 0;
            values = new ScoreValue[HISTORY_TAGS.length];
        }

        /**
         * Clears this DiscreteScoreHistory.
         */
        public void clear()
        {
            size = 0;
            for (int i = 0; i < values.length; ++i)
                values[i] = null;
        }

        /**
         * Saves this DiscreteScoreHistory.
         * @param scoreEditor The SharedPreferences.Editor to which to write
         * @param name The name of the history
         */
        private void save(SharedPreferences.Editor scoreEditor, String name)
        {
            // Save each element of the history
            ScoreValue value;
            for (int i = 0; i < values.length; ++i)
            {
                value = values[i];

                if (value == null)
                    scoreEditor.putBoolean(name + "-b" + i, false);
                else
                {
                    scoreEditor.putBoolean(name + "-b" + i, true);
                    value.save(scoreEditor, name, i++);
                }
            }
        }

        /**
         * Loads this DiscreteScoreHistory.
         * @param savedChordScores The SharedPreferences from which to load
         * @param name The name of the history to load
         */
        private void load(SharedPreferences savedChordScores, String name)
        {
             ScoreValue value;

            // Load each element of the history
            for (int i = 0; i < values.length; ++i)
            {
                if (savedChordScores.getBoolean(name + "-b" + i, false))
                {
                    value = new ScoreValue();
                    value.load(savedChordScores, name, i);
                    values[i] = value;
                    size++;
                }
            }

            // Collect all score points
            List<ScoreValue> scoreValues = new LinkedList<>();

            // Add the points in the history to the list
            for (ScoreValue v: values)
                if (v != null)
                    scoreValues.add(v);

            // Clear the history and re-add the points
            clear();
            addValuesToHistory(scoreValues);

            // Re-save the history
            SharedPreferences.Editor editor = savedChordScores.edit();
            save(editor, name);
            editor.apply();
        }

        /**
         * Adds a single ScoreValue to this DiscreteScoreHistory, putting it in an appropriate bin.
         * @param value The ScoreValue to add
         * @param time The current time in seconds
         */
        public void addValueToHistory(ScoreValue value, long time)
        {
            // Determine the bin for the current value
            int bin = getBin(Math.abs(time - value.time));

            // Add the value to the bin
            if (this.values[bin] == null)
            {
                ++size;
                this.values[bin] = new ScoreValue();
                this.values[bin].time = value.time; // Set time
            }
            else
                this.values[bin].time = (this.values[bin].time + value.time) / 2L; // Average times together

            this.values[bin].numCorrectGuesses += value.numCorrectGuesses;
            this.values[bin].numTotalGuesses += value.numTotalGuesses;
        }

        /**
         * Adds the given ScoreValue to this DiscreteScoreHistory, putting it in an appropriate bin and
         * overwriting any previous contents of that bin.
         * @param value The ScoreValue to add
         * @param time The current time in seconds
         */
        public void setValueInHistory(ScoreValue value, long time)
        {
            // Determine the bin for the current value
            int bin = getBin(Math.abs(time - value.time));

            // Add the value to the bin
            if (this.values[bin] == null)
            {
                ++size;
                this.values[bin] = new ScoreValue();
            }

            this.values[bin].time = value.time;
            this.values[bin].numCorrectGuesses = value.numCorrectGuesses;
            this.values[bin].numTotalGuesses = value.numTotalGuesses;
        }

        /**
         * Adds a list of ScoreValues to this DiscreteScoreHistory, putting each value in an appropriate bin.
         * @param values The list of ScoreValues to add
         */
        public void addValuesToHistory(List<ScoreValue> values)
        {
            // Get the current time in seconds
            long time = getScoreTime();

            // Loop over the values
            for (ScoreValue value: values)
            {
                addValueToHistory(value, time);
            }
        }

        /**
         * Gets the bin index for the given ScoreValue age.
         * @param age The age of the ScoreValue to use
         * @return The bin index for the given ScoreValue age
         */
        private int getBin(long age)
        {
            int bin = -1;

            for (int i = 1; bin == -1 && i < HISTORY_UPDATE_INTERVALS.length; ++i)
            {
                if (age < HISTORY_UPDATE_INTERVALS[i])
                {
                    bin = i - 1;
                }
            }

            if (bin == -1)
                bin = HISTORY_UPDATE_INTERVALS.length - 1;

            return bin;
        }
    }

    /**
     * Wrapper class for a score value, measured as the number of correct guesses out the number of total guesses.
     */
    public static class ScoreValue
    {
        /** The number of correct guesses */
        public int numCorrectGuesses;
        /** The number of total guesses */
        public int numTotalGuesses;
        /** The time stamp of this ScoreValue */
        public long time;

        /**
         * Loads this ScoreValue from the given SharedPreferences.
         * @param sharedPreferences The SharedPreferences from which to load
         * @param name The name to attach to this ScoreValue
         * @param index The index to attach to this ScoreValue
         */
        public void load(SharedPreferences sharedPreferences, String name, int index)
        {
            numCorrectGuesses = sharedPreferences.getInt(name + "-" + index + "-c", 0);
            numTotalGuesses = sharedPreferences.getInt(name + "-" + index + "-t", 0);
            time = sharedPreferences.getLong(name + "-" + index + "-h", -1L);
        }

        /**
         * Saves this ScoreValue to the given SharedPreferences.Editor.
         * @param editor The SharedPreferences.Editor to which to write
         * @param name The name to attach to this ScoreValue
         * @param index The index to attach to this ScoreValue
         */
        public void save(SharedPreferences.Editor editor, String name, int index)
        {
            editor.putInt(name + "-" + index + "-c", numCorrectGuesses);
            editor.putInt(name + "-" + index + "-t", numTotalGuesses);
            editor.putLong(name + "-" + index + "-h", time);
        }

        /**
         * Gets a String for displaying the value of this ScoreValue.
         * @return A String for displaying the value of this ScoreValue
         */
        public String getDisplayString()
        {
            return numCorrectGuesses + " / " + numTotalGuesses;
        }
    }
}

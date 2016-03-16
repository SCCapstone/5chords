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

public class Score extends AppCompatActivity
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
    public static ScoreWrapper[] scores;

    /**
     * Static class.
     */
    private Score()
    {    }


    /**
     * Gets the SharedPreferences used to load and save scores
     * @param activity The calling Activity
     */
    public static SharedPreferences getScoreLoader(Activity activity)
    {
        return activity.getSharedPreferences(CHORD_SCORES_SAVE_FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Gets the number of correct guesses for the chord of the given index.
     * @param chordIndex The index of the chord
     * @return The number of correct guesses for the chord of the given index
     */
    public static int getNumCorrectGuesses(int chordIndex) { return scores[chordIndex].value.numCorrectGuesses; }

    /**
     * Gets the number of total guesses for the chord of the given index.
     * @param chordIndex The index of the chord
     * @return The number of total guesses for the chord of the given index
     */
    public static int getNumTotalGuesses(int chordIndex) { return scores[chordIndex].value.numTotalGuesses; }

    /**
     * Called to go back to the Main Activity.
     * @ param  The current Activity
     *  MainActivity Call
     */

    public void backToMain(View view) {

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

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
     * Loads the score data for each chord and initializes the correctChords and totalChords arrays.
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
        scores = new ScoreWrapper[chordNames.length];

        // Load scores
        final SharedPreferences savedChordScores = getScoreLoader(main);

        for (int i = 0; i < scores.length; ++i)
        {
            scores[i] = new ScoreWrapper(chordNames[i]);
            scores[i].load(savedChordScores);
        }
    }

    /**
     * Updates the score for the chord of the given index.
     * @param activity The calling Activity
     * @param chordIndex The index of the chord
     * @param correct Whether or not the chord guess was correct
     */
    public static void setScore(Activity activity, int chordIndex, boolean correct)
    {
        ScoreWrapper scoreWrapper = scores[chordIndex];
        SharedPreferences savedChordScores = getScoreLoader(activity);

        // Edit the score
        scoreWrapper.value.numTotalGuesses++;
        if (correct)
            scoreWrapper.value.numCorrectGuesses++;

        // Save the new score
        scoreWrapper.save(savedChordScores);
    }



    /**
     * Wrapper class for a single chord score that represents a most up to date version of that score,
     * and contains a history of that score.
     */
    public static class ScoreWrapper
    {
        /** The chord name of this ScoreWrapper */
        public final String CHORD_NAME;

        /** Records the value of this ScoreWrapper */
        private ScoreValue value;

        /** The history of this ScoreWrapper split into discrete bins for scores of different age categories */
        private DiscreteScoreHistory discreteScoreHistory;

        /**
         * Constructs a new ScoreWrapper with default scores of zero.
         * @param name The name of the chord
         */
        public ScoreWrapper(String name)
        {
            CHORD_NAME = name;
            value = new ScoreValue();
        }

        /**
         * Gets the number of correct guesses of this ScoreWrapper.
         * @return The number of correct guesses of this ScoreWrapper
         */
        public int getNumCorrectGuesses() { return value.numCorrectGuesses; }

        /**
         * Gets the number of total guesses of this ScoreWrapper.
         * @return The number of total guesses of this ScoreWrapper
         */
        public int getNumTotalGuesses() { return value.numTotalGuesses; }

        /**
         * Gets the ScoreValue of this ScoreWrapper.
         * @return The ScoreValue of this ScoreWrapper
         */
        public ScoreValue getValue()
        {
            return value;
        }

        /**
         * Loads this ScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences from which to load
         */
        public void load(SharedPreferences savedChordScores)
        {
            // Read from index zero (default)
            value.load(savedChordScores, CHORD_NAME, 0);
        }

        /*****************************************************************
         * Loads this CurrentScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences to which to save
         **/
        public void save(SharedPreferences savedChordScores)
        {
            // Update time
            value.time = new Date().getTime() / 1000L;

            // Save this score
            SharedPreferences.Editor editor = savedChordScores.edit();
            value.save(editor, CHORD_NAME, 0);
            editor.apply();

            // Update history if needed
            updateHistory(savedChordScores);
        }

        /**
         * Loads the history of this ScoreWrapper.
         * @param activity The current Activity
         * @param overwrite Whether or not to overwrite the history if it is already loaded
         */
        public void loadHistory(Activity activity, boolean overwrite)
        {
            if (overwrite || discreteScoreHistory == null)
                loadHistory(getScoreLoader(activity));
        }

        /**
         * Gets the score history of this ScoreWrapper.
         * @return The score history of this ScoreWrapper
         */
        public DiscreteScoreHistory getHistory()
        {
            return discreteScoreHistory;
        }

        /**
         * Gets a String representation of this ScoreWrapper.
         * @return A String representation of this ScoreWrapper
         */
        @Override
        public String toString()
        {
            return CHORD_NAME + ": " + value.numCorrectGuesses + " / " + value.numTotalGuesses + " (" +
                    (100.0 * value.numCorrectGuesses / + value.numTotalGuesses) + " %), " +
                    new Date(value.time * 1000L).toString();
        }

        /**
         * Updates the history of this ScoreWrapper.
         * @param savedChordScores The SharedPreferences from which to load
         */
        private void updateHistory(SharedPreferences savedChordScores)
        {
            // Calculate the current time in seconds
            long time = new Date().getTime() / 1000;

            // If this score has aged past the update interval, add it
            if (value.time - time > HISTORY_UPDATE_INTERVALS[1])
            {
                // Collect all score points
                List<ScoreValue> scoreValues = new LinkedList<>();
                scoreValues.add(value);

                // Load old history if needed
                if (discreteScoreHistory == null)
                    loadHistory(savedChordScores);

                // Add the points in the history to the list
                for (ScoreValue value: discreteScoreHistory.values)
                    if (value != null)
                        scoreValues.add(value);

                // Clear the history and re-add the points
                discreteScoreHistory.clear();
                discreteScoreHistory.addValuesToHistory(scoreValues);

                // Re-save the history
                SharedPreferences.Editor editor = savedChordScores.edit();
                saveHistory(editor);
                editor.apply();
            }
            else if (discreteScoreHistory != null)
            {
                // Update first element of the history
                discreteScoreHistory.setValueInHistory(value, time);

                // Re-save the history
                SharedPreferences.Editor editor = savedChordScores.edit();
                saveHistory(editor);
                editor.apply();
            }
        }

        /**
         * Loads the history of this ScoreWrapper.
         * @param savedChordScores The SharedPreferences from which to load
         */
        private void loadHistory(SharedPreferences savedChordScores)
        {
            discreteScoreHistory = new DiscreteScoreHistory();

            ScoreValue value;

            // Load each element of the history
            for (int i = 0; i < discreteScoreHistory.values.length; ++i)
            {
                if (savedChordScores.getBoolean(CHORD_NAME + "-b" + i, false))
                {
                    value = new ScoreValue();
                    value.load(savedChordScores, CHORD_NAME, i);
                    discreteScoreHistory.values[i] = value;
                    discreteScoreHistory.size++;
                }
            }

            // Collect all score points
            List<ScoreValue> scoreValues = new LinkedList<>();

            // TODO test - Fill history with random points
            discreteScoreHistory.clear();
            long time = new Date().getTime() / 1000L;
            Random random = new Random();
            int num = 1 + random.nextInt(HISTORY_UPDATE_INTERVALS.length - 1);
            for (int i = 0; i < num; ++i)
            {
                value = new ScoreValue();
                value.numTotalGuesses = 1 + random.nextInt(100);
                value.numCorrectGuesses = random.nextInt(value.numTotalGuesses + 1);
                value.time = time - HISTORY_UPDATE_INTERVALS[i];
                scoreValues.add(value);

                if (i == 0)
                    this.value = value;
            }

            // Add the points in the history to the list
            for (ScoreValue v: discreteScoreHistory.values)
                if (v != null)
                    scoreValues.add(v);

            // Clear the history and re-add the points
            discreteScoreHistory.clear();
            discreteScoreHistory.addValuesToHistory(scoreValues);

            // Re-save the history
            SharedPreferences.Editor editor = savedChordScores.edit();
            saveHistory(editor);
            editor.apply();
        }

        /**
         * Writes the history of this CurrentScoreWrapper without applying changes.
         * @param scoreEditor The SharedPreferences.Editor to which to write
         */
        private void saveHistory(SharedPreferences.Editor scoreEditor)
        {
            // Save each element of the history
            ScoreValue value;
            for (int i = 0; i < discreteScoreHistory.values.length; ++i)
            {
                value = discreteScoreHistory.values[i];

                if (value == null)
                    scoreEditor.putBoolean(CHORD_NAME + "-b" + i, false);
                else
                {
                    scoreEditor.putBoolean(CHORD_NAME + "-b" + i, true);
                    value.save(scoreEditor, CHORD_NAME, i++);
                }
            }
        }
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
            long time = new Date().getTime() / 1000;

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
        /** The time stamp of this ScoreTimeValue */
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
    }
}

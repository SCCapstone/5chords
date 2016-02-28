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
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Score
{
    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** The maximum number of scores to keep in the history */
    public static final int NUM_SCORES_TO_KEEP = 100;

    /** The amount of time to wait between adding scores to the continuous history, in milliseconds */
    public static final long SCORE_UPDATE_INTERVAL = 12L * 3600L * 1000L;

    /** Tags for the score history view */
    public static final String[] HISTORY_TAGS = new String[] {"Today", "Earlier Today", "Yesterday", "A Day Ago",
                                            "Earlier this Week", "Last Week", "Earlier this Month",
                                            "Last Month", "Earlier this Year", "Last Year"};

    /** Update times for each history tag, in milliseconds */
    public static final long[] HISTORY_UPDATE_INTERVALS = new long[] {2L * 3600000L, 12L * 3600000L, 24L * 3600000L,
                                                            72L * 3600000L, 168L * 3600000L, 336L * 3600000L,
                                                            672L * 3600000L, 4032L * 3600000L, 8064L * 3600000L};

    /** The array of chord Scores */
    public static ScoreWrapper[] scores;

    /**
     * Static class.
     */
    private Score()
    {    }

    /*********************************************************
     * Gets the SharedPreferences used to load and save scores
     * @param activity The calling Activity
     **/
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

    /***********************************************************************************************
     * Loads the score data for each chord and initializes the correctChords and totalChords arrays.
     * @param main The calling Activity
     * @param overwrite Whether or not to overwrite the score history if it is already loaded
     **/
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

    /************************************************************
     * Updates the score for the chord of the given index.
     * @param activity The calling Activity
     * @param chordIndex The index of the chord
     * @param correct Whether or not the chord guess was correct
     **/
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

    /************************************************************************************************
     * Wrapper class for a single chord score that represents a most up to date version of that score,
     * and contains a history of that score.
     */
    public static class ScoreWrapper
    {
        /** The chord name of this ScoreWrapper */
        public final String CHORD_NAME;

        /** Records the value of this ScoreWrapper */
        private ScoreTimeValue value;

        /** Records the history of this score */
        private LinkedList<ScoreTimeValue> scoreHistory;

        /** The history of this ScoreWrapper split into discrete bins for scores of different age categories */
        private DiscreteScoreHistory discreteScoreHistory;

        /**
         * Constructs a new ScoreWrapper with default scores of zero.
         * @param name The name of the chord
         */
        public ScoreWrapper(String name)
        {
            CHORD_NAME = name;
            value = new ScoreTimeValue();
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
         * Loads this ScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences from which to load
         */
        public void load(SharedPreferences savedChordScores)
        {
            // Read from index zero (default)
            value.load(savedChordScores, CHORD_NAME, 0);

            // Update history
            updateHistory(savedChordScores);
        }

        /*****************************************************************
         * Loads this CurrentScoreWrapper from the given SharedPreferences.
         * @param savedChordScores The SharedPreferences to which to save
         **/
        public void save(SharedPreferences savedChordScores)
        {
            // Update history
            updateHistory(savedChordScores);
        }

        /**
         * Loads the discrete history of this ScoreWrapper.
         * @param activity The current Activity
         */
        public void loadDiscreteHistory(Activity activity)
        {
            // Create the discrete history if needed
            if (discreteScoreHistory == null)
                discreteScoreHistory = new DiscreteScoreHistory();
            else
                discreteScoreHistory.clear();

            // Load the continuous history
            if (scoreHistory == null)
                loadHistory(getScoreLoader(activity));

            // Add the continuous history to the discrete history
            discreteScoreHistory.addValuesToHistory(scoreHistory);
        }

        /**
         * Gets the continuous score history of this ScoreWrapper.
         * @return The continuous score history of this ScoreWrapper
         */
        public LinkedList<ScoreTimeValue> getHistory()
        {
            return scoreHistory;
        }

        /**
         * Gets the discrete score history of this ScoreWrapper.
         * @return The discrete score history of this ScoreWrapper
         */
        public DiscreteScoreHistory getDiscreteHistory()
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
                    (100.0 * value.numCorrectGuesses / + value.numTotalGuesses) + " %), " + new Date(value.time).toString();
        }

        /**
         * Updates the history of this CurrentScoreWrapper, adding a new instance of this ScoreWrapper if needed.
         * @param savedChordScores The SharedPreferences from which to load
         */
        private void updateHistory(SharedPreferences savedChordScores)
        {
            // Calculate the current time
            long time = new Date().getTime();

            // If this score has aged past the update interval, add it
            if (this.value.time - time > SCORE_UPDATE_INTERVAL)
            {
                // Load old history if needed
                if (scoreHistory == null)
                    loadHistory(savedChordScores);

                // Add this score to the history
                scoreHistory.addFirst(this.value);

                // Remove last point if too history too large (Actually average together last and next to last)
                if (scoreHistory.size() > NUM_SCORES_TO_KEEP)
                {
                    ScoreTimeValue last = scoreHistory.removeLast();

                    // Average with next last
                    ScoreTimeValue newLast = scoreHistory.getLast();
                    newLast.numCorrectGuesses += last.numCorrectGuesses;
                    newLast.numTotalGuesses += last.numTotalGuesses;
                    newLast.time = (newLast.time + last.time) / 2;
                }

                // Re-save the history
                SharedPreferences.Editor editor = savedChordScores.edit();
                saveHistory(editor);
                editor.apply();
            }
            else if (scoreHistory != null)
            {
                // Update first element of the history
                if (scoreHistory.isEmpty())
                    scoreHistory.add(value);
                else
                {
                    scoreHistory.removeFirst();
                    scoreHistory.addFirst(value);
                }

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
            if (scoreHistory == null)
                scoreHistory = new LinkedList<>();
            else
                scoreHistory.clear();

            // Load the size of the history
            int size = savedChordScores.getInt(CHORD_NAME + "-n", 0);
            ScoreTimeValue value;

            // Load each element of the history
            for (int i = 0; i < size; ++i)
            {
                value = new ScoreTimeValue();
                value.load(savedChordScores, CHORD_NAME, i);
                scoreHistory.add(value);
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

            // Save each element of the history
            int i = 0;
            for (ScoreTimeValue value: scoreHistory)
            {
                value.save(scoreEditor, CHORD_NAME, i++);
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
         * Adds a list of ScoreTimeValue to this DiscreteScoreHistory, putting each value in an appropriate bin.
         * @param values The list of ScoreTimeValues to add
         */
        public void addValuesToHistory(List<ScoreTimeValue> values)
        {
            // Get the current time
            long time = new Date().getTime();

            // Loop over the values
            int bin;
            long age;
            for (ScoreTimeValue value: values)
            {
                // Calculate the age of the value
                age = time - value.time;

                // Determine the bin for the current value
                if (age < HISTORY_UPDATE_INTERVALS[0])
                    bin = 0;
                else
                {
                    bin = -1;
                    for (int i = 0; bin == -1 && i < HISTORY_UPDATE_INTERVALS.length; ++i)
                    {
                        if (age < HISTORY_UPDATE_INTERVALS[i])
                            bin = i + 1;
                    }

                    if (bin == -1)
                        bin = HISTORY_UPDATE_INTERVALS.length;
                }

                // Add the value to the bin
                if (this.values[bin] == null)
                {
                    ++size;
                    this.values[bin] = new ScoreValue();
                }

                this.values[bin].numCorrectGuesses += value.numCorrectGuesses;
                this.values[bin].numTotalGuesses += value.numTotalGuesses;
            }
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
        }
    }

    /**
     * Wrapper class extending the ScoreValue class to also contain a timestamp.
     */
    public static class ScoreTimeValue extends ScoreValue
    {
        /** The time stamp of this ScoreTimeValue */
        public long time;

        /**
         * Loads this ScoreValue from the given SharedPreferences.
         * @param sharedPreferences The SharedPreferences from which to load
         * @param name The name to attach to this ScoreValue
         * @param index The index to attach to this ScoreValue
         */
        @Override
        public void load(SharedPreferences sharedPreferences, String name, int index)
        {
            super.load(sharedPreferences, name, index);
            time = sharedPreferences.getLong(name + "-" + index + "-h", new Date().getTime());
        }

        /**
         * Saves this ScoreValue to the given SharedPreferences.Editor.
         * @param editor The SharedPreferences.Editor to which to write
         * @param name The name to attach to this ScoreValue
         * @param index The index to attach to this ScoreValue
         */
        @Override
        public void save(SharedPreferences.Editor editor, String name, int index)
        {
            super.save(editor, name, index);
            editor.putLong(name + "-" + index + "-h", time);
        }
    }
}

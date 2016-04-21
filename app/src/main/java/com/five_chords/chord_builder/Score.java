package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.AlertFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class encapsulating the Score of a single chord. Each Score keeps track of the total number of correct and
 * incorrect chord guesses as well as a series of snapshots for a history. Scores can be saved and loaded.
 * Score also contains a method to clear the entire Score history for the application.
 * @date 04 April 2016
 * @author: Drea,Steven,Zach,Kevin,Bo,Theodore
 **/
public class Score
{
    /** The name of the saved chord scores in the SharedPreferences */
    public static final String CHORD_SCORES_SAVE_FILENAME = "ScoreFile";

    /** The Bundle id for the number of saved scores. */
    private static final String NUM_SAVED_SCORES_BUNDLE_ID = "Score.NUM_SAVED_SCORES";

    /** The Bundle id a single saved score. */
    private static final String SAVED_SCORE_BUNDLE_ID = "Score.SAVED_SCORE";

    /** Tags for the score history view */
    public static final String[] HISTORY_TAGS = new String[] {"Current", "1H", "2H", "6H", "12H", "1D",
                                                "2D", "4D", "1W", "2W", "1M", "2M", "6M", "1Y"};

    /** Update times for each history tag, in seconds */
    public static final long[] HISTORY_UPDATE_INTERVALS = new long[] {0L, 3600L, 2 * 3600L, 6 * 3600L,
                                                                        12 * 3600L, 24 * 3600L, 48 * 3600L,
                                                                        96 * 3600L, 168 * 3600L, 336 * 3600L,
                                                                        672 * 3600L, 1344 * 3600L, 4032 * 3600L, 8064 * 3600L};

    /** The map of used scores. */
    private static Map<Long, Score> scores = new HashMap<>();

    /** The chord id of this Score. */
    public final long CHORD_ID;

    /** The id of this Score. */
    public final String SCORE_ID;

    /** Records the overall value of this Score. */
    private ScoreValue overallValue;

    /** Records the current value of this Score. */
    private ScoreValue currentValue;

    /** The history of this Score split into discrete bins for scores of different age categories */
    private DiscreteScoreHistory discreteScoreHistory;

    /**
     * Constructs a new Score for the given chord.
     * @param chord The Chord for the Score
     */
    public Score(Chord chord)
    {
        this (chord.FUNDAMENTAL, chord.TYPE);
    }

    /**
     * Constructs a new Score for the given chord.
     * @param fundamental The fundamental note of the chord for the Score
     * @param type The ChordType of the chord for the Score
     */
    public Score(int fundamental, Chord.ChordType type)
    {
        CHORD_ID = Chord.getChordId(fundamental, type);
        SCORE_ID = "" + CHORD_ID;
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
        return getScore(ChordHandler.getCurrentSelectedChord());
    }

    /**
     * Gets the Score for the given Chord.
     * @param chord The Chord whose Score to get.
     * @return The Score for the given Chord
     */
    public static Score getScore(Chord chord)
    {
        Score score = scores.get(chord.ID);

        if (score == null)
            score = new Score(chord);

        return score;
    }

    /**
     * Called to reset the scores.
     * @param activity The current Activity
     * @param onScoresReset Optional Runnable to be executed when and if the scores are actually reset
     */
    public static void resetScores(final Activity activity, final Runnable onScoresReset)
    {
        // Launch dialog
        AlertFragment alert = AlertFragment.newInstance(R.string.clear_scores, R.string.clearscore_dialog_message);

        alert.setYesAction(new Runnable()
        {
            @Override
            public void run()
            {
                // Clear the history
                SharedPreferences savedChordScores = getScoreLoader(activity);
                SharedPreferences.Editor editor = savedChordScores.edit();
                editor.clear();
                editor.apply();
                scores.clear();

                // Reload scores
                loadScores(activity, true);

                // Show confirmation toast
                Toast.makeText(activity, "Scores cleared", Toast.LENGTH_SHORT).show();

                // Run action
                if (onScoresReset != null)
                    onScoresReset.run();
            }
        });

        alert.show(activity.getFragmentManager(), "alert");
    }

    /**
     * Loads the score data for each chord.
     * @param main The calling Activity
     * @param overwrite Whether or not to overwrite the score history if it is already loaded
     */
    public static void loadScores(Activity main, boolean overwrite)
    {
        if (!overwrite && !scores.isEmpty())
            return;

        // Clear score array
        scores.clear();

        // Load scores
        final SharedPreferences savedChordScores = getScoreLoader(main);
        int numScores = savedChordScores.getInt(NUM_SAVED_SCORES_BUNDLE_ID, 0);

        // Read elements
        long id;
        Chord chord;
        Score score;
        for (int i = 0; i < numScores; ++i)
        {
            id = savedChordScores.getLong(SAVED_SCORE_BUNDLE_ID + i, -1L);

            if (id != -1L)
            {
                chord = ChordHandler.getChord(id);
                score = new Score(chord);
                score.load(savedChordScores);
                scores.put(id, score);
            }
        }
    }

    /**
     * Updates and saves the list containing which Scores have been loaded.
     * @param score The new Score to include
     * @param preferences The SharedPreferences to use to save
     */
    private static void updateSavedScores(Score score, SharedPreferences preferences)
    {
        if (scores.containsKey(score.CHORD_ID))
            return;

        SharedPreferences.Editor editor = preferences.edit();

        // Update the set
        scores.put(score.CHORD_ID, score);

        // Write the number of elements in the set
        editor.putInt(NUM_SAVED_SCORES_BUNDLE_ID, scores.size());

        // Write the elements of the set
        int i = 0;
        for (long L: scores.keySet())
            editor.putLong(SAVED_SCORE_BUNDLE_ID + (i++), L);

        editor.apply();
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
    public void load(SharedPreferences savedChordScores)
    {
        // Load the history
        discreteScoreHistory.load(savedChordScores, SCORE_ID);

        // Load the overall value
        overallValue.load(savedChordScores, SCORE_ID, -1);
    }

    /**
     * Saves this Score to the given SharedPreferences.
     * @param savedChordScores The SharedPreferences to which to save
     */
    public void save(SharedPreferences savedChordScores)
    {
        SharedPreferences.Editor editor = savedChordScores.edit();

        // Save the history
        discreteScoreHistory.save(editor, SCORE_ID);

        // Save the overall value
        overallValue.save(editor, SCORE_ID, -1);

        editor.apply();

        // Make sure this Score is saved in the global list
        updateSavedScores(this, savedChordScores);
    }

    /**
     * Wrapper class for a score history split into bins.\
     * @author tstone95
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
     * @author tstone95
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

        /**
         * Gets a String for displaying the value of this ScoreValue as a percentage.
         * @return A String for displaying the value of this ScoreValue as a percentage
         */
        public String getDisplayPercentageString()
        {
            return "" + Math.round(numCorrectGuesses * 100.0 / numTotalGuesses) + " %";
        }
    }
}

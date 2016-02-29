/***************************************************************************************************
 * chordHandler.java
 * This class will loop through raw folder for each music note. User can pick a chord by either
 * random or their selection check the chord to see if it matches.Play the user chosen chord or at
 * random. Give back the feedback on screen "Correct" or "Wrong" accordingly.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class chordHandler
{
    /** Denotes a 'small' number of times that the user has guessed the wrong chord */
    private static final int NUM_TIMES_WRONG_SMALL = 2;

    /** Denotes a 'medium' number of times that the user has guessed the wrong chord */
    private static final int NUM_TIMES_WRONG_MEDIUM = 4;

    /** Denotes a 'large' number of times that the user has guessed the wrong chord */
    private static final int NUM_TIMES_WRONG_LARGE = 8;

    /** The number of chords per type (Major, Minor, Dominant) */
    private static final int CHORDS_PER_TYPE = 12;

    /** The minimum number of notes per chord (for Major and Minor) */
    private static final int MIN_NOTES_PER_CHORD = 3;

    /** The maximum number of notes per chord (for Dominant) */
    private static final int MAX_NOTES_PER_CHORD = 4;

    /** Array containing the currently available chords */
    private static int[][] availableChords = null;

    /** Records the current chord index */
    private static int currentChordIndex;

    /** Records the current wrong streak of the current chord for hinting purposes. */
    private static int currentWrongStreak;

    /**
     * Static class.
     */
    private chordHandler()
    {   }

    /**
     * Called to initialize this chordHandler.
     */
    public static void initialize()
    {
        clearChords();
        addMajorChords();
        addMinorChords();
        addDominantChords();
    }

    /**
     * Get the chord array at the current chord index.
     * @return The current chord array
     */
    public static int[] getCurrentChord() {
        return availableChords[currentChordIndex];
    }

    /**
     * Called to set the index of the currently selected chord.
     * @param chordIndex The index of the new chord
     */
    public static void setSelectedChord(int chordIndex) {

        // Reset wrong streak if needed
        if (chordIndex != currentChordIndex)
            currentWrongStreak = 0;

        currentChordIndex = chordIndex;
    }

    /**
     * Gets the index of the currently selected chord.
     * @return The index of the currently selected chord
     */
    public static int getSelectedChord() {
        return currentChordIndex;
    }

    /**
     * Changes the current chord to a random chord.
     */
    public static void getRandomChord() {
        int previousChordIndex = currentChordIndex;
        int newChordIndex;
        Random random = new Random();

        // Make sure new chord index is different than the previous
        do
        {
            newChordIndex = random.nextInt(availableChords.length - 1);
        }
        while (newChordIndex == previousChordIndex);

        setSelectedChord(newChordIndex);
    }

    /**
     * Clears the available chord array.
     */
    public static void clearChords() {
        availableChords = null;
    }

    /**
     * Adds the major chords to the available chord array.
     */
    public static void addMajorChords() {

        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 4, note + 7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the minor chords to the available chord array.
     */
    public static void addMinorChords() {

        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 3, note + 7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the dominant chords to the available chord array.
     */
    public static void addDominantChords() {
        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 4, note + 7, note + 10};
        }

        availableChords = returnArray;
    }

    /**
     * Checks whether two chords (as integer arrays) are equivalent.
     * @param builtChord The array containing the notes of the built chord
     * @param setChord The array containing the notes of the set chord
     */
    public static boolean compareChords(int[] builtChord, int[] setChord) {
        if (setChord == null || builtChord == null) return false;
        else if (setChord.length > MAX_NOTES_PER_CHORD || builtChord.length > MAX_NOTES_PER_CHORD) return false;
        else if (setChord.length < MIN_NOTES_PER_CHORD || builtChord.length < MIN_NOTES_PER_CHORD) return false;
        else return Arrays.equals(builtChord, setChord);
    }

    /**
     * Builds the current chord that the user has defined on the sliders.
     * @return An array containing the root, third, fifth, and option values of the built chord
     */
    public static int[] buildCurrentChord(Activity activity)
    {
        int root = ((SeekBar) activity.findViewById(R.id.slider_root)).getProgress();
        int third = ((SeekBar) activity.findViewById(R.id.slider_third)).getProgress();
        int fifth = ((SeekBar) activity.findViewById(R.id.slider_fifth)).getProgress();
        int seventh = ((SeekBar) activity.findViewById(R.id.slider_option)).getProgress();

        if (getCurrentChord().length == MIN_NOTES_PER_CHORD)
        {
            return new int[]{root, third, fifth};
        }
        else
        {
            return new int[]{root, third, fifth, seventh};
        }
    }

    /**
     * Called when the user checks the current chord.
     * @param activity Handle to the MainActivity
     */
    public static void checkCurrentChord(final MainActivity activity)
    {
        boolean isCorrect = chordHandler.compareChords(chordHandler.buildCurrentChord(activity),
                chordHandler.getCurrentChord());

        // Handle result TODO add sounds for right and wrong
        if (isCorrect)
        {
            // Launch dialog TODO maybe replace with a custom dialog that also shows the current score info for the chord
            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.thats_correct))
                    .setMessage("Do you want to try another chord?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getRandomChord();
                            activity.updateChordSelectSpinner();
                            setUpGUI.resetChordSliders(activity);
                            soundHandler.stopSound();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setSelectedChord(getSelectedChord()); // Resets the wrong streak counter
                            setUpGUI.resetChordSliders(activity);
                            soundHandler.stopSound();
                        }

                    })
                    .show();
        }
        else
        {
            // Increment the wrong streak counter
            currentWrongStreak++;

            // Show hints
            Log.e("HINTS", "UseHints = " + MainActivity.getOptions().useHints + ", Current Streak = " + currentWrongStreak);
            if (MainActivity.getOptions().useHints)
            {
                if (currentWrongStreak > NUM_TIMES_WRONG_LARGE)
                {
                    activity.makeHintOne(); // TODO
                }
                else if (currentWrongStreak > NUM_TIMES_WRONG_MEDIUM)
                {
                    activity.makeHintOne(); // TODO
                }
                else if (currentWrongStreak > NUM_TIMES_WRONG_SMALL)
                {
                    activity.makeHintOne();
                }
            }

            // Show toast
            Toast.makeText(activity, activity.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT).show();
        }

        // Set the score
        Score.setScore(activity, chordHandler.getSelectedChord(), isCorrect);
        activity.displayAnswer();
    }

    /**
     * Creates a blank chord array, which will contain the currently available chords if non-null.
     * @return A blank chord array
     */
    private static int[][] createEmptyChordArray()
    {
        int[][] returnArray;

        if (availableChords == null)
            returnArray = new int[CHORDS_PER_TYPE][];
        else
        {
            returnArray = new int[availableChords.length + CHORDS_PER_TYPE][];
            System.arraycopy(availableChords, 0, returnArray, 0, availableChords.length);
        }

        return returnArray;
    }
}

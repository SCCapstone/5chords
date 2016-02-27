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
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

public class chordHandler
{
    private static final int CHORDS_PER_TYPE = 12;
    private static final int MIN_NOTES_PER_CHORD = 3;
    private static final int MAX_NOTES_PER_CHORD = 4;

    private static int[][] availableChords = new int[0][];
    private static int currentChordIndex;

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
     * Clears the available chord array.
     */
    public static void clearChords() {
        availableChords = new int[0][];
    }

    /**
     * Adds the major chords to the available chord array.
     */
    public static void addMajorChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        System.arraycopy(availableChords, 0, returnArray, 0, len);

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+4, note+7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the minor chords to the available chord array.
     */
    public static void addMinorChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        System.arraycopy(availableChords, 0, returnArray, 0, len);

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+3, note+7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the dominant chords to the available chord array.
     */
    public static void addDominantChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        System.arraycopy(availableChords, 0, returnArray, 0, len);

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+4, note+7, note+10};
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
     * Change current chord to the one selected
     * @param newChordIndex The index of the new chord
     */
    public static int getSelectedChord(int newChordIndex) {
        currentChordIndex = newChordIndex - 1;
        return currentChordIndex;
    }

    /**
     * Change current chord to a random chord
     */
    public static int getRandomChord() {
        int previousChordIndex = currentChordIndex;
        Random random = new Random();

        // Make sure new chord index is different than the previous
        do
        {
            currentChordIndex = random.nextInt(availableChords.length - 1);
        }
        while (currentChordIndex == previousChordIndex);

        return currentChordIndex;
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
                            activity.updateSpinner();
                            setUpGUI.resetChordSliders(activity);
                            soundHandler.stopSound();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setUpGUI.resetChordSliders(activity);
                            soundHandler.stopSound();
                        }

                    })
                    .show();
        }
        else
        {
            // Show toast
            Toast.makeText(activity, activity.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT).show();
        }

        // Set the score
        Score.setScore(activity, chordHandler.getCurrentChordIndex(), isCorrect);
        activity.displayAnswer();
    }

    /****************************************************************
     * Return internal variables
     **/
    public static int getCurrentChordIndex() { return currentChordIndex; }
    public static int[] getChord(int chordIndex) { return availableChords[chordIndex]; }
    public static int[] getCurrentChord() {
        return availableChords[currentChordIndex];
    }
}

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
import android.util.Log;
import android.widget.SeekBar;

import java.util.Arrays;
import java.util.Random;

public class chordHandler {

    static final int CHORDS_PER_TYPE = 12;
    static final int MIN_NOTES_PER_CHORD = 3;
    static final int MAX_NOTES_PER_CHORD = 4;

    static int[][] availableChords = new int[0][];
    static int currentChordIndex;

    public chordHandler() {
        clearChords();
        addMajorChords();
        addMinorChords();
        addDominantChords();
    }

    public void clearChords() {
        availableChords = new int[0][];
    }

    public void addMajorChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        for (int i = 0; i < len; i++) {
            returnArray[i] = availableChords[i];
        }

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+4, note+7};
        }

        availableChords = returnArray;
    }

    public void addMinorChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        for (int i = 0; i < len; i++) {
            returnArray[i] = availableChords[i];
        }

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+3, note+7};
        }

        availableChords = returnArray;
    }

    public void addDominantChords() {
        int len = availableChords.length;
        int[][] returnArray = new int[len + CHORDS_PER_TYPE][];

        for (int i = 0; i < len; i++) {
            returnArray[i] = availableChords[i];
        }

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i%CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note+4, note+7, note+10};
        }

        availableChords = returnArray;
    }

    /****************************************************************
     * Checks whether two chords (as integer arrays) are equivalent.
     **/
    public boolean compareChords(int[] builtChord, int[] setChord) {
        if (setChord == null || builtChord == null) return false;
        else if (setChord.length > MAX_NOTES_PER_CHORD || builtChord.length > MAX_NOTES_PER_CHORD) return false;
        else if (setChord.length < MIN_NOTES_PER_CHORD || builtChord.length < MIN_NOTES_PER_CHORD) return false;
        else return Arrays.equals(builtChord, setChord);
    }

    /**
     * Change current chord to the one selected
     * @param newChordIndex The index of the new chord
     */
    public int getSelectedChord(int newChordIndex) {
        currentChordIndex = newChordIndex - 1;
        return currentChordIndex;
    }

    /**
     * Change current chord to a random chord
     */
    public int getRandomChord() {
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
    public int[] buildCurrentChord(Activity activity)
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

    /****************************************************************
     * Return internal variables
     **/
    public int getCurrentChordIndex() { return currentChordIndex; }
    public int[] getChord(int chordIndex) { return availableChords[chordIndex]; }
    public int[] getCurrentChord() {
        return availableChords[currentChordIndex];
    }
}

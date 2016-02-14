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
import java.util.Arrays;
import java.util.Random;

public class chordHandler
{
    String[] chordNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B",
            "C_minor", "C#_minor","D_minor","Eb_minor","E_minor","F_minor","F#_minor","G_minor","Ab_minor","A_minor","Bb_minor","B_minor"};

    int[][] chords = new int[12*2][];

    public chordHandler() {
        for (int i = 0; i < 12; i++) {
            chords[i] = new int[] {i, i+4, i+7};
            chords[12+i] = new int[] {i, i+3, i+7};
            //chords[24+i] = {i, i+4, i+7, i+10};
        }
    }

    /****************************************************************
     * Picks a Chord. Either random, or the selected chord.
     **/
    public int newChordIndex(int chordChoice)
    {
        if (chordChoice > 0) return chordChoice - 1;
        else return new Random().nextInt(chordNames.length - 1);
    }

    /****************************************************************
     * Checks whether two chords (as integer arrays) are equivalent.
     **/
    public boolean compareChords(int[] builtChord, int[] setChord)
    {
        if (setChord == null || builtChord == null) return false;
        if (setChord.length > 4 || builtChord.length > 4) return false;
        if (setChord.length < 3 || builtChord.length < 3) return false;

        if (setChord.length == 3) {
            setChord = new int[] {setChord[0], setChord[1], setChord[2], setChord[2]};
            builtChord = new int[] {builtChord[0], builtChord[1], builtChord[2], builtChord[2]};
        }

        return Arrays.equals(builtChord, setChord);
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playChord(int[] chord, int chordLength, int instrument)
    {
        if (chord == null) return;
        else if (chordLength > 4) return;
        else if (chordLength < 0) return;
    }

    /****************************************************************
     * Returns the name of a chord given the index
     **/
    public String getChordName(int chordIndex) {
        return chordNames[chordIndex];
    }

    /****************************************************************
     * Returns a chord given the index
     **/
    public int[] getChord(int chordIndex) {
        return chords[chordIndex];
    }
}

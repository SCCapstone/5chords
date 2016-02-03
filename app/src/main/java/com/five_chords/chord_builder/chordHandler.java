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
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class chordHandler extends MainActivity
{

    ArrayList<Integer> notes;
    SoundPool mySound;
    SeekBar[] seekBars;

    int chordIndex;
    final int LOOP = 3;
    int[] setChord, builtChord;

    //Since we have a WAV file with less than 1mb all we have to change the loop parameter to -1,
    // variable LOOP is where i set the parameter to reloop the clip
//    int[][] chords = {
//            // this organization makes it easier on the score code
//            // also easier to debug if the client wants to add more chord types (diminished or when we add the fourth slider)
//
//            // major chords
//            {0,4,7}, {1,5,8}, {2,6,9},          // C, C#, D
//            {3,7,10}, {4,8,11}, {5,9,12},       // Eb, E, F
//            {6,10,13}, {7,11,14}, {8,12,15},    // F#, G, Ab
//            {9,13,16}, {10,14,17}, {11,15,18},  // A, Bb, B
//
//            // minor chords
//            {0,3,7}, {1,4,8}, {2,5,9},           // C, C#, D
//            {3,6,10}, {4,7,11}, {5,10,12},       // Eb, E, F
//            {6,9,13}, {7,10,14}, {8,11,15},      // F#, G, Ab
//            {9,12,16}, {10,13,17}, {11,14,18}};  // A, Bb, B

    public void initialize(Activity activity)
    {
        mySound =  new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        notes = new ArrayList<>();

        // Loop through raw folder for notes
        for (Field f : R.raw.class.getFields())
        {
            try {
                if (f.getName().contains("note"))
                {
                    notes.add(mySound.load(activity, f.getInt(null), 0));
                }
            } catch (IllegalAccessException e) {

            }
        }

        seekBars = new SeekBar[] {
                (SeekBar) activity.findViewById(R.id.root),
                (SeekBar) activity.findViewById(R.id.third),
                (SeekBar) activity.findViewById(R.id.fifth),
                (SeekBar) activity.findViewById(R.id.option) };
    }


    /****************************************************************
     * Picks a Chord. Either random, or the selected chord.
     **/
    public void getChord(Activity activity)
    {
        // Grab chord index array from resources
        TypedArray chordIndices = activity.getResources().obtainTypedArray(R.array.chordIndices);

        // Determine chordIndex from slider position
        int pos = ((Spinner) activity.findViewById(R.id.spinner2)).getSelectedItemPosition();

        // A non random chord was selected
        if (pos > 0)
            chordIndex = pos - 1;
            // A random chord was selected
        else
            chordIndex =  new Random().nextInt(chordIndices.length() - 1);

//        setChord = chords[chordIndex];
        // Grab the id of the select chord array from the chord index array
        int id = chordIndices.getResourceId(chordIndex, 0);
        setChord = activity.getResources().getIntArray(id);
        playChord(activity, setChord, LOOP);

        // Recycle chordIndices
        chordIndices.recycle();
    }

    /****************************************************************
     * Create the chord made on the sliders, call the check methos
     **/
    public void buildChord(Activity activity, Score s)
    {
        // this will come in handy when we start using the fourth slider
        if (setChord.length == 3) builtChord = new int[] {seekBars[0].getProgress(), seekBars[1].getProgress(),
                seekBars[2].getProgress()};
        else builtChord = new int[] {seekBars[0].getProgress(), seekBars[1].getProgress(),
                seekBars[2].getProgress(), seekBars[3].getProgress()};

        playChord(activity, builtChord, 0);
        // shows if the built chord matches the set chord
        TextView answerLabel = (TextView) activity.findViewById(R.id.answer);
        TextView tv = (TextView) activity.findViewById(R.id.chord);
        tv.setText(Score.correctChords[chordIndex] + " / " + Score.totalChords[chordIndex]);

        if (checkChord(builtChord, setChord)) {
            answerLabel.setText(R.string.correct);
            s.setScore(activity, chordIndex, true);
        } else {
            answerLabel.setText(R.string.wrong);
            s.setScore(activity, chordIndex, false);
        }
    }

    /****************************************************************
     * Checks chord built against random chord
     **/
    public boolean checkChord(int[] builtChord, int[] setChord)
    {
        if (setChord == null) return false;

        if (Arrays.equals(builtChord, setChord))
        {
            return true;
        } else {
            return false;
        }
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playChord(Activity activity, int[] chord, int loop)
    {
        if (chord == null)
            return;

        // Show correct chord on a label
        TextView chordLabel = (TextView) activity.findViewById(R.id.chord);
//        chordLabel.setText(chordNames[chord[0]]);
        chordLabel.setText(activity.getResources().getStringArray(R.array.chordNames)[chord[0]]);

        // play the chord again
        for (int i : chord)
            mySound.play(notes.get(i), 1, 1, 1, loop, .99f);
    }

}

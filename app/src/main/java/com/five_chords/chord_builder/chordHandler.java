package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

public class chordHandler extends MainActivity {

    String[] chordNames = {"C","C_minor", "C#","C#_minor", "D","D_minor", "Eb","Eb_minor", "E","E_minor", "F","F_minor", "F#","F#_minor", "G","G_minor", "Ab","Ab_minor", "A","A_minor", "Bb","Bb_minor", "B","B_minor"};
    SoundPool mySound;
    int LOOP =3;

    //Since we have a WAV file with less than 1mb all we have to change the loop parameter to -1,
    // variable LOOP is where i set the parameter to reloop the clip
    // major chords
    int[][] chords = {{0,4,7},{0,3,7}, {1,5,8},{1,4,8}, {2,6,9},{2,5,9},// C,C minor, C#,C# minor, D,D minor
            {3,7,10},{3,6,10}, {4,8,11},{4,7,11}, {5,9,12},{5,8,12},       // Eb,Eb minor, E,E minor, F,F minor
            {6,10,13},{6,9,13}, {7,11,14},{7,10,14}, {8,12,15},{8,11,15},    // F#,F# minor, G,G minor, Ab,Ab minor
            {9,13,16},{9,12,16}, {10,14,17},{10,13,17}, {11,15,18},{11,14,18}}; // A,A minor, Bb,Bb minor, B,B minor
    ArrayList<Integer> notes;
    int chordRoot, chordThird, chordFifth, chordIndex;
    int[] chord;

    public void initialize(Activity activity) {

        mySound =  new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        notes = new ArrayList<>();

        // Loop through raw folder for notes
        for (Field f : R.raw.class.getFields()) {
            try {
                if (f.getName().contains("note")) {
                    notes.add(mySound.load(activity, f.getInt(null), 0));
                }
            } catch (IllegalAccessException e) {

            }
        }
    }


    /****************************************************************
     * Picks a Random Chord and plays it
     **/
    public void getChord() {

        // new random int from 0-11
        Random rand = new Random();
        chordIndex = rand.nextInt(chords.length-1);
        chord = chords[chordIndex];

        // get notes in chord
        chordRoot = notes.get(chord[0]);
        chordThird = notes.get(chord[1]);
        chordFifth = notes.get(chord[2]);

        //play chord
        mySound.play(chordRoot,1,1,1,LOOP,0.99f);
        mySound.play(chordThird,1,1,1,LOOP,0.99f);
        mySound.play(chordFifth,1,1,1,LOOP,0.99f);

    }

    /****************************************************************
     * check chord built against random chord
     **/
    public void checkChord(Activity activity) {

        // get the seek bars
        SeekBar root = (SeekBar) activity.findViewById(R.id.root);
        SeekBar third = (SeekBar) activity.findViewById(R.id.third);
        SeekBar fifth = (SeekBar) activity.findViewById(R.id.fifth);

        // get the note of each seek bar
        int thisRoot = notes.get(root.getProgress());
        int thisThird = notes.get(third.getProgress());
        int thisFifth = notes.get(fifth.getProgress());

        // play the notes
        // play(SoundID, leftVolume, rightVolume, priority, loop, rate(speed))
        mySound.play(thisRoot,1,1,1,LOOP,0.99f);
        mySound.play(thisThird,1,1,1,LOOP,0.99f);
        mySound.play(thisFifth,1,1,1,LOOP,0.99f);

        // get the labels
        // show correct answer on one label
        // show if the chords match on the other
        TextView answerLabel = (TextView) activity.findViewById(R.id.answer);

        if ((thisRoot == chordRoot) && (thisThird == chordThird) && (thisFifth == chordFifth)) {
            answerLabel.setText("Correct");
            //s.setScore(chordIndex, true);
        } else {
            answerLabel.setText("Wrong");
            //s.setScore(chordIndex, false);
        }

    }

    /****************************************************************
     * Replays the selected chord
     **/
    public void playChord(Activity activity) {
        // show correct chord on a label
        TextView chordLabel = (TextView) activity.findViewById(R.id.chord);
        chordLabel.setText(chordNames[chord[0]]);

        // play the chord again
        mySound.play(chordRoot,1,1,1,LOOP,0.99f);
        mySound.play(chordThird,1,1,1,LOOP,0.99f);
        mySound.play(chordFifth,1,1,1,LOOP,0.99f);
    }

}
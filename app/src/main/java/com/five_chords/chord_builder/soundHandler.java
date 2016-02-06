package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 *
 */
public class soundHandler extends MainActivity {

    ArrayList<Integer> notes;
    SoundPool mySound;

    public soundHandler(Activity main) {
        mySound = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        notes = new ArrayList<>();

        // Loop through raw folder for notes
        for (Field f : R.raw.class.getFields())

        {
            try {
                if (f.getName().contains("note")) {
                    notes.add(mySound.load(main, f.getInt(null), 0));
                }
            } catch (IllegalAccessException e) {

            }
        }
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playNote(int note)
    {
        // only 0-18 are valid, 19 is our escape note (if necessary)
        if (note > 18) return;
        mySound.play(notes.get(note), 1, 1, 1, 0, .99f);
    }
}

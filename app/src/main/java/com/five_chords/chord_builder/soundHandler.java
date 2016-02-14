package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 *
 */
public class soundHandler extends MainActivity {

    static MediaPlayer mediaPlayer;
    static soundHandlerMidi midi;
    static String midiFile;

    public soundHandler(Activity main) {
        midiFile = main.getFilesDir() + "/midi.mid";
        midi = new soundHandlerMidi();
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playNote(int note, int instrument)
    {
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }

        try {
            midi.createMidi(midiFile, instrument, 128, note+60, 127, 8192);
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {}
    }
}

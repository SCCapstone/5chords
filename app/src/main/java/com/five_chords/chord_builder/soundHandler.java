package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

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
     * Plays a note
     **/
    public void playNote(int note, int instrument)
    {
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }

        // TODO: use the sliders to change pitch of note
        //       replace 8192 (centered) with the new pitch
        int mostSignificantbits = (8192 >> 7) & 0x7F;
        int leastSignificantbits = 8192 & 0x7F;

        midi.newMidi();
        midi.progChange(instrument);
        midi.bendPitch(mostSignificantbits, leastSignificantbits);

        midi.noteOn(0, note+60, 127);
        midi.noteOff(128, note+60);

        try {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {}
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playChord(int[] chord, int instrument)
    {
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }

        // TODO: use the sliders to change pitch of note
        //       replace 8192 (centered) with the new pitch
        int mostSignificantbits = (8192 >> 7) & 0x7F;
        int leastSignificantbits = 8192 & 0x7F;

        midi.newMidi();
        midi.progChange(instrument);
        midi.bendPitch(mostSignificantbits, leastSignificantbits);

        // use a silent dummy note to keep chord sustained
        midi.noteOn(0, 0, 0);
        for (int note : chord) midi.noteOn(0, note + 60, 127);
        midi.noteOff(128, 0);
        for (int note : chord) midi.noteOff(0, note + 60);

        try {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(this, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {}
    }
}

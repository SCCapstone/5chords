package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * soundHandler.java
 * @version 1.1
 * @date 3 April 2016
 * @author: Drea,Zach
 **/
public class soundHandler extends MainActivity
{
    /** The tag for this class. */
    private static final String TAG = "soundHandler";

    static final int FULL_VOLUME = 127;
    static final int SUSTAIN_NOTE = 128;
    static final int RELEASE_NOTE = 0;
    static final int NOTE_OFFSET_OCTAVE3 = 60;
    static final int NOTE_OFFSET_OCTAVE4 = 72;

    static final int TRUMPET = 57;
    static final int PIANO = 2;
    static final int ORGAN = 16;
    static final int VIOLIN = 41;
    static final int FLUTE = 74;

    static int instrument = PIANO;

    MediaPlayer mediaPlayer;
    soundHandlerMidi midi;
    String midiFile;

    /**
     * Static class.
     */
    public soundHandler(Activity main, String midiFileName)
    {
        midiFile = main.getFilesDir() + midiFileName + ".mid";
        midi = new soundHandlerMidi();
    }

    /**
     * Stop the media player
     */
    public void stopSound() {
        Log.d(TAG, "Stop Sound");
        try {
            mediaPlayer.release();
        } catch (Exception e) {

        }
    }

    /****************************************************************
     * Add a note to the MIDI file
     **/
    public void addNote(Note note, int channel)
    {
        addNote(note.index, 8192 + (int)(4096 * note.distanceToIndex), channel);
    }

    /****************************************************************
     * Add a note to the MIDI file
     **/
    public void addNote(int note, int pitch, int channel)
    {
        // TODO: use the sliders to change pitch of note
        //       replace 8192 (centered) with the new pitch
        int mostSignificantbits = (pitch >> 7) & 0x7F;
        int leastSignificantbits = pitch & 0x7F;

        int midiOffSet = NOTE_OFFSET_OCTAVE3;

        switch (instrument) {
            case TRUMPET: midiOffSet = NOTE_OFFSET_OCTAVE3;
                          break;
            case PIANO:   midiOffSet = NOTE_OFFSET_OCTAVE3;
                          break;
            case ORGAN:   midiOffSet = NOTE_OFFSET_OCTAVE3;
                          break;
            case VIOLIN:  midiOffSet = NOTE_OFFSET_OCTAVE4;
                          break;
            case FLUTE:   midiOffSet = NOTE_OFFSET_OCTAVE4;
                          break;
        }

        midi.setChannel(channel);
        midi.progChange(instrument);
        midi.bendPitch(mostSignificantbits, leastSignificantbits);
        midi.noteOn(RELEASE_NOTE, note + midiOffSet, FULL_VOLUME);
        midi.noteOff(SUSTAIN_NOTE, note + midiOffSet);
        midi.commitTrack();
    }

    /****************************************************************
     * Plays a chord
     **/
    public void playChord(Activity activity, Note[] chord, int numNotes)
    {
        stopSound();

        midi.newMidi(numNotes, 1);
        for (int i = 0; i < numNotes; i++)
            addNote(chord[i], i);

        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }

        Log.d(TAG, "Done Playing Chord");
    }

    /****************************************************************
     * Plays a note
     **/
    public void playNote(Activity activity, Note note)
    {
        stopSound();

        midi.newMidi(1, 0);
        addNote(note, 1);

        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }

        Log.d(TAG, "Done Playing Note");
    }

    public static void switchInstrument(int i)
    {
        if(i == 0) instrument = TRUMPET;
        if(i == 1) instrument = PIANO;
        if(i == 2) instrument = ORGAN;
        if(i == 3) instrument = VIOLIN;
        if(i == 4) instrument = FLUTE;
    }

    public static int getInstrument() {
        return instrument;
    }
}

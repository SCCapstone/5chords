package com.five_chords.chord_builder;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * Class to handle playing sounds. Sounds are played as MIDI files, which are created with one or more
 * notes using the functions of this class.
 * @version 1.1
 * @date 3 April 2016
 * @author: Drea, Zach, Theodore
 */
public class SoundHandler
{
    /** The tag for this class. */
    private static final String TAG = "soundHandler";

    /** Constant representing full volume. */
    static final int FULL_VOLUME = 127;

    /** Constant denoting that a note should be sustained. */
    static final int SUSTAIN_NOTE = 128;

    /** Constant denoting that a note should be ended. */
    static final int RELEASE_NOTE = 0;

    /** The offset in notes of the third octave. */
    static final int NOTE_OFFSET_OCTAVE3 = 60;

    /** The offset in notes of the fourth octave. */
    static final int NOTE_OFFSET_OCTAVE4 = 72;

    /** Constant id representing a Trumpet. */
    static final int TRUMPET = 57;

    /** Constant id representing a Piano. */
    static final int PIANO = 2;

    /** Constant id representing an organ. */
    static final int ORGAN = 16;

    /** Constant id representing a Violin. */
    static final int VIOLIN = 41;

    /** Constant id representing a Flute. */
    static final int FLUTE = 74;

    /** Reference to the current instrument being used to create sounds. */
    static int instrument = PIANO;

    /** The MediaPlayer to use to play the MIDI files. */
    private MediaPlayer mediaPlayer;

    /** The soundHandlerMidi to use to create the MIDI files. */
    private SoundHandlerMidi midi;

    /** The name of the current MIDI file. */
    private String midiFile;

    /**
     * Creates a new SoundHandler.
     * @param main The current Activity
     * @param midiFileName The name of the MIDI file to use
     */
    public SoundHandler(Activity main, String midiFileName)
    {
        midiFile = main.getFilesDir() + midiFileName + ".mid";
        midi = new SoundHandlerMidi();
    }

    /**
     * Called to change the global instrument being used to play sounds.
     * @param i The index of the new Instrument to use
     */
    public static void switchInstrument(int i)
    {
        if(i == 0) instrument = TRUMPET;
        if(i == 1) instrument = PIANO;
        if(i == 2) instrument = ORGAN;
        if(i == 3) instrument = VIOLIN;
        if(i == 4) instrument = FLUTE;
    }

    /**
     * Gets the index of the current global instrument being used to play sounds.
     * @return The index of the current global instrument being used to play sounds
     */
    public static int getInstrument()
    {
        return instrument;
    }

    /**
     * Stops the media player for this soundHandler.
     */
    public void stopSound() {
        Log.d(TAG, "Stop Sound");
        try {
            mediaPlayer.release();
        } catch (Exception e) {
            /* Ignore */
        }
    }

    /**
     * Adds a note to the MIDI file.
     * @param note The note to add
     * @param channel The channel to which to add the note
     */
    public void addNote(Note note, int channel)
    {
        addNote(note.index, 8192 + (int)(4096 * note.distanceToIndex), channel);
    }

    /**
     * Adds a note to the MIDI file.
     * @param note The note to add, represented by an integer
     * @param pitch The pitch of the note to add, in the range [0, 16383]
     *              (corresponding to an offset in the range [-1, 1] from the integer note index)
     * @param channel The channel to which to add the note
     */
    public void addNote(int note, int pitch, int channel)
    {
        // Calculate the two byte form of the pitch
        int mostSignificantbits = (pitch >> 7) & 0x7F;
        int leastSignificantbits = pitch & 0x7F;

        // The default note offset
        int midiOffSet = NOTE_OFFSET_OCTAVE3;

        // If needed, change the note offset to compensate for the unique sound of the current instrument
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

        // Set the channel, instrument, and pitch of the note in the MIDI file
        midi.setChannel(channel);
        midi.progChange(instrument);
        midi.bendPitch(mostSignificantbits, leastSignificantbits);

        // Set the volume and duration of the note in the MIDI file
        midi.noteOn(RELEASE_NOTE, note + midiOffSet, FULL_VOLUME);
        midi.noteOff(SUSTAIN_NOTE, note + midiOffSet);
        midi.commitTrack();
    }

    /**
     * Plays a chord represented by an array of notes.
     * @param activity The current Activity
     * @param chord The chord to play
     * @param numNotes The number of notes in the chord to play
     */
    public void playChord(Activity activity, Note[] chord, int numNotes)
    {
        // Stop the current sound if needed
        stopSound();

        // Create a new MIDI with the correct number of channels
        midi.newMidi(numNotes, 1);

        // Add the notes to the MIDI
        for (int i = 0; i < numNotes; i++)
            addNote(chord[i], i);

        // Write and play the MIDI file
        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }
    }

    /**
     * Plays a single note.
     * @param activity The current Activity
     * @param note The note to play
     */
    public void playNote(Activity activity, Note note)
    {
        // Stop the current sound if needed
        stopSound();

        // Create a new MIDI with the correct number of channels
        midi.newMidi(1, 0);

        // Add the note to the MIDI
        addNote(note, 1);

        // Write and play the MIDI file
        try
        {
            midi.writeToFile(midiFile);
            mediaPlayer = MediaPlayer.create(activity, Uri.parse("file://" + midiFile));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        catch (Exception e)
        { /* Ignored */ }
    }
}

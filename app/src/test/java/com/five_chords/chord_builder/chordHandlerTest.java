package com.five_chords.chord_builder;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

/**
 * Class to test the chordHandler class.
 * Created by Theodore on 3/3/2016.
 */
public class chordHandlerTest
{
    /**
     * Tests the getRandomChord method.
     */
    @Test
    public void testGetRandomChord()
    {
        // Add Chords
        chordHandler.addMajorChords();
        chordHandler.addMinorChords();
        chordHandler.addDominantChords();

        int chord = chordHandler.getSelectedChordIndex();
        chordHandler.getRandomChord();
        assertNotEquals(chord, chordHandler.getSelectedChordIndex());

        // Clear
        chordHandler.clearChords();
    }
}

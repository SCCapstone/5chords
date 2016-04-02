package com.five_chords.chord_builder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the Chord class.
 * Created by Theodore on 4/2/2016.
 */
public class ChordUnitTest
{
    /** Two chord spellings to use in testing. */
    private Note[] spelling1, spelling2;

    /** A List of chords to use for testing. */
    private List<Chord> chords;

    @Before
    public void setUp() throws Exception
    {
        // Init
        chords = new ArrayList<>();
        spelling1 = new Note[4];
        spelling2 = new Note[4];

        for (int i = 0; i < spelling1.length; ++i)
        {
            spelling1[i] = new Note();
            spelling2[i] = new Note();
        }

        // Add all chords to the list
        for (Chord.ChordType type: Chord.ChordType.values())
            for (int i = 0; i < Note.NUM_NOTES; ++i)
                chords.add(new Chord(i, type));
    }

    /**
     * Tests the chord ids, making sure that no two are the same for different chords.
     * @throws Exception
     */
    @Test
    public void testChordIds() throws Exception
    {
        // Make sure no two ids are the same
        for (Chord c1: chords)
        {
            for (Chord c2: chords)
            {
                if (c1 != c2)
                    assertNotEquals(c1.ID, c2.ID);
            }
        }
    }

    /**
     * Tests comparing two chords.
     * @throws Exception
     */
    @Test
    public void testCompareChords() throws Exception
    {
        // Make sure no two chords are the same
        for (Chord c1: chords)
        {
            for (Chord c2: chords)
            {
                c1.getRootPosition(spelling1);
                c2.getRootPosition(spelling2);

                if (c1 != c2)
                    assertFalse(Chord.compareChords(spelling1, spelling2, 4));
                else
                    assertTrue(Chord.compareChords(spelling1, spelling2, 4));
            }
        }
    }

    /**
     * Tests the getNumNotes method in the Chord class.
     * @throws Exception
     */
    @Test
    public void testGetNumNotes() throws Exception
    {
        // Loop over all chords
        for (Chord chord: chords)
        {
            assertEquals(chord.TYPE.offsets.length, chord.getNumNotes());
        }
    }

    /**
     * Tests getting the root position of the chords.
     * @throws Exception
     */
    @Test
    public void testGetRootPosition() throws Exception
    {
        Chord.ChordType type;

        // Loop over all chords
        for (Chord chord: chords)
        {
            // Get the current type
            type = chord.TYPE;

            // Get the root position
            chord.getRootPosition(spelling1);

            // Test the spelling
            for (int i = 0; i < type.offsets.length; ++i)
                assertEquals(chord.FUNDAMENTAL + type.offsets[i], spelling1[i].index);
        }
    }

    /**
     * Tests getting inversions for each chord.
     * @throws Exception
     */
    @Test
    public void testGetInversion() throws Exception
    {
        // Loop over all chords
        for (Chord chord: chords)
        {
            // Loop over the possible inversions for the chord
            for (int i = 0; i < chord.TYPE.offsets.length; ++i)
            {
                // Get the inversion
                chord.getInversion(spelling1, i);

                // Test the inversion
                for (int j = 0; j < i; ++j)
                    assertEquals(chord.FUNDAMENTAL + chord.TYPE.offsets[j] + Note.NUM_NOTES, spelling1[j].index);
            }
        }
    }
}
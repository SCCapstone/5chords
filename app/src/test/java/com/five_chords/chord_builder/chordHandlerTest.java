package com.five_chords.chord_builder;


import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Test class for chordHandler.
 * Created by Theodore on 4/1/2016.
 */
public class chordHandlerTest
{
    /** A Note array to use in testing. */
    private Note[] spelling;

    @Before
    public void setUp() throws Exception
    {
        // Create an Note array
        spelling = new Note[4];
        for (int i = 0; i < spelling.length; ++i)
            spelling[i] = new Note();

        // Set chord types in use
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            MainActivity.getOptions().setChordTypeUse(type.ordinal(), true);
        }
    }

    @After
    public void tearDown() throws Exception
    {

    }

    /**
     * Tests resetting the current wrong streak counter.
     * @throws Exception
     */
    @Test
    public void testResetCurrentWrongStreak() throws Exception
    {
        int current = chordHandler.getCurrentWrongStreak();

        if (current != 0)
        {
            chordHandler.resetCurrentWrongStreak();
            assertEquals(0, chordHandler.getCurrentWrongStreak());
        }

        try
        {
            chordHandler.makeHints(null);
        }
        catch (NullPointerException e)
        {/* Ignore */ }

        assertNotEquals(0, chordHandler.getCurrentWrongStreak());
        chordHandler.resetCurrentWrongStreak();
        assertEquals(0, chordHandler.getCurrentWrongStreak());
    }

    /**
     * Tests selecting and getting chord spellings.
     * @throws Exception
     */
    @Test
    public void testGetCurrentSelectedChordSpelling() throws Exception
    {
        Note[] spelling;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Select the chord
                chordHandler.setSelectedChord(new Chord(i, type), false);

                // Get the spelling
                spelling = chordHandler.getCurrentSelectedChordSpelling();

                // Test the spelling
                for (int j = 0; j < type.offsets.length; ++j)
                    assertEquals(spelling[j].index, i + type.offsets[j]);
            }
        }
    }

    /**
     * Tests building and getting chord spellings.
     * @throws Exception
     */
    @Test
    public void testGetCurrentBuiltChordSpelling() throws Exception
    {
        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Build the chord
                new Chord(i, type).getRootPosition(chordHandler.getCurrentBuiltChordSpelling());

                // Get the spelling
                spelling = chordHandler.getCurrentBuiltChordSpelling();

                // Test the spelling
                for (int j = 0; j < type.offsets.length; ++j)
                    assertEquals(spelling[j].index, i + type.offsets[j]);
            }
        }
    }

    /**
     * Tests getting the current selected chord.
     * @throws Exception
     */
    @Test
    public void testGetCurrentSelectedChord() throws Exception
    {
        Chord get;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Select the chord
                chordHandler.setSelectedChord(new Chord(i, type), false);

                // Get the chord
                get = chordHandler.getCurrentSelectedChord();

                // Test equality
                assertEquals(get.ID, Chord.getChordId(i, type));
            }
        }
    }

    /**
     * Tests the chord getting function.
     * @throws Exception
     */
    @Test
    public void testGetChord() throws Exception
    {
        Chord get1, get2, compare;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Get the chord
                get1 = chordHandler.getChord(i, type);
                get2 = chordHandler.getChord(Chord.getChordId(i, type));
                compare = new Chord(i, type);

                // Test equality
                assertEquals(get1.ID, get2.ID);
                assertEquals(get1.ID, compare.ID);
            }
        }
    }


    /**
     * Tests setting the selected chord.
     * @throws Exception
     */
    @Test
    public void testSetSelectedChord() throws Exception
    {
        Chord get;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Select the chord
                chordHandler.setSelectedChord(new Chord(i, type), false);

                // Get the chord
                get = chordHandler.getCurrentSelectedChord();

                // Test equality
                assertEquals(get.ID, Chord.getChordId(i, type));
            }
        }
    }

    /**
     * Tests getting a random chord.
     * @throws Exception
     */
    @Test
    public void testGetRandomChord() throws Exception
    {
        Chord get, compare;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Select the chord
                compare = new Chord(i, type);
                chordHandler.setSelectedChord(compare, false);

                // Get a random chord
                chordHandler.getRandomChord();
                get = chordHandler.getCurrentSelectedChord();

                // Test
                assertNotEquals(get.ID, compare.ID);
            }
        }
    }

    /**
     * Tests comparing current chords.
     * @throws Exception
     */
    @Test
    public void testTestCurrentChords() throws Exception
    {
        Chord built, compare;

        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Loop over all notes in an octave
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                // Get the built chord
                built = new Chord(i, type);

                // Get the compare chord
                chordHandler.setSelectedChord(built, false);
                chordHandler.getRandomChord();
                compare = chordHandler.getCurrentSelectedChord();

                // Get the spellings
                built.getRootPosition(chordHandler.getCurrentBuiltChordSpelling());
                compare.getRootPosition(chordHandler.getCurrentSelectedChordSpelling());

                // Test
                assertFalse(chordHandler.testCurrentChords());

                // Now set the same
                built.getRootPosition(chordHandler.getCurrentBuiltChordSpelling());
                built.getRootPosition(chordHandler.getCurrentSelectedChordSpelling());
                chordHandler.setSelectedChord(built, false);

                // Test
                assertTrue(chordHandler.testCurrentChords());
            }
        }
    }
}
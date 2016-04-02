package com.five_chords.chord_builder;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Class to test the Note class.
 * Created by Theodore on 4/2/2016.
 */
public class NoteUnitTest
{
    /** A Note to use in testing. */
    private Note note;

    /** A Random to use in some of the tests. */
    private Random random;

    @Before
    public void setUp() throws Exception
    {
        random = new Random();
    }

    /**
     * Tests setting the note values.
     * @throws Exception
     */
    @Test
    public void testSet() throws Exception
    {
        note = new Note();

        // Test setting integer values
        for (int i = 0; i < Note.NUM_NOTES; ++i)
        {
            note.set(i);
            assertEquals(note.index, i);
        }

        // Test setting integer and double values
        double d;
        for (int i = 0; i < Note.NUM_NOTES; ++i)
        {
            for (int j = 0; j < 100; ++j)
            {
                // Get a double in the range [-1, 1]
                d = (0.5 - random.nextDouble()) * 2.0;
                note.set(i, d);
                assertEquals(note.index, i);
                assertEquals(note.distanceToIndex, d, 0.001);
            }
        }
    }

    /**
     * Tests setting the fractional index of a Note.
     * @throws Exception
     */
    @Test
    public void testGetFractionalIndex() throws Exception
    {
        double d;
        note = new Note();

        // Test setting integer and double values
        for (int i = 0; i < Note.NUM_NOTES; ++i)
        {
            for (int j = 0; j < 100; ++j)
            {
                // Get a double in the range [-1, 1]
                d = (0.5 - random.nextDouble()) * 2.0;
                note.set(i, d);

                // Calculate what the fractional index should be
                d = i + d;

                // Test
                assertEquals(note.getFractionalIndex(), d, 0.001);
            }
        }
    }
}
package com.five_chords.chord_builder;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Class to test the Options class.
 * Created by Theodore on 4/2/2016.
 */
public class OptionsUnitTest implements Options.OptionsChangedListener
{
    /** Field to record whether or not callbacks were called. */
    private boolean callbackCalled;

    /** An Options to use in the tests. */
    private Options options;

    @Before
    public void setUp() throws Exception
    {
        options = new Options();

        // Set the listener
        options.setOptionsChangedListener(this);
    }

    /**
     * Tests setting the chord types in use.
     * @throws Exception
     */
    @Test
    public void testGetChordTypesInUse() throws Exception
    {
        // Test adding chord types
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            callbackCalled = false;
            options.setChordTypeUse(type.ordinal(), true);

            // Make sure the callback was called
            assertTrue(callbackCalled);

            // Make sure the type was set
            assertTrue(options.getChordTypesInUse().contains(type));
        }

        // Test removing chord types
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            callbackCalled = false;
            options.setChordTypeUse(type.ordinal(), false);

            // Make sure the callback was called
            assertTrue(callbackCalled);

            // Make sure the type was removed
            assertFalse(options.getChordTypesInUse().contains(type));
        }
    }

    /**
     * Tests the set show answer sequence method.
     * @throws Exception
     */
    @Test
    public void testSetShowAnswerSequence() throws Exception
    {
        // Reset callback flag
        callbackCalled = false;

        // Set to true
        options.setShowAnswerSequence(true);

        // Make sure the callback was called
        assertTrue(callbackCalled);

        // Test if true
        assertTrue(options.showAnswerSequence);

        // Reset callback flag
        callbackCalled = false;

        // Set to false
        options.setShowAnswerSequence(false);

        // Make sure the callback was called
        assertTrue(callbackCalled);

        // Test if false
        assertFalse(options.showAnswerSequence);
    }

    /**
     * Tests adding chord inversions.
     * @throws Exception
     */
    @Test
    public void testAddChordInversion() throws Exception
    {
        // Add conversions 0 to 3
        for (byte i = 0; i < 4; ++i)
        {
            // Reset callback flag
            callbackCalled = false;

            // Add the conversion
            options.addChordInversion(i);

            // Make sure the callback was called
            assertTrue(callbackCalled);

            // Make sure the inversion is included
            assertTrue(options.chordInversionsToUse[i]);
        }

        // Remove conversions 0 to 3
        for (byte i = 0; i < 4; ++i)
        {
            // Reset callback flag
            callbackCalled = false;

            // Add the conversion
            options.removeChordInversion(i);

            // Make sure the callback was called
            assertTrue(callbackCalled);

            // Make sure the inversion is not included
            assertFalse(options.chordInversionsToUse[i]);
        }
    }

    /**
     * Tests changing the pitch bend settings.
     * @throws Exception
     */
    @Test
    public void testChangePitchBendSettings() throws Exception
    {
        // Test 0 to 100 increments between notes
        for (int i = 0; i <= 100; ++i)
        {
            // Test 0 to 0.5 allowable error margins
            for (double j = 0.0; j <= 0.5; j += 0.01)
            {
                // Reset callback flag
                callbackCalled = false;

                // Add the conversion
                options.changePitchBendSettings(i, j);

                // Make sure the callback was called
                assertTrue(callbackCalled);

                // Test
                assertEquals(options.sliderDivisionsPerNote, i);
                assertEquals(options.allowableCheckError, j, 0.001);
            }
        }
    }

    /**
     * Tests changing the instrument flag.
     * @throws Exception
     */
    @Test
    public void testChangeInstrument() throws Exception
    {
        // Test 10 hypothetical instruments
        for (int i = 0; i < 10; ++i)
        {
            // Reset callback flag
            callbackCalled = false;

            // Set the instrument
            options.changeInstrument(i);

            // Make sure the callback was called
            assertTrue(callbackCalled);

            // Test
            assertEquals(options.instrument, i);
        }
    }

    /**
     * Tests changing the hint settings.
     * @throws Exception
     */
    @Test
    public void testChangeHints() throws Exception
    {
        // Test various hint delays in the range 0 to 24
        for (int i = 0; i < 24; ++i)
        {
            for (int j = i; j < 24; ++j)
            {
                for (int k = j; k < 24; ++k)
                {
                    // Reset callback flag
                    callbackCalled = false;

                    // Set the hint delays
                    options.changeHints(true, false, i, j, k);

                    // Make sure the callback was called
                    assertTrue(callbackCalled);

                    // Test
                    assertEquals(options.hintTypeDelays[0], i);
                    assertEquals(options.hintTypeDelays[1], j);
                    assertEquals(options.hintTypeDelays[2], k);
                }
            }
        }

        // Test enabling and disabling hints

        // Reset callback flag
        callbackCalled = false;

        // Disable hints
        options.changeHints(false, false, 0, 0, 0);

        // Make sure the callback was called
        assertTrue(callbackCalled);

        // Test
        assertFalse(options.useHints);

        // Reset callback flag
        callbackCalled = false;

        // Enable
        options.changeHints(true, false, 0, 0, 0);

        // Make sure the callback was called
        assertTrue(callbackCalled);

        // Test
        assertTrue(options.useHints);
    }

    /**
     * Called when the chord type changes.
     *
     * @param chordTypesInUse A List containing the ChordTypes that are now in use
     */
    @Override
    public void onChordTypeOptionsChanged(List<Chord.ChordType> chordTypesInUse)
    {
        callbackCalled = true;
    }

    /**
     * Called when the hints options changes.
     *
     * @param useHints Whether or not hints are now enabled.
     */
    @Override
    public void onHintsOptionsChanged(boolean useHints)
    {
        callbackCalled = true;
    }

    /**
     * Called when the showAnswerSequence flag changes.
     *
     * @param showAnswerSeq The new value of the showAnswerSequence flag
     */
    @Override
    public void onShowAnswerSequenceChanged(boolean showAnswerSeq)
    {
        callbackCalled = true;
    }

    /**
     * Called when the instrument selection changes.
     *
     * @param instrument The new instrument
     */
    @Override
    public void onInstrumentChanged(int instrument)
    {
        callbackCalled = true;
    }

    /**
     * Called when the chord inversion selection changes.
     *
     * @param inversions The new inversion selection
     */
    @Override
    public void onInversionSelectionChanged(boolean[] inversions)
    {
        callbackCalled = true;
    }

    /**
     * Called when the pitch bend settings change.
     *
     * @param incrementsPerNote The new increments per note
     * @param maxCheckError     The new max check error
     */
    @Override
    public void onPitchBendSettingsChanged(int incrementsPerNote, double maxCheckError)
    {
        callbackCalled = true;
    }
}
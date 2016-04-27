package com.five_chords.chord_builder;

import android.support.test.rule.ActivityTestRule;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Instrumented test for the Options class. This class tests saving and loading the Options.
 * Created by Theodore on 4/2/2016.
 */
public class OptionsTest
{
    /** A Random Object to use in the tests. */
    private Random random;

    /** Two Options to use in the tests. */
    private Options options1, options2;

    /** The MainActivity rule. */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception
    {
        random = new Random();
        options1 = new Options();
        options2 = new Options();
    }

    /**
     * Tests saving and loading the options.
     * @throws Exception
     */
    @Test
    public void testLoadAndSave() throws Exception
    {
        // The random seed
        long seed = System.nanoTime();

        // Test 100 times
        for (int i = 0; i < 100; ++i)
        {
            // Set options
            random.setSeed(seed);
            setTestValues(options1);
            random.setSeed(seed);
            setTestValues(options2);

            // Save and load the first options
            options1.save(mActivityRule.getActivity());
            options1 = new Options();
            options1.load(mActivityRule.getActivity());

            // Compare the two options
            compareOptions();

            // New seed
            seed = random.nextLong();
        }
    }

    /**
     * Compares the values of the two Options objects.
     */
    private void compareOptions()
    {
        // Hints
        assertEquals(options1.useHints, options2.useHints);
        assertEquals(options1.hintTypeDelays[0], options2.hintTypeDelays[0]);
        assertEquals(options1.hintTypeDelays[1], options2.hintTypeDelays[1]);
        assertEquals(options1.hintTypeDelays[2], options2.hintTypeDelays[2]);

        // Instrument
        assertEquals(options1.instrument, options2.instrument);

        // Pitch Bend Settings
        assertEquals(options1.sliderDivisionsPerNote, options2.sliderDivisionsPerNote);
        assertEquals(options1.allowableCheckError, options2.allowableCheckError, 0.001);

        // Chord inversions
        for (int i = 0; i < options1.chordInversionsToUse.length; ++i)
            assertEquals(options1.chordInversionsToUse[i], options2.chordInversionsToUse[i]);

        // Chord types in use
        for (Chord.ChordType type: options1.getChordTypesInUse())
            assertTrue(options2.chordTypesInUseArray[type.ordinal()]);

        // Answer sequence flag
        assertEquals(options1.showAnswerSequence, options2.showAnswerSequence);
    }

    /**
     * Sets random values on the given Options object.
     * @param options The Options to edit
     */
    private void setTestValues(Options options)
    {
        // Hints
        options.changeHints(random.nextBoolean(), random.nextBoolean(), random.nextInt(24), random.nextInt(24), random.nextInt(24));

        // Instrument
        options.changeInstrument(random.nextInt(10));

        // Pitch bend settings
        options.changePitchBendSettings(random.nextInt(100), random.nextDouble() * 0.5);

        // Chord inversions
        for (int i = 0; i < random.nextInt(4); ++i)
            options.addChordInversion((byte)random.nextInt(4));

        // Chord types in use
        for (int i = 0; i < random.nextInt(Chord.ChordType.values().length); ++i)
            options.setChordTypeUse(random.nextInt(Chord.ChordType.values().length), random.nextBoolean());

        // Answer sequence flag
        options.setShowAnswerSequence(random.nextBoolean());
    }
}
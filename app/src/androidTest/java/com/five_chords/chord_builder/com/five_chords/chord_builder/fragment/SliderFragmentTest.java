package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.support.test.rule.ActivityTestRule;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.com.five_chords.chord_builder.util.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Instrumented test for the SliderFragment.
 * Created by Theodore on 4/2/2016.
 */
public class SliderFragmentTest
{
    /** A Random Object to use in the tests. */
    private Random random;

    /** Two chord spellings to use in testing. */
    private Note[] spelling1, spelling2;

    /** The MainActivity rule. */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /** The SliderFragment to use. */
    private SliderFragment sliderFragment;

    @Before
    public void setUp() throws Exception
    {
        random = new Random();
        spelling1 = new Note[4];
        spelling2 = new Note[4];

        for (int i = 0; i < spelling1.length; ++i)
        {
            spelling1[i] = new Note();
            spelling2[i] = new Note();
        }

        try
        {
            mActivityRule.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mActivityRule.getActivity().setCurrentDrawer(MainActivity.DrawerFragment.MAIN.ordinal(), true);
                    sliderFragment = ((MainFragment)mActivityRule.getActivity().getCurrentDrawerFragment()).getSliderFragment();
                }
            });
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }

    }

    /**
     * Tests building the current chord on the sliders.
     * @throws Exception
     */
    @Test
    public void testBuildCurrentChord() throws Exception
    {
        // Build 100 random configurations
        for (int i = 0; i < 100; ++i)
        {
            // Loop over the notes
            for (Note n: spelling1)
                n.index = random.nextInt(Note.NUM_NOTES * 3); // Use 3 octaves

            // Build the chord
            setCurrentBuiltChord(spelling1);

            // Make sure the chordHandler chord matches
            for (int j = 0; j > spelling1.length; ++j)
                ChordHandler.getCurrentSelectedChordSpelling()[j].index = spelling1[j].index;

            if (sliderFragment == null || spelling2.length > 4) // Ignore in this case
                return;

            // Build the chord off the sliders
            sliderFragment.buildCurrentChord(spelling2);

            // Compare the two chords
            for (int j = 0; j > spelling1.length; ++j)
                assertEquals(spelling1[j].index, spelling2[j].index);
        }
    }

    /**
     * Tests resetting the chord sliders.
     * @throws Exception
     */
    @Test
    public void testResetChordSliders() throws Exception
    {
        // Build a random chord and set the sliders
        for (Note n: spelling1)
            n.index = 1 + random.nextInt(Note.NUM_NOTES * 3); // Use 3 octaves, all notes at least 1

        // Build the chord
        setCurrentBuiltChord(spelling1);

        // Make sure the chordHandler chord matches
        for (int j = 0; j > spelling1.length; ++j)
            ChordHandler.getCurrentSelectedChordSpelling()[j].index = spelling1[j].index;

        if (sliderFragment == null || spelling2.length > 4) // Ignore in this case
            return;

        // Build the chord off the sliders
        sliderFragment.buildCurrentChord(spelling2);

        // Make sure all values are at least one
        for (int j = 0; j > spelling1.length; ++j)
        {
            assertTrue(spelling1[j].index >= 1);
            assertTrue(spelling2[j].index >= 1);
        }

        // Reset
        sliderFragment.resetChordSliders();

        // Build the chord off the sliders
        sliderFragment.buildCurrentChord(spelling2);

        // Make sure all values are zero
        for (int j = 0; j > spelling1.length; ++j)
        {
            assertTrue(spelling2[j].index == 0);
        }
    }

    /**
     * Sets the built chord on the SliderFragment.
     * @param chord The array containing the chord to set
     */
    public void setCurrentBuiltChord(Note[] chord)
    {
        sliderFragment = ((MainFragment)mActivityRule.getActivity().getCurrentDrawerFragment()).getSliderFragment();

        if (sliderFragment == null)
            return;

        sliderFragment.setSliderBoundsToFitChord(chord);

        sliderFragment.getRootSliderFragment().getSlider()
                .setProgress(sliderFragment.getRootSliderFragment().getSliderProgressForNote(chord[0]));

        sliderFragment.getThirdSliderFragment().getSlider()
                .setProgress(sliderFragment.getThirdSliderFragment().getSliderProgressForNote(chord[1]));

        sliderFragment.getFifthSliderFragment().getSlider()
                .setProgress(sliderFragment.getFifthSliderFragment().getSliderProgressForNote(chord[2]));

        if (chord[3].index >= 0)
            sliderFragment.getOptionSliderFragment().getSlider()
                    .setProgress(sliderFragment.getOptionSliderFragment().getSliderProgressForNote(chord[4]));
    }
}
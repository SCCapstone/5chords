package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.support.test.rule.ActivityTestRule;
import android.widget.SeekBar;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.chordHandler;
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
                chordHandler.getCurrentSelectedChordSpelling()[j].index = spelling1[j].index;

            // Build the chord off the sliders
            mActivityRule.getActivity().getSliderFragment().buildCurrentChord(spelling2);

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
            chordHandler.getCurrentSelectedChordSpelling()[j].index = spelling1[j].index;

        // Build the chord off the sliders
        mActivityRule.getActivity().getSliderFragment().buildCurrentChord(spelling2);

        // Make sure all values are at least one
        for (int j = 0; j > spelling1.length; ++j)
        {
            assertTrue(spelling1[j].index >= 1);
            assertTrue(spelling2[j].index >= 1);
        }

        // Reset
        mActivityRule.getActivity().getSliderFragment().resetChordSliders();

        // Build the chord off the sliders
        mActivityRule.getActivity().getSliderFragment().buildCurrentChord(spelling2);

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
        MainActivity activity = mActivityRule.getActivity();
        SliderFragment sliderFragment = activity.getSliderFragment();
        sliderFragment.setSliderBoundsToFitChord(chord);

        ((SeekBar) activity.findViewById(R.id.slider_root)).setProgress(sliderFragment.getSliderProgressForNote(chord[0]));
        ((SeekBar) activity.findViewById(R.id.slider_third)).setProgress(sliderFragment.getSliderProgressForNote(chord[1]));
        ((SeekBar) activity.findViewById(R.id.slider_fifth)).setProgress(sliderFragment.getSliderProgressForNote(chord[2]));

        if (chord[3].index >= 0)
            ((SeekBar) activity.findViewById(R.id.slider_option)).setProgress(sliderFragment.getSliderProgressForNote(chord[3]));
    }
}
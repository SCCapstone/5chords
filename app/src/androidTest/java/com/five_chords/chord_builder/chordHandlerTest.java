package com.five_chords.chord_builder;

import android.app.Fragment;
import android.support.test.rule.ActivityTestRule;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.MainFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.util.ChordHandler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

/**
 * Instrumented test for chordHandler.
 * Created by Theodore on 4/1/2016.
 */
public class chordHandlerTest
{
    /** A Note array to use in testing. */
    private Note[] spelling;

    /** The MainActivity rule. */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception
    {
        // Create an Note array
        spelling = new Note[4];
        for (int i = 0; i < spelling.length; ++i)
            spelling[i] = new Note();
    }

    /**
     * Sets the built chord on the SliderFragment.
     * @param chord The array containing the chord to set
     */
    public void setCurrentBuiltChord(Note[] chord)
    {
        MainActivity activity = mActivityRule.getActivity();
        Fragment current = activity.getCurrentDrawerFragment();

        if (current instanceof MainFragment)
        {
            MainFragment mainFragment = (MainFragment) current;
            SliderFragment sliderFragment = mainFragment.getSliderFragment();

            if (sliderFragment != null)
            {
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
    }

    /**
     * Tests building chords on the sliders.
     * @throws Exception
     */
    @Test
    public void testBuildCurrentChord() throws Exception
    {
        // Loop over all ChordTypes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            // Build the chord on the sliders
            new Chord(0, type).getRootPosition(spelling);
            setCurrentBuiltChord(spelling);

            // Build the chord in the chordHandler
            Fragment fragment = mActivityRule.getActivity().getCurrentDrawerFragment();

            if (fragment instanceof MainFragment)
                ChordHandler.buildCurrentChord((MainFragment)fragment);

            // Test
            assertTrue(Chord.compareChords(spelling, ChordHandler.getCurrentBuiltChordSpelling(), type.offsets.length));
        }
    }
}
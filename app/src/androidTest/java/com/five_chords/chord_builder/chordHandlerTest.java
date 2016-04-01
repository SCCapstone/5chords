package com.five_chords.chord_builder;

import android.support.test.rule.ActivityTestRule;
import android.widget.SeekBar;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;

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
        SliderFragment sliderFragment = activity.getSliderFragment();
        sliderFragment.setSliderBoundsToFitChord(chord);

        ((SeekBar) activity.findViewById(R.id.slider_root)).setProgress(sliderFragment.getSliderProgressForNote(chord[0]));
        ((SeekBar) activity.findViewById(R.id.slider_third)).setProgress(sliderFragment.getSliderProgressForNote(chord[1]));
        ((SeekBar) activity.findViewById(R.id.slider_fifth)).setProgress(sliderFragment.getSliderProgressForNote(chord[2]));

        if (chord[3].index >= 0)
            ((SeekBar) activity.findViewById(R.id.slider_option)).setProgress(sliderFragment.getSliderProgressForNote(chord[3]));
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
            chordHandler.buildCurrentChord(mActivityRule.getActivity());

            // Test
            assertTrue(Chord.compareChords(spelling, chordHandler.getCurrentBuiltChordSpelling(), type.offsets.length));
        }
    }
}
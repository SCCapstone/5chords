package com.five_chords.chord_builder;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.SeekBar;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

/**
 * Instrumentation test for the chordBuilder class.
 * Created by Theodore on 3/3/2016.
 */

@RunWith(AndroidJUnit4.class)
@FixMethodOrder (MethodSorters.NAME_ASCENDING)
@SmallTest
public class chordHandlerTest
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    /** Array of real chords to test */
    private Note[][] chordsToTest;
    /** Array of bad chords to test */
    private Note[][] badChordsToTest;

    /**
     * Initializes the chordBuilder.
     */
    @Before
    public void initialize()
    {
        chordHandler.initialize();

    }

    /**
     * First test, to test building chords on the sliders.
     */
    @Test
    public void A_testBuildingChords()
    {
        MainActivity activity = mActivityRule.getActivity();

//        int[] inputChord;
//        int[] builtChord;
//        for (int i = 0; i < chordsToTest.length; ++i)
//        {
//            // Test good chords
//            inputChord = chordsToTest[i];
//            setCurrentBuiltChord(inputChord);
//            builtChord = chordHandler.getCurrentBuiltChord(activity);
//            Log.d("Chord ", "InputChord = " + Arrays.toString(inputChord) + ", Built Chord = " + Arrays.toString(builtChord));
//            Assert.assertArrayEquals(inputChord, builtChord);
//
//            // Test bad chords
//            inputChord = badChordsToTest[i];
//            setCurrentBuiltChord(inputChord);
//            builtChord = chordHandler.getCurrentBuiltChord(activity);
//            Log.d("Chord ", "InputChord = " + Arrays.toString(inputChord) + ", Built Chord = " + Arrays.toString(builtChord));
//            Assert.assertArrayEquals(inputChord, builtChord);
//        }
    }

    /**
     * Second test, to test selecting chords on the slider.
     */
    @Test
    public void B_testSelectingChords()
    {
//        for (int i = 0; i < chordsToTest.length; ++i)
//        {
//            chordHandler.setSelectedChord(i);
//            Assert.assertArrayEquals(chordsToTest[i], chordHandler.getCurrentSelectedChord());
//        }
    }

    /**
     * Sets the built chord on the MainActivity.
     * @param chord The array containing the chord to set
     */
    public void setCurrentBuiltChord(int ... chord)
    {
        Log.d("Set Built Chord", Arrays.toString(chord));
        MainActivity activity = mActivityRule.getActivity();
        ((SeekBar) activity.findViewById(R.id.slider_root)).setProgress(chord[0]);
        ((SeekBar) activity.findViewById(R.id.slider_third)).setProgress(chord[1]);
        ((SeekBar) activity.findViewById(R.id.slider_fifth)).setProgress(chord[2]);

        if (chord.length > 3)
            ((SeekBar) activity.findViewById(R.id.slider_option)).setProgress(chord[3]);
    }
}

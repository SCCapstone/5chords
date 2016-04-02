package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.support.test.rule.ActivityTestRule;
import android.widget.NumberPicker;

import com.five_chords.chord_builder.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Instrumented test for the CheckSettingActivity.
 * Created by Theodore on 4/2/2016.
 */
public class CheckSettingsActivityTest
{
    /** A Random to use in some of the tests. */
    private Random random;

    /** The MainActivity rule. */
    @Rule
    public ActivityTestRule<CheckSettingsActivity> mActivityRule = new ActivityTestRule<>(CheckSettingsActivity.class);

    @Before
    public void setUp() throws Exception
    {
        random = new Random();
    }

    /**
     * Tests setting the hint delays.
     * @throws Exception
     */
    @Test
    public void testSetHintDelays() throws Exception
    {
        // Get the Activity
        final CheckSettingsActivity activity = mActivityRule.getActivity();

        // Get the number pickers
        final NumberPicker hintDelay1Picker = (NumberPicker) activity.findViewById(R.id.hint_level1_picker);
        final NumberPicker hintDelay2Picker = (NumberPicker) activity.findViewById(R.id.hint_level2_picker);
        final NumberPicker hintDelay3Picker = (NumberPicker) activity.findViewById(R.id.hint_level3_picker);

        // Test setting various values on the pickers
        for (int t = 0; t < 100; ++t)
        {
            // Set values
            final int h1 = random.nextInt(24);
            final int h2 = random.nextInt(24);
            final int h3 = random.nextInt(24);

            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    hintDelay1Picker.setValue(h1);
                    hintDelay2Picker.setValue(h2);
                    hintDelay3Picker.setValue(h3);

                    // Cap i, j, k if needed
                    int ii = Math.max(Math.min(h1, hintDelay1Picker.getMaxValue()), hintDelay1Picker.getMinValue());
                    int jj = Math.max(Math.min(h2, hintDelay2Picker.getMaxValue()), hintDelay2Picker.getMinValue());
                    int kk = Math.max(Math.min(h3, hintDelay3Picker.getMaxValue()), hintDelay3Picker.getMinValue());

                    // Make sure the values were set
                    assertEquals(ii, hintDelay1Picker.getValue());
                    assertEquals(jj, hintDelay2Picker.getValue());
                    assertEquals(kk, hintDelay3Picker.getValue());
                }
            });
        }
    }
}
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Fragment;
import android.support.test.rule.ActivityTestRule;
import android.widget.NumberPicker;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.CheckSettingsFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.MainFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SettingsPageFragment;

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

    /** The CheckSettingsFragment to use */
    private SettingsPageFragment settingsFragment;

    /** The Activity rule. */
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception
    {
        random = new Random();

        try
        {
            mActivityRule.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mActivityRule.getActivity().setCurrentDrawer(MainActivity.DrawerFragment.SETTINGS.ordinal(), true);

                    settingsFragment = (SettingsPageFragment) mActivityRule.getActivity().getCurrentDrawerFragment();
                }
            });
        }
        catch (Throwable throwable)
        {
            throwable.printStackTrace();
        }


    }

    /**
     * Tests setting the hint delays.
     * @throws Exception
     */
    @Test
    public void testSetHintDelays() throws Exception
    {
        settingsFragment.setSettingsSubFragment(SettingsPageFragment.SettingsSubFragmentDef.CHECK_OPTIONS.ordinal());

//        // Get the number pickers
//        final NumberPicker hintDelay1Picker = (NumberPicker) activity.findViewById(R.id.hint_level1_picker);
//        final NumberPicker hintDelay2Picker = (NumberPicker) activity.findViewById(R.id.hint_level2_picker);
//        final NumberPicker hintDelay3Picker = (NumberPicker) activity.findViewById(R.id.hint_level3_picker);
//
//        // Test setting various values on the pickers
//        for (int t = 0; t < 100; ++t)
//        {
//            // Set values
//            final int h1 = random.nextInt(24);
//            final int h2 = random.nextInt(24);
//            final int h3 = random.nextInt(24);
//
//            activity.runOnUiThread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    hintDelay1Picker.setValue(h1);
//                    hintDelay2Picker.setValue(h2);
//                    hintDelay3Picker.setValue(h3);
//
//                    // Cap i, j, k if needed
//                    int ii = Math.max(Math.min(h1, hintDelay1Picker.getMaxValue()), hintDelay1Picker.getMinValue());
//                    int jj = Math.max(Math.min(h2, hintDelay2Picker.getMaxValue()), hintDelay2Picker.getMinValue());
//                    int kk = Math.max(Math.min(h3, hintDelay3Picker.getMaxValue()), hintDelay3Picker.getMinValue());
//
//                    // Make sure the values were set
//                    assertEquals(ii, hintDelay1Picker.getValue());
//                    assertEquals(jj, hintDelay2Picker.getValue());
//                    assertEquals(kk, hintDelay3Picker.getValue());
//                }
//            });
//        }
    }
}
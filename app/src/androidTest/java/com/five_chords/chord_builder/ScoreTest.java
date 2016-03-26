package com.five_chords.chord_builder;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test for the Score class.
 * Created by Theodore on 3/3/2016.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class ScoreTest
{
    /** The id of the shared preferences for the test */
    private static final String TEST_SCORE_SHARED_PREFERENCES = "TestScoreSharedPrefs";

    /** A ScoreWrapper Object */
    private Score score;

    /** A SharedPreferences for saving scores */
    private SharedPreferences scorePreferences;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void createScoreWrapper()
    {
        score = new Score(0, Chord.ChordType.MAJOR);
        score.getOverallValue().numCorrectGuesses = 5;
        score.getOverallValue().numTotalGuesses = 10;
        scorePreferences = mActivityRule.getActivity().getSharedPreferences(TEST_SCORE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Test
    public void loadSaveScoreWrapper()
    {
        // Save
        score.save(scorePreferences);

        // Load
        score.load(scorePreferences);

        assertEquals(score.getOverallValue().numCorrectGuesses, 5);
        assertEquals(score.getOverallValue().numTotalGuesses, 10);
        assertEquals(score.CHORD_ID, Chord.getChordId(0, Chord.ChordType.MAJOR));
    }
}

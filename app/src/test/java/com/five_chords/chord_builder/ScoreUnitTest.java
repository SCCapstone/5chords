package com.five_chords.chord_builder;

import org.junit.Test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class to test non instrumented Score components.
 * Created by Theodore on 3/3/2016.
 */
public class ScoreUnitTest
{
    /**
     * Tests the DiscreteScoreHistory class.
     */
    @Test
    public void testScoreHistory()
    {
        Score.ScoreValue value;
        Score.DiscreteScoreHistory history = new Score.DiscreteScoreHistory();
        List<Score.ScoreValue> values = new LinkedList<>();

        // Scores per date, out of 100
        int[] scores = new int[Score.HISTORY_UPDATE_INTERVALS.length];
        for (int i = 0; i < scores.length; ++i)
            scores[i] = i;

        long time = new Date().getTime() / 1000L;

        for (int i = 0; i < scores.length; ++i)
        {
            value = new Score.ScoreValue();
            value.numCorrectGuesses = scores[i];
            value.numTotalGuesses = 100;
            value.time = time - Score.HISTORY_UPDATE_INTERVALS[i];
            values.add(value);
        }

        // Add to history
        history.addValuesToHistory(values);

        // Tests
        assertEquals(history.values.length, Score.HISTORY_TAGS.length);
        assertEquals(history.size, scores.length);

        for (int i = 0; i < scores.length; ++i)
        {
            assertEquals(history.values[i].numCorrectGuesses, scores[i]);
        }

        value = new Score.ScoreValue();
        history.setValueInHistory(value, 0L);
        assertEquals(history.values[0].numCorrectGuesses, value.numCorrectGuesses);
        assertEquals(history.values[0].numTotalGuesses, value.numTotalGuesses);
        assertEquals(history.values[0].time, value.time);

        value = new Score.ScoreValue();
        history.setValueInHistory(value, 1000000000L);
        assertEquals(history.values[history.size - 1].numCorrectGuesses, value.numCorrectGuesses);
        assertEquals(history.values[history.size - 1].numTotalGuesses, value.numTotalGuesses);
        assertEquals(history.values[history.size - 1].time, value.time);

        history.clear();
        assertEquals(history.values.length, Score.HISTORY_TAGS.length);
        assertEquals(history.size, 0);
    }

}

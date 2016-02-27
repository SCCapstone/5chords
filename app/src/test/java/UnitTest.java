/**
 * Unit Tests
 */
import com.five_chords.chord_builder.chordHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnitTest {

    @Test
    public void testCompareChord() {
        int[] chord1 = {1,2,3};
        int[] chord2 = {1,2,3};
        int[] chord3 = {1,2,3,4};
        int[] chord4 = {1,2,2};
        int[] chord5 = {1,2};
        int[] chord6 = {1,2,3,4,5};

        assertTrue(chordHandler.compareChords(chord1, chord2));
        assertTrue(chordHandler.compareChords(chord2, chord2));
        assertFalse(chordHandler.compareChords(chord2, chord3));
        assertFalse(chordHandler.compareChords(null, chord3));
        assertFalse(chordHandler.compareChords(chord3, null));
        assertFalse(chordHandler.compareChords(chord2, chord4));
        assertFalse(chordHandler.compareChords(chord5, chord5));
        assertFalse(chordHandler.compareChords(chord6, chord6));
    }

    @Test
    public void testNewChord() {
        assertEquals(chordHandler.getSelectedChord(2), 1);
        assertNotEquals(chordHandler.getSelectedChord(2), 2);
    }
}
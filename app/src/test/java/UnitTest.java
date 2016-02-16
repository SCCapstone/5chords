/**
 * Unit Tests
 */
import com.five_chords.chord_builder.chordHandler;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class UnitTest {

    chordHandler cH = new chordHandler();

    @Test
    public void testCompareChord() {
        int[] chord1 = {1,2,3};
        int[] chord2 = {1,2,3};
        int[] chord3 = {1,2,3,4};
        int[] chord4 = {1,2,2};
        int[] chord5 = {1,2};
        int[] chord6 = {1,2,3,4,5};

        assertTrue(cH.compareChords(chord1, chord2));
        assertTrue(cH.compareChords(chord2, chord2));
        assertFalse(cH.compareChords(chord2, chord3));
        assertFalse(cH.compareChords(null, chord3));
        assertFalse(cH.compareChords(chord3, null));
        assertFalse(cH.compareChords(chord2, chord4));
        assertFalse(cH.compareChords(chord5, chord5));
        assertFalse(cH.compareChords(chord6, chord6));
    }

    @Test
    public void testNewChord() {
        assertEquals(cH.getSelectedChord(2), 1);
        assertNotEquals(cH.getSelectedChord(2), 2);
    }
}
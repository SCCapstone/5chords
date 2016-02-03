/**
 * Unit Tests
 */
import com.five_chords.chord_builder.chordHandler;

import org.junit.Test;
import static org.junit.Assert.*;

public class UnitTest {

    @Test
    public void chordReturnsCorrect() {
        chordHandler cH = new chordHandler();

        int[] chord1 = {1,2,3};
        int[] chord2 = {1,2,3};
        int[] chord3 = {1,2,2};

        assertTrue(cH.checkChord(chord1, chord2));
        assertTrue(cH.checkChord(chord2, chord2));
        assertFalse(cH.checkChord(chord2, chord3));
        assertFalse(cH.checkChord(null, chord2));
    }

}
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.Fragment;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.MainFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Class for handling playing, choosing, building, and checking chords. This class keeps track of two chords, one
 * which represents the current chord the user has selected and one which represents the current chord that the
 * user has built on the sliders.
 * This class also keeps track of a mapping of chords that have been built so far for convenient reference.
 * @date 04 April 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class ChordHandler
{
    /** Denotes the first level of hints. */
    public static final byte HINT_ONE = 1;

    /** Denotes the second level of hints. */
    public static final byte HINT_TWO = 2;

    /** Denotes the third level of hints. */
    public static final byte HINT_THREE = 3;

    /** The current Chord selected on the chord spinner. */
    private static Chord currentSelectedChord;

    /** Contains the spelling of current chord built by the user on the sliders. */
    private static Note[] currentBuiltChordSpelling = new Note[4];

    /** Contains the spelling of the current chord selected on the chord spinner. */
    private static Note[] currentSelectedChordSpelling = new Note[4];

    // Initialize the Note arrays
    static
    {
        for (int i = 0; i < 4; ++i)
        {
            currentBuiltChordSpelling[i] = new Note();
            currentSelectedChordSpelling[i] = new Note();
        }
    }

    /** Records the current wrong streak of the current chord for hinting purposes. */
    private static int currentWrongStreak;

    /** The OnChordSelectedListener attached to the chordHandler. */
    private static OnChordSelectedListener onChordSelectedListener;

    /** The map of used chords. */
    private static Map<Long, Chord> chords = new HashMap<>();

    /**
     * Static class.
     */
    private ChordHandler()
    {    }

    /**
     * Initializes this chordHandler.
     */
    public static void initialize()
    {
        currentSelectedChord = getChord(0, Chord.ChordType.MAJOR);
    }

    /**
     * Gets the counter for the number of times the user has incorrectly guessed the current chord.
     * @return The counter for the number of times the user has incorrectly guessed the current chord
     */
    public static int getCurrentWrongStreak()
    {
        return currentWrongStreak;
    }

    /**
     * Resets the counter for the number of times the user has incorrectly guessed the current chord.
     */
    public static void resetCurrentWrongStreak()
    {
        currentWrongStreak = 0;
    }

    /**
     * Sets the OnChordSelectedListener attached to the chordHandler.
     * @param listener The new listener
     */
    public static void setOnChordSelectedListener(OnChordSelectedListener listener)
    {
        onChordSelectedListener = listener;
    }

    /**
     * Gets the spelling of the current selected chord.
     * @return The spelling of the current selected chord
     */
    public static Note[] getCurrentSelectedChordSpelling()
    {
        return currentSelectedChordSpelling;
    }

    /**
     * Gets the spelling of the current built chord.
     * @return The spelling of the current built chord
     */
    public static Note[] getCurrentBuiltChordSpelling()
    {
        return currentBuiltChordSpelling;
    }

    /**
     * Gets the currently selected Chord.
     * @return The currently selected Chord
     */
    public static Chord getCurrentSelectedChord()
    {
        return currentSelectedChord;
    }

    /**
     * Gets the Chord of the given name and type.
     * @param fundamental The value of the fundamental note
     * @param type The type of the chord
     * @return The Chord of the given name and type
     */
    public static Chord getChord(int fundamental, Chord.ChordType type)
    {
        long id = Chord.getChordId(fundamental, type);
        Chord chord = chords.get(id);

        if (chord == null)
        {
            chord = new Chord(fundamental, type);
            chords.put(id, chord);
        }

        return chord;
    }

    /**
     * Gets the Chord of the given id.
     * @param id The id of the Chord
     * @return The Chord of the given name and type
     */
    public static Chord getChord(long id)
    {
        Chord chord = chords.get(id);

        if (chord == null)
        {
            chord = new Chord(id);
            chords.put(id, chord);
        }

        return chord;
    }

    /**
     * Called to set currently selected chord.
     * @param chord The new Chord
     */
    public static void setSelectedChord(Chord chord, boolean isRandom)
    {
        // Reset wrong streak if needed
        if (chord != currentSelectedChord)
            currentWrongStreak = 0;

        // Update current chord
        currentSelectedChord = chord;

        // Set the value of the chord
        Options options = MainActivity.getOptions();

        int numInversions = 0;
        boolean[] inversionsInUse = options.chordInversionsToUse;

        for (boolean b: inversionsInUse)
            if (b)
                ++numInversions;

        if (numInversions == 0)
            currentSelectedChord.getRootPosition(currentSelectedChordSpelling);
        else
        {
            List<Byte> inversions = new LinkedList<>();
            for (byte i = 0; i < inversionsInUse.length; ++i)
                if (inversionsInUse[i])
                    inversions.add(i);

            int maxInversions = Math.min(inversions.size(), currentSelectedChord.getNumNotes() - 1);
            int inversionToUse = new Random().nextInt(maxInversions);
            currentSelectedChord.getInversion(currentSelectedChordSpelling, inversions.get(inversionToUse));
        }

        // Call the listener
        if (onChordSelectedListener != null)
            onChordSelectedListener.onChordSelected(isRandom);
    }

    /**
     * Changes the current chord to a random chord.
     */
    public static void getRandomChord()
    {
        Options options = MainActivity.getOptions();
        Random random = new Random();

        // Select a random type from the available types
        Chord.ChordType newType = options.getChordTypesInUse().get(random.nextInt(options.getNumChordTypesInUse()));

        // Select a random chord of the type, making sure that it is different than the current chord
        int newChordFund;
        int previousChordFund = currentSelectedChord.FUNDAMENTAL;

        do
        {
            newChordFund = random.nextInt(Note.NUM_NOTES);
        }
        while (newChordFund == previousChordFund);

        // Get the chosen chord
        setSelectedChord(getChord(newChordFund, newType), true);
    }

    /**
     * Builds the current chord that the user has defined on the sliders.
     * @param mainFragment The current MainFragment
     */
    public static void buildCurrentChord(MainFragment mainFragment)
    {
        SliderFragment sliderFragment = mainFragment.getSliderFragment();

        if (sliderFragment != null)
            sliderFragment.buildCurrentChord(currentBuiltChordSpelling);
    }

    /**
     * Called when Hints should be displayed.
     * @param fragment The current MainFragment
     */
    public static void makeHints(MainFragment fragment)
    {
        // Increment the wrong streak counter
        currentWrongStreak++;

        // Show hints
        if (MainActivity.getOptions().useHints)
        {
            int[] hintDelays = MainActivity.getOptions().hintTypeDelays;

            if (currentWrongStreak > hintDelays[2])
                fragment.makeHint(HINT_THREE);
            else if (currentWrongStreak > hintDelays[1])
                fragment.makeHint(HINT_TWO);
            else if (currentWrongStreak > hintDelays[0])
                fragment.makeHint(HINT_ONE);
        }
    }

    /**
     * Tests whether or not the current selected and built chords are correct.
     * @return Whether or not the current selected and built chords are correct
     */
    public static boolean testCurrentChords()
    {
        return Chord.compareChords(currentBuiltChordSpelling, currentSelectedChordSpelling,
                getCurrentSelectedChord().TYPE.offsets.length);
    }

    /**
     * Called when the user checks the current chord.
     * @param fragment Handle to the MainFragment
     */
    public static void checkCurrentChord(final MainFragment fragment)
    {
        // Either show the answer sequence or go right to the answer
        if (MainActivity.getOptions().showAnswerSequence)
            fragment.showChordSequence();
        else
            fragment.showChordCheckResult();
    }

    /**
     * Interface for classes listening for chord selection.
     */
    public interface OnChordSelectedListener
    {
        /**
         * Called when a new chord is selected.
         */
        void onChordSelected(boolean random);
    }
}

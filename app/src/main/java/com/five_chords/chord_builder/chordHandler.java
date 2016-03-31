/***************************************************************************************************
 * chordHandler.java
 * This class will loop through raw folder for each music note. User can pick a chord by either
 * random or their selection check the chord to see if it matches.Play the user chosen chord or at
 * random. Give back the feedback on screen "Correct" or "Wrong" accordingly.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class chordHandler
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
    private chordHandler()
    {    }

    /**
     * Initializes this chordHandler.
     */
    public static void initialize()
    {
        currentSelectedChord = getChord(0, Chord.ChordType.MAJOR);
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

//    /**
//     * Called to notify chordHandler that the built chord has changed.
//     */
//    public static void builtChordChanged()
//    {
//        currentSelectedChordSpelling = null;
//        currentBuiltChordSpelling = null;
//    }

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

        if (options.chordInversionsToUse.isEmpty())
            currentSelectedChord.getRootPosition(currentSelectedChordSpelling);
        else
        {
            int maxInversions = Math.min(options.chordInversionsToUse.size(), currentSelectedChord.getNumNotes() - 1);
            int inversionToUse = new Random().nextInt(maxInversions);
            currentSelectedChord.getInversion(currentSelectedChordSpelling, options.chordInversionsToUse.get(inversionToUse));
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

//    /**
//     * Clears the available chord array.
//     */
//    public static void clearChords() {
//        availableChords = null;
//    }

//    /**
//     * Checks whether two chords (as integer arrays) are equivalent.
//     * @param chordA The array containing the notes of the first
//     * @param chordB The array containing the notes of the second chord
//     */
//    public static boolean compareChords(int[] chordA, int[] chordB)
//    {
//
//
//        return !(setChord == null || builtChord == null || setChord.length != builtChord.length) &&
//                Arrays.equals(builtChord, setChord);
//    }

    /**
     * Builds the current chord that the user has defined on the sliders.
     * @param activity The current Activity
     */
    public static void buildCurrentChord(Activity activity)
    {
        Fragment fragment = activity.getFragmentManager().findFragmentById(R.id.fragment_sliders);

        if (fragment != null && fragment instanceof SliderFragment)
        {
            SliderFragment sliderFragment = (SliderFragment)fragment;
            sliderFragment.buildCurrentChord(currentBuiltChordSpelling);
        }
    }

    /**
     * Called when the user checks the current chord.
     * @param activity Handle to the MainActivity
     */
    public static void checkCurrentChord(final MainActivity activity)
    {
        // Make sure the current Chord is built
        buildCurrentChord(activity);

        // Test correctness
        boolean isCorrect = Chord.compareChords(currentBuiltChordSpelling, currentSelectedChordSpelling,
                getCurrentSelectedChord().TYPE.offsets.length);

        // Set the score
        Score.getCurrentScore().update(activity, isCorrect);
        activity.updateDisplayedScore();

        // Handle result TODO add sounds for right and wrong
        if (isCorrect)
        {
            // Launch dialog
            new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.thats_correct))
                    .setMessage("Do you want to try another chord?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            getRandomChord();
                            activity.getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();

                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setSelectedChord(currentSelectedChord, false); // Resets the wrong streak counter
                            activity.getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();
                        }

                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            activity.getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();
                        }
                    })
                    .show();
        }
        else
        {
            // Increment the wrong streak counter
            currentWrongStreak++;

            // Show hints
            if (MainActivity.getOptions().useHints)
            {
                int[] hintDelays = MainActivity.getOptions().hintTypeDelays;

                if (currentWrongStreak > hintDelays[2])
                    activity.makeHint(HINT_THREE);
                else if (currentWrongStreak > hintDelays[1])
                    activity.makeHint(HINT_TWO);
                else if (currentWrongStreak > hintDelays[0])
                    activity.makeHint(HINT_ONE);
            }

            // Show toast
            Toast toast = Toast.makeText(activity, activity.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            // Show correct chord
            activity.showCorrectChord();
        }
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

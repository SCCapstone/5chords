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
import android.content.DialogInterface;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.VerticalSeekBar;

import java.util.Arrays;
import java.util.Random;

public class chordHandler
{
    /** Denotes the first level of hints */
    public static final byte HINT_ONE = 1;

    /** Denotes the second level of hints */
    public static final byte HINT_TWO = 2;

    /** Denotes the third level of hints */
    public static final byte HINT_THREE = 3;

//    /** Denotes a 'small' number of times that the user has guessed the wrong chord */
//    private static int NUM_TIMES_WRONG_SMALL = 2;
//
//    /** Denotes a 'medium' number of times that the user has guessed the wrong chord */
//    private static int NUM_TIMES_WRONG_MEDIUM = 6;
//
//    /** Denotes a 'large' number of times that the user has guessed the wrong chord */
//    private static int NUM_TIMES_WRONG_LARGE = 12;

    /** The number of chords per type (Major, Minor, Dominant) */
    private static final int CHORDS_PER_TYPE = 12;

    /** The minimum number of notes per chord (for Major and Minor) */
    private static final int MIN_NOTES_PER_CHORD = 3;

//    /** The maximum number of notes per chord (for Dominant) */
//    private static final int MAX_NOTES_PER_CHORD = 4;

    /** Array containing the currently available chords */
    private static int[][] availableChords = null;

    /** Contains the current chord built by the user on the sliders */
    private static int[] currentBuiltChord;

    /** Contains the current chord built by the user on the sliders at the VerticalSeekBar precision */
    private static int[] currentPreciseBuiltChord;

    /** Records the current chord index */
    private static int currentChordIndex;

    /** Records the current wrong streak of the current chord for hinting purposes. */
    private static int currentWrongStreak;

    private static int[] currentCorrectChord;
    private static int[] currentSliderOffset;
    private static int currentNumInterval;

    /** The OnDominantChordSelectedListener attached to the chordHandler. */
    private static OnChordSelectedListener onChordSelectedListener;

    private static SliderFragment sF;

    /**
     * Static class.
     */
    private chordHandler()
    {
        sF = new SliderFragment();
    }

    /**
     * Called to initialize this chordHandler.
     */
    public static void initialize()
    {
        clearChords();
        addMajorChords();
        addMinorChords();
        addDominantChords();

        sF = new SliderFragment();
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
     * Get the chord array at the current chord index.
     * @return The current chord array
     */
    public static int[] getCurrentSelectedChord() {
        return availableChords[currentChordIndex];
    }

    /**
     * Gets the current built chord.
     * @param activity The current Activity
     * @return The current built chord
     */
    public static int[] getCurrentBuiltChord(Activity activity) {

        if (currentBuiltChord == null)
            buildCurrentChord(activity);

        return currentBuiltChord;
    }

    /**
     * Gets the current precise built chord.
     * @param activity The current Activity
     * @return The current precise built chord
     */
    public static int[] getCurrentPreciseBuiltChord(Activity activity) {

        if (currentPreciseBuiltChord == null)
            buildCurrentPreciseChord(activity);

        return currentPreciseBuiltChord;
    }

    /**
     * Called to notify chordHandler that the built chord has changed.
     */
    public static void builtChordChanged() {
        currentBuiltChord = null;
        currentPreciseBuiltChord = null;
    }

    /**
     * Called to set the index of the currently selected chord.
     * @param chordIndex The index of the new chord
     */
    public static void setSelectedChord(int chordIndex) {

        // Reset wrong streak if needed
        if (chordIndex != currentChordIndex)
            currentWrongStreak = 0;

        // Update current chord index
        currentChordIndex = chordIndex;

        // Call the listener
        if (onChordSelectedListener != null)
            onChordSelectedListener.onChordSelected();

        newChord();
    }

    /**
     * Gets the index of the currently selected chord.
     * @return The index of the currently selected chord
     */
    public static int getSelectedChordIndex() {
        return currentChordIndex;
    }

    public static int[] getCurrentCorrectChord() {
        return currentCorrectChord;
    }

    public static int getCurrentNumInterval() {
        return currentNumInterval;
    }

    public static int[] getCurrentSliderOffset() {
        return currentSliderOffset;
    }

    /**
     * Changes the current chord to a random chord.
     */
    public static void getRandomChord() {
        int previousChordIndex = currentChordIndex;
        int newChordIndex;
        Random random = new Random();

        // Make sure new chord index is different than the previous
        do
        {
            newChordIndex = random.nextInt(availableChords.length - 1);
        }
        while (newChordIndex == previousChordIndex);

        setSelectedChord(newChordIndex);
    }

    public static void newChord() {
        Random random = new Random();
        int notes = random.nextInt(2) + 3;
        int intervalMax = 40/notes;
        currentNumInterval = random.nextInt(intervalMax/2) + intervalMax/2;
        int maxProgress = notes*currentNumInterval;

        int[] thisChord = availableChords[currentChordIndex];

        // Set random note position on sliders
        int rootNote = random.nextInt(maxProgress);
        int thirdNote = random.nextInt(maxProgress);
        int fifthNote = random.nextInt(maxProgress);
        int optionNote = random.nextInt(maxProgress);

        // This is the new correct seekbar positions
        currentCorrectChord = (thisChord.length == 3) ?
                new int[] {rootNote, thirdNote, fifthNote} :
                new int[] {rootNote, thirdNote, fifthNote, optionNote};


        // This is where the first note on the sliders start
        int rootSliderOffset = thisChord[0] - rootNote/currentNumInterval;
        int thirdSliderOffset = thisChord[1] - thirdNote/currentNumInterval;
        int fifthSliderOffset = thisChord[2] - fifthNote/currentNumInterval;
        int optionSliderOffset = (thisChord.length == 3) ? 0 : thisChord[3] - optionNote/currentNumInterval;

        currentSliderOffset = new int[] {rootSliderOffset, thirdSliderOffset,
                                         fifthSliderOffset, optionSliderOffset};

        sF.setMaxProgress(maxProgress);
    }

    /**
     * Clears the available chord array.
     */
    public static void clearChords() {
        availableChords = null;
    }

    /**
     * Adds the major chords to the available chord array.
     */
    public static void addMajorChords() {

        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 4, note + 7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the minor chords to the available chord array.
     */
    public static void addMinorChords() {

        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 3, note + 7};
        }

        availableChords = returnArray;
    }

    /**
     * Adds the dominant chords to the available chord array.
     */
    public static void addDominantChords() {
        int len = availableChords == null ? 0 : availableChords.length;
        int[][] returnArray = createEmptyChordArray();

        for (int i = len; i < len + CHORDS_PER_TYPE; i++) {
            int note = i % CHORDS_PER_TYPE;
            returnArray[i] = new int[] {note, note + 4, note + 7, note + 10};
        }

        availableChords = returnArray;
    }

    /**
     * Checks whether two chords (as integer arrays) are equivalent.
     * @param builtChord The array containing the notes of the built chord
     * @param setChord The array containing the notes of the set chord
     */
    public static boolean compareChords(int[] builtChord, int[] setChord)
    {
        Log.d("set chord", setChord[0] + ", " + setChord[1] + ", " + setChord[2]);
        Log.d("built chord", builtChord[0] + ", " + builtChord[1] + ", " + builtChord[2]);
        Log.d("intervals", "" + currentNumInterval);
        Log.d("offsets", currentSliderOffset[0] + ", " + currentSliderOffset[1] + ", " + currentSliderOffset[2]);

        return !(setChord == null || builtChord == null || setChord.length != builtChord.length) &&
                Arrays.equals(builtChord, setChord);
    }

    /**
     * Builds the current chord that the user has defined on the sliders.
     * @param activity The current Activity
     */
    private static void buildCurrentChord(Activity activity)
    {
        int root = ((VerticalSeekBar) activity.findViewById(R.id.slider_root)).getProgress();
        int third = ((VerticalSeekBar) activity.findViewById(R.id.slider_third)).getProgress();
        int fifth = ((VerticalSeekBar) activity.findViewById(R.id.slider_fifth)).getProgress();
        int seventh = ((VerticalSeekBar) activity.findViewById(R.id.slider_option)).getProgress();

        if (getCurrentSelectedChord().length == MIN_NOTES_PER_CHORD)
        {
            currentBuiltChord = new int[]{root/currentNumInterval + currentSliderOffset[0],
                                          third/currentNumInterval + currentSliderOffset[1],
                                          fifth/currentNumInterval + currentSliderOffset[2]};
        }
        else
        {
            currentBuiltChord = new int[]{root/currentNumInterval + currentSliderOffset[0],
                                          third/currentNumInterval + currentSliderOffset[1],
                                          fifth/currentNumInterval + currentSliderOffset[2],
                                          seventh/currentNumInterval + currentSliderOffset[3]};
        }
    }

    /**
     * Builds the current precise chord that the user has defined on the sliders.
     * @param activity The current Activity
     */
    private static void buildCurrentPreciseChord(Activity activity)
    {
        int root = ((SeekBar) activity.findViewById(R.id.slider_root)).getProgress();
        int third = ((SeekBar) activity.findViewById(R.id.slider_third)).getProgress();
        int fifth = ((SeekBar) activity.findViewById(R.id.slider_fifth)).getProgress();
        int seventh = ((SeekBar) activity.findViewById(R.id.slider_option)).getProgress();

        if (getCurrentSelectedChord().length == MIN_NOTES_PER_CHORD)
        {
            currentPreciseBuiltChord = new int[]{root, third, fifth};
        }
        else
        {
            currentPreciseBuiltChord = new int[]{root, third, fifth, seventh};
        }
    }

    /**
     * Called when the user checks the current chord.
     * @param activity Handle to the MainActivity
     */
    public static void checkCurrentChord(final MainActivity activity)
    {
        boolean isCorrect = compareChords(getCurrentPreciseBuiltChord(activity), getCurrentCorrectChord());

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
                            activity.updateChordSelectSpinner();
                            SliderFragment.resetChordSliders();
                            soundHandler.stopSound();

                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            setSelectedChord(getSelectedChordIndex()); // Resets the wrong streak counter
                            SliderFragment.resetChordSliders();
                            soundHandler.stopSound();
                        }

                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            SliderFragment.resetChordSliders();
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
            Toast.makeText(activity, activity.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT).show();
        }

        // Set the score
        Score.getCurrentScore().update(activity, isCorrect);
        activity.updateDisplayedScore();

        builtChordChanged();
    }

    /**
     * Creates a blank chord array, which will contain the currently available chords if non-null.
     * @return A blank chord array
     */
    private static int[][] createEmptyChordArray()
    {
        int[][] returnArray;

        if (availableChords == null)
            returnArray = new int[CHORDS_PER_TYPE][];
        else
        {
            returnArray = new int[availableChords.length + CHORDS_PER_TYPE][];
            System.arraycopy(availableChords, 0, returnArray, 0, availableChords.length);
        }

        return returnArray;
    }

    /**
     * Interface for classes listening for chord selection.
     */
    public interface OnChordSelectedListener
    {
        /**
         * Called when a new chord is selected.
         */
        void onChordSelected();
    }
}

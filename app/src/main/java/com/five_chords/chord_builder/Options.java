package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.PitchBendSettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Class wrapping handles to the possible option values of the application. Options has methods to change these values
 * and notify an optional listener. Options can be saved and loaded.
 */
public class Options
{
    /** The name of the options in the SharedPreferences. */
    public static final String OPTIONS_SAVE_FILENAME = "OptionsFile";

    /** Bundle id for the useHints flag. */
    private static final String HINTS_BUNDLE_ID = "OptionsFragment.Options.HINT";

    /** Bundle id for the hintTypeDelays settings. */
    private static final String HINT_DELAYS_BUNDLE_ID = "OptionsFragment.Options.HINT_DELAYS";

    /** Bundle id for the number of chord inversions in use flag. */
    private static final String CHORDS_INVERSIONS_SIZE_BUNDLE_ID = "OptionsFragment.Options.CHORDS_INVERSIONS_SIZE";

    /** Bundle id for used Chord inversions. */
    private static final String CHORDS_INVERSIONS_BUNDLE_ID = "OptionsFragment.Options.CHORDS_INVERSIONS";

    /** Bundle id for the chords in use list. */
    private static final String CHORDS_IN_USE_BUNDLE_ID = "OptionsFragment.Options.CHORDS_IN_USE";

    /** Bundle id for the number of intermediate note slider positions. */
    private static final String NUM_SLIDER_DIVISIONS_BUNDLE_ID = "OptionsFragment.Options.INTER_NOTE_POS";

    /** Bundle id for the maximum allowable error for checking notes on the Chord sliders as a fraction. */
    private static final String CHECK_ERROR_BUNDLE_ID = "OptionsFragment.Options.CHECK_ERROR";

    /** Bundle id for the instrument settings. */
    private static final String INSTRUMENT_BUNDLE_ID = "OptionsFragment.Options.INSTRUMENT";

    /** Bundle id for the show answer sequence flag. */
    private static final String SHOW_ANS_SEQ_BUNDLE_ID = "OptionsFragment.Options.ANS_SEQ";

    /** The default number of slider positions per note. */
    private static final int DEFAULT_SLIDER_DIVISIONS_PER_NOTE = 1;

    /** The default maximum allowable error for checking notes on the Chord sliders as a fraction. */
    private static final float DEFAULT_CHECK_ERROR = (float) PitchBendSettingsFragment.MAXIMUM_CHECK_ERROR;

    /** The array of default hint delays. */
    private static final int[] DEFAULT_HINT_DELAYS = new int[] {2, 6, 10};

    /** The maximum number of chord inversions. */
    private static final int NUM_INVERSIONS = 4;

    /** Whether or not hints are enabled */
    public boolean useHints;

    /** The number of wrong attempts to wait between hint types. */
    public int[] hintTypeDelays;

    /** Which instrument does the user want? **/
    public int instrument;

    /** Whether or not to show the answer sequence when the user guesses. */
    public boolean showAnswerSequence;

    /** The number of intermediate slider positions per note on the Chord sliders. */
    public int sliderDivisionsPerNote;

    /** The maximum allowable error for checking notes on the Chord sliders as a fraction. */
    public double allowableCheckError;

//    /** The chord inversions to use. */
//    public List<Byte> chordInversionsToUse;
    /** The chord inversions to use. */
    public boolean[] chordInversionsToUse;

    /** Array denoting what chord types are being used. */
    public boolean[] chordTypesInUseArray;

    /** List containing each ChordType in use. */
    private List<Chord.ChordType> chordTypesInUse;

    /** The OptionsChangedListener attached to this Options. */
    private OptionsChangedListener optionsChangedListener;

    /**
     * Default constructor.
     */
    public Options()
    {
        // Hints
        useHints = true;
        hintTypeDelays = new int[3];

        // Show answer sequence
        showAnswerSequence = true;

        // Slider divisions per note
        sliderDivisionsPerNote = DEFAULT_SLIDER_DIVISIONS_PER_NOTE;

        // Chord Types in use
        chordTypesInUse = new ArrayList<>();
        chordTypesInUseArray = new boolean[Chord.ChordType.values().length];
        chordTypesInUseArray[Chord.ChordType.MAJOR.ordinal()] = true;

        // Chord Inversions to use
        chordInversionsToUse = new boolean[NUM_INVERSIONS];
//        chordInversionsToUse = new ArrayList<>();
    }

    /**
     * Gets the SharedPreferences used to load and save the options.
     * @param activity The calling Activity
     */
    private static SharedPreferences getOptionsLoader(Activity activity)
    {
        return activity.getSharedPreferences(OPTIONS_SAVE_FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Gets the number of ChordTypes that are currently in use.
     * @return The number of ChordTypes that are currently in use
     */
    public int getNumChordTypesInUse()
    {
        return chordTypesInUse.size();
    }

    /**
     * Gets the List containing the ChordTypes that are currently in use.
     * @return The List containing the ChordTypes that are currently in use
     */
    public List<Chord.ChordType> getChordTypesInUse()
    {
        return chordTypesInUse;
    }

    /**
     * Sets the OptionsChangedListener attached to this Options.
     * @param listener The OptionsChangedListener attached to this Options
     */
    public void setOptionsChangedListener(OptionsChangedListener listener)
    {
        this.optionsChangedListener = listener;
    }

    /**
     * Called to change the show answer sequence flag.
     * @param show The new value of the show answer sequence flag
     */
    public void setShowAnswerSequence(boolean show)
    {
        showAnswerSequence = show;
        if (optionsChangedListener != null)
            optionsChangedListener.onShowAnswerSequenceChanged(showAnswerSequence);
    }

    /**
     * Adds a chord inversion to use. 0 denotes first inversion, 1 denotes second, and so on.
     * @param inversion The inversion to add
     */
    public void addChordInversion(byte inversion)
    {

//        if (!chordInversionsToUse.contains(inversion))
//        {
//            chordInversionsToUse.add(inversion);
//
//            if (optionsChangedListener != null)
//                optionsChangedListener.onInversionSelectionChanged(chordInversionsToUse);
//        }
    }

    /**
     * Removes a chord inversion. Zero denotes first inversion, One denotes second, and so on.
     * @param inversion The inversion to remove
     */
    public void removeChordInversion(byte inversion)
    {
        if (!chordInversionsToUse[inversion])
        {
            chordInversionsToUse[inversion] = true;

            if (optionsChangedListener != null)
                optionsChangedListener.onInversionSelectionChanged(chordInversionsToUse);
        }
    }

    /**
     * Called to change the pitch bend settings.
     * @param incrementsPerNote The new number of increments per note
     * @param maxCheckError The new max check error
     */
    public void changePitchBendSettings(int incrementsPerNote, double maxCheckError)
    {
        if (incrementsPerNote != sliderDivisionsPerNote || maxCheckError != allowableCheckError)
        {
            sliderDivisionsPerNote = incrementsPerNote;
            allowableCheckError = maxCheckError;

            if (optionsChangedListener != null)
                optionsChangedListener.onPitchBendSettingsChanged(sliderDivisionsPerNote, allowableCheckError);
        }
    }

    /**
     * Called to change the instrument selection.
     * @param instrumentIndex The new instrument index
     */
    public void changeInstrument(int instrumentIndex)
    {
        instrument = instrumentIndex;

        if (optionsChangedListener != null)
            optionsChangedListener.onInstrumentChanged(instrument);
    }

    /**
     * Called to change the hint options.
     * @param useHints Whether or not to use Hints
     * @param hintDelay1 The delay for the first hint type
     * @param hintDelay2 The delay for the second hint type
     * @param hintDelay3 The delay for the third hint type
     */
    public void changeHints(boolean useHints, int hintDelay1, int hintDelay2, int hintDelay3)
    {
        if (useHints == this.useHints && hintTypeDelays[0] == hintDelay1 &&
                hintTypeDelays[1] == hintDelay2 && hintTypeDelays[2] == hintDelay3)
            return;

        this.useHints = useHints;
        this.hintTypeDelays[0] = hintDelay1;
        this.hintTypeDelays[1] = hintDelay2;
        this.hintTypeDelays[2] = hintDelay3;

        if (optionsChangedListener != null)
            optionsChangedListener.onHintsOptionsChanged(this.useHints);
    }

    /**
     * Sets whether or not the ChordType of the given index should be used.
     * @param index The index of the ChordType
     * @param use Whether or not the ChordType should be used
     */
    public void setChordTypeUse(int index, boolean use)
    {
        // Set new value
        chordTypesInUseArray[index] = use;

        // Reset ChordType List
        populateChordTypesInUse();

        // Notify Listener that changes were made
        if (optionsChangedListener != null)
            optionsChangedListener.onChordTypeOptionsChanged(chordTypesInUse);
    }

    /**
     * Saves this Options.
     * @param activity The current Activity
     */
    public void save(Activity activity)
    {
        SharedPreferences.Editor editor = getOptionsLoader(activity).edit();

        // Save flags
        editor.putBoolean(HINTS_BUNDLE_ID, useHints);
        editor.putInt(NUM_SLIDER_DIVISIONS_BUNDLE_ID, sliderDivisionsPerNote);
        editor.putFloat(CHECK_ERROR_BUNDLE_ID, (float) allowableCheckError);
        editor.putInt(INSTRUMENT_BUNDLE_ID, instrument);
        editor.putBoolean(SHOW_ANS_SEQ_BUNDLE_ID, showAnswerSequence);

        // Save hint delays
        for (int i = 0; i < hintTypeDelays.length; ++i)
            editor.putInt(HINT_DELAYS_BUNDLE_ID + i, hintTypeDelays[i]);

        // Save chord types in use
        for (int i = 0; i < chordTypesInUseArray.length; ++i)
            editor.putBoolean(CHORDS_IN_USE_BUNDLE_ID + i, chordTypesInUseArray[i]);

        // Save chord inversions to use
//        editor.putInt(CHORDS_INVERSIONS_SIZE_BUNDLE_ID, chordInversionsToUse.size());
        for (int i = 0; i < NUM_INVERSIONS; ++i)
            editor.putBoolean(CHORDS_INVERSIONS_BUNDLE_ID + i, chordInversionsToUse[i]);

        editor.apply();
    }

    /**
     * Loads this Options.
     * @param activity The current Activity
     */
    public void load(Activity activity)
    {
        SharedPreferences preferences = getOptionsLoader(activity);

        // Read flags
        useHints = preferences.getBoolean(HINTS_BUNDLE_ID, true);
        instrument = preferences.getInt(INSTRUMENT_BUNDLE_ID, 0);
        sliderDivisionsPerNote = preferences.getInt(NUM_SLIDER_DIVISIONS_BUNDLE_ID, DEFAULT_SLIDER_DIVISIONS_PER_NOTE);
        allowableCheckError = preferences.getFloat(CHECK_ERROR_BUNDLE_ID, DEFAULT_CHECK_ERROR);
        showAnswerSequence = preferences.getBoolean(SHOW_ANS_SEQ_BUNDLE_ID, true);

        // Read hint delays
        for (int i = 0; i < hintTypeDelays.length; ++i)
            hintTypeDelays[i] = preferences.getInt(HINT_DELAYS_BUNDLE_ID + i, DEFAULT_HINT_DELAYS[i]);

        // Read chord types in use
        for (int i = 0; i < chordTypesInUseArray.length; ++i)
            chordTypesInUseArray[i] = preferences.getBoolean(CHORDS_IN_USE_BUNDLE_ID + i, chordTypesInUseArray[i]);

        // Load chord inversions to use
//        int size = preferences.getInt(CHORDS_INVERSIONS_SIZE_BUNDLE_ID, 0);
        for (int i = 0; i < NUM_INVERSIONS; ++i)
        {
            try
            {
                chordInversionsToUse[i] = preferences.getBoolean(CHORDS_INVERSIONS_BUNDLE_ID + i, false);
            }
            catch (Exception e)
            {/* Ignore */}
        }
//            chordInversionsToUse.add((byte)preferences.getInt(CHORDS_INVERSIONS_BUNDLE_ID + i, 0));

        // Populate ChordType List
        populateChordTypesInUse();
    }

    /**
     * Fills the List containing the current ChordTypes in use based on the use array.
     */
    private void populateChordTypesInUse()
    {
        chordTypesInUse.clear();

        for (int i = 0; i < chordTypesInUseArray.length; ++i)
            if (chordTypesInUseArray[i])
                chordTypesInUse.add(Chord.ChordType.values()[i]);
    }

    /**
     * Interface for listening Options changes.
     */
    public interface OptionsChangedListener
    {
        /**
         * Called when the chord type changes.
         * @param chordTypesInUse A List containing the ChordTypes that are now in use
         */
        void onChordTypeOptionsChanged(List<Chord.ChordType> chordTypesInUse);

        /**
         * Called when the hints options changes.
         * @param useHints Whether or not hints are now enabled.
         */
        void onHintsOptionsChanged(boolean useHints);

        /**
         * Called when the showAnswerSequence flag changes.
         * @param showAnswerSeq The new value of the showAnswerSequence flag
         */
        void onShowAnswerSequenceChanged(boolean showAnswerSeq);

        /**
         * Called when the instrument selection changes.
         * @param instrument The new instrument
         */
        void onInstrumentChanged(int instrument);

        /**
         * Called when the chord inversion selection changes.
         * @param inversions The new inversion selection
         */
        void onInversionSelectionChanged(boolean[] inversions);

        /**
         * Called when the pitch bend settings change.
         * @param incrementsPerNote The new increments per note
         * @param maxCheckError The new max check error
         */
        void onPitchBendSettingsChanged(int incrementsPerNote, double maxCheckError);
    }
}

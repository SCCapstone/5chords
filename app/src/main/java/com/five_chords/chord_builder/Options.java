package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Class wrapping handles to the possible option values of the application.
 */
public class Options
{
    /** The name of the options in the SharedPreferences. */
    public static final String OPTIONS_SAVE_FILENAME = "OptionsFile";

    /** Bundle id for the useHints flag. */
    private static final String HINTS_BUNDLE_ID = "OptionsFragment.Options.HINT";

    /** Bundle id for the hintTypeDelays settings. */
    private static final String HINT_DELAYS_BUNDLE_ID = "OptionsFragment.Options.HINT_DELAYS";

    /** Bundle id for useScrambledRootPositions flag. */
    private static final String CHORDS_SCRAM_POS_BUNDLE_ID = "OptionsFragment.Options.CHORDS_SCRAM_POS";

    /** Bundle id for useChordInversions flag. */
    private static final String CHORDS_INVERSIONS_BUNDLE_ID = "OptionsFragment.Options.CHORDS_INVERSIONS";

    /** Bundle id for the chords in use list. */
    private static final String CHORDS_IN_USE_BUNDLE_ID = "OptionsFragment.Options.CHORDS_IN_USE";

    /** Bundle id for the number of intermediate note slider positions. */
    private static final String NUM_SLIDER_DIVISIONS_BUNDLE_ID = "OptionsFragment.Options.INTER_NOTE_POS";

    /** Bundle id for the maximum allowable error for checking notes on the Chord sliders as a fraction. */
    private static final String CHECK_ERROR_BUNDLE_ID = "OptionsFragment.Options.CHECK_ERROR";

    /** The default number of slider positions per note. */
    private static final int DEFAULT_SLIDER_DIVISIONS_PER_NOTE = 1;

    /** The default maximum allowable error for checking notes on the Chord sliders as a fraction. */
    private static final float DEFAULT_CHECK_ERROR = 0.25f;

    /** The array of default hint delays. */
    private static final int[] DEFAULT_HINT_DELAYS = new int[] {2, 6, 10};

    /** Whether or not hints are enabled */
    public boolean useHints;

    /** The number of wrong attempts to wait between hint types. */
    public int[] hintTypeDelays;

    /** The number of intermediate slider positions per note on the Chord sliders. */
    public int sliderDivisionsPerNote;

    /** The maximum allowable error for checking notes on the Chord sliders as a fraction. */
    public double allowableCheckError;

    /** Whether or not to use Chord inversions. */
    public boolean useChordInversions;

    /** Whether or not to use Chord root positions where note after the root may be scrambled. */
    public boolean useScrambledRootPositions;

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

        // Chord Types in use
        chordTypesInUse = new ArrayList<>();
        chordTypesInUseArray = new boolean[Chord.ChordType.values().length];
        chordTypesInUseArray[Chord.ChordType.MAJOR.ordinal()] = true;
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
     * Gets whether or not this given ChordType is currently in use.
     * @param type The ChordType to check
     * @return Whether or not this given ChordType is currently in use
     */
    public boolean isUsingChordType(Chord.ChordType type)
    {
        return chordTypesInUseArray[type.ordinal()];
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
        // Do nothing if there are no changes
        if (chordTypesInUseArray[index] == use)
            return;

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
        editor.putBoolean(CHORDS_SCRAM_POS_BUNDLE_ID, useScrambledRootPositions);
        editor.putBoolean(CHORDS_INVERSIONS_BUNDLE_ID, useChordInversions);
        editor.putInt(NUM_SLIDER_DIVISIONS_BUNDLE_ID, sliderDivisionsPerNote);
        editor.putFloat(CHECK_ERROR_BUNDLE_ID, (float) allowableCheckError);

        // Save hint delays
        for (int i = 0; i < hintTypeDelays.length; ++i)
            editor.putInt(HINT_DELAYS_BUNDLE_ID + i, hintTypeDelays[i]);

        // Save chord types in use
        for (int i = 0; i < chordTypesInUseArray.length; ++i)
            editor.putBoolean(CHORDS_IN_USE_BUNDLE_ID + i, chordTypesInUseArray[i]);

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
        useScrambledRootPositions = preferences.getBoolean(CHORDS_SCRAM_POS_BUNDLE_ID, false);
        useChordInversions = preferences.getBoolean(CHORDS_INVERSIONS_BUNDLE_ID, false);

        // TODO temporary
        sliderDivisionsPerNote = DEFAULT_SLIDER_DIVISIONS_PER_NOTE;
//        sliderDivisionsPerNote = preferences.getInt(NUM_SLIDER_DIVISIONS_BUNDLE_ID, DEFAULT_SLIDER_DIVISIONS_PER_NOTE);
        allowableCheckError = preferences.getFloat(CHECK_ERROR_BUNDLE_ID, DEFAULT_CHECK_ERROR);

        // Read hint delays
        for (int i = 0; i < hintTypeDelays.length; ++i)
            hintTypeDelays[i] = preferences.getInt(HINT_DELAYS_BUNDLE_ID + i, DEFAULT_HINT_DELAYS[i]);

        // Read chord types in use
        for (int i = 0; i < chordTypesInUseArray.length; ++i)
            chordTypesInUseArray[i] = preferences.getBoolean(CHORDS_IN_USE_BUNDLE_ID + i, chordTypesInUseArray[i]);

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
     * Interface for listening for chord type changes.
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
    }
}

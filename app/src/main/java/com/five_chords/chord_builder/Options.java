package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class wrapping handles to the possible option values of the application.
 */
public class Options
{
    /** The name of the options in the SharedPreferences */
    public static final String OPTIONS_SAVE_FILENAME = "OptionsFile";

    /** Bundle id for the useMajorChords flag */
    private static final String MAJOR_CHORDS_BUNDLE_ID = "OptionsFragment.Options.MAJOR";

    /** Bundle id for the useMinorChords flag */
    private static final String MINOR_CHORDS_BUNDLE_ID = "OptionsFragment.Options.MINOR";

    /** Bundle id for the useDominantChords flag */
    private static final String DOMINANT_CHORDS_BUNDLE_ID = "OptionsFragment.Options.DOMINANT";

    /** Bundle id for the useHints flag */
    private static final String HINTS_BUNDLE_ID = "OptionsFragment.Options.HINT";

    /** Bundle id for the hintTypeDelays settings */
    private static final String HINT_DELAYS_BUNDLE_ID = "OptionsFragment.Options.HINT_DELAYS";

    /** Bundle id for the usePitchBending settings */
    private static final String PITCH_BENDING_BUNDLE_ID = "OptionsFragment.Options.BEND_PITCH";

    /** Bundle id for the instrument settings */
    private static final String INSTRUMENT_BUNDLE_ID = "OptionsFragment.Options.INSTRUMENT";

    /** Bundle id for the instrument settings */
    private static final String INVERSION_BUNDLE_ID = "OptionsFragment.Options.INVERSION";

    /** Bundle id for the user levels */
    private static final String USER_LEVEL = "OptionsFragment.Options.USERLEVEL";

    /** Whether or not major chords are being used */
    public boolean useMajorChords;

    /** Whether or not minor chords are being used */
    public boolean useMinorChords;

    /** Whether or not dominant chords are being used */
    public boolean useDominantChords;

    /** Whether or not hints are enabled */
    public boolean useHints;

    /** The number of wrong attempts to wait between hint types. */
    public int[] hintTypeDelays;

    /** Does the user want to train with pitch **/
    public boolean usePitchBending;

    /** Which instrument does the user want? **/
    public int instrument;

    /** Does the user what to practice with chord inversions? **/
    public boolean useInversion;

    /** What level does the user want to practice at? **/
    public int userLevel;

    /** The OptionsChangedListener attached to this Options. */
    private OptionsChangedListener optionsChangedListener;

    /**
     * Default constructor.
     */
    public Options()
    {
        useMajorChords = true;
        useMinorChords = false;
        useDominantChords = false;
        useHints = true;
        hintTypeDelays = new int[] {2, 6, 10};
        usePitchBending = false;
        userLevel = 0;
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
     * Called to change the chord type selections.
     * @param useMajors Whether or not major chords are now being used
     * @param useMinors  Whether or not minor chords are now being used
     * @param useDominants  Whether or not dominant chords are now being used
     */
    public void changeChordSelections(boolean useMajors, boolean useMinors, boolean useDominants)
    {
        if (this.useMajorChords == useMajors && this.useMinorChords == useMinors &&
                this.useDominantChords == useDominants)
            return;

        this.useMajorChords = useMajors;
        this.useMinorChords = useMinors;
        this.useDominantChords = useDominants;

        if (optionsChangedListener != null)
            optionsChangedListener.onChordTypeOptionsChanged(this.useMajorChords,
                    this.useMinorChords, this.useDominantChords);
    }

    /**
     * Called to enable or disable pitch bending
     * @param usePitch Whether or not pitch bending is enabled
     */
    public void changePitchOptions(boolean usePitch) {
        this.usePitchBending = usePitch;

        if (optionsChangedListener != null)
            optionsChangedListener.onPitchOptionsChanged(this.usePitchBending);
    }

    /**
     * Called when an instrument changes.
     * @param instrumentIndex Which instrument was selected.
     */
    public void changeInstrument(int instrumentIndex) {
        this.instrument = instrumentIndex;

        if (optionsChangedListener != null)
            optionsChangedListener.onInstrumentChanged(instrument);
    }

    /**
     * Called to enable or disable chord inversions
     * @param useInversion Whether or not inversions are enabled
     */
    public void changeInversionOptions(boolean useInversion) {
        this.useInversion = useInversion;

        if (optionsChangedListener != null)
            optionsChangedListener.onChangeInversionOptions(this.useInversion);
    }

    /**
     * Called to change the user level
     * @param userLevel the level the user wants to practice at
     */
    public void changeUserLevel(int userLevel) {
        this.userLevel = userLevel;

        if (optionsChangedListener != null)
            optionsChangedListener.onChangeUserLevel(this.userLevel);
    }

    /**
     * Saves this Options.
     * @param activity The current Activity
     */
    public void save(Activity activity)
    {
        SharedPreferences.Editor editor = getOptionsLoader(activity).edit();

        editor.putBoolean(MAJOR_CHORDS_BUNDLE_ID, useMajorChords);
        editor.putBoolean(MINOR_CHORDS_BUNDLE_ID, useMinorChords);
        editor.putBoolean(DOMINANT_CHORDS_BUNDLE_ID, useDominantChords);
        editor.putBoolean(HINTS_BUNDLE_ID, useHints);
        editor.putBoolean(PITCH_BENDING_BUNDLE_ID, usePitchBending);
        editor.putInt(INSTRUMENT_BUNDLE_ID, instrument);
        editor.putBoolean(INVERSION_BUNDLE_ID, useInversion);
        editor.putInt(USER_LEVEL, userLevel);

        for (int i = 0; i < hintTypeDelays.length; ++i)
            editor.putInt(HINT_DELAYS_BUNDLE_ID + i, hintTypeDelays[i]);

        editor.apply();
    }

    /**
     * Loads this Options.
     * @param activity The current Activity
     */
    public void load(Activity activity)
    {
        SharedPreferences preferences = getOptionsLoader(activity);
        useMajorChords = preferences.getBoolean(MAJOR_CHORDS_BUNDLE_ID, true);
        useMinorChords = preferences.getBoolean(MINOR_CHORDS_BUNDLE_ID, false);
        useDominantChords = preferences.getBoolean(DOMINANT_CHORDS_BUNDLE_ID, false);
        useHints = preferences.getBoolean(HINTS_BUNDLE_ID, true);
        usePitchBending = preferences.getBoolean(PITCH_BENDING_BUNDLE_ID, true);
        instrument = preferences.getInt(INSTRUMENT_BUNDLE_ID, 57);
        useInversion = preferences.getBoolean(INVERSION_BUNDLE_ID, false);
        userLevel = preferences.getInt(USER_LEVEL, 0);

        for (int i = 0; i < hintTypeDelays.length; ++i)
            hintTypeDelays[i] = preferences.getInt(HINT_DELAYS_BUNDLE_ID + i, 2 + i * 4);
    }

    /**
     * Interface for listening for chord type changes.
     */
    public interface OptionsChangedListener
    {
        /**
         * Called when the chord type changes.
         * @param useMajors Whether or not major chords are now being used
         * @param useMinors  Whether or not minor chords are now being used
         * @param useDominants  Whether or not dominant chords are now being used
         */
        void onChordTypeOptionsChanged(boolean useMajors, boolean useMinors, boolean useDominants);

        /**
         * Called when the hints options changes.
         * @param useHints Whether or not hints are now enabled.
         */
        void onHintsOptionsChanged(boolean useHints);


        /**
         * Called when the pitch options changes.
         * @param usePitchBending Whether or not pitch bending is enabled.
         */
        void onPitchOptionsChanged(boolean usePitchBending);

        /**
         * Called when an instrument changes.
         * @param instrument Which instrument was selected.
         */
        void onInstrumentChanged(int instrument);

        /**
         * Called when the inversion options changes.
         * @param useInversions Whether or not inversions are enabled.
         */
        void onChangeInversionOptions(boolean useInversions);


        /**
         * Called to change the user level
         * @param userLevel the level the user wants to practice at
         */
        void onChangeUserLevel(int userLevel);
    }
}

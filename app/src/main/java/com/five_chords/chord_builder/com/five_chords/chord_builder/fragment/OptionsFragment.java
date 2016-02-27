package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;

/**
 * A Fragment containing the options for the Main Activity
 * @author tstone95
 */
public class OptionsFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener
{
    /** The minor chord checkbox */
    private CheckBox minorBox;
    /** The major chord checkbox */
    private CheckBox majorBox;
    /** The dominant chord checkbox */
    private CheckBox dominantBox;

    /** The hints switch */
    private Switch hintsSwitch;

    /** Button to clear the scores */
    private Button clearScoresButton;

    /** The OnChordTypeChangeListener of this Fragment */
    private OptionsChangedListener optionsChangedListener;

    /**
     * Required empty public constructor.
     */
    public OptionsFragment()
    {   }

    /**
     * Create a new instance of OptionsFragment.
     * @param options The current Options object, used to set default values of the OptionsFragment
     * @return A new instance of OptionsFragment
     */
    public static OptionsFragment newInstance(Options options)
    {
        OptionsFragment f = new OptionsFragment();

        // Supply arguments to Bundle
        Bundle args = new Bundle();
        options.writeToBundle(args);
        f.setArguments(args);

        return f;
    }

    /**
     * Gets whether or not major chords should be used.
     * @return Whether or not major chords should be used
     */
    public boolean useMajors()
    {
        return majorBox.isChecked();
    }

    /**
     * Gets whether or not minor chords should be used.
     * @return Whether or not minor chords should be used
     */
    public boolean useMinors()
    {
        return minorBox.isChecked();
    }

    /**
     * Gets whether or not dominant chords should be used.
     * @return Whether or not dominant chords should be used
     */
    public boolean useDominants()
    {
        return dominantBox.isChecked();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // Dialog specific conditions
        if (getDialog() != null)
        {
            // Set size if dialog
            int width = getResources().getDimensionPixelSize(R.dimen.options_dialog_width);
            int height = getResources().getDimensionPixelSize(R.dimen.options_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragmet's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_options, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.options);

        // Add listener to checkboxes
        assignViews(view);

        // Read options
        Options options = new Options();

        if (getArguments() != null)
            options.readFromBundle(getArguments());

        // Set options
        majorBox.setChecked(options.useMajorChords);
        minorBox.setChecked(options.useMinorChords);
        dominantBox.setChecked(options.useDominantChords);
        hintsSwitch.setChecked(options.useHints);

        // Add listeners
        updateEnables();
        minorBox.setOnCheckedChangeListener(this);
        majorBox.setOnCheckedChangeListener(this);
        dominantBox.setOnCheckedChangeListener(this);
        hintsSwitch.setOnCheckedChangeListener(this);
        clearScoresButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Score.resetScores(getActivity());
            }
        });

        // Return the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            optionsChangedListener = (OptionsChangedListener)activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OptionsChangedListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        optionsChangedListener = null;
    }


    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        // A CheckBox was changed
        if (buttonView instanceof CheckBox)
        {
            updateEnables();

            if (optionsChangedListener != null)
                optionsChangedListener.onChordTypeOptionsChanged(useMajors(), useMinors(), useDominants());
        }
        // A Switch was changed
        else if (buttonView instanceof Switch)
        {
            if (optionsChangedListener != null)
                optionsChangedListener.onHintsOptionsChanged(isChecked);
        }

        // Debug
        if (optionsChangedListener == null)
            Log.e("CheckOptionsFragment", "No OptionsChangedListener assigned");
    }

    /**
     * Assigns the View references in this class.
     * @param view The View containing the layout containing the Views
     */
    private void assignViews(View view)
    {
        minorBox = (CheckBox) view.findViewById(R.id.checkbox_minor_chords);
        majorBox = (CheckBox) view.findViewById(R.id.checkbox_major_chords);
        dominantBox = (CheckBox) view.findViewById(R.id.checkbox_dominant_chords);
        hintsSwitch = (Switch) view.findViewById(R.id.switch_hints);
        clearScoresButton = (Button) view.findViewById(R.id.button_clear_scores);
    }

    /**
     * Updates the enabled state of each check box, determined by the number of checks.
     */
    private void updateEnables()
    {
        // Count the number of checks
        int numChecks = (minorBox.isChecked() ? 1:0) + (majorBox.isChecked() ? 1:0) + (dominantBox.isChecked() ? 1:0);

        // Disable or enable checkboxes to prevent all of them from being unchecked
        minorBox.setEnabled(numChecks > 1 || !minorBox.isChecked());
        majorBox.setEnabled(numChecks > 1 || !majorBox.isChecked());
        dominantBox.setEnabled(numChecks > 1 || !dominantBox.isChecked());
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
    }

    /**
     * Class wrapping handles to the possible option values of the application.
     */
    public static class Options
    {
        /** Bundle id for the useMajorChords flag */
        private static final String MAJOR_CHORDS_BUNDLE_ID = "OptionsFragment.Options.MAJOR";

        /** Bundle id for the useMinorChords flag */
        private static final String MINOR_CHORDS_BUNDLE_ID = "OptionsFragment.Options.MINOR";

        /** Bundle id for the useDominantChords flag */
        private static final String DOMINANT_CHORDS_BUNDLE_ID = "OptionsFragment.Options.DOMINANT";

        /** Bundle id for the useHints flag */
        private static final String HINTS_BUNDLE_ID = "OptionsFragment.Options.HINT";

        /** Whether or not major chords are being used */
        public boolean useMajorChords;

        /** Whether or not minor chords are being used */
        public boolean useMinorChords;

        /** Whether or not dominant chords are being used */
        public boolean useDominantChords;

        /** Whether or not hints are enabled */
        public boolean useHints;

        /**
         * Default constructor.
         */
        public Options()
        {
            useMajorChords = true;
            useMinorChords = false;
            useDominantChords = false;
            useHints = true;
        }

        /**
         * Reads the values of this OptionsFragment from the given Bundle.
         * @param bundle The Bundle from which to read
         */
        public void readFromBundle(Bundle bundle)
        {
            useMajorChords = bundle.getBoolean(MAJOR_CHORDS_BUNDLE_ID);
            useMinorChords = bundle.getBoolean(MINOR_CHORDS_BUNDLE_ID);
            useDominantChords = bundle.getBoolean(DOMINANT_CHORDS_BUNDLE_ID);
            useHints = bundle.getBoolean(HINTS_BUNDLE_ID);
        }

        /**
         * Writes the values of this OptionsFragment to the given Bundle.
         * @param bundle The Bundle to which to write
         */
        public void writeToBundle(Bundle bundle)
        {
            bundle.putBoolean(MAJOR_CHORDS_BUNDLE_ID, useMajorChords);
            bundle.putBoolean(MINOR_CHORDS_BUNDLE_ID, useMinorChords);
            bundle.putBoolean(DOMINANT_CHORDS_BUNDLE_ID, useDominantChords);
            bundle.putBoolean(HINTS_BUNDLE_ID, useHints);
        }
    }
}

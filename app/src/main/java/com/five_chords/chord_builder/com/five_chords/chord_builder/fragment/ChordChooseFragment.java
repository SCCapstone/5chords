package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.five_chords.chord_builder.MainActivity;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Options;

/**
 * A Fragment containing the hint settings.
 * @author tstone95
 */
public class ChordChooseFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener
{
    /** The minor chord checkbox */
    private CheckBox minorBox;
    /** The major chord checkbox */
    private CheckBox majorBox;
    /** The dominant chord checkbox */
    private CheckBox dominantBox;

    /**
     * Required empty public constructor.
     */
    public ChordChooseFragment()
    {   }

    /**
     * Create a new instance of ChordChooseFragment.
     * @return A new instance of ChordChooseFragment
     */
    public static ChordChooseFragment newInstance()
    {
        ChordChooseFragment f = new ChordChooseFragment();
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

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chord_choose_settings, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.choose_chords);

        // Set views
        minorBox = (CheckBox) view.findViewById(R.id.checkbox_minor_chords);
        majorBox = (CheckBox) view.findViewById(R.id.checkbox_major_chords);
        dominantBox = (CheckBox) view.findViewById(R.id.checkbox_dominant_chords);

        // Add listeners
        updateEnables();
        minorBox.setOnCheckedChangeListener(this);
        majorBox.setOnCheckedChangeListener(this);
        dominantBox.setOnCheckedChangeListener(this);

        // Set Default values
        Options options = MainActivity.getOptions();

        // Set options
        majorBox.setChecked(options.useMajorChords);
        minorBox.setChecked(options.useMinorChords);
        dominantBox.setChecked(options.useDominantChords);

        // Return the View
        return view;
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
            MainActivity.getOptions().changeChordSelections(useMajors(), useMinors(), useDominants());
        }
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
}

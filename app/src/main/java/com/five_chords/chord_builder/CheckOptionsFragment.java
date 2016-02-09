package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Date;

/**
 * A Fragment containing the chord check options.
 * @author tstone95
 */
public class CheckOptionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener
{
    /** The minor chord checkbox */
    private CheckBox minorBox;
    /** The major chord checkbox */
    private CheckBox majorBox;
    /** The dominant chord checkbox */
    private CheckBox dominantBox;

    /** The OnChordTypeChangeListener of this Fragment */
    private OnChordTypeChangeListener chordTypeChangeListener;

    /**
     * Required empty public constructor.
     */
    public CheckOptionsFragment()
    {   }

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
        View view = inflater.inflate(R.layout.fragment_check_options, container, false);

        // Add listener to checkboxes
        assignCheckboxes(view);
        updateEnables();
        minorBox.setOnCheckedChangeListener(this);
        majorBox.setOnCheckedChangeListener(this);
        dominantBox.setOnCheckedChangeListener(this);

        // Return the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            chordTypeChangeListener = (OnChordTypeChangeListener)activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnChordTypeChangeListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        chordTypeChangeListener = null;
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
        updateEnables();

        if (chordTypeChangeListener != null)
            chordTypeChangeListener.onChordTypeChanged(useMajors(), useMinors(), useDominants());
        else
            Log.e("CheckOptionsFragment", "No ChordTypeChangeListener assigned");
    }

    /**
     * Assigns the CheckBox references in this class.
     * @param view The View containing the layout containing the Checkboxes
     */
    private void assignCheckboxes(View view)
    {
        minorBox = ((CheckBox) view.findViewById(R.id.checkbox_minor_chords));
        majorBox = ((CheckBox) view.findViewById(R.id.checkbox_major_chords));
        dominantBox = ((CheckBox) view.findViewById(R.id.checkbox_dominant_chords));
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
    public interface OnChordTypeChangeListener
    {
        /**
         * Called when the chord type changes.
         * @param useMajors Whether or not major chords are now being used
         * @param useMinors  Whether or not minor chords are now being used
         * @param useDominants  Whether or not dominant chords are now being used
         */
        void onChordTypeChanged(boolean useMajors, boolean useMinors, boolean useDominants);
    }
}

package com.five_chords.chord_builder;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * A Fragment containing the chord check options.
 * @author tstone95
 */
public class CheckOptionsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener
{
//    /** The Bundle id of the minor chords checkbox value */
//    private static final String CHECKBOX_MINOR_VALUE_ID = "CheckOptionsFragment.minor";
//
//    /** The Bundle id of the major chords checkbox value */
//    private static final String CHECKBOX_MAJOR_VALUE_ID = "CheckOptionsFragment.major";
//
//    /** The Bundle id of the dominant chords checkbox value */
//    private static final String CHECKBOX_DOMINANT_VALUE_ID = "CheckOptionsFragment.dominant";

    /** The minor chord checkbox */
    private CheckBox minorBox;
    /** The major chord checkbox */
    private CheckBox majorBox;
    /** The dominant chord checkbox */
    private CheckBox dominantBox;

    /**
     * Required empty public constructor.
     */
    public CheckOptionsFragment()
    {   }

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
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
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
}

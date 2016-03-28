package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * A Fragment containing the Chord inversion settings
 * @author tstone95
 */
public class ChordInversionSettingsFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener
{
    /** The CheckBoxes for toggling chord inversions. */
    private CheckBox[] inversionCheckBoxes;

    /**
     * Required empty public constructor.
     */
    public ChordInversionSettingsFragment()
    {   }

    /**
     * Create a new instance of HintSettingsFragment.
     * @return A new instance of HintSettingsFragment
     */
    public static ChordInversionSettingsFragment newInstance()
    {
        ChordInversionSettingsFragment f = new ChordInversionSettingsFragment();
        return f;
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
        View view = inflater.inflate(R.layout.fragment_chord_inversion_settings, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.chord_inversion_settings);

        // Init CheckBoxes
        inversionCheckBoxes = new CheckBox[4];

        //Assign CheckBoxes
        inversionCheckBoxes[0] = (CheckBox)view.findViewById(R.id.checkbox_first_inversion);
        inversionCheckBoxes[1] = (CheckBox)view.findViewById(R.id.checkbox_second_inversion);
        inversionCheckBoxes[2] = (CheckBox)view.findViewById(R.id.checkbox_third_inversion);
        inversionCheckBoxes[3] = (CheckBox)view.findViewById(R.id.checkbox_fourth_inversion);

        // Add listeners
        byte i = 0;
        Options options = MainActivity.getOptions();
        for (CheckBox checkBox: inversionCheckBoxes)
        {
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setChecked(options.chordInversionsToUse.contains(i++));
        }

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
        Options options = MainActivity.getOptions();

        byte i = 0;
        for (CheckBox checkBox: inversionCheckBoxes)
        {
            if (checkBox == buttonView)
            {
                if (isChecked)
                    options.addChordInversion(i);
                else
                    options.removeChordInversion(i);
            }
            ++i;
        }
    }
}

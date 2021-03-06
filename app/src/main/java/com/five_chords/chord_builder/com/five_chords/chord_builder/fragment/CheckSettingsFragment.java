package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * Fragment containing the interface for changing the check settings. This Activity contains a switch to toggle
 * the check sequence and a switch to toggle showing hints, as well as the number pickers for selecting
 * the delays between hint types.
 * @author tstone95
 */
public class CheckSettingsFragment extends SettingsPageFragment.SettingsSubFragment
        implements CompoundButton.OnCheckedChangeListener, NumberPicker.OnValueChangeListener
{
    /** The available hint delay settings. */
    private static final String[] PICKER_DISPLAY_VALUES = new String[25];

    /** Initialize the hint delay settings. */
    static
    {
        for (int i = 0; i < PICKER_DISPLAY_VALUES.length; ++i)
            PICKER_DISPLAY_VALUES[i] = "" + i;
    }

    /** The show answer sequence switch */
    private Switch showAnswerSeqSwitch;

    /** The hints switch. */
    private Switch hintsSwitch;

    /** The hint percent errors switch. */
    private Switch hintPercentErrorSwitch;

    /** The NumberPicker for type 1 hint delays. */
    private NumberPicker hintDelay1Picker;

    /** The NumberPicker for type 2 hint delays. */
    private NumberPicker hintDelay2Picker;

    /** The NumberPicker for type 3 hint delays. */
    private NumberPicker hintDelay3Picker;

    /**
     * Gets the delay for type 1 hints
     * @return The delay for type 1 hints
     */
    public int getHintDelay1()
    {
        return hintDelay1Picker.getValue();
    }

    /**
     * Gets the delay for type 2 hints
     * @return The delay for type 2 hints
     */
    public int getHintDelay2()
    {
        return hintDelay2Picker.getValue();
    }

    /**
     * Gets the delay for type 3 hints
     * @return The delay for type 3 hints
     */
    public int getHintDelay3()
    {
        return hintDelay3Picker.getValue();
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
        // Use Hints were changed
        if (buttonView.getId() == R.id.switch_hints || buttonView.getId() == R.id.switch_hints_percent_errors)
        {
            MainActivity.getOptions().changeHints(hintsSwitch.isChecked(), hintPercentErrorSwitch.isChecked(),
                    getHintDelay1(), getHintDelay2(), getHintDelay3());

            hintPercentErrorSwitch.setEnabled(hintsSwitch.isChecked());
            hintDelay1Picker.setEnabled(hintsSwitch.isChecked());
            hintDelay2Picker.setEnabled(hintsSwitch.isChecked());
            hintDelay3Picker.setEnabled(hintsSwitch.isChecked());
        }
        // Show answer sequence was changed
        else if (buttonView.getId() == R.id.switch_check_sequence)
        {
            MainActivity.getOptions().setShowAnswerSequence(isChecked);
        }
    }

    /**
     * Called upon a change of the current value.
     *
     * @param picker The NumberPicker associated with this listener.
     * @param oldVal The previous value.
     * @param newVal The new value.
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
    {
        if (picker == hintDelay1Picker)
        {
            if (oldVal >= hintDelay2Picker.getValue())
                oldVal = hintDelay2Picker.getValue() - 1;

            if (newVal >= hintDelay2Picker.getValue())
            {
                hintDelay1Picker.setValue(oldVal);
                return;
            }
        }
        else if (picker == hintDelay2Picker)
        {
            if (oldVal >= hintDelay3Picker.getValue())
                oldVal = hintDelay3Picker.getValue() - 1;
            else if (oldVal <= hintDelay1Picker.getValue())
                oldVal = hintDelay1Picker.getValue() + 1;

            if (newVal >= hintDelay3Picker.getValue() || newVal <= hintDelay1Picker.getValue())
            {
                hintDelay2Picker.setValue(oldVal);
                return;
            }
        }
        else
        {
            if (oldVal <= hintDelay2Picker.getValue())
                oldVal = hintDelay2Picker.getValue() + 1;

            if (newVal <= hintDelay2Picker.getValue())
            {
                hintDelay3Picker.setValue(oldVal);
                return;
            }
        }

        MainActivity.getOptions().changeHints(hintsSwitch.isChecked(), hintPercentErrorSwitch.isChecked(),
                getHintDelay1(), getHintDelay2(), getHintDelay3());
    }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_settings, container, false);

        // Get Views
        showAnswerSeqSwitch = (Switch) view.findViewById(R.id.switch_check_sequence);
        hintsSwitch = (Switch) view.findViewById(R.id.switch_hints);
        hintPercentErrorSwitch = (Switch) view.findViewById(R.id.switch_hints_percent_errors);

        hintDelay1Picker = (NumberPicker) view.findViewById(R.id.hint_level1_picker);
        initPicker(hintDelay1Picker);
        hintDelay2Picker = (NumberPicker) view.findViewById(R.id.hint_level2_picker);
        initPicker(hintDelay2Picker);
        hintDelay3Picker = (NumberPicker) view.findViewById(R.id.hint_level3_picker);
        initPicker(hintDelay3Picker);

        // Set default values
        Options options = MainActivity.getOptions();
        showAnswerSeqSwitch.setChecked(options.showAnswerSequence);
        hintsSwitch.setChecked(options.useHints);
        hintPercentErrorSwitch.setChecked(options.useHintPercentErrors);
        hintDelay1Picker.setValue(options.hintTypeDelays[0]);
        hintDelay2Picker.setValue(options.hintTypeDelays[1]);
        hintDelay3Picker.setValue(options.hintTypeDelays[2]);

        // Add listeners
        showAnswerSeqSwitch.setOnCheckedChangeListener(this);
        hintsSwitch.setOnCheckedChangeListener(this);
        hintPercentErrorSwitch.setOnCheckedChangeListener(this);

        // Set Done Button action
        Button doneButton = (Button)view.findViewById(R.id.button_settings_check_settings_done);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (CheckSettingsFragment.this.getSettingsPageFragment() != null)
                    CheckSettingsFragment.this.getSettingsPageFragment().
                            setSettingsSubFragment(SettingsPageFragment.SettingsSubFragmentDef.MAIN.ordinal());
            }
        });

        return view;
    }

    /**
     * Helper method to initialize one of the NumberPickers in this Fragment.
     * @param picker The NumberPicker to initialize
     */
    private void initPicker(NumberPicker picker)
    {
        picker.setMinValue(0);
        picker.setMaxValue(PICKER_DISPLAY_VALUES.length - 1);
        picker.setDisplayedValues(PICKER_DISPLAY_VALUES);
        picker.setOnValueChangedListener(this);
    }

//    /**
//     * Called to return to the MainActivity.
//     * @param view The calling View
//     */
//    public void backToMain(View view)
//    {
//        finish();
//        this.overridePendingTransition(0, 0);
//    }
}

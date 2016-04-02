package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Options;

/**
 * Activity containing the interface for changing the check settings. This Activity contains a switch to toggle
 * the check sequence and a switch to toggle showing hints, as well as the number pickers for selecting
 * the delays between hint types.
 * @author tstone95
 */
public class CheckSettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener,
        NumberPicker.OnValueChangeListener
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
        if (buttonView.getId() == R.id.switch_hints)
        {
            MainActivity.getOptions().changeHints(isChecked, getHintDelay1(), getHintDelay2(), getHintDelay3());

            hintDelay1Picker.setEnabled(isChecked);
            hintDelay2Picker.setEnabled(isChecked);
            hintDelay3Picker.setEnabled(isChecked);
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
            if (newVal >= hintDelay2Picker.getValue())
            {
                hintDelay1Picker.setValue(oldVal);
                return;
            }
        }
        else if (picker == hintDelay2Picker)
        {
            if (newVal >= hintDelay3Picker.getValue() || newVal <= hintDelay1Picker.getValue())
            {
                hintDelay2Picker.setValue(oldVal);
                return;
            }
        }
        else
        {
            if (newVal <= hintDelay2Picker.getValue())
            {
                hintDelay3Picker.setValue(oldVal);
                return;
            }
        }

        MainActivity.getOptions().changeHints(hintsSwitch.isChecked(), getHintDelay1(), getHintDelay2(), getHintDelay3());
    }

    /**
     * Called when the Activity has been created.
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_check_settings);

        // Get Views
        showAnswerSeqSwitch = (Switch) findViewById(R.id.switch_check_sequence);
        hintsSwitch = (Switch) findViewById(R.id.switch_hints);

        hintDelay1Picker = (NumberPicker) findViewById(R.id.hint_level1_picker);
        initPicker(hintDelay1Picker);
        hintDelay2Picker = (NumberPicker) findViewById(R.id.hint_level2_picker);
        initPicker(hintDelay2Picker);
        hintDelay3Picker = (NumberPicker) findViewById(R.id.hint_level3_picker);
        initPicker(hintDelay3Picker);

        // Set default values
        Options options = MainActivity.getOptions();
        showAnswerSeqSwitch.setChecked(options.showAnswerSequence);
        hintsSwitch.setChecked(options.useHints);
        hintDelay1Picker.setValue(options.hintTypeDelays[0]);
        hintDelay2Picker.setValue(options.hintTypeDelays[1]);
        hintDelay3Picker.setValue(options.hintTypeDelays[2]);

        // Add listeners
        showAnswerSeqSwitch.setOnCheckedChangeListener(this);
        hintsSwitch.setOnCheckedChangeListener(this);
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

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        finish();
    }
}

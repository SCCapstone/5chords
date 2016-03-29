package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Options;

/**
 * A Fragment containing the hint settings.
 * @author tstone95
 */
public class HintSettingsFragment extends Activity implements CompoundButton.OnCheckedChangeListener,
        NumberPicker.OnValueChangeListener
{
    /** The available hint delay settings. */
    private static final String[] PICKER_DISPLAY_VALUES = new String[25];

    static
    {
        for (int i = 0; i < PICKER_DISPLAY_VALUES.length; ++i)
            PICKER_DISPLAY_VALUES[i] = "" + i;
    }

    /** The hints switch */
    private Switch hintsSwitch;
    /** The NumberPicker for type 1 hint delays. */
    private NumberPicker hintDelay1Picker;
    /** The NumberPicker for type 2 hint delays. */
    private NumberPicker hintDelay2Picker;
    /** The NumberPicker for type 3 hint delays. */
    private NumberPicker hintDelay3Picker;

    /**
     * Required empty public constructor.
     */
    public HintSettingsFragment()
    {   }

    /**
     * Create a new instance of HintSettingsFragment.
     * @return A new instance of HintSettingsFragment
     */
    public static HintSettingsFragment newInstance()
    {
        HintSettingsFragment f = new HintSettingsFragment();

        return f;
    }

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
        if (buttonView instanceof Switch)
        {
            MainActivity.getOptions().changeHints(isChecked, getHintDelay1(), getHintDelay2(), getHintDelay3());

            hintDelay1Picker.setEnabled(isChecked);
            hintDelay2Picker.setEnabled(isChecked);
            hintDelay3Picker.setEnabled(isChecked);
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
     * @return This Fragment's layout
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_hint_settings);

        // Get Views
        hintsSwitch = (Switch) findViewById(R.id.switch_hints);
        hintDelay1Picker = (NumberPicker) findViewById(R.id.hint_level1_picker);
        initPicker(hintDelay1Picker);
        hintDelay2Picker = (NumberPicker) findViewById(R.id.hint_level2_picker);
        initPicker(hintDelay2Picker);
        hintDelay3Picker = (NumberPicker) findViewById(R.id.hint_level3_picker);
        initPicker(hintDelay3Picker);

        // Set default values
        Options options = MainActivity.getOptions();
        hintsSwitch.setChecked(options.useHints);
        hintDelay1Picker.setValue(options.hintTypeDelays[0]);
        hintDelay2Picker.setValue(options.hintTypeDelays[1]);
        hintDelay3Picker.setValue(options.hintTypeDelays[2]);
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
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * The MainActivity call
     */
    public void backToMain(View view)
    {
        finish();
    }

}

package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;

import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * A Fragment containing the pitch bend settings.
 * @author tstone95
 */
public class PitchBendSettingsFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener
{
    /** The maximum allowed distance to a Note as a fraction to consider checking correct. */
    private static double MAXIMUM_CHECK_ERROR = 0.5;

    /** The minimum allowed distance to a Note as a fraction to consider checking correct. */
    private static double MINIMUM_CHECK_ERROR = 0.1;

    /** The maximum number of divisions per note. */
    private static final int MAXIMUM_DIVISIONS_PER_NOTE = 100;

    /** The maximum difficulty value. */
    private static final int MAXIMUM_DIFFICULTY_VALUE = 100;

    /** The note division setting bar. */
    private SeekBar divisionSetBar;

    /** The difficulty setting bar. */
    private SeekBar difficultySetBar;

    /**
     * Required empty public constructor.
     */
    public PitchBendSettingsFragment()
    {   }

    /**
     * Create a new instance of HintSettingsFragment.
     * @return A new instance of HintSettingsFragment
     */
    public static PitchBendSettingsFragment newInstance()
    {
        PitchBendSettingsFragment f = new PitchBendSettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_pitch_bend_settings, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.pitch_bend_settings);

        // Init Increment SeekBar
        divisionSetBar = (SeekBar)view.findViewById(R.id.seekbar_note_divisions);
        divisionSetBar.setMax(MAXIMUM_DIVISIONS_PER_NOTE - 1);
        divisionSetBar.setProgress(MainActivity.getOptions().sliderDivisionsPerNote + 1);
        divisionSetBar.setOnSeekBarChangeListener(this);

        // Init Difficulty SeekBar
        difficultySetBar = (SeekBar)view.findViewById(R.id.seekbar_check_difficulties);
        difficultySetBar.setMax(MAXIMUM_DIFFICULTY_VALUE);

        // Set default progress, calculating backwards from the error margin value
        double v = MainActivity.getOptions().allowableCheckError - MINIMUM_CHECK_ERROR;
        v /= (MAXIMUM_CHECK_ERROR - MINIMUM_CHECK_ERROR);
        v = 1.0 - v;
        difficultySetBar.setProgress((int)(MAXIMUM_DIFFICULTY_VALUE * v));
        difficultySetBar.setOnSeekBarChangeListener(this);

        // Return the View
        return view;
    }

    /**
     * Notification that the progress level has changed. Clients can use the fromUser parameter
     * to distinguish user-initiated changes from those that occurred programmatically.
     *
     * @param seekBar  The SeekBar whose progress has changed
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        // Get number of divisions between notes
        int divisions = 1 + divisionSetBar.getProgress();

        // Get the allowable error margin (will be in range MINIMUM_CHECK_ERROR to MAXIMUM_CHECK_ERROR)
        double checkError = 1.0 - difficultySetBar.getProgress() / (double)MAXIMUM_DIFFICULTY_VALUE;
        checkError = MINIMUM_CHECK_ERROR + checkError * (MAXIMUM_CHECK_ERROR - MINIMUM_CHECK_ERROR);

        // Update options
        MainActivity.getOptions().changePitchBendSettings(divisions, checkError);
    }

    /**
     * Notification that the user has started a touch gesture. Clients may want to use this
     * to disable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    /**
     * Notification that the user has finished a touch gesture. Clients may want to use this
     * to re-enable advancing the seekbar.
     *
     * @param seekBar The SeekBar in which the touch gesture began
     */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }
}

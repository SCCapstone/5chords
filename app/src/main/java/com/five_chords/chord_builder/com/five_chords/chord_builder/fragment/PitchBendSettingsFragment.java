package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
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
        int divisions = 1 + divisionSetBar.getProgress();
        double checkError = 0.01 + (MAXIMUM_DIFFICULTY_VALUE - difficultySetBar.getProgress() / (double)difficultySetBar.getProgress()) * 0.49; // TODO Make constants

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

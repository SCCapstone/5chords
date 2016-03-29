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
import android.widget.TextView;

import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import org.w3c.dom.Text;

/**
 * A Fragment containing the pitch bend settings.
 * @author tstone95
 */
public class PitchBendSettingsFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener
{
    /** The maximum allowed distance to a Note as a fraction to consider checking correct. */
    public static double MAXIMUM_CHECK_ERROR = 0.5;

    /** The minimum allowed distance to a Note as a fraction to consider checking correct. */
    public static double MINIMUM_CHECK_ERROR = 0.0;

    /** The maximum difficulty value. */
    private static final int MAXIMUM_DIFFICULTY_VALUE = 100;

    /** The allowable divisions between notes. */
    private static final int[] ALLOWABLE_DIVISIONS_BETWEEN_NOTES = new int[] {1, 2, 4, 8, 16, 32, 64, 128};

    /** The note division setting bar. */
    private SeekBar divisionSetBar;

    /** The difficulty setting bar. */
    private SeekBar difficultySetBar;

    /** The label for the divisionSetBar. */
    private TextView divisionBarLabel;

    /** The label for the difficultySetBar. */
    private TextView difficultyBarLabel;

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
        divisionSetBar.setMax(ALLOWABLE_DIVISIONS_BETWEEN_NOTES.length - 1);
        divisionSetBar.setOnSeekBarChangeListener(this);

        // Init Difficulty SeekBar
        difficultySetBar = (SeekBar)view.findViewById(R.id.seekbar_check_error);
        difficultySetBar.setMax(MAXIMUM_DIFFICULTY_VALUE);
        difficultySetBar.setOnSeekBarChangeListener(this);

        // Get references to labels
        divisionBarLabel = (TextView)view.findViewById(R.id.textview_pitch_slider_label);
        difficultyBarLabel = (TextView)view.findViewById(R.id.textview_pitch_check_error_label);

        // Set default progress for division bar
        int p = -1;
        for (int i = 0; p == -1 && i < ALLOWABLE_DIVISIONS_BETWEEN_NOTES.length; ++i)
        {
            if (MainActivity.getOptions().sliderDivisionsPerNote == ALLOWABLE_DIVISIONS_BETWEEN_NOTES[i])
                p = i;
        }
        if (p == -1)
            p = 0;

        divisionSetBar.setProgress(p);

        // Set default progress for difficulty bar
        double v = MainActivity.getOptions().allowableCheckError - MINIMUM_CHECK_ERROR;
        v /= (MAXIMUM_CHECK_ERROR - MINIMUM_CHECK_ERROR);
        difficultySetBar.setProgress((int) (MAXIMUM_DIFFICULTY_VALUE * v));

        // Set default values
        setBarLabels();

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
        if (!fromUser)
            return;

        // Get number of divisions between notes
        int divisions = ALLOWABLE_DIVISIONS_BETWEEN_NOTES[divisionSetBar.getProgress()];

        // Get the allowable error margin (will be in range MINIMUM_CHECK_ERROR to MAXIMUM_CHECK_ERROR)
        double checkError = difficultySetBar.getProgress() / (double)MAXIMUM_DIFFICULTY_VALUE;
        checkError = MINIMUM_CHECK_ERROR + checkError * (MAXIMUM_CHECK_ERROR - MINIMUM_CHECK_ERROR);

        // Update labels
        setBarLabels();

        // Update options
        MainActivity.getOptions().changePitchBendSettings(divisions, checkError);
    }

    /**
     * Sets the labels of the SeekBars in the Fragment.
     */
    public void setBarLabels()
    {
        divisionBarLabel.setText("" + ALLOWABLE_DIVISIONS_BETWEEN_NOTES[divisionSetBar.getProgress()]);
        difficultyBarLabel.setText("" + Math.round(MainActivity.getOptions().allowableCheckError * 100.0) + " %");
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

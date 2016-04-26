package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.VerticalSeekBar;
import com.five_chords.chord_builder.SoundHandler;

/**
 * A Fragment containing the chord select sliders.
 */
public class SliderFragment extends Fragment
{
    /** Bundle Id for the slider positions. */
    private static final String SLIDER_POSITION_BUNDLE_ID = "SliderFragment.sliderPos";

    /** The thumb offset of the sliders. */
    public static final int THUMB_OFFSET = 0;

    /** The slider for the root note */
    private VerticalSeekBar rootSlider;

    /** The slider for the third note */
    private VerticalSeekBar thirdSlider;

    /** The slider for the fifth note */
    private VerticalSeekBar fifthSlider;

    /** The slider for the option note */
    private VerticalSeekBar optionSlider;

    /** Don't use onTouch methods when blocking */
    private boolean isBlocked;

    /** The index of the notes at the bottom of the sliders. */
    private int minNoteOnSlider;

    /** The index of the notes at the top of the sliders. */
    private int maxNoteOnSlider;

    /** Thumb drawables for when sliders are not pressed. */
    private Drawable[] thumb;

    /** Thumb drawables for when sliders are pressed. */
    private Drawable[] thumbTouched;

    /** SoundHandlers for each slider. */
    private SoundHandler[] soundHandlers;

    /** If the thumb is touched we can move the slider **/
    private boolean isMoving[];

    /**
     * Required empty public constructor.
     */
    public SliderFragment()
    {   }

    /**
     * Gets the current chord built on the note sliders and puts it in the givn array.
     * @param chord The array into which to put the built chord
     */
    public void buildCurrentChord(Note[] chord)
    {
        // Set slider bounds to fit chord
        setSliderBoundsToFitChord(ChordHandler.getCurrentSelectedChordSpelling());

        // Assign values
        if (rootSlider != null)
            getNoteFromSlider(rootSlider, chord[0]);

        if (thirdSlider != null)
            getNoteFromSlider(thirdSlider, chord[1]);

        if (fifthSlider != null)
            getNoteFromSlider(fifthSlider, chord[2]);

        if (optionSlider != null)
            getNoteFromSlider(optionSlider, chord[3]);
    }

    /**
     * Called to reset the positions of the chord sliders.
     */
    public void resetChordSliders()
    {
        isBlocked = true;
        rootSlider.setProgress(0);
        thirdSlider.setProgress(0);
        fifthSlider.setProgress(0);
        optionSlider.setProgress(0);
        isBlocked = false;

        // Save Slider positions
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 0, rootSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 1, thirdSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 2, fifthSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 3, optionSlider.getProgress());
        }
    }

    /**
     * Stop sound from all sliders
     */
    public void silenceSliders()
    {
        if (soundHandlers != null)
            for (SoundHandler sH : soundHandlers)
                sH.stopSound();
    }

    /**
     * Called to create the View of this SliderFragment.
     * @param inflater The LayoutInflater to use to inflate the View
     * @param container The ViewGroup containing the View
     * @param savedInstanceState The Bundle containing the saved instance state
     * @return The View of this Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Set blocked
        isBlocked = true;

        // Inflate the layout for this fragment
        View sliders = inflater.inflate(R.layout.fragment_sliders, container, false);

        // Every slider needs it's own thumb image
        thumb = new Drawable[4];//getResources().getDrawable(R.drawable.thumb_icon);
        thumbTouched = new Drawable[4];//getResources().getDrawable(R.drawable.thumb_icon_pressed);
        soundHandlers = new SoundHandler[4];
        isMoving = new boolean[4];

        for (int i = 0; i < thumb.length; ++i)
        {
            thumb[i] = getResources().getDrawable(R.drawable.thumb_icon);
            thumbTouched[i] = getResources().getDrawable(R.drawable.thumb_icon_pressed);
        }

        // Assign sliders
        rootSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_root);
        rootSlider.setThumbOffset(THUMB_OFFSET);
        soundHandlers[0] = new SoundHandler(getActivity(), "slider0");
        isMoving[0] = false;

        thirdSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_third);
        thirdSlider.setThumbOffset(THUMB_OFFSET);
        soundHandlers[1] = new SoundHandler(getActivity(), "slider1");
        isMoving[1] = false;

        fifthSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_fifth);
        fifthSlider.setThumbOffset(THUMB_OFFSET);
        soundHandlers[2] = new SoundHandler(getActivity(), "slider2");
        isMoving[2] = false;

        optionSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_option);
        optionSlider.setThumbOffset(THUMB_OFFSET);
        soundHandlers[3] = new SoundHandler(getActivity(), "slider3");
        isMoving[3] = false;

        // Add the seek bar listeners
        Activity activity = getActivity();
        addSeekBarListener(activity, rootSlider, 0);
        addSeekBarListener(activity, thirdSlider, 1);
        addSeekBarListener(activity, fifthSlider, 2);
        addSeekBarListener(activity, optionSlider, 3);

        // Hide fourth slider by default
        sliders.findViewById(R.id.slider_option_layout).setVisibility(View.GONE);

        // Initialize Slider sizes
        setSliderBoundsToFitChord(ChordHandler.getCurrentSelectedChordSpelling());

        // Load Slider positions
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            rootSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 0));
            thirdSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 1));
            fifthSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 2));
            optionSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 3));
        }

        return sliders;
    }

    /**
     * Called when the view of this Fragment is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        // Save Slider positions
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 0, rootSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 1, thirdSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 2, fifthSlider.getProgress());
            arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 3, optionSlider.getProgress());
        }

        // Remove the seek bar listeners
        removeSeekBarListener(rootSlider);
        removeSeekBarListener(thirdSlider);
        removeSeekBarListener(fifthSlider);
        removeSeekBarListener(optionSlider);

        rootSlider = null;
        thirdSlider = null;
        fifthSlider = null;
        optionSlider = null;

        // Stop Sound
        silenceSliders();

        // Delete arrays
        for (int i = 0; i < 4; ++i)
        {
            thumb[i] = null;
            thumbTouched[i] = null;
            soundHandlers[i] = null;
        }

        thumb = null;
        thumbTouched = null;
        soundHandlers = null;

        // Call super method
        super.onDestroyView();
    }

    /**
     * Called when the view state of this Fragment is restored.
     * @param savedInstanceState The Bundle from which to restore the view state
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);

        // Load Slider positions
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            rootSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 0));
            thirdSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 1));
            fifthSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 2));
            optionSlider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + 3));
        }

        // Unblock sounds
        isBlocked = false;
    }

    /**
     * Called when the instance state of this Fragment should be saved.
     * @param bundle The Bundle in which to save the instance state
     */
    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        // Save Slider positions
        Bundle arguments = getArguments();
        if (arguments != null)
        {
            if (rootSlider != null)
                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 0, rootSlider.getProgress());

            if (thirdSlider != null)
                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 1, thirdSlider.getProgress());

            if (fifthSlider != null)
                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 2, fifthSlider.getProgress());

            if (optionSlider != null)
                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 3, optionSlider.getProgress());
        }
    }

    /**
     * Gets what the Slider progress would be if it were on the given note.
     * @param note The note
     * @return What the Slider progress would be if it were on the given note
     */
    public int getSliderProgressForNote(Note note)
    {
        int index = note.index;
        index -= minNoteOnSlider;
        index *= MainActivity.getOptions().sliderDivisionsPerNote;
        return index;
    }

    /**
     * Sets the bounds of the Sliders to fit the given chord.
     * @param chord The chord to fit
     */
    public void setSliderBoundsToFitChord(Note[] chord)
    {
        // Find max and min values
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (Note i: chord)
        {
            if (i.index > max)
                max = i.index;
            if (i.index < min)
                    min = i.index;
        }

        // Set values, rounding to nearest multiple of Chord.NUM_NOTES
        minNoteOnSlider = (min / Note.NUM_NOTES) * Note.NUM_NOTES;
        maxNoteOnSlider = (max / Note.NUM_NOTES + 1) * Note.NUM_NOTES;

        // Calculate max
        max = (maxNoteOnSlider - minNoteOnSlider - 1) * MainActivity.getOptions().sliderDivisionsPerNote;

        // Set values
        boolean wasBlocked = isBlocked;
        isBlocked = true;

        if (rootSlider != null)
            rootSlider.setMax(max);

        if (thirdSlider != null)
            thirdSlider.setMax(max);

        if (fifthSlider != null)
            fifthSlider.setMax(max);

        if (optionSlider != null)
            optionSlider.setMax(max);

        isBlocked = wasBlocked;
    }

//    public void setIsBlocked(boolean blocked)
//    {
//        isBlocked = blocked;
//    }

    /**
     * Gets a String representation of this SliderFragment.
     * @return A String representation of this SliderFragment
     */
    public String toString()
    {
        // Get the slider positions
        Note[] chord = new Note[] {new Note(), new Note(), new Note(), new Note()};
        buildCurrentChord(chord);

        String value = "";
        for (int i = 0; i < chord.length; ++i)
        {
            value += chord[i].getFractionalIndex();
            if (i != chord.length - 1)
                value += "\t";
        }

        return "SliderFragment: Min Note = " + minNoteOnSlider + ", Max Note = " + maxNoteOnSlider +
                "\n\tSlider Notes = " + value;
    }

    /**
     * Gets the current note on the slider, taking into account the number of intermediate slider
     * positions.
     * @param slider The slider to test
     * @param note A Note Object in which to put the value
     */
    private void getNoteFromSlider(VerticalSeekBar slider, Note note)
    {
        SliderNotePosition position = new SliderNotePosition(slider);

        int nearestNote;
        double fracDistToNearestNote;

        if (position.fracDistAboveNote > 0.5)
        {
            nearestNote = position.note + 1;
            fracDistToNearestNote = position.fracDistAboveNote - 1.0;
        }
        else
        {
            nearestNote = position.note;
            fracDistToNearestNote = position.fracDistAboveNote;
        }

        note.index = nearestNote;
        note.distanceToIndex = fracDistToNearestNote;
    }

    /**
     * Called to remove the seek bar listener from a single seek bar which were added with addSeekBarListener().
     * @param bar The VerticalSeekBar to remove listeners from
     */
    private void removeSeekBarListener(VerticalSeekBar bar)
    {
        bar.setOnClickListener(null);
        bar.setOnSeekBarChangeListener(null);
        bar.setOnTouchListener(null);
    }

    /**
     * Called to add the seek bar listener to a single seek bar.
     * @param bar The VerticalSeekBar to add listeners to
     */
    private void addSeekBarListener(final Activity activity, final VerticalSeekBar bar, final int slider)
    {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (isBlocked)
                    return;

                // Only play note if progress change is from user
                if (seekBar instanceof VerticalSeekBar)
                {
                    Note note = new Note();
                    getNoteFromSlider(bar, note);
                    soundHandlers[slider].playNote(activity, note);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {   }

            public void onStopTrackingTouch(SeekBar seekBar)
            {   }
        });

        bar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (isBlocked)
                    return true;

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Note note = new Note();
                    getNoteFromSlider(bar, note);
                    soundHandlers[slider].playNote(activity, note);
                    bar.setThumb(thumbTouched[slider]);
                    bar.setThumbOffset(THUMB_OFFSET);
                                    }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    isMoving[slider] = false;
                    soundHandlers[slider].stopSound();
                    bar.setThumb(thumb[slider]);
                    bar.setThumbOffset(THUMB_OFFSET);
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    if (isMoving[slider])
                        return false;

//                    int verticalPadding = bar.getBaseline() + bar.getPaddingBottom() + bar.getWidth();
//                    int tempProgress = (bar.getMax() - (int) (bar.getMax() * (event.getY() - verticalPadding) / (bar.getHeight() - verticalPadding * 2)));
//
////                    int tempProgress = (bar.getMax() - (int) (bar.getMax() * event.getY() / bar.getHeight()));
//                    int diff = Math.abs(tempProgress - bar.getProgress());
//
//                    if (diff < MainActivity.getOptions().sliderDivisionsPerNote + 1)
//                    {
//                        isMoving[slider] = true;
//                        return false;
//                    }
//                    else
//                    {
//                        isMoving[slider] = false;
//                        return true;
//                    }
                }

                return false;
            }
        });
    }

    /**
     * Wrapper class representing the position of a slider as a fractional distance above a note.
     */
    public class SliderNotePosition
    {
        /** The note position of the slider. */
        public int note;
        /** The fractional distance above note position of the slider. */
        public double fracDistAboveNote;

        /**
         * Constructs a new SliderNotePosition for the given SeekBar.
         * @param slider The Seekbar whose position to get
         */
        public SliderNotePosition(SeekBar slider)
        {
            calculate(slider);
        }

        /**
         * Calculates the values of this SliderNotePosition for the given slider.
         * @param slider The slider whose note position to get
         */
        public void calculate(SeekBar slider)
        {
            Options options = MainActivity.getOptions();
            // Scale the value to the right range
            note = slider.getProgress() / options.sliderDivisionsPerNote;
            // Calculate the fractional position of the note
            fracDistAboveNote = slider.getProgress() / ((double)options.sliderDivisionsPerNote) - note;
            // Shift the value to the right range
            note += minNoteOnSlider;
        }
    }
}

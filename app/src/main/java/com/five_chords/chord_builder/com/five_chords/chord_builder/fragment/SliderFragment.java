package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.VerticalSeekBar;
import com.five_chords.chord_builder.soundHandler;

/**
 * A Fragment containing the chord select sliders.
 */
public class SliderFragment extends Fragment
{
    /** The slider for the root note */
    private VerticalSeekBar rootSlider;

    /** The slider for the third note */
    private VerticalSeekBar thirdSlider;

    /** The slider for the fifth note */
    private VerticalSeekBar fifthSlider;

    /** The slider for the option note */
    private VerticalSeekBar optionSlider;

    /** Don't use onTouch methods when blocking */
    private boolean isBlocked = false;

    /** The index of the notes at the bottom of the sliders. */
    private int minNoteOnSlider;

    /** The index of the notes at the top of the sliders. */
    private int maxNoteOnSlider;

    /** Every slider gets it's own thumb image and soundHandler **/
    private static Drawable[] thumb;
    private static Drawable[] thumbTouched;
    private static soundHandler[] soundHandlers;

    /** If the thumb is touched we can move the slider **/
    private static boolean isMoving[];

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
        setSliderBoundsToFitChord(chordHandler.getCurrentSelectedChordSpelling());

        // Assign values
        getNoteFromSlider(rootSlider, chord[0]);
        getNoteFromSlider(thirdSlider, chord[1]);
        getNoteFromSlider(fifthSlider, chord[2]);
        getNoteFromSlider(optionSlider, chord[3]);
    }

//    // TODO
//    public void test()
//    {
//        Note[] chord = new Note[] {new Note(), new Note(), new Note(), new Note()};
//        buildCurrentChord(chord);
//
//        Log.e("SliderFragment", "Test");
//        Log.e("->", "Min Note on Sliders = " + minNoteOnSlider);
//        Log.e("->", "Max Note on Sliders = " + maxNoteOnSlider);
//
//        String value = "";
//        for (int i = 0; i < chord.length; ++i)
//        {
//            value += chord[i].getFractionalIndex();
//            if (i != chord.length - 1)
//                value += "\t";
//        }
//
//        Log.e("->", "Slider Values = " + value);
//
//        value = "";
//        chord = chordHandler.getCurrentSelectedChordSpelling();
//        for (int i = 0; i < chord.length; ++i)
//        {
//            value += chord[i].getFractionalIndex();
//            if (i != chord.length - 1)
//                value += "\t";
//        }
//
//        Log.e("->", "Selected Chord Notes = " + value);
//    }

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
    }

    /**
     * Stop sound from all sliders
     */
    public void silenceSliders() {
        for (soundHandler sH : soundHandlers)
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
        // Inflate the layout for this fragment
        View sliders = inflater.inflate(R.layout.fragment_sliders, container, false);

        // Every slider needs it's own thumb image
        thumb = new Drawable[4];
        thumbTouched = new Drawable[4];
        soundHandlers = new soundHandler[4];
        isMoving = new boolean[4];

        for (int i = 0; i < 4; i++) {
            thumb[i] = getResources().getDrawable(R.drawable.eq_slide_knob);
            thumb[i].setBounds(new Rect(0, 0, thumb[i].getIntrinsicWidth(), thumb[i].getIntrinsicHeight()));

            thumbTouched[i] = getResources().getDrawable(R.drawable.eq_slide_knob_touched);
            thumbTouched[i].setBounds(new Rect(0, 0, thumbTouched[i].getIntrinsicWidth(), thumbTouched[i].getIntrinsicHeight()));
        }

        // Assign sliders
        rootSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_root);
        rootSlider.initialize();
        rootSlider.setThumb(thumb[0]);
        soundHandlers[0] = new soundHandler(getActivity(), "slider0");
        isMoving[0] = false;

        thirdSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_third);
        thirdSlider.initialize();
        thirdSlider.setThumb(thumb[1]);
        soundHandlers[1] = new soundHandler(getActivity(), "slider1");
        isMoving[1] = false;

        fifthSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_fifth);
        fifthSlider.initialize();
        fifthSlider.setThumb(thumb[2]);
        soundHandlers[2] = new soundHandler(getActivity(), "slider2");
        isMoving[2] = false;

        optionSlider = (VerticalSeekBar) sliders.findViewById(R.id.slider_option);
        optionSlider.initialize();
        optionSlider.setThumb(thumb[3]);
        soundHandlers[3] = new soundHandler(getActivity(), "slider3");
        isMoving[3] = false;


        // Add the seek bar listeners
        Activity activity = getActivity();
        addSeekBarListener(activity, rootSlider, 0);
        addSeekBarListener(activity, thirdSlider, 1);
        addSeekBarListener(activity, fifthSlider, 2);
        addSeekBarListener(activity, optionSlider, 3);

        // Hide fourth slider by default
        sliders.findViewById(R.id.slider_option_layout).setVisibility(View.GONE);

        return sliders;
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
        isBlocked = true;
        rootSlider.setMax(max);
        thirdSlider.setMax(max);
        fifthSlider.setMax(max);
        optionSlider.setMax(max);
        isBlocked = false;
    }

    public void setIsBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    /**
     * Gets a String representantation of this SliderFragment.
     * @return A String representantation of this SliderFragment
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
                if (isBlocked) return;

                // Only play note if progress change is from user
                if (seekBar instanceof VerticalSeekBar)
                {
                    Note note = new Note();
                    getNoteFromSlider(bar, note);
                    soundHandlers[slider].playNote(activity, note);
                }

//                test(); // TODO
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
                if (isBlocked) return true;

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Note note = new Note();
                    getNoteFromSlider(bar, note);
                    soundHandlers[slider].playNote(activity, note);
                    bar.setThumb(thumbTouched[slider]);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandlers[slider].stopSound();
                    bar.setThumb(thumb[slider]);
                    isMoving[slider] = false;
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (isMoving[slider]) return false;

                    int tempProgress = (bar.getMax() - (int) (bar.getMax() * event.getY() / bar.getHeight()));
                    int diff = Math.abs(tempProgress - bar.getProgress());
                    if (diff < MainActivity.getOptions().sliderDivisionsPerNote + 1) {
                        isMoving[slider] = true;
                        return false;
                    }
                }

                return true;
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
         * Constructor.
         */
        public SliderNotePosition()
        {   }

        /**
         * Constructs a new SliderNotePosition for the given SeekBar.
         * @param slider The Seekbar whose position to get
         */
        public SliderNotePosition(SeekBar slider)
        {
            calculate(slider);
        }

//        /**
//         * Gets the Midi pitch of this SliderNotePosition.
//         * @return The Midi pitch of this SliderNotePosition
//         */
//        public int getMidiPitch()
//        {
//            return 8192 + (int)Math.round(fracDistAboveNote * 4096);
//        }

        /**
         * Calculates the values of this SliderNotePosition for the given slider.
         * @param slider The slider whose note position to get
         */
        public void calculate(SeekBar slider)
        {
            Options options = MainActivity.getOptions();
            note = slider.getProgress() / options.sliderDivisionsPerNote;
            fracDistAboveNote = slider.getProgress() / ((double)options.sliderDivisionsPerNote) - note;
            note += minNoteOnSlider;
        }
    }
}

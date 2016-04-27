package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.SliderHintView;
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

    /** The main content view of this SliderFragment. */
    private View view;

    /** The SingleSliderFragment containing the root slider. */
    private SingleSliderFragment rootSliderFragment;

    /** The SingleSliderFragment containing the third slider. */
    private SingleSliderFragment thirdSliderFragment;

    /** The SingleSliderFragment containing the fifth slider. */
    private SingleSliderFragment fifthSliderFragment;

    /** The SingleSliderFragment containing the option slider. */
    private SingleSliderFragment optionSliderFragment;

    /**
     * Required empty public constructor.
     */
    public SliderFragment()
    {   }

    /**
     * Sets the bounds of the Sliders in this SliderFragment to fit the given chord.
     * @param chord The chord to fit
     */
    public void setSliderBoundsToFitChord(Note[] chord)
    {
        rootSliderFragment.setSliderBoundsToFitChord(chord);
        fifthSliderFragment.setSliderBoundsToFitChord(chord);
        thirdSliderFragment.setSliderBoundsToFitChord(chord);
        optionSliderFragment.setSliderBoundsToFitChord(chord);
    }

    /**
     * Generates one instance of the given type of hint.
     *
     * @param type The Hint type
     */
    public void makeHint(final byte type)
    {
        // Calculate the chord differences
        final Note[] builtChord = ChordHandler.getCurrentBuiltChordSpelling();
        final Note[] selectedChord = ChordHandler.getCurrentSelectedChordSpelling();

        // Root slider
        rootSliderFragment.hintView.setHint(type, builtChord[0], selectedChord[0], rootSliderFragment, 500L);

        // Third slider
        thirdSliderFragment.hintView.setHint(type, builtChord[1], selectedChord[1], thirdSliderFragment, 500L);

        // Fifth slider
        fifthSliderFragment.hintView.setHint(type, builtChord[2], selectedChord[2], fifthSliderFragment, 500L);

        // Option slider
        if (ChordHandler.getCurrentSelectedChord().TYPE.offsets.length == 4)
            optionSliderFragment.hintView.setHint(type, builtChord[3], selectedChord[3], optionSliderFragment, 500L);
    }

    /**
     * Gets the current chord built on the note sliders and puts it in the givn array.
     * @param chord The array into which to put the built chord
     */
    public void buildCurrentChord(Note[] chord)
    {
        // Set slider bounds to fit chord
        Note[] selectedChord = ChordHandler.getCurrentSelectedChordSpelling();

        // Assign values
        if (rootSliderFragment != null)
        {
            rootSliderFragment.setSliderBoundsToFitChord(selectedChord);
            rootSliderFragment.getNoteFromSlider(chord[0]);
        }

        if (thirdSliderFragment != null)
        {
            thirdSliderFragment.setSliderBoundsToFitChord(selectedChord);
            thirdSliderFragment.getNoteFromSlider(chord[1]);
        }

        if (fifthSliderFragment != null)
        {
            fifthSliderFragment.setSliderBoundsToFitChord(selectedChord);
            fifthSliderFragment.getNoteFromSlider(chord[2]);
        }

        if (optionSliderFragment != null)
        {
            optionSliderFragment.setSliderBoundsToFitChord(selectedChord);
            optionSliderFragment.getNoteFromSlider(chord[3]);
        }
    }

    /**
     * Saves the positions of the sliders on this SliderFragment.
     */
    public void saveSliderPositions()
    {
        if (rootSliderFragment != null)
            rootSliderFragment.loadSliderPosition(getArguments());

        if (thirdSliderFragment != null)
            thirdSliderFragment.loadSliderPosition(getArguments());

        if (fifthSliderFragment != null)
            fifthSliderFragment.loadSliderPosition(getArguments());

        if (optionSliderFragment != null)
            optionSliderFragment.loadSliderPosition(getArguments());
    }

    /**
     * Loads the positions of the sliders on this SliderFragment.
     */
    public void loadSliderPositions()
    {
        if (rootSliderFragment != null)
            rootSliderFragment.loadSliderPosition(getArguments());

        if (thirdSliderFragment != null)
            thirdSliderFragment.loadSliderPosition(getArguments());

        if (fifthSliderFragment != null)
            fifthSliderFragment.loadSliderPosition(getArguments());

        if (optionSliderFragment != null)
            optionSliderFragment.loadSliderPosition(getArguments());
    }

    /**
     * Toggles whether or not the fourth slider is showing.
     * @param show Whether or not to show the fourth slider
     */
    public void showFourthSlider(boolean show)
    {
        view.findViewById(R.id.slider_option_frame).setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Called to reset the positions of the chord sliders.
     */
    public void resetChordSliders()
    {
        rootSliderFragment.reset();
        thirdSliderFragment.reset();
        fifthSliderFragment.reset();
        optionSliderFragment.reset();
    }

    /**
     * Stop sound from all sliders
     */
    public void silenceSliders()
    {
        if (rootSliderFragment != null)
            rootSliderFragment.silenceSlider();

        if (thirdSliderFragment != null)
            thirdSliderFragment.silenceSlider();

        if (fifthSliderFragment != null)
            fifthSliderFragment.silenceSlider();

        if (optionSliderFragment != null)
            optionSliderFragment.silenceSlider();
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
        Log.w("SLIDER_FRAGMENT", "onCreateView");

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sliders, container, false);

        // Create Fragments
        rootSliderFragment = new SingleSliderFragment();
        rootSliderFragment.setSliderIndex(0);
        rootSliderFragment.setArguments(getArguments());

        thirdSliderFragment = new SingleSliderFragment();
        thirdSliderFragment.setSliderIndex(1);
        thirdSliderFragment.setArguments(getArguments());

        fifthSliderFragment = new SingleSliderFragment();
        fifthSliderFragment.setSliderIndex(2);
        fifthSliderFragment.setArguments(getArguments());

        optionSliderFragment = new SingleSliderFragment();
        optionSliderFragment.setSliderIndex(3);
        optionSliderFragment.setArguments(getArguments());

        // Add Fragments to layout
        getFragmentManager().beginTransaction()
                .add(R.id.slider_root_frame, rootSliderFragment)
                .add(R.id.slider_third_frame, thirdSliderFragment)
                .add(R.id.slider_fifth_frame, fifthSliderFragment)
                .add(R.id.slider_option_frame, optionSliderFragment).commit();

        // Hide fourth slider by default
        showFourthSlider(false);

        return view;
    }

    /**
     * Called when this Fragment is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();

        if (rootSliderFragment != null)
            rootSliderFragment.loadSliderPosition(getArguments());

        if (thirdSliderFragment != null)
            thirdSliderFragment.loadSliderPosition(getArguments());

        if (fifthSliderFragment != null)
            fifthSliderFragment.loadSliderPosition(getArguments());

        if (optionSliderFragment != null)
            optionSliderFragment.loadSliderPosition(getArguments());
    }

    /**
     * Called when this Fragment is paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Destroy Fragments
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (rootSliderFragment != null)
        {
            rootSliderFragment.saveSliderPosition(getArguments());
            transaction.detach(rootSliderFragment);
            transaction.remove(rootSliderFragment);
        }

        if (thirdSliderFragment != null)
        {
            thirdSliderFragment.saveSliderPosition(getArguments());
            transaction.detach(thirdSliderFragment);
            transaction.remove(thirdSliderFragment);
        }

        if (fifthSliderFragment != null)
        {
            fifthSliderFragment.saveSliderPosition(getArguments());
            transaction.detach(fifthSliderFragment);
            transaction.remove(fifthSliderFragment);
        }

        if (optionSliderFragment != null)
        {
            optionSliderFragment.saveSliderPosition(getArguments());
            transaction.detach(optionSliderFragment);
            transaction.remove(optionSliderFragment);
        }

        try
        {
            transaction.commit();
        }
        catch (Exception e)
        {/* Ignore */}
    }

//    /**
//     * Called when the view state of this Fragment is restored.
//     * @param savedInstanceState The Bundle from which to restore the view state
//     */
//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState)
//    {
//        super.onViewStateRestored(savedInstanceState);
//
//        // Load Slider positions
//        if (rootSliderFragment != null)
//            rootSliderFragment.loadSliderPosition(getArguments());
//
//        if (thirdSliderFragment != null)
//            thirdSliderFragment.loadSliderPosition(getArguments());
//
//        if (fifthSliderFragment != null)
//            fifthSliderFragment.loadSliderPosition(getArguments());
//
//        if (optionSliderFragment != null)
//            optionSliderFragment.loadSliderPosition(getArguments());
//    }

//    /**
//     * Called when the instance state of this Fragment should be saved.
//     * @param bundle The Bundle in which to save the instance state
//     */
//    @Override
//    public void onSaveInstanceState(Bundle bundle)
//    {
//        super.onSaveInstanceState(bundle);
//
//        // Save Slider positions
//        Bundle arguments = getArguments();
//        if (arguments != null)
//        {
//            if (rootSlider != null)
//                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 0, rootSlider.getProgress());
//
//            if (thirdSlider != null)
//                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 1, thirdSlider.getProgress());
//
//            if (fifthSlider != null)
//                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 2, fifthSlider.getProgress());
//
//            if (optionSlider != null)
//                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + 3, optionSlider.getProgress());
//        }
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

        return "SliderFragment: Min Note = " + rootSliderFragment.minNoteOnSlider +
                ", Max Note = " + rootSliderFragment.maxNoteOnSlider +
                "\n\tSlider Notes = " + value;
    }


//    /**
//     * Called to remove the seek bar listener from a single seek bar which were added with addSeekBarListener().
//     * @param bar The VerticalSeekBar to remove listeners from
//     */
//    private void removeSeekBarListener(VerticalSeekBar bar)
//    {
//        bar.setOnClickListener(null);
//        bar.setOnSeekBarChangeListener(null);
//        bar.setOnTouchListener(null);
//    }

//    /**
//     * Called to add the seek bar listener to a single seek bar.
//     * @param bar The VerticalSeekBar to add listeners to
//     */
//    private void addSeekBarListener(final Activity activity, final VerticalSeekBar bar, final int slider)
//    {
//        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
//        {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
//            {
//                if (isBlocked)
//                    return;
//
//                // Only play note if progress change is from user
//                if (seekBar instanceof VerticalSeekBar)
//                {
//                    Note note = new Note();
//                    getNoteFromSlider(bar, note);
//                    soundHandlers[slider].playNote(activity, note);
//                }
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar)
//            {   }
//
//            public void onStopTrackingTouch(SeekBar seekBar)
//            {   }
//        });
//
//        bar.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                if (isBlocked)
//                    return true;
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN)
//                {
//                    Note note = new Note();
//                    getNoteFromSlider(bar, note);
//                    soundHandlers[slider].playNote(activity, note);
//                    bar.setThumb(thumbTouched[slider]);
//                    bar.setThumbOffset(THUMB_OFFSET);
//                                    }
//                else if (event.getAction() == MotionEvent.ACTION_UP)
//                {
//                    isMoving[slider] = false;
//                    soundHandlers[slider].stopSound();
//                    bar.setThumb(thumb[slider]);
//                    bar.setThumbOffset(THUMB_OFFSET);
//                }
//                else if (event.getAction() == MotionEvent.ACTION_MOVE)
//                {
//                    if (isMoving[slider])
//                        return false;
//
////                    int verticalPadding = bar.getBaseline() + bar.getPaddingBottom() + bar.getWidth();
////                    int tempProgress = (bar.getMax() - (int) (bar.getMax() * (event.getY() - verticalPadding) / (bar.getHeight() - verticalPadding * 2)));
////
//////                    int tempProgress = (bar.getMax() - (int) (bar.getMax() * event.getY() / bar.getHeight()));
////                    int diff = Math.abs(tempProgress - bar.getProgress());
////
////                    if (diff < MainActivity.getOptions().sliderDivisionsPerNote + 1)
////                    {
////                        isMoving[slider] = true;
////                        return false;
////                    }
////                    else
////                    {
////                        isMoving[slider] = false;
////                        return true;
////                    }
//                }
//
//                return false;
//            }
//        });
//    }

    /**
     * Wrapper class representing the position of a slider as a fractional distance above a note.
     */
    public static class SliderNotePosition
    {
        /** The note position of the slider. */
        public int note;
        /** The fractional distance above note position of the slider. */
        public double fracDistAboveNote;

        /**
         * Constructs a new SliderNotePosition for the given SeekBar.
         * @param singleSliderFragment The SingleSliderFragment containing the slider whose position to get
         */
        public SliderNotePosition(SingleSliderFragment singleSliderFragment)
        {
            calculate(singleSliderFragment);
        }

        /**
         * Calculates the values of this SliderNotePosition for the given slider.
         * @param singleSliderFragment The SingleSliderFragment containing the slider whose position to get
         */
        public void calculate(SingleSliderFragment singleSliderFragment)
        {
            Options options = MainActivity.getOptions();

            // Scale the value to the right range
            note = singleSliderFragment.slider.getProgress() / options.sliderDivisionsPerNote;

            // Calculate the fractional position of the note
            fracDistAboveNote = singleSliderFragment.slider.getProgress() / ((double)options.sliderDivisionsPerNote) - note;

            // Shift the value to the right range
            note += singleSliderFragment.minNoteOnSlider;
        }
    }

    /**
     * An individual slider fragment.
     */
    public static class SingleSliderFragment extends Fragment
    {
        /** The index of this SingleSliderFragment. */
        private int sliderIndex;

        /** Don't use onTouch methods when blocking */
        private boolean isBlocked;

        /** The index of the notes at the bottom of the slider. */
        private int minNoteOnSlider;

        /** The index of the notes at the top of the slider. */
        private int maxNoteOnSlider;

        /** The slider on the Fragment. */
        private VerticalSeekBar slider;

        /** The SliderHintView attached to this SliderFragment. */
        private SliderHintView hintView;

        /** The Slider thumb drawable. */
        private Drawable thumb;

        /** The Slider thumb drawable for when the slider is pressed. */
        private Drawable thumbTouched;

        /** The SoundHandler for the slider. */
        private SoundHandler soundHandlers;

        /** Whether or not the slider is moving. **/
        private boolean isMoving;

        /**
         * Sets the index of this SingleSliderFragment.
         * @param sliderIndex The index of this SingleSliderFragment
         */
        public void setSliderIndex(int sliderIndex)
        {
            this.sliderIndex = sliderIndex;
        }

        /**
         * Resets the position of this SingleSliderFragment.
         */
        public void reset()
        {
            isBlocked = true;
            slider.setProgress(0);
            isBlocked = false;
        }

        /**
         * Saves the position of the slider on this SingleSliderFragment.
         * @param arguments The Bundle to save to
         */
        public void saveSliderPosition(Bundle arguments)
        {
            if (arguments != null)
                arguments.putInt(SLIDER_POSITION_BUNDLE_ID + sliderIndex, slider.getProgress());
        }

        /**
         * Loads the position of the slider on this SingleSliderFragment.
         * @param arguments The Bundle to load from
         */
        public void loadSliderPosition(Bundle arguments)
        {
            if (arguments != null && slider != null)
            {
                slider.setProgress(arguments.getInt(SLIDER_POSITION_BUNDLE_ID + sliderIndex));
            }
        }

        /**
         * Stops the sound from this SingleSliderFragment.
         */
        public void silenceSlider()
        {
            if (soundHandlers != null)
                soundHandlers.stopSound();
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
            // Block sounds
            isBlocked = true;

            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_slider, container, false);

            // Get thumb images
            thumb = getResources().getDrawable(R.drawable.thumb_icon);
            thumbTouched = getResources().getDrawable(R.drawable.thumb_icon_pressed);

            // Create sound handler
            soundHandlers = new SoundHandler(getActivity(), "slider" + sliderIndex);

            // Get the seek bar
            slider = (VerticalSeekBar) view.findViewById(R.id.slider_fragment_slider);
            hintView = (SliderHintView) view.findViewById(R.id.slider_fragment_hint_view);

            // Add the seek bar listeners
            final Activity activity = getActivity();
            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
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
                        getNoteFromSlider(note);
                        soundHandlers.playNote(activity, note);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar)
                {   }

                public void onStopTrackingTouch(SeekBar seekBar)
                {   }
            });

            slider.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (isBlocked)
                        return true;

                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        Note note = new Note();
                        getNoteFromSlider(note);
                        soundHandlers.playNote(activity, note);
                        slider.setThumb(thumbTouched);
                        slider.setThumbOffset(THUMB_OFFSET);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        isMoving = false;
                        soundHandlers.stopSound();
                        slider.setThumb(thumb);
                        slider.setThumbOffset(THUMB_OFFSET);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    {
                        if (isMoving)
                            return false;
                    }

                    return false;
                }
            });

            // Set Play button function
            Button playButton = (Button) view.findViewById(R.id.button_slider_play);
            playButton.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (isBlocked)
                        return true;

                    if (event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        Note note = new Note();
                        getNoteFromSlider(note);
                        soundHandlers.playNote(activity, note);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        soundHandlers.stopSound();
                    }

                    return false;
                }
            });


            // Set Up and Down Button functions
            Button upButton = (Button) view.findViewById(R.id.button_slider_up);
            upButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (slider != null)
                    {
                        isBlocked = true;
                        int moveAmount = MainActivity.getOptions().sliderDivisionsPerNote / 4;
                        slider.incrementProgress(Math.max(1, moveAmount));
                        isBlocked = false;
                    }
                }
            });

            Button downButton = (Button) view.findViewById(R.id.button_slider_down);
            downButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (slider != null)
                    {
                        isBlocked = true;
                        int moveAmount = -MainActivity.getOptions().sliderDivisionsPerNote / 4;
                        slider.incrementProgress(Math.min(-1, moveAmount));
                        isBlocked = false;
                    }
                }
            });

            // Load slider position
            loadSliderPosition(getArguments());

            return view;
        }

        /**
         * Called when this SliderFragment is resumed.
         */
        @Override
        public void onResume()
        {
            super.onResume();

            // Set bounds
            setSliderBoundsToFitChord(ChordHandler.getCurrentSelectedChordSpelling());

            // Load slider position
            loadSliderPosition(getArguments());

            isBlocked = false;
        }

        /**
         * Called when this SliderFragment is paused.
         */
        @Override
        public void onPause()
        {
            super.onPause();

            // Save position
            saveSliderPosition(getArguments());

            isBlocked = true;
        }

        /**
         * Called when the view of this Fragment is destroyed.
         */
        @Override
        public void onDestroyView()
        {
            // Save position
            saveSliderPosition(getArguments());

            slider.setOnClickListener(null);
            slider.setOnSeekBarChangeListener(null);
            slider.setOnTouchListener(null);

            thumb = null;
            thumbTouched = null;
            soundHandlers.stopSound();
            soundHandlers = null;

            // Call super method
            super.onDestroyView();
        }

        /**
         * Sets the bounds of the Slider in this SingleSliderFragment to fit the given chord.
         * @param chord The chord to fit
         */
        private void setSliderBoundsToFitChord(Note[] chord)
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

            if (slider != null)
                slider.setMax(max);

            isBlocked = wasBlocked;
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
         * Gets the current note on the slider, taking into account the number of intermediate slider
         * positions.
         * @param note A Note Object in which to put the value
         */
        private void getNoteFromSlider(Note note)
        {
            SliderNotePosition position = new SliderNotePosition(this);

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
    }
}

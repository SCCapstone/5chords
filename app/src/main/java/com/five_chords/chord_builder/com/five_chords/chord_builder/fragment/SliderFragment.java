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
import android.widget.TextView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.VerticalSeekBar;
import com.five_chords.chord_builder.soundHandler;

/**
 * A Fragment containing the chord select sliders.
 */
public class SliderFragment extends Fragment
{
    /** The slider for the root note */
    private static VerticalSeekBar rootSlider;

    /** The slider for the third note */
    private static VerticalSeekBar thirdSlider;

    /** The slider for the fifth note */
    private static VerticalSeekBar fifthSlider;

    /** The slider for the option note */
    private static VerticalSeekBar optionSlider;

    /** Don't use onTouch methods when blocking **/
    private static boolean blockSliders = false;

    private static Drawable thumb;
    private static Drawable thumbTouched;

    /**
     * Required empty public constructor.
     */
    public SliderFragment()
    {

    }


    /**
     * Gets the root slider.
     * @return The given slider
     */
    public static VerticalSeekBar getRootSlider()
    {
        return rootSlider;
    }

    /**
     * Gets the third slider.
     * @return The given slider
     */
    public static VerticalSeekBar getThirdSlider()
    {
        return thirdSlider;
    }

    /**
     * Gets the fifth slider.
     * @return The given slider
     */
    public static VerticalSeekBar getFifthSlider()
    {
        return fifthSlider;
    }

    /**
     * Gets the option slider.
     * @return The given slider
     */
    public static VerticalSeekBar getOptionSlider()
    {
        return optionSlider;
    }

    /**
     * Called to reset the positions of the chord sliders.
     */
    public static void resetChordSliders()
    {
        VerticalSeekBar slider = rootSlider;

        if (slider != null)
        {
            slider.setProgress(0);
            slider.setTouched(false);
        }

        slider = thirdSlider;
        if (slider != null)
        {
            slider.setProgress(0);
            slider.setTouched(false);
        }

        slider = fifthSlider;
        if (slider != null)
        {
            slider.setProgress(0);
            slider.setTouched(false);
        }

        slider = optionSlider;
        if (slider != null)
        {
            slider.setProgress(0);
            slider.setTouched(false);
        }
    }

    /**
     * Called to add the seek bar listener to a single seek bar.
     * @param bar The seekbar to add listeners to
     */
    private static void addSeekBarListener(final Activity activity, final VerticalSeekBar bar, final int slider)
    {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // Only play note if progress change is from user
                if (seekBar instanceof VerticalSeekBar && ((VerticalSeekBar)seekBar).isTouched() && !blockSliders) {
                    int intervals = chordHandler.getCurrentNumInterval();
                    int[] offsets = chordHandler.getCurrentSliderOffset();
                    int[] correctChord = chordHandler.getCurrentCorrectChord();

                    soundHandler.playNote(activity, bar.getProgress(), intervals, offsets[slider], correctChord[slider]);
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    int intervals = chordHandler.getCurrentNumInterval();
                    int[] offsets = chordHandler.getCurrentSliderOffset();
                    int[] correctChord = chordHandler.getCurrentCorrectChord();

                    soundHandler.playNote(activity, bar.getProgress(), intervals, offsets[slider], correctChord[slider]);
                    bar.setThumb(thumbTouched);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    soundHandler.stopSound();
                    bar.setThumb(thumb);
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return false;

                return true;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sliders = inflater.inflate(R.layout.fragment_sliders, container, false);


        thumb = getResources().getDrawable(R.drawable.eq_slide_knob);
        thumb.setBounds(new Rect(0, 0, thumb.getIntrinsicWidth(), thumb.getIntrinsicHeight()));

        thumbTouched = getResources().getDrawable(R.drawable.eq_slide_knob_touched);
        thumbTouched.setBounds(new Rect(0, 0, thumbTouched.getIntrinsicWidth(), thumbTouched.getIntrinsicHeight()));

        // Assign sliders
        rootSlider = (VerticalSeekBar)sliders.findViewById(R.id.slider_root);
        rootSlider.initialize();
        rootSlider.setThumb(thumb);

        thirdSlider = (VerticalSeekBar)sliders.findViewById(R.id.slider_third);
        thirdSlider.initialize();
        thirdSlider.setThumb(thumb);

        fifthSlider = (VerticalSeekBar)sliders.findViewById(R.id.slider_fifth);
        fifthSlider.initialize();
        fifthSlider.setThumb(thumb);

        optionSlider = (VerticalSeekBar)sliders.findViewById(R.id.slider_option);
        optionSlider.initialize();
        optionSlider.setThumb(thumb);

        // Hide the fourth slider by default
        sliders.findViewById(R.id.slider_option_layout).setVisibility(View.GONE);

        // Add the seek bar listeners
        Activity activity = getActivity();
        addSeekBarListener(activity, rootSlider, 0);
        addSeekBarListener(activity, thirdSlider, 1);
        addSeekBarListener(activity, fifthSlider, 2);
        addSeekBarListener(activity, optionSlider, 3);
//        setUpGUI.addSeekBarListeners(getActivity(), sliders);

        return sliders;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        rootSlider = null;
        thirdSlider = null;
        fifthSlider = null;
        optionSlider = null;
    }

    public void setMaxProgress(int max) {
        blockSliders = true;
        rootSlider.setMax(max);
        thirdSlider.setMax(max);
        fifthSlider.setMax(max);
        optionSlider.setMax(max);

        rootSlider.setProgress(0);
        thirdSlider.setProgress(0);
        fifthSlider.setProgress(0);
        optionSlider.setProgress(0);
        blockSliders = false;
    }
}

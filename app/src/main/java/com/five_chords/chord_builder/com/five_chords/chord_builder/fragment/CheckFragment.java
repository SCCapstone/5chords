package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.soundHandler;

/**
 * A Fragment containing the chord check buttons.
 * @author tstone95
 */
public class CheckFragment extends Fragment
{
    /** The Button for playing the built chord. */
    private Button playBuiltChordButton;

    /**
     * Required empty public constructor.
     */
    public CheckFragment()
    {   }

    /**
     * Called to play the build chord programmatically.
     * @param play Whether or not to play the chord
     */
    public void playBuiltChord(boolean play)
    {
        MainActivity.pressButton(playBuiltChordButton, play);
    }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragmet's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_check, container, false);

        // Initialize Buttons
        final Button checkChord = (Button) view.findViewById(R.id.button_check_user_chord);
        final Button playBuiltChord = (Button) view.findViewById(R.id.button_playback_slider_chord);
        playBuiltChordButton = playBuiltChord;

        // Set the preview button function
        playBuiltChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    chordHandler.buildCurrentChord(getActivity());
                    soundHandler.playChord(getActivity(), chordHandler.getCurrentBuiltChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                }
                return false;
            }
        });

        // Set the check button function
        checkChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    // Momentarily disable the button
                    v.setEnabled(false);
                    v.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            v.setEnabled(true);
                        }
                    }, 1000L);

                    // Check the result
                    Activity activity = getActivity();

                    if (activity instanceof MainActivity)
                        chordHandler.checkCurrentChord((MainActivity)activity);
                }
                return false;
            }
        });

        // Return the view
        return view;
    }
}

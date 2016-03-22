/*************************************************************************************************
 * setUpGUI.java
 * This class will set up user interface(GUI) for the main screen, which consist of drop-down menu
 * of "instrument" and dropdown-menu of lists of chord. Clicking on each chord and press check will
 * play the according chord. seekBarListener will allow user to adjust the chord manually.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ChordInstrumentSelectFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.VerticalSeekBar;

public class setUpGUI
{
    /**
     * Static class.
     */
    private setUpGUI()
    {   }

    /**
     * Called to initialize setUpGUI.
     * @param activity The calling MainActivity
     */
    public static void initialize(MainActivity activity)
    {
        loadSpinners(activity, true, false, false);
        assignButtons(activity);
    }

    /**
     * loadSpinners function
     * This method consists of two dropdown menus, one for instruments selection and one for chord
     * selection.
     * @param activity The calling Activity
     * @param majorChords Whether or not to load the major chords
     * @param minorChords Whether or not to load the minor chords
     * @param dominantChords Whether or not to load the dominant chords
     */
    public static void loadSpinners(final Activity activity, boolean majorChords, boolean minorChords, boolean dominantChords)
    {
        // Clear the list of currently loaded chords
        chordHandler.clearChords();

        // An array of the indices of the available chords
        int[] chordIndices;

        // All chords added
        if (majorChords && minorChords && dominantChords)
        {
            chordHandler.addMajorChords();
            chordHandler.addMinorChords();
            chordHandler.addDominantChords();
            chordIndices = null;
        }
        else
        {
            int i = 0;
            chordIndices = new int[12 * ((majorChords ? 1:0) + (minorChords ? 1:0) + (dominantChords ? 1:0))]; // TODO get constant for 12

            // Add major chords
            if (majorChords)
            {
                for (int j = 0; j < 12; ++j)
                    chordIndices[i + j] = j;
                i += 12;

                chordHandler.addMajorChords();
            }

            // Add minor chords
            if (minorChords)
            {
                for (int j = 0; j < 12; ++j)
                    chordIndices[i + j] = 12 + j;
                i += 12;

                chordHandler.addMinorChords();
            }

            // Add dominant chords
            if (dominantChords)
            {
                for (int j = 0; j < 12; ++j)
                    chordIndices[i + j] = 24 + j;

                chordHandler.addDominantChords();
            }
        }

        // Update the Chord select spinner
        FragmentManager manager = activity.getFragmentManager();
        ChordInstrumentSelectFragment fragment = (ChordInstrumentSelectFragment)manager.findFragmentById(R.id.fragment_chord_select);
        fragment.updateChordSpinner(activity, chordIndices);
    }

    /**
     * Called to assign functions to buttons on the MainActivity page.
     * @param activity Handle to the MainAcitvity
     */
    public static void assignButtons(final MainActivity activity) {
        final Button playBuiltChord = (Button) activity.findViewById(R.id.button_playback_slider_chord);
        final Button playSelectedChord = (Button) activity.findViewById(R.id.button_select_chord_play);
        final Button selectRandomChord = (Button) activity.findViewById(R.id.button_select_random_chord);
        final Button checkChord = (Button) activity.findViewById(R.id.button_check_user_chord);

        playBuiltChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundHandler.playBuiltChord(activity,
                                                chordHandler.getCurrentPreciseBuiltChord(activity),
                                                chordHandler.getCurrentNumInterval(),
                                                chordHandler.getCurrentSliderOffset(),
                                                chordHandler.getCurrentCorrectChord());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                }
                return true;
            }
        });

        //This will listen for touch on whether to play the sound, and to now change the image of the button
        //decided this was a better option as, it listens for onclick already
        playSelectedChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundHandler.playChord(activity, chordHandler.getCurrentSelectedChord());
                   playSelectedChord.setBackgroundResource(R.drawable.testbtn1);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                    playSelectedChord.setBackgroundResource(R.drawable.play_btn_image1);
                }
                return true;
            }
        });

        selectRandomChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chordHandler.getRandomChord();
                    activity.updateChordSelectSpinner();
                    soundHandler.playChord(activity, chordHandler.getCurrentSelectedChord());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                }

                return true;
            }
        });

        checkChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    // Momentarily disable the button
                    v.setEnabled(false);
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.setEnabled(true);
                        }
                    }, 1000L);

                    // Check the result
                    chordHandler.checkCurrentChord(activity);
                }
                return true;
            }
        });
    }
}

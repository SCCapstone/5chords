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


import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

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
        assignButtons(activity);
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
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    chordHandler.buildCurrentChord(activity);
                    soundHandler.playChord(activity, chordHandler.getCurrentBuiltChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                    playBuiltChord.setBackgroundResource(R.drawable.buttons_touched);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                    playBuiltChord.setBackgroundResource(R.drawable.buttons_touched);
                }
                return true;
            }
        });

        //This will listen for touch on whether to play the sound, and to now change the image of the button
        //decided this was a better option as, it listens for onclick already
        playSelectedChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    soundHandler.playChord(activity, chordHandler.getCurrentSelectedChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                    playSelectedChord.setBackgroundResource(R.drawable.round_button_play_touched);
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    soundHandler.stopSound();
                    playSelectedChord.setBackgroundResource(R.drawable.round_button_play);
                }
                return true;
            }
        });

        selectRandomChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    chordHandler.getRandomChord();
                    soundHandler.playChord(activity, chordHandler.getCurrentSelectedChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                    selectRandomChord.setBackgroundResource(R.drawable.round_button_shuffle_touched);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                    selectRandomChord.setBackgroundResource(R.drawable.round_button_shuffle);
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
                    v.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
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

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class setUpGUI
{
    public setUpGUI(MainActivity activity) {

        loadSpinners(activity, true, false, false);
        assignButtons(activity);
    }

    public setUpGUI(Activity activity, View view) {
        seekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_root), (TextView) view.findViewById(R.id.textview_root));
        seekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_third), (TextView) view.findViewById(R.id.textview_third));
        seekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_fifth), (TextView) view.findViewById(R.id.textview_fifth));
        seekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_option), (TextView) view.findViewById(R.id.textview_option));
    }

    /**********************************************************************************************
     * seekBarListener function
     * This function will allow user to adjust the chord manually using seekBar
     * @param view The context of the resources
     * @param bar The seekbar to add listeners to
     * @param text the textview associated with the seekbar
     **/
    public void seekBarListener(final Activity activity, View view, final SeekBar bar, final TextView text)
    {
        // A reference to the noteNames to pass to the Listener
        final String[] noteNames = view.getResources().getStringArray(R.array.noteNames);
        text.setText(noteNames[0]);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                text.setText(noteNames[progress % 12]);

                // Only play note if progress change is from user
                if (seekBar instanceof VerticalSeekBar && ((VerticalSeekBar)seekBar).isTouched())
                {
                    soundHandler.playNote(activity, bar.getProgress());
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        bar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    soundHandler.playNote(activity, bar.getProgress());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    soundHandler.stopSound();
                }
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                {
                    return false;
                }
                return true;
            }
        });
    }

    /***********************************************************************************************
     * loadSpinners function
     * This functions consist of two dropdown menus, one for instruments selection and one for chord
     * selection.
     * @param activity The calling Activity
     * @param majorChords Whether or not to load the major chords
     * @param minorChords Whether or not to load the minor chords
     * @param dominantChords Whether or not to load the dominant chords
     */
    public void loadSpinners(final Activity activity, boolean majorChords, boolean minorChords, boolean dominantChords)
    {
        // Populate the chord select spinner
        final Spinner chordSelector = (Spinner) activity.findViewById(R.id.spinner_chord_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);

        // Load items
        String[] chordNames = activity.getResources().getStringArray(R.array.chordNames);

        chordHandler.clearChords();

        // All chords added
        if (majorChords && minorChords && dominantChords) {
            adapter.addAll(chordNames);
            chordHandler.addMajorChords();
            chordHandler.addMinorChords();
            chordHandler.addDominantChords();
        } else {
            String[] items = new String[12];

            // Add major chords
            if (majorChords)
            {
                System.arraycopy(chordNames, 0, items, 0, 12);
                adapter.addAll(items);
                chordHandler.addMajorChords();
            }

            // Add minor chords
            if (minorChords)
            {
                System.arraycopy(chordNames, 12, items, 0, 12);
                adapter.addAll(items);
                chordHandler.addMinorChords();
            }

            // Add dominant chords
            if (dominantChords)
            {
                System.arraycopy(chordNames, 24, items, 0, 12);
                adapter.addAll(items);
                chordHandler.addDominantChords();
            }
        }

        // Set the adapter
        chordSelector.setAdapter(adapter);

        // Set the OnItemSelectedListener for the spinner
        chordSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if (activity instanceof MainActivity)
                {
                    // Update the selected chord
                    chordHandler.getSelectedChord(chordSelector.getSelectedItemPosition() + 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });
    }

    public void assignButtons(final MainActivity activity) {
        final Button playBuiltChord = (Button) activity.findViewById(R.id.button_playback_slider_chord);
        final Button playSelectedChord = (Button) activity.findViewById(R.id.button_select_chord_play);
        final Button switchInstrument = (Button) activity.findViewById(R.id.button_select_instrument);
        final Button selectRandomChord = (Button) activity.findViewById(R.id.button_select_random_chord);
        final Button checkChord = (Button) activity.findViewById(R.id.button_check_user_chord);

        playBuiltChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundHandler.playChord(activity, chordHandler.buildCurrentChord(activity));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                }
                return true;
            }
        });

        playSelectedChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundHandler.playChord(activity, chordHandler.getCurrentChord());
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.stopSound();
                }
                return true;
            }
        });

        switchInstrument.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    soundHandler.switchInstrument();
                }
                return true;
            }
        });

        selectRandomChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    chordHandler.getRandomChord();
                    activity.updateSpinner();
                    soundHandler.playChord(activity, chordHandler.getCurrentChord());
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
                    v.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            v.setEnabled(true);
                        }
                    }, 500L);

                    // Check the result
                    boolean isCorrect = chordHandler.compareChords(chordHandler.buildCurrentChord(activity),
                            chordHandler.getCurrentChord());

                    // Handle result
                    if (isCorrect)
                    {
                        // Launch dialog TODO
                    }
                    else
                    {
                        // Show toast
                        Toast.makeText(activity, activity.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT).show();
                    }

                    Score.setScore(activity, chordHandler.getCurrentChordIndex(), isCorrect);
                    activity.displayAnswer();
                }
                return true;
            }
        });
    }
}

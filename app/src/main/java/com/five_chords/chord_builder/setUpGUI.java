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
     * Called to add the seek bar listeners to the chord sliders.
     * @param activity The calling activity
     * @param view The parent View of the seek bars
     */
    public static void addSeekBarListeners(Activity activity, View view)
    {
        addSeekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_root), (TextView) view.findViewById(R.id.textview_root));
        addSeekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_third), (TextView) view.findViewById(R.id.textview_third));
        addSeekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_fifth), (TextView) view.findViewById(R.id.textview_fifth));
        addSeekBarListener(activity, view, (SeekBar) view.findViewById(R.id.slider_option), (TextView) view.findViewById(R.id.textview_option));
    }

    /**
     * Called to reset the positions of the chord sliders.
     * @param activity The current Activity
     */
    public static void resetChordSliders(Activity activity)
    {
        VerticalSeekBar slider = ((VerticalSeekBar) activity.findViewById(R.id.slider_root));
        slider.setProgress(0);
        slider.setTouched(false);

        slider = ((VerticalSeekBar) activity.findViewById(R.id.slider_third));
        slider.setProgress(0);
        slider.setTouched(false);

        slider = ((VerticalSeekBar) activity.findViewById(R.id.slider_fifth));
        slider.setProgress(0);
        slider.setTouched(false);

        slider = ((VerticalSeekBar) activity.findViewById(R.id.slider_option));
        slider.setProgress(0);
        slider.setTouched(false);
    }

    /**********************************************************************************************
     * Called to add the seek bar listener to a single seek bar.
     * @param view The context of the resources
     * @param bar The seekbar to add listeners to
     * @param text the textview associated with the seekbar
     **/
    private static void addSeekBarListener(final Activity activity, View view, final SeekBar bar, final TextView text)
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
                    soundHandler.playNote(activity, bar.getProgress());
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
                    soundHandler.playNote(activity, bar.getProgress());
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    soundHandler.stopSound();
                else if (event.getAction() == MotionEvent.ACTION_MOVE)
                    return false;

                return true;
            }
        });
    }

    /***********************************************************************************************
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
                    chordHandler.setSelectedChord(chordSelector.getSelectedItemPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });
    }

    /**
     * Called to assign functions to buttons on the MainActivity page.
     * @param activity Handle to the MainAcitvity
     */
    public static void assignButtons(final MainActivity activity) {
        final Button playBuiltChord = (Button) activity.findViewById(R.id.button_playback_slider_chord);
        final Button playSelectedChord = (Button) activity.findViewById(R.id.button_select_chord_play);
        final Button switchInstrument = (Button) activity.findViewById(R.id.button_select_instrument);
        final Button selectRandomChord = (Button) activity.findViewById(R.id.button_select_random_chord);
        final Button checkChord = (Button) activity.findViewById(R.id.button_check_user_chord);

        playBuiltChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    soundHandler.playChord(activity, chordHandler.getCurrentBuiltChord(activity));
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
                    soundHandler.playChord(activity, chordHandler.getCurrentSelectedChord());
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

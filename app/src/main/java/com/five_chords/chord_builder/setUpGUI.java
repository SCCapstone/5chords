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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class setUpGUI extends MainActivity
{

//    String[] noteNames = {"C", "C#", "D", "E\u266D", "E", "F", "F#", "G", "A\u266D", "A", "B\u266D", "B"};

    /**********************************************************************************************
     * seekBarListener function
     * This function will allow user to adjust the chord manually using seekBar
     * @param activity The calling Activity
     * @param bar
     * @param text
     **/
    public void seekBarListener(Activity activity, SeekBar bar, final TextView text)
    {
        // A reference to the noteNames to pass to the Listener
        final String[] noteNames = activity.getResources().getStringArray(R.array.noteNames);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                text.setText(noteNames[progress % 12]);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    /***********************************************************************************************
     * loadSpinners function
     * This functions consist of two dropdown menus, one for instruments selection and one for chord
     * selection.
     * @param activity
     **/
    public void loadSpinners(Activity activity)
    {
        String[] chordNames = activity.getResources().getStringArray(R.array.chordNames);
        Spinner dropdown = (Spinner) activity.findViewById(R.id.spinner);
        String[] items = {"Piano", "Violin", "Trombone", "Frequency Tone"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) activity.findViewById(R.id.spinner2);

        // Populate items from the list of chord names in chordHandle
        items = new String[chordNames.length + 1];
        items[0] = "Random";
        System.arraycopy(chordNames, 0, items, 1, chordNames.length);

        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    /****************************************************************
     * Buttons can't call from other classes.
     * Solution 1: find a way to add listener
     * Solution 2: shove chord stuff in a new activity with fragments
     **********************
     public void buttonListeners(Activity activity) {
     Button getChord = (Button) activity.findViewById(R.id.getChord);
     Button checkChord = (Button) activity.findViewById(R.id.checkChord);
     Button playChord = (Button) activity.findViewById(R.id.playChord);

     getChord.setOnClickListener(new View.OnClickListener() {
     public void onClick(View v) { cH.getChord(); }
     });
     checkChord.setOnClickListener(new View.OnClickListener() {
     public void onClick(View v) { cH.checkChord(); }
     });
     playChord.setOnClickListener(new View.OnClickListener() {
     public void onClick(View v) { cH.playChord(); }
     });

     }
     */
}

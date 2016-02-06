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
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class setUpGUI extends MainActivity
{
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
    public void loadSpinners(final Activity activity)
    {
        // Populate the chord select spinner
        String[] chordNames = activity.getResources().getStringArray(R.array.chordNames);
        final Spinner chordSelector = (Spinner) activity.findViewById(R.id.spinner_chord_select);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, chordNames);
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
                    ((MainActivity) activity).getChord(chordSelector);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });
    }
}

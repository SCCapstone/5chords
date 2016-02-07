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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class setUpGUI
{
    public setUpGUI(Activity activity) {
        loadSpinners(activity, true, false, false);
    }

    public setUpGUI(View view) {
        seekBarListener(view, (SeekBar) view.findViewById(R.id.slider_root), (TextView) view.findViewById(R.id.textview_root));
        seekBarListener(view, (SeekBar) view.findViewById(R.id.slider_third), (TextView) view.findViewById(R.id.textview_third));
        seekBarListener(view, (SeekBar) view.findViewById(R.id.slider_fifth), (TextView) view.findViewById(R.id.textview_fifth));
        seekBarListener(view, (SeekBar) view.findViewById(R.id.slider_option), (TextView) view.findViewById(R.id.textview_option));
    }

    /**********************************************************************************************
     * seekBarListener function
     * This function will allow user to adjust the chord manually using seekBar
     * @param view The context of the resources
     * @param bar
     * @param text
     **/
    public void seekBarListener(View view, SeekBar bar, final TextView text)
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
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
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

        // All chords added
        if (majorChords && minorChords/* && dominantChords*/)
            adapter.addAll(chordNames);
        else
        {
            String[] items = new String[12];

            // Add major chords
            if (majorChords)
            {
                System.arraycopy(chordNames, 0, items, 0, 12);
                adapter.addAll(items);
            }

            // Add minor chords
            if (minorChords)
            {
                System.arraycopy(chordNames, 12, items, 0, 12);
                adapter.addAll(items);
            }

//            // Add dominant chords TODO implement dominant chords
//            if (dominantChords)
//            {
//                System.arraycopy(chordNames, 24, items, 0, 12);
//                adapter.addAll(items);
//            }
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
                    ((MainActivity) activity).getChord(chordSelector);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });
    }
}

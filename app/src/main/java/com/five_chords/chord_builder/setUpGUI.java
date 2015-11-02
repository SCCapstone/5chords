package com.five_chords.chord_builder;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class setUpGUI extends MainActivity {

    String[] chordNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};

    public void seekBarListener(SeekBar bar, final TextView text) {
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(chordNames[progress % 12]);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    public void loadSpinners(Activity activity) {
        Spinner dropdown = (Spinner) activity.findViewById(R.id.spinner);
        String[] items = new String[]{"Piano", "Violin", "Trombone", "Frequency Tone"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown = (Spinner) activity.findViewById(R.id.spinner2);
        items = new String[]{"Random", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};
        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
    }

    /****************************************************************
     * Buttons can't call from other classes.
     * Solution 1: find a way to add listener
     * Solution 2: shove chord stuff in a new activity with fragments
     **
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

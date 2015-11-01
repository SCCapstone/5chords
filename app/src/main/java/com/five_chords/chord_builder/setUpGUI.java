package com.five_chords.chord_builder;

import android.app.Activity;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class setUpGUI extends MainActivity {

    String[] chordNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};

    public void seekbarListeners(Activity activity) {
        SeekBar rootBar = (SeekBar) activity.findViewById(R.id.root);
        final TextView rootBarValue = (TextView) activity.findViewById(R.id.rootText);

        rootBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rootBarValue.setText(chordNames[progress % 12]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar thirdBar = (SeekBar) activity.findViewById(R.id.third);
        final TextView thirdBarValue = (TextView) activity.findViewById(R.id.thirdText);

        thirdBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thirdBarValue.setText(chordNames[progress % 12]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar fifthBar = (SeekBar) activity.findViewById(R.id.fifth);
        final TextView fifthBarValue = (TextView) activity.findViewById(R.id.fifthText);

        fifthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fifthBarValue.setText(chordNames[progress % 12]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        SeekBar optionBar = (SeekBar) activity.findViewById(R.id.option);
        final TextView optionBarValue = (TextView) activity.findViewById(R.id.optionText);

        optionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                optionBarValue.setText(chordNames[progress % 12]);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
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

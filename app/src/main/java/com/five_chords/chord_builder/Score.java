package com.five_chords.chord_builder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Score extends MainActivity {

    String[] chordNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B"};

    private SharedPreferences chordScores;
    public static final String CHORD_SCORES = "ScoreFile";

    int[] correctChords = new int[chordNames.length];
    int[] totalChords = new int[chordNames.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);
    }

    public void loadScores() {
        chordScores = getApplication().getSharedPreferences(CHORD_SCORES, 0);
        for (int i = 0; i < chordNames.length; i++) {
            correctChords[i] = chordScores.getInt("correct_" + chordNames[i], 0);
            totalChords[i] = chordScores.getInt("total_" + chordNames[i], 0);
        }
    }

    public void setScore(int chordIndex, boolean Correct) {
        SharedPreferences.Editor scoreEditor = chordScores.edit();
        if (Correct) scoreEditor.putInt("correct_" + chordNames[chordIndex], correctChords[chordIndex]++);
        scoreEditor.putInt("total_" + chordNames[chordIndex], totalChords[chordIndex]++);
        scoreEditor.apply();
    }

    public void displayScores(View view) {
        TextView scoreLabel;
        ArrayList<Integer> texts = new ArrayList<>();

        for (Field f : R.id.class.getFields()) {
            try {
                if (f.getName().contains("chord_score")) {
                    texts.add(f.getInt(null));
                }
            } catch (IllegalAccessException e) {

            }
        }

        for (int i = 0; i < chordNames.length; i++) {
            scoreLabel = (TextView) findViewById(texts.get(i));
            scoreLabel.setText(chordNames[i] + " : " + correctChords[i] + "/" + totalChords[i]);
        }
    }

    public void BackToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }

}

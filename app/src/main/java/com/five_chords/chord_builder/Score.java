
/*************************************************************************************************
 * Score.java
 * This class will keep the user performance on how are they doing so far.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class Score extends MainActivity {

    static String[] chordNames = {"C", "C#", "D", "Eb", "E", "F", "F#", "G", "Ab", "A", "Bb", "B",
                                  "C_minor", "C#_minor","D_minor","Eb_minor","E_minor","F_minor","F#_minor","G_minor","Ab_minor","A_minor","Bb_minor","B_minor"};

    private static SharedPreferences chordScores;
    public static final String CHORD_SCORES = "ScoreFile";

    static int[] correctChords = new int[chordNames.length];
    static int[] totalChords = new int[chordNames.length];

    RadioGroup chordClass;
    ArrayList<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_page);

        chordClass = (RadioGroup) findViewById(R.id.chordClass);

        // get all every TextView for displaying score
        RelativeLayout scorePage = (RelativeLayout) findViewById( R.id.scorePage );
        for( int i = 0; i < scorePage.getChildCount(); i++ )
            if( scorePage.getChildAt(i) instanceof TextView )
                textViews.add((TextView) scorePage.getChildAt(i));

        displayScores(0);

        // listen for change of chord class
        chordClass.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.major) {
                    displayScores(0);
                } else if (checkedId == R.id.minor) {
                    displayScores(1);
                }

            }
        });

    }

    /***********************************************************************************************
     * loadScores function
     * @param activity
     **********************************************************************************************/
    public void loadScores(Activity activity) {
        chordScores = activity.getApplication().getSharedPreferences(CHORD_SCORES, 0);
        for (int i = 0; i < chordNames.length; i++) {
            correctChords[i] = chordScores.getInt("correct_" + chordNames[i], 0);
            totalChords[i] = chordScores.getInt("total_" + chordNames[i], 0);
        }
    }

    /***********************************************************************************************
     * setScore function
     * @param chordIndex
     * @param Correct
     **********************************************************************************************/
    public void setScore(int chordIndex, boolean Correct) {
        SharedPreferences.Editor scoreEditor = chordScores.edit();

        if (Correct) scoreEditor.putInt("correct_" + chordNames[chordIndex], (correctChords[chordIndex]++)+1);
        scoreEditor.putInt("total_" + chordNames[chordIndex], (totalChords[chordIndex]++)+1);

        scoreEditor.apply();
    }
    /***********************************************************************************************
     * displayScores function
     * @param chords
     **********************************************************************************************/
    public void displayScores(int chords) {
        int chord = 12*chords;

        for (int i = chord; i < 12+chord; i++) {
            textViews.get(i%12).setText(chordNames[i] + " : " + correctChords[i] + "/" + totalChords[i]);
        }
    }
    /***********************************************************************************************
     * BacktoMain function
     * @param view
     **********************************************************************************************/
    public void BackToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class );
        startActivity(intent);
    }

}

package com.five_chords.chord_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    /****************************
     * This will probably become a fragment holder, with the sidebar/chord classes.
     * Those two will handle the bulk of the code.
     */

    chordHandler cH;
    setUpGUI gui;
    //Score s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cH = new chordHandler();
        gui = new setUpGUI();
        //s = new Score();

        cH.initialize(this);
        gui.loadSpinners(this);
        gui.seekbarListeners(this);
        //gui.buttonListeners(this);
        //s.loadScores(this);
    }


    // temporary solution to call methods in different classes
    public void getChord(View v) { cH.getChord(); }
    public void checkChord(View v) { cH.checkChord(this); }
    public void playChord(View v) { cH.playChord(this); }


    /****************************************************************
     * Go to help activity
     **/
    public void toHelpPage( View v){
        Intent intent = new Intent(this, HelpPage.class );
        startActivity(intent);
    }

}
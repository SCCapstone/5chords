/*************************************************************************************************
 * MainActivity.java
 * This class is where most of the activity happened. This is where all the other class and their
 * functions will be called from.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /****************************
     * This will probably become a fragment holder, with the sidebar/chord classes.
     * Those two will handle the bulk of the code.
     */

    chordHandler cH;
    setUpGUI gui;
    Score s;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cH = new chordHandler();
        gui = new setUpGUI();
        s = new Score();

        cH.initialize(this);

        gui.loadSpinners(this);
        gui.seekBarListener((SeekBar) findViewById(R.id.root), (TextView) findViewById(R.id.rootText));
        gui.seekBarListener((SeekBar) findViewById(R.id.third), (TextView) findViewById(R.id.thirdText));
        gui.seekBarListener((SeekBar) findViewById(R.id.fifth), (TextView) findViewById(R.id.fifthText));
        gui.seekBarListener((SeekBar) findViewById(R.id.option), (TextView) findViewById(R.id.optionText));

        //gui.buttonListeners(this);
        s.loadScores(this);

        /*
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentParentViewGroup, new fragmentOne())
                .commit();
       */
    }


    // temporary solution to call methods in different classes
    public void getChord(View v)   { cH.getChord(this); }
    public void checkChord(View v) { cH.checkChord(this, s); }
    public void playChord(View v)  { cH.playChord(this, cH.setChord, 0); }



    /****************************************************************
     * Go to help activity
     **/
    public void toHelpPage( View v){
        Intent intent = new Intent(this, HelpPage.class );
        startActivity(intent);
    }

}
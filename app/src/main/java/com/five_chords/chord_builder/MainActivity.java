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
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    /****************************
     * This will probably become a fragment holder, with the sidebar/chord classes.
     * Those two will handle the bulk of the code.
     */

    chordHandler cH;
    setUpGUI gui;
    Score s;

    int currentChordIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cH = new chordHandler();
        gui = new setUpGUI();
        s = new Score();

        cH.initialize(this);

        gui.loadSpinners(this);
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.root), (TextView) findViewById(R.id.rootText));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.third), (TextView) findViewById(R.id.thirdText));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.fifth), (TextView) findViewById(R.id.fifthText));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.option), (TextView) findViewById(R.id.optionText));

        //gui.buttonListeners(this);
        s.loadScores(this);

        /*
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentParentViewGroup, new fragmentOne())
                .commit();
       */
    }


    public void getChord(View v) {
        int chordChoice = ((Spinner) this.findViewById(R.id.spinner2)).getSelectedItemPosition();
        currentChordIndex = cH.newChordIndex(chordChoice);

        int[] setChord = cH.getChord(currentChordIndex);
        cH.playChord(setChord, setChord.length);
    }

    public void compareChords(View v) {
        int[] builtChord = buildChord(v);
        int[] setChord = cH.getChord(currentChordIndex);

        cH.playChord(builtChord, setChord.length);

        if (cH.compareChords(builtChord, setChord)) {
            displayAnswer(v, R.string.correct);
            s.setScore(this, currentChordIndex, true);
        } else {
            displayAnswer(v, R.string.wrong);
            s.setScore(this, currentChordIndex, false);
        }
    }

    public void displayAnswer(View v, int result) {
        // shows if the built chord matches the set chord
        TextView answerLabel = (TextView) this.findViewById(R.id.answer);
        TextView tv = (TextView) this.findViewById(R.id.chord);
        tv.setText(Score.correctChords[currentChordIndex] + " / " + Score.totalChords[currentChordIndex]);

        answerLabel.setText(result);
    }

    public int[] buildChord(View v) {
        int root = ((SeekBar) this.findViewById(R.id.root)).getProgress();
        int third = ((SeekBar) this.findViewById(R.id.third)).getProgress();
        int fifth = ((SeekBar) this.findViewById(R.id.fifth)).getProgress();
        int seventh = ((SeekBar) this.findViewById(R.id.option)).getProgress();

        return new int[] {root, third, fifth, seventh};
    }

    public void playChord(View v) {
        int[] setChord = cH.getChord(currentChordIndex);
        cH.playChord(setChord, setChord.length);
    }

    /****************************************************************
     * Go to help activity
     **/
    public void toHelpPage(View v) {
        Intent intent = new Intent(this, HelpPage.class);
        startActivity(intent);
    }

    /****************************************************************
     * Opens the Start Page
     **/
    public void openStartPage() {
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
    }
}
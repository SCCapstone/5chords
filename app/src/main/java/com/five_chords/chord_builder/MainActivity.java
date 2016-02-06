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

public class MainActivity extends AppCompatActivity
{
    /****************************
     * This will probably become a fragment holder, with the sidebar/chord classes.
     * Those two will handle the bulk of the code.
     */

    chordHandler cH;
    setUpGUI gui;
    Score s;

    int currentChordIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cH = new chordHandler();
        gui = new setUpGUI();
        s = new Score();

        cH.initialize(this);

        gui.loadSpinners(this);
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.slider_root), (TextView) findViewById(R.id.textview_root));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.slider_third), (TextView) findViewById(R.id.textview_third));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.slider_fifth), (TextView) findViewById(R.id.textview_fifth));
        gui.seekBarListener(this, (SeekBar) findViewById(R.id.slider_option), (TextView) findViewById(R.id.textview_option));

        s.loadScores(this);

    }

    /**
     * Called when a new chord is selected.
     * @param v The calling view
     */
    public void getChord(View v)
    {
        // The chord choice
        int chordChoice;

        // Check to make sure a random chord was not selected
        if (v.getId() == R.id.button_select_random_chord)
            chordChoice = 0;
        else
            chordChoice = ((Spinner) findViewById(R.id.spinner_chord_select)).getSelectedItemPosition() + 1;

        // Assign the chord
        currentChordIndex = cH.newChordIndex(chordChoice);

        // Update the chord spinner position if a random chord was selected
        if (chordChoice == 0)
        {
            Spinner dropdown = (Spinner) findViewById(R.id.spinner_chord_select);
            dropdown.setSelection(currentChordIndex - 1);

            // Play the selected chord
            playSelectedChord(null);
        }
    }

    /**
     * Called to compare the user defined chord with the currently selected chord.
     * @param v The calling View
     */
    public void compareChords(View v)
    {
        int[] builtChord = buildChord();
        int[] setChord = cH.getChord(currentChordIndex);

//        cH.playChord(builtChord, setChord.length);

        if (cH.compareChords(builtChord, setChord))
        {
            displayAnswer(v, R.string.correct);
            s.setScore(this, currentChordIndex, true);
        }
        else
        {
            displayAnswer(v, R.string.wrong);
            s.setScore(this, currentChordIndex, false);
        }
    }

    public void displayAnswer(View v, int result)
    {
        // shows if the built chord matches the set chord
        TextView tv = (TextView) this.findViewById(R.id.textview_answer);
        tv.setText(Score.correctChords[currentChordIndex] + " / " + Score.totalChords[currentChordIndex]);
    }

    /**
     * Builds the current chord that the user has defined on the sliders.
     * @return An array containing the root, third, fifth, and option values of the built chord
     */
    public int[] buildChord()
    {
        int root = ((SeekBar) this.findViewById(R.id.slider_root)).getProgress();
        int third = ((SeekBar) this.findViewById(R.id.slider_third)).getProgress();
        int fifth = ((SeekBar) this.findViewById(R.id.slider_fifth)).getProgress();
        int seventh = ((SeekBar) this.findViewById(R.id.slider_option)).getProgress();

        return new int[] {root, third, fifth, seventh};
    }

    /**
     * Plays the currently selected chord.
     * @param v The calling View
     */
    public void playSelectedChord(View v)
    {
        int[] setChord = cH.getChord(currentChordIndex);
        cH.playChord(setChord, setChord.length);
    }

    /**
     * Plays the current chord defined on the sliders.
     * @param v The calling View
     */
    public void playBuiltChord(View v)
    {
        int[] setChord = buildChord();
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
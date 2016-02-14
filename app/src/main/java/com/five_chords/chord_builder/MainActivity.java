/*************************************************************************************************
 * MainActivity.java
 * This class is where most of the activity happened. This is where all the other class and their
 * functions will be called from.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.CheckOptionsFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ScorePageFragment;

public class MainActivity extends AppCompatActivity implements CheckOptionsFragment.OnChordTypeChangeListener
{
    /******************************************************************************
     * This will probably become a fragment holder, with the sidebar/chord classes.
     * Those two will handle the bulk of the code.
     */
    SharedPreferences sPrefs;
    chordHandler cH;
    setUpGUI gui;
    Score s;
    CheckOptionsFragment cof;

    int currentChordIndex;
    static int instrument = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
            Intent intent = new Intent(this,demo.class);
            startActivity(intent);

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
        }

        instrument = 21;

        cH = new chordHandler(this);
        gui = new setUpGUI(this);
        s = new Score(this);

        // Put the heck options in a sliding view
        cof = new CheckOptionsFragment();
        this.findViewById(R.id.fragment_content).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLeft() {
                openOptions();
            }

            public void onSwipeRight() {
                closeOptions();
            }
        });
    }

    /**************************************
     * Called when a new chord is selected.
     * @param v The calling view
     **/
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
            dropdown.setSelection((currentChordIndex) % dropdown.getCount());

            // Play the selected chord
            playSelectedChord(null);
        }
    }

    /*****************************************************************************
     * Called to compare the user defined chord with the currently selected chord.
     * @param v The calling View
     */
    public void compareChords(View v)
    {
        int[] builtChord = buildCurrentChord();
        int[] setChord = cH.getChord(currentChordIndex);

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

    /*******************************************
     * Called to launch the score page fragment.
     * @param v The calling View
     */
    public void launchScorePage(View v)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("score_dialog");

        if (prev != null)
        {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ScorePageFragment.newInstance();
        newFragment.show(ft, "score_dialog");
    }

    public void displayAnswer(View v, int result)
    {
        // shows if the built chord matches the set chord
        Button view = (Button)findViewById(R.id.button_answer);
        view.setText(Score.scores[currentChordIndex].numCorrectGuesses + " / " + Score.scores[currentChordIndex].numTotalGuesses);
    }

    /******************************************************************************************
     * Builds the current chord that the user has defined on the sliders.
     * @return An array containing the root, third, fifth, and option values of the built chord
     */
    public int[] buildCurrentChord()
    {
        int root = ((SeekBar) this.findViewById(R.id.slider_root)).getProgress();
        int third = ((SeekBar) this.findViewById(R.id.slider_third)).getProgress();
        int fifth = ((SeekBar) this.findViewById(R.id.slider_fifth)).getProgress();
        int seventh = ((SeekBar) this.findViewById(R.id.slider_option)).getProgress();

        return new int[] {root, third, fifth, seventh};
    }

    /*************************************
     * Plays the currently selected chord.
     * @param v The calling View
     */
    public void playSelectedChord(View v)
    {
        int[] setChord = cH.getChord(currentChordIndex);
        cH.playChord(setChord, setChord.length, instrument);
    }

    /*************************************************
     * Plays the current chord defined on the sliders.
     * @param v The calling View
     */
    public void playBuiltChord(View v)
    {
        int[] setChord = buildCurrentChord();
        cH.playChord(setChord, setChord.length, instrument);
    }

    /*********************
     * Go to help activity
     * @param v The calling View
     **/
    public void toHelpPage(View v)
    {
        Intent intent = new Intent(this, HelpPage.class);
        startActivity(intent);
    }

    /**********************
     * Opens the Start Page
     **/
    public void openStartPage()
    {
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
    }

    public void switchInstrument(View v)
    {
        if (instrument == 21) {
            instrument = 58;
        } else {
            instrument = 21;
        }
    }

    int optionsAreOpen = 0;
    public void closeOptions()
    {
        if (optionsAreOpen == 1) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.remove(cof);
            ft.commit();

            optionsAreOpen = 0;
        }
    }

    public void openOptions()
    {
        if (optionsAreOpen == 0)
        {
            FragmentTransaction ft = getFragmentManager().beginTransaction();

            ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
            ft.add(R.id.fragment_content, cof);
            ft.commit();

            optionsAreOpen = 1;
        }
    }

    public int getInstrument()
    {
        return instrument;
    }

    /************************************************************************
     * Called when the chord type changes.
     * @param useMajors    Whether or not major chords are now being used
     * @param useMinors    Whether or not minor chords are now being used
     * @param useDominants Whether or not dominant chords are now being used
     */
    @Override
    public void onChordTypeChanged(boolean useMajors, boolean useMinors, boolean useDominants)
    {
        gui.loadSpinners(this, useMajors, useMinors, useDominants);

        // Hide dominant slider if needed
        findViewById(R.id.slider_option).setEnabled(useDominants);
        findViewById(R.id.textview_option).setEnabled(useDominants);
    }
}
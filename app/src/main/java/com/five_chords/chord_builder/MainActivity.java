/*************************************************************************************************
 * MainActivity.java
 * This class is where most of the activity happened. This is where all the other class and their
 * functions will be called from.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.CheckOptionsFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ScorePageFragment;

public class MainActivity extends AppCompatActivity implements CheckOptionsFragment.OnChordTypeChangeListener {
    static chordHandler cH;
    static setUpGUI gui;
    static Score s;
    static CheckOptionsFragment cof;
    static soundHandler sH;
    static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            Intent intent = new Intent(this,demo.class);
            startActivity(intent);

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.apply();
        }

        fm = getFragmentManager();

        cH = new chordHandler();
        gui = new setUpGUI(this);
        s = new Score(this);
        sH = new soundHandler(this);
        cof = new CheckOptionsFragment();
    }

    @Override
    protected void onDestroy() {

        // Make sure sound stops
        if (sH != null)
            sH.stopSound();

        super.onDestroy();
    }

    /**
     * Called to launch the score page fragment.
     * @param v The calling View
     */
    public void launchScorePage(View v) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("score_dialog");

        if (prev != null)
        {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ScorePageFragment.newInstance();
        newFragment.show(ft, "score_dialog");
    }

    public void displayAnswer(Activity activity) {
        // shows if the built chord matches the set chord
        Button view = (Button) activity.findViewById(R.id.button_answer);
        view.setText(s.getNumCorrectGuesses(cH.getCurrentChordIndex()) + " / " + s.getNumTotalGuesses(cH.getCurrentChordIndex()));
    }

    public void updateSpinner(Activity activity) {
        Spinner dropdown = (Spinner) activity.findViewById(R.id.spinner_chord_select);
        dropdown.setSelection((cH.getCurrentChordIndex()) % dropdown.getCount());
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

    int optionsAreOpen = 0;
    public void closeOptions() {
        if (optionsAreOpen == 1) {
            FragmentTransaction ft = fm.beginTransaction();

            ft.remove(cof);
            ft.commit();

            optionsAreOpen = 0;
        }
    }

    public void openOptions() {
        if (optionsAreOpen == 0) {
            FragmentTransaction ft = fm.beginTransaction();

            ft.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim);
            ft.add(R.id.fragment_content, cof);
            ft.commit();

            optionsAreOpen = 1;
        }
    }

    /**
     * Called when the chord type changes.
     * @param useMajors    Whether or not major chords are now being used
     * @param useMinors    Whether or not minor chords are now being used
     * @param useDominants Whether or not dominant chords are now being used
     */
    @Override
    public void onChordTypeChanged(boolean useMajors, boolean useMinors, boolean useDominants) {
        gui.loadSpinners(this, useMajors, useMinors, useDominants);

        // Hide dominant slider if needed
        findViewById(R.id.slider_option).setEnabled(useDominants);
        findViewById(R.id.textview_option).setEnabled(useDominants);
    }
}
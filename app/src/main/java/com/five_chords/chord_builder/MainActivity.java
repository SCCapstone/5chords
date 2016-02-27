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
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.OptionsFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ScorePageFragment;


public class MainActivity extends AppCompatActivity implements OptionsFragment.OnChordTypeChangeListener {
    static chordHandler cH;
    static setUpGUI gui;
    static OptionsFragment cof;
    static soundHandler sH;
    static FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Lock orientation in portrait mode with small screen devices
        if (!getResources().getBoolean(R.bool.isTablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Display demo if needed
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
        sH = new soundHandler(this);
        cof = new OptionsFragment();
        Score.loadScores(this, false);
    }

    @Override
    protected void onDestroy() {

        // Make sure sound stops
        if (sH != null)
            sH.stopSound();

        super.onDestroy();
    }

    /**
     * Called to create the options Menu
     * @param menu The Menu to hold the options menu
     * @return True
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_item_main_options)
            launchCheckOptionsDialog(findViewById(R.id.fragment_content));
        else if (item.getItemId() == R.id.menu_item_main_scores)
            toScorePage(null);
        else if (item.getItemId() == R.id.menu_item_main_about)
            toAboutPage(null);
        else if (item.getItemId() == R.id.menu_item_main_help)
            toHelpPage(null);

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called to launch the check options dialog.
     * @param v The calling View
     */
    public void launchCheckOptionsDialog(View v) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");

        if (prev != null)
        {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = OptionsFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    /**
     * Called to launch the score page fragment.
     * @param v The calling View
     */
    public void launchScorePage(View v) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag("dialog");

        if (prev != null)
        {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ScorePageFragment.newInstance(true, false, true);
        newFragment.show(ft, "dialog");
    }

    public void displayAnswer(Activity activity) {
        // shows if the built chord matches the set chord
        Button view = (Button) activity.findViewById(R.id.button_answer);
        view.setText(Score.getNumCorrectGuesses(cH.getCurrentChordIndex()) + " / " + Score.getNumTotalGuesses(cH.getCurrentChordIndex()));
    }

    public void updateSpinner(Activity activity) {
        Spinner dropdown = (Spinner) activity.findViewById(R.id.spinner_chord_select);
        dropdown.setSelection((cH.getCurrentChordIndex()) % dropdown.getCount());
    }

    /****************************************************************
     * Go to help activity
     * @param view The calling View
     **/
    public void toHelpPage(View view) {
        Intent intent = new Intent(this, HelpPage.class);
        startActivity(intent);
    }

    /**
     * Goes to the Score page.
     * @param view The calling View
     */
    public void toScorePage(View view)
    {
        Intent intent = new Intent(this, Score.ScoreActivity.class);
        startActivity(intent);
    }

    /**
     * Goes to the About page.
     * @param view The calling View
     */
    public void toAboutPage(View view)
    {
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    /****************************************************************
     * Opens the Start Page
     **/
    public void openStartPage() {
        Intent intent = new Intent(this, StartPage.class);
        startActivity(intent);
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
        findViewById(R.id.slider_option_layout).setVisibility(useDominants ? View.VISIBLE : View.GONE);
    }
}
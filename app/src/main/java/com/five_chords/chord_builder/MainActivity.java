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


public class MainActivity extends AppCompatActivity implements OptionsFragment.OptionsChangedListener
{

    /** The current options selected in this MainActivity */
    private static OptionsFragment.Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create Options if needed
        if (options == null)
            options = new OptionsFragment.Options();

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

        setUpGUI.initialize(this);
        chordHandler.initialize();
        soundHandler.initialize(this);
        Score.loadScores(this, false);
    }

    @Override
    protected void onDestroy() {

        // Make sure sound stops
        soundHandler.stopSound();

        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Initialize views from options
        onChordTypeOptionsChanged(options.useMajorChords, options.useMinorChords, options.useDominantChords);
        onHintsOptionsChanged(options.useHints);
    }

    /**
     * Called when the state of this Activity should be saved.
     * @param savedInstanceState The Bundle to which to save
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save options
        options.writeToBundle(savedInstanceState);

        // Call superclass method
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Called when the state of this Activity should be read at start up, provided it was previously saved.
     * @param savedInstanceState The Bundle from which to read
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Call superclass method
        super.onRestoreInstanceState(savedInstanceState);

        // Read options
        if (options == null)
            options = new OptionsFragment.Options();

        options.readFromBundle(savedInstanceState);
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

    /**
     * Called when an item in the menu is selected.
     * @param item The selected item
     * @return super.onOptionsItemSelected()
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.menu_item_main_options)
            launchOptionsDialog(findViewById(R.id.fragment_content));
        else if (item.getItemId() == R.id.menu_item_main_scores)
            toScorePage(null);
        else if (item.getItemId() == R.id.menu_item_main_about)
            toAboutPage(null);
        else if (item.getItemId() == R.id.menu_item_main_help)
            toHelpPage(null);

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called to launch the options dialog.
     * @param v The calling View
     */
    public void launchOptionsDialog(View v)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = OptionsFragment.newInstance(options);
        newFragment.show(ft, "dialog");
    }

    /**
     * Called to launch the score page dialog.
     * @param v The calling View
     */
    public void launchScorePageDialog(View v)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ScorePageFragment.newInstance(true, false, true);
        newFragment.show(ft, "dialog");
    }

    public void displayAnswer() {
        // shows if the built chord matches the set chord
        Button view = (Button) findViewById(R.id.button_answer);
        view.setText(Score.getNumCorrectGuesses(chordHandler.getCurrentChordIndex()) +
                " / " + Score.getNumTotalGuesses(chordHandler.getCurrentChordIndex()));
    }

    public void updateSpinner() {
        Spinner dropdown = (Spinner) findViewById(R.id.spinner_chord_select);
        dropdown.setSelection((chordHandler.getCurrentChordIndex()) % dropdown.getCount());
    }

    /**
     * Goes to Help page.
     * @param view The calling View
     */
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
    public void onChordTypeOptionsChanged(boolean useMajors, boolean useMinors, boolean useDominants) {

        // Update options
        options.useMajorChords = useMajors;
        options.useMinorChords = useMinors;
        options.useDominantChords = useDominants;

        // Update the spinners
        setUpGUI.loadSpinners(this, useMajors, useMinors, useDominants);

        // Hide dominant slider if needed
        findViewById(R.id.slider_option_layout).setVisibility(useDominants ? View.VISIBLE : View.GONE);
    }

    /**
     * Called when the hints options changes.
     * @param useHints Whether or not hints are now enabled.
     */
    @Override
    public void onHintsOptionsChanged(boolean useHints)
    {
        // Update options
        options.useHints = useHints;
    }

}
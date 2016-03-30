/*************************************************************************************************
 * MainActivity.java
 * This class is where most of the activity happened. This is where all the other class and their
 * functions will be called from.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlertDialog;


import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ChordSelectFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.SliderHintView;
import com.five_chords.chord_builder.soundHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements Options.OptionsChangedListener,
        chordHandler.OnChordSelectedListener
{

    /** The current options selected in this MainActivity. */
    private static Options options;

    /** The DrawerLayout containing the navigation pane. */
    private static DrawerLayout mDrawerLayout;

    /** The List of items in the navigation pane. */
    private static ListView mDrawerList;

    /** The Fragment containing the chord build sliders attached to this Activity. */
    private SliderFragment sliderFragment;

    /** The Fragment for selecting chords and instruments attached to this Activity. */
    private ChordSelectFragment chordInstrumentSelectFragment;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Gets the current global Options wrapper, creating a default Options if the global is null.
     *
     * @return The current global Options wrapper
     */
    public static Options getOptions() {
        if (options == null)
            options = new Options();

        return options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ScoreProgressView paint
        ScoreProgressView.initializePaint(this);

        // Create Options if needed
        if (options == null)
            options = new Options();
        options.load(this);
        options.setOptionsChangedListener(this);

        // Lock orientation in portrait mode with small screen devices
        if (!getResources().getBoolean(R.bool.isTablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Load Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ArrayList<String> drawerOptions = new ArrayList<>();
        drawerOptions.add("View History");
        drawerOptions.add("Settings");
        drawerOptions.add("About");
        drawerOptions.add("Help");
        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, R.id.textLabel, drawerOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // Make sure sounds are stopped
                soundHandler.stopSound();
            }
        });

        // Display demo if needed
        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun)
        {
            Intent intent = new Intent(this, demo.class);
            startActivity(intent);

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.apply();
        }

        // Initialize Static Classes
        chordHandler.initialize();
        chordHandler.setOnChordSelectedListener(this);
        soundHandler.initialize(this);
        soundHandler.switchInstrument(options.instrument);
        Score.loadScores(this, false);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        // Make sure sound stops
        soundHandler.stopSound();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get references to fragments
        sliderFragment = (SliderFragment)getFragmentManager().findFragmentById(R.id.fragment_sliders);
        chordInstrumentSelectFragment =
                (ChordSelectFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_select);
    }

    /**
     * Called when the state of this Activity should be saved.
     *
     * @param savedInstanceState The Bundle to which to save
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save options
        options.save(this);

        // Call superclass method
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Called when the state of this Activity should be read at start up, provided it was previously saved.
     *
     * @param savedInstanceState The Bundle from which to read
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Call superclass method
        super.onRestoreInstanceState(savedInstanceState);

        // Read options
        if (options == null)
            options = new Options();
        options.load(this);
        options.setOptionsChangedListener(this);
    }

    /**
     * Gets the SliderFragment within this MainActivity.
     * @return The SliderFragment within this MainActivity
     */
    public SliderFragment getSliderFragment() {
        return sliderFragment;
    }

    /**
     * Called to update the displayed score.
     */
    public void updateDisplayedScore() {

        // Get the TextView
        TextView currentProgress = (TextView) findViewById(R.id.textview_score_display);

        // Get the current Score
        Score current = Score.getCurrentScore();

        // Set the Views
        currentProgress.setText(current.getCurrentValue().getDisplayString());

//        // Update the Chord Select Spinner
//        chordInstrumentSelectFragment.updateDisplayedChord();
    }

    /**
     * Goes to Help page.
     */
    public void toHelpPage() {
        Intent intent = new Intent(this, HelpPage.class);
        startActivity(intent);
    }

    /**
     * Goes to the About page.
     */
    public void toAboutPage() {
        Intent intent = new Intent(this, AboutPage.class);
        startActivity(intent);
    }

    /**
     * Goes to the Settings page.
     */
    public void toSettingsPage() {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    /**
     * Goes to the Score page.
     */
    public void toScorePage() {
        Intent intent = new Intent(this, ScorePage.class);
        startActivity(intent);
    }

    /**
     * Generates one instance of the given type of hint.
     *
     * @param type The Hint type
     */
    public void makeHint(final byte type) {
        // Calculate the chord differences
        final Note[] builtChord = chordHandler.getCurrentBuiltChordSpelling();
        final Note[] selectedChord = chordHandler.getCurrentSelectedChordSpelling();

        // Add hints
        SliderHintView view;

        // Root slider
        view = (SliderHintView) findViewById(R.id.slider_root_layout);
        view.setHint(type, builtChord[0], selectedChord[0], sliderFragment, 500L);

        // Third slider
        view = (SliderHintView) findViewById(R.id.slider_third_layout);
        view.setHint(type, builtChord[1], selectedChord[1], sliderFragment, 500L);

        // Fifth slider
        view = (SliderHintView) findViewById(R.id.slider_fifth_layout);
        view.setHint(type, builtChord[2], selectedChord[2], sliderFragment, 500L);

        // Option slider
        if (chordHandler.getCurrentSelectedChord().TYPE.offsets.length == 4)
        {
            view = (SliderHintView) findViewById(R.id.slider_option_layout);
            view.setHint(type, builtChord[3], selectedChord[3], sliderFragment, 500L);
        }
    }

    /**
     * Called when a new chord is selected.
     */
    @Override
    public void onChordSelected(boolean random) {
        // Update current chord score
        updateDisplayedScore();

        // Update slider bounds
        sliderFragment.setSliderBoundsToFitChord(chordHandler.getCurrentSelectedChordSpelling());

        // Update ChordInstrumentSelectFragment
        chordInstrumentSelectFragment.setDisplayedChord(chordHandler.getCurrentSelectedChord(), random);

        // Hide fourth slider if needed
        if (chordHandler.getCurrentSelectedChord().getNumNotes() != 4)
            findViewById(R.id.slider_option_layout).setVisibility(View.GONE);
        else
            findViewById(R.id.slider_option_layout).setVisibility(View.VISIBLE);
    }

    /**
     * Called when the chord type changes.
     *
     * @param chordTypesInUse A List containing the ChordTypes that are now in use
     */
    @Override
    public void onChordTypeOptionsChanged(List<Chord.ChordType> chordTypesInUse) {
        // Save options
        options.save(this);

        // Update the spinners
        chordInstrumentSelectFragment.updateAvailableChordTypes(this);
    }

    /**
     * Called when the hints options changes.
     *
     * @param useHints Whether or not hints are now enabled.
     */
    @Override
    public void onHintsOptionsChanged(boolean useHints) {
        // Save options
        options.save(this);
    }

    /**
     * Called when the instrument selection changes.
     * @param instrument The new instrument
     */
    @Override
    public void onInstrumentChanged(int instrument) {
        // Save options
        options.save(this);
        soundHandler.switchInstrument(instrument);
    }

    /**
     * Called when the chord inversion selection changes.
     * @param inversions The new inversion selection
     */
    @Override
    public void onInversionSelectionChanged(List<Byte> inversions){
        // Save options
        options.save(this);
    }

    /**
     * Called when the pitch bend settings change.
     * @param incrementsPerNote The new increments per note
     * @param maxCheckError The new max check error
     */
    @Override
    public void onPitchBendSettingsChanged(int incrementsPerNote, double maxCheckError){
        // Update SliderFragment
        sliderFragment.setSliderBoundsToFitChord(chordHandler.getCurrentSelectedChordSpelling());

        // Save options
        options.save(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.five_chords.chord_builder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.five_chords.chord_builder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * Called when the user attempts to back out of the MainActivity to launch a dialog
     * confirming this action.
     */
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Class for listening for clicks on the Options drawer.
     */
    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switch (position) {
                case 0: toScorePage();
                    break;
                case 1: toSettingsPage();
                    break;
                case 2: toAboutPage();
                    break;
                case 3: toHelpPage();
                    break;
            }
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}
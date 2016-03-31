package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.content.DialogInterface;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.app.AlertDialog;


import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.CheckFragment;
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
import android.widget.Toast;

/**
 * The Main Activity. This class contains callbacks to handle the majority of the
 * applications functionality as well handles to some of the main fragments.
 * MainActivity also contains and maintains the global use Options object and hosts
 * the navigation pane.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class MainActivity extends AppCompatActivity implements Options.OptionsChangedListener,
        chordHandler.OnChordSelectedListener, View.OnClickListener
{
    /** The current options selected in this MainActivity. */
    private static Options options;

    /** The DrawerLayout containing the navigation pane. */
    private DrawerLayout mDrawerLayout;

    /** The List of items in the navigation pane. */
    private ListView mDrawerList;

    /** The Fragment containing the chord build sliders attached to this Activity. */
    private SliderFragment sliderFragment;

    /** The Fragment for selecting chords and instruments attached to this Activity. */
    private ChordSelectFragment chordInstrumentSelectFragment;

    /** The Fragment containing the chord check button. */
    private CheckFragment checkFragment;

    /** The Fragment containing the chord selection interface. */
    private ChordSelectFragment chordSelectFragment;

    /** The client to handle contacting the developers. */
    private GoogleApiClient client;

    /** Thread to use for chord playback when the user guesses incorrectly. */
    private Thread playbackThread;

//    /** The chord playback functionality. */
//    private Runnable playBackFunction = new Runnable()
//    {
//        @Override
//        public void run()
//        {
//
//        }
//    };

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

    /**
     * Called to programmatically press a Button.
     * @param button The Button to press
     * @param press Whether to press or release the Button
     */
    public static void pressButton(Button button, boolean press)
    {
        MotionEvent motionEvent = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                press ? MotionEvent.ACTION_DOWN : MotionEvent.ACTION_UP,
                0.0f, 0.0f, 1.0f, 1.0f, 0, 0.0f, 0.0f, 0, 0
        );
        button.dispatchTouchEvent(motionEvent);
    }

    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
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

        // Create the client to handle contacting the developers
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Called when this Activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        // Make sure sound stops
        soundHandler.stopSound();

        super.onDestroy();
    }

    /**
     * Called when this Activity is started.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Setup the system for contacting the developers
        client.connect();
        Action viewAction = Action.newAction(Action.TYPE_VIEW, "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.five_chords.chord_builder/http/host/path"));
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    /**
     * Called when this Activity is stopped.
     */
    @Override
    public void onStop() {
        super.onStop();

        // Clean up the system for contacting the developers
        Action viewAction = Action.newAction(Action.TYPE_VIEW, "Main Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.five_chords.chord_builder/http/host/path"));
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * Called when this Activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Set listener to Views
        findViewById(R.id.MainLayout).setOnClickListener(this);

        // Get references to fragments
        sliderFragment = (SliderFragment)getFragmentManager().findFragmentById(R.id.fragment_sliders);
        chordInstrumentSelectFragment =
                (ChordSelectFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_select);
        checkFragment = (CheckFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_check);
        chordSelectFragment = (ChordSelectFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_select);
    }

    /**
     * Called when this Activity is paused.
     */
    @Override
    protected void onPause()
    {
        super.onPause();

        // Stop the playback thread if needed
        if (playbackThread != null && playbackThread.isAlive())
            playbackThread.interrupt();
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
     * Called to update the displayed score.
     */
    public void updateDisplayedScore() {

        // Get the TextView
        TextView currentProgress = (TextView) findViewById(R.id.textview_score_display);

        // Get the current Score
        Score current = Score.getCurrentScore();

        // Set the Views
        currentProgress.setText(current.getCurrentValue().getDisplayString());
    }

    /**
     * Shows the chord sequence.
     */
    public void showChordSequence()
    {
        // Stop the playback thread if needed
        if (playbackThread != null && playbackThread.isAlive())
            playbackThread.interrupt();

        playbackThread = new PlaybackSequence();
        playbackThread.start();
    }

    /**
     * Shows the chord check result dialog.
     */
    public void showChordCheckResult()
    {
        // Make sure the current Chord is built
        chordHandler.buildCurrentChord(this);

        // Test correctness
        boolean isCorrect = chordHandler.testCurrentChords();

        // Set the score
        Score.getCurrentScore().update(this, isCorrect);
        updateDisplayedScore();

        // Handle result TODO add sounds for right and wrong
        if (isCorrect)
        {
            // Launch dialog
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.thats_correct))
                    .setMessage("Do you want to try another chord?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            chordHandler.getRandomChord();
                            getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            chordHandler.setSelectedChord(chordHandler.getCurrentSelectedChord(), false); // Resets the wrong streak counter
                            getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();
                        }

                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            getSliderFragment().resetChordSliders();
                            soundHandler.stopSound();
                        }
                    })
                    .show();
        }
        else
        {
            // Show hints if needed
            chordHandler.makeHints(this);

            // Show toast
            Toast toast = Toast.makeText(this, this.getString(R.string.thats_incorrect), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
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
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v)
    {
        if (playbackThread != null && !playbackThread.isInterrupted())
            playbackThread.interrupt();
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

        // Reset chord sliders
        sliderFragment.resetChordSliders();

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

    /**
     * A Thread to handle the playback sequence.
     */
    private class PlaybackSequence extends Thread
    {
        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run()
        {
            // Sleep
            try {Thread.sleep(50L);} catch (InterruptedException e)
            {
                onEnd();
                return;
            }

            // Play Built chord
            MainActivity.this.checkFragment.playBuiltChord(true);

            // Sleep
            try {Thread.sleep(1000L);} catch (InterruptedException e)
            {
                MainActivity.this.checkFragment.playBuiltChord(false);
                onEnd();
                return;
            }

            // Stop Built chord
            MainActivity.this.checkFragment.playBuiltChord(false);

            // Sleep
            try {Thread.sleep(250L);} catch (InterruptedException e)
            {
                onEnd();
                return;
            }

            // Play Correct chord
            MainActivity.this.chordSelectFragment.playSelectedChord(true);

            // Sleep
            try {Thread.sleep(1000L);} catch (InterruptedException e)
            {
                MainActivity.this.chordSelectFragment.playSelectedChord(false);
                onEnd();
                return;
            }

            // Stop Correct chord
            MainActivity.this.chordSelectFragment.playSelectedChord(false);

            onEnd();
        }

        /**
         * Called when this PlaybackSequence ends.
         */
        public void onEnd()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    showChordCheckResult();
                }
            });
        }
    }
}
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.SystemClock;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.AboutPageFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.AlertFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ChordSelectFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.HelpPageFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.MainFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ScorePageFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SettingsPageFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;
import com.five_chords.chord_builder.SoundHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Main Activity. This class contains callbacks to handle the majority of the
 * applications functionality as well handles to some of the main fragments.
 * MainActivity also contains and maintains the global use Options object and hosts
 * the navigation pane.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class MainActivity extends FragmentActivity implements Options.OptionsChangedListener,
        FragmentManager.OnBackStackChangedListener
{
    /** The tag for this class. */
    private static final String TAG = "MainActivity";

    /** The Bundle id of the drawer index field. */
    private static final String DRAWER_INDEX_BUNDLE_ID = "MainActivity.drawerIndex";

    /** The Bundle id of the drawer fragment Bundles. */
    private static final String DRAWER_BUNDLE_BUNDLE_ID = "MainActivity.drawerBundle";

    /** The current options selected in this MainActivity. */
    private static Options options;

    /** The index of the currently selected navigation drawer. */
    private int drawerIndex;

    /** The current fragment selected by the navigation drawer. */
    private Fragment drawerFragment;

    /** Map containing the Bundle arguments for each Fragment available in the navigation drawer. */
    private Map<Integer, Bundle> drawerFragmentArguments;

    /** The DrawerLayout containing the navigation pane. */
    private DrawerLayout mDrawerLayout;

    /** The List of items in the navigation pane. */
    private ListView mDrawerList;

    /** The client to handle contacting the developers. */
    private GoogleApiClient client;

    /** Enum containing the fragments available in the navigation drawer. */
    public enum DrawerFragment
    {
        MAIN (R.string.drawer_frag_build, MainFragment.class),
        HISTORY (R.string.drawer_frag_history, ScorePageFragment.class),
        SETTINGS (R.string.drawer_frag_setting, SettingsPageFragment.class),
        ABOUT (R.string.drawer_frag_about, AboutPageFragment.class),
        HELP (R.string.drawer_frag_help, HelpPageFragment.class);

        /** String resource id for the name of this Drawer */
        public final int NAME_RES_ID;

        // Fragment represented by this drawer
        private final Class<? extends Fragment> FRAGMENT_CLASS;

        /**
         * Creates a DrawerFragment.
         */
        DrawerFragment(@StringRes int nameResId, Class<? extends Fragment> fragmentClass)
        {
            NAME_RES_ID = nameResId;
            FRAGMENT_CLASS = fragmentClass;
        }
    }

    /**
     * Gets the current global Options wrapper, creating a default Options if the global is null.
     *
     * @return The current global Options wrapper
     */
    public static Options getOptions()
    {
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

//    /**
//     * Gets the current Fragment selected in the navigation drawer.
//     * @return The current Fragment selected in the navigation drawer
//     */
//    public Fragment getCurrentDrawerFragment()
//    {
//        return drawerFragment;
//    }

    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.content_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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

        // Create Navigation Drawer fragment argument map
        drawerFragmentArguments = new HashMap<>();

        // Load drawer fragment arguments
        if (savedInstanceState != null)
        {
            Bundle argument;
            for (int i = 0; i < DrawerFragment.values().length; ++i)
            {
                argument = savedInstanceState.getBundle(DRAWER_BUNDLE_BUNDLE_ID + i);

                if (argument != null)
                    drawerFragmentArguments.put(i, argument);
            }
        }

        // Create Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ArrayList<String> drawerOptions = new ArrayList<>();

        for (DrawerFragment drawerFragment: DrawerFragment.values())
            drawerOptions.add(getString(drawerFragment.NAME_RES_ID));

        mDrawerList.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, R.id.textLabel, drawerOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // Make sure sounds are stopped
                stopAllSound();
            }
        });

        // Load saved drawer
        if (savedInstanceState != null)
            drawerIndex = savedInstanceState.getInt(DRAWER_INDEX_BUNDLE_ID);
        else
            drawerIndex = 0;

        setCurrentDrawer(drawerIndex, true);

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
        ChordHandler.initialize();
        SoundHandler.switchInstrument(options.instrument);
        Score.loadScores(this, false);

        // Set back stack listener
        getFragmentManager().addOnBackStackChangedListener(this);

        // Create the client to handle contacting the developers
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * Called when this Activity is destroyed.
     */
    @Override
    protected void onDestroy()
    {
        // Remove listeners
        options.setOptionsChangedListener(null);
        ChordHandler.setOnChordSelectedListener(null);
        mDrawerList.setOnItemClickListener(null);
        mDrawerLayout.setOnFocusChangeListener(null);

        super.onDestroy();
    }

    /**
     * Called when this Activity is started.
     */
    @Override
    public void onStart()
    {
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
    public void onStop()
    {
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
    protected void onResume()
    {
        super.onResume();

//        // Get references to fragments
//        sliderFragment = (SliderFragment)getFragmentManager().findFragmentById(R.id.fragment_sliders);
//        chordInstrumentSelectFragment =
//                (ChordSelectFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_select);
//        checkFragment = (CheckFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_check);
//        chordSelectFragment = (ChordSelectFragment)getFragmentManager().findFragmentById(R.id.fragment_chord_select);
    }


    /**
     * Called when the state of this Activity should be saved.
     *
     * @param savedInstanceState The Bundle to which to save
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        // Save options
        options.save(this);

        // Save drawer fragment arguments
        for (Map.Entry<Integer, Bundle> entry: drawerFragmentArguments.entrySet())
        {
            savedInstanceState.putBundle(DRAWER_BUNDLE_BUNDLE_ID + entry.getKey(), entry.getValue());
        }

        // Call superclass method
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Called when the state of this Activity should be read at start up, provided it was previously saved.
     *
     * @param savedInstanceState The Bundle from which to read
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        // Call superclass method
        super.onRestoreInstanceState(savedInstanceState);

        // Read options
        if (options == null)
            options = new Options();
        options.load(this);
        options.setOptionsChangedListener(this);

        // Load drawer fragment arguments
        Bundle argument;
        for (int i = 0; i < DrawerFragment.values().length; ++i)
        {
            argument = savedInstanceState.getBundle(DRAWER_BUNDLE_BUNDLE_ID + i);

            if (argument != null)
                drawerFragmentArguments.put(i, argument);
        }
    }

    /**
     * Called when the chord type changes.
     *
     * @param chordTypesInUse A List containing the ChordTypes that are now in use
     */
    @Override
    public void onChordTypeOptionsChanged(List<Chord.ChordType> chordTypesInUse)
    {
        // Save options
        options.save(this);

        // Update the spinners
        if (drawerFragment != null && drawerFragment instanceof MainFragment)
        {
            ChordSelectFragment chordSelectFragment = ((MainFragment)drawerFragment).getChordSelectFragment();

            if (chordSelectFragment != null)
                chordSelectFragment.updateAvailableChordTypes(this);
        }
    }

    /**
     * Called when the hints options changes.
     *
     * @param useHints Whether or not hints are now enabled.
     */
    @Override
    public void onHintsOptionsChanged(boolean useHints)
    {
        // Save options
        options.save(this);
    }

    /**
     * Called when the showAnswerSequence flag changes.
     * @param showAnswerSeq The new value of the showAnswerSequence flag
     */
    @Override
    public void onShowAnswerSequenceChanged(boolean showAnswerSeq)
    {
        // Save options
        options.save(this);
    }

    /**
     * Called when the instrument selection changes.
     * @param instrument The new instrument
     */
    @Override
    public void onInstrumentChanged(int instrument)
    {
        // Save options
        options.save(this);
        SoundHandler.switchInstrument(instrument);
    }

    /**
     * Called when the chord inversion selection changes.
     * @param inversions The new inversion selection
     */
    @Override
    public void onInversionSelectionChanged(List<Byte> inversions)
    {
        // Save options
        options.save(this);
    }

    /**
     * Called when the pitch bend settings change.
     * @param incrementsPerNote The new increments per note
     * @param maxCheckError The new max check error
     */
    @Override
    public void onPitchBendSettingsChanged(int incrementsPerNote, double maxCheckError)
    {
        // Update SliderFragment
        if (drawerFragment != null && drawerFragment instanceof MainFragment)
        {
            SliderFragment sliderFragment = ((MainFragment)drawerFragment).getSliderFragment();

            if (sliderFragment != null)
                sliderFragment.setSliderBoundsToFitChord(ChordHandler.getCurrentSelectedChordSpelling());
        }

        // Save options
        options.save(this);
    }

    /**
     * Called to stop all playback
     */
    public void stopAllSound()
    {
        if (drawerFragment != null && drawerFragment instanceof MainFragment)
        {
            MainFragment mainFragment = (MainFragment)drawerFragment;

            if (mainFragment.getChordSelectFragment() != null)
                mainFragment.getChordSelectFragment().silenceButtons();
            if (mainFragment.getSliderFragment() != null)
                mainFragment.getSliderFragment().silenceSliders();
            if (mainFragment.getCheckFragment() != null)
                mainFragment.getCheckFragment().silenceButtons();
        }
    }

    /**
     * Called when the user attempts to back out of the MainActivity to launch a dialog
     * confirming this action.
     */
    @Override
    public void onBackPressed()
    {
        if (getFragmentManager().getBackStackEntryCount() > 1)
        {
            getFragmentManager().popBackStack();
        }
        else
        {
            // Launch dialog
            AlertFragment alert = AlertFragment.newInstance(R.string.exit, R.string.exit_dialog_message);

            alert.setYesAction(new Runnable()
            {
                @Override
                public void run()
                {
                    stopAllSound();
                    MainActivity.this.superBackPressed();
                }
            });

            alert.setDismissAction(new Runnable()
            {
                @Override
                public void run()
                {
                    stopAllSound();
                }
            });

            alert.show(getFragmentManager(), "alert");
        }
    }

    /**
     * Used as a handle to the super version of onBackPressed for the alert dialog.
     */
    private void superBackPressed()
    {
        super.onBackPressed();
    }

    /**
     * Swaps fragments in the main content view.
     * @param position The selected position
     * @param firstTime True if this is the first transaction to occue
     */
    public void setCurrentDrawer(int position, boolean firstTime)
    {
        // Don't go to the fragment if you are already there
        if (!firstTime && position == drawerIndex)
        {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }

        // Select the new Fragment to show based on selected position
        Fragment fragment;

        try
        {
            fragment = DrawerFragment.values()[position].FRAGMENT_CLASS.newInstance();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error setting current navigation drawer: " + e.getMessage());
            return;
        }

        // Update drawer index and current fragment
        drawerIndex = position;
        drawerFragment = fragment;

        // Set Fragment arguments
        Bundle arguments = drawerFragmentArguments.get(drawerIndex);

        if (arguments == null)
        {
            arguments = new Bundle();
            drawerFragmentArguments.put(drawerIndex, arguments);
        }

        drawerFragment.setArguments(arguments);

        // Insert the fragment by replacing any existing fragments
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged()
    {
        // Get the new fragment in the content layout
        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_main);

        // Update the current fragment index and reference
        for (DrawerFragment drawerFragment: DrawerFragment.values())
        {
            if (drawerFragment.FRAGMENT_CLASS.isInstance(fragment))
            {
                this.drawerFragment = fragment;
                drawerIndex = drawerFragment.ordinal();
            }
        }
    }

    /**
     * Class for listening for clicks on the Options drawer.
     */
    public class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id)
        {
            setCurrentDrawer(position, false);
        }
    }
}
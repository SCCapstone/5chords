package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.five_chords.chord_builder.R;

/**
 * Activity containing the App settings user interface. This interface contains buttons to navigate to several sub
 * activities where particular settings can be changed.
 * @version 1.0
 * @date 2 April 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsPage extends Activity
{
    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Inflate the layout for this fragment
        ListView view = (ListView) findViewById(R.id.settings_content);

        // Populate the list
        ArrayAdapter<SettingsOption> optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        optionsAdapter.add(CHOOSE_CHORD_TYPES);
        optionsAdapter.add(CHOOSE_CHORD_OPTIONS);
        optionsAdapter.add(CHOOSE_CHECK_OPTIONS);
        optionsAdapter.add(INSTRUMENT_OPTIONS);

        // Set click listener
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);

                if (obj instanceof SettingsOption)
                    ((SettingsOption) obj).performAction();
            }
        });

        // Get the list view of settings
        view.setAdapter(optionsAdapter);
    }

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        finish();
    }

    /**
     * The SettingsOption for choosing chords.
     */
    private final SettingsOption CHOOSE_CHORD_TYPES = new SettingsOption("Choose Chords")
    {
        @Override
        public void performAction()
        {
            toChordSettings();
        }
    };

    /**
     * The SettingsOption for choosing the chord options.
     */
    private final SettingsOption CHOOSE_CHORD_OPTIONS = new SettingsOption("Chord Options") {
        @Override
        public void performAction() {
            toChordOptions();
        }
    };

    /**
     * The SettingsOption for choosing the check options.
     */
    private final SettingsOption CHOOSE_CHECK_OPTIONS = new SettingsOption("Check Options")
    {
        @Override
        public void performAction()
        {
            toCheckOptions();
        }
    };

    /**
     * The SettingsOption for changing the instrument.
     */
    private final SettingsOption INSTRUMENT_OPTIONS = new SettingsOption("Change Instrument")
    {
        @Override
        public void performAction()
        {
            toInstrumentSelection();
        }
    };

    /**
     * Called to launch the choose chords dialog.
     */
    public void toChordSettings()
    {
        Intent intent = new Intent(this, SettingsChords.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Called to goto the choose chord options.
     */
    public void toChordOptions()
    {
        Intent intent = new Intent(this, SettingsChordOptions.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Called to goto the choose check options.
     */
    public void toCheckOptions()
    {
        Intent intent = new Intent(this, CheckSettingsActivity.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Called to goto the instrument selection activity.
     */
    public void toInstrumentSelection() {
        Intent intent = new Intent(this, SettingsInstruments.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Class representing an object in the SettingsPage list of options.
     */
    public static abstract class SettingsOption
    {
        /** The name of the SettingsOption. */
        public String name;

        /**
         * Constructs a new SettingsOption.
         * @param name The name of the SettingsOption
         */
        public SettingsOption(String name)
        {
            this.name = name;
        }

        /**
         * Called to perform the action of this SettingsOption.
         */
        public abstract void performAction();

        /**
         * Gets a String representation of this SettingsObject.
         * @return A String representation of this SettingsObject
         */
        @Override
        public String toString()
        {
            return name;
        }
    }
}

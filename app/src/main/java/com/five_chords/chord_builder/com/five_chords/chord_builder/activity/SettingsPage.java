/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.HintSettingsFragment;

import com.five_chords.chord_builder.R;

public class SettingsPage extends Activity
{
    private ArrayAdapter<SettingsOption> optionsAdapter;

    /**
     * Activity Creator
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        // Inflate the layout for this fragment
        ListView view = (ListView) findViewById(R.id.settings_content);

        // Populate the list
        optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        optionsAdapter.add(EDIT_USER_LEVEL_OPTIONS);
        optionsAdapter.add(CHOOSE_CHORD_TYPES);
        optionsAdapter.add(CHOOSE_CHORD_OPTIONS);
        optionsAdapter.add(EDIT_HINTS_OPTIONS);
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
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * The MainActivity call
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

    private final SettingsOption CHOOSE_CHORD_OPTIONS = new SettingsOption("Chord Options") {
        @Override
        public void performAction() {
            toChordOptions();
        }
    };

    /**
     * The SettingsOption for editing hints.
     */
    private final SettingsOption EDIT_HINTS_OPTIONS = new SettingsOption("Edit Hint Settings")
    {
        @Override
        public void performAction()
        {
            toHintOptions();
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
     * The SettingsOption for enabling/disabling chord inversions.
     */
    private final SettingsOption EDIT_USER_LEVEL_OPTIONS = new SettingsOption("User Level - WIP")
    {
        @Override
        public void performAction()
        {
            launchUserLevelDialog();
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
     * Called to launch the choose chord options.
     */
    public void toChordOptions()
    {
        Intent intent = new Intent(this, SettingsChordOptions.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Called to launch the edit hints dialog.
     */
    public void toHintOptions()
    {
        Intent intent = new Intent(this, HintSettingsFragment.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    /**
     * Launches the Instrument select
     */
    public void toInstrumentSelection() {
        Intent intent = new Intent(this, SettingsInstruments.class);
        startActivity(intent);
        this.overridePendingTransition(0, 0);
    }

    public void launchUserLevelDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        // Replace this with user level dialog class
        //DialogFragment newFragment = changeInstrumentFragment.newInstance();
        //newFragment.show(ft, "dialog");
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

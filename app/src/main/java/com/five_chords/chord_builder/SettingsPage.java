/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.ChordChooseFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.HintSettingsFragment;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.changeInstrumentFragment;

public class SettingsPage extends Activity
{
    private ArrayAdapter<SettingsOption> optionsAdapter;
    private Activity activity;

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

        optionsAdapter.add(CHOOSE_CHORDS_OPTIONS);
        optionsAdapter.add(EDIT_HINTS_OPTIONS);
        optionsAdapter.add(CLEAR_SCORES_OPTIONS);
        EDIT_PITCH_OPTIONS.name = (MainActivity.getOptions().usePitchBending)
                                ? "Pitch Bending is Enabled"
                                : "Pitch Bending is Disabled";
        optionsAdapter.add(EDIT_PITCH_OPTIONS);
        EDIT_INVERSION_OPTIONS.name = (MainActivity.getOptions().useInversion)
                                    ? "Chord Inversions are Enabled - WIP"
                                    : "Chord Inversions are Disabled - WIP";
        optionsAdapter.add(EDIT_INVERSION_OPTIONS);
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

        activity = this;
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
    private final SettingsOption CHOOSE_CHORDS_OPTIONS = new SettingsOption("Choose Chords")
    {
        @Override
        public void performAction()
        {
            launchChooseChordsDialog();
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
            launchEditHintsDialog();
        }
    };

    /**
     * The SettingsOption for clearing the scores.
     */
    private final SettingsOption CLEAR_SCORES_OPTIONS = new SettingsOption("Clear Scores")
    {
        @Override
        public void performAction()
        {
            Score.resetScores(activity);
        }
    };

    /**
     * The SettingsOption for enabling/disabling pitch bending.
     */
    private final SettingsOption EDIT_PITCH_OPTIONS = new SettingsOption("Pitch Bending is Enabled")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Pitch Bending is Enabled") {
                this.name = "Pitch Bending is Disabled";
                MainActivity.getOptions().changePitchOptions(false);
            } else {
                this.name = "Pitch Bending is Enabled";
                MainActivity.getOptions().changePitchOptions(true);
            }

            optionsAdapter.notifyDataSetChanged();
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
            launchInstrumentDialog();
        }
    };

    /**
     * The SettingsOption for enabling/disabling chord inversions.
     */
    private final SettingsOption EDIT_INVERSION_OPTIONS = new SettingsOption("Chord Inversions are Disabled - WIP")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Chord Inversions are Disabled - WIP") {
                this.name = "Chord Inversions are Enabled - WIP";
                MainActivity.getOptions().changeInversionOptions(true);
            } else {
                this.name = "Chord Inversions are Disabled - WIP";
                MainActivity.getOptions().changeInversionOptions(false);
            }

            optionsAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Called to launch the choose chords dialog.
     */
    public void launchChooseChordsDialog()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ChordChooseFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    /**
     * Called to launch the edit hints dialog.
     */
    public void launchEditHintsDialog()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = HintSettingsFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public void launchInstrumentDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = changeInstrumentFragment.newInstance();
        newFragment.show(ft, "dialog");
    }

    public void closeInstrumentDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");

        if (prev != null)
            ft.remove(prev);

        ft.addToBackStack(null);
    }

    /**
     * Class representing an object in the SettingsPage list of options.
     */
    public abstract class SettingsOption
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

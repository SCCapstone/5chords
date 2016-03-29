/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.five_chords.chord_builder.R;

public class SettingsChords extends Activity
{
    private ArrayAdapter<SettingsOption> optionsAdapter;
    private Activity activity;

    /** The chords the user wants to use */
    public boolean useMajorChords;
    public boolean useMinorChords;
    public boolean useDominantChords;
    public boolean useAugmentedChords;

    /**
     * Activity Creator
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chords);

        // Inflate the layout for this fragment
        ListView view = (ListView) findViewById(R.id.settings_content);

        // set chord type selections
        useMajorChords = MainActivity.getOptions().chordTypesInUseArray[0];
        useMinorChords = MainActivity.getOptions().chordTypesInUseArray[1];
        useDominantChords = MainActivity.getOptions().chordTypesInUseArray[2];
        useAugmentedChords = MainActivity.getOptions().chordTypesInUseArray[3];

        // Populate the list
        optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        MAJOR_CHORD_OPTION.name = (useMajorChords)
                                ? "Major Triad Chords are Enabled"
                                : "Major Triad Chords are Disabled";
        optionsAdapter.add(MAJOR_CHORD_OPTION);

        MINOR_CHORD_OPTION.name = (useMinorChords)
                                ? "Minor Triad Chords are Enabled"
                                : "Minor Triad Chords are Disabled";
        optionsAdapter.add(MINOR_CHORD_OPTION);

        DOMINANT_CHORD_OPTION.name = (useDominantChords)
                                   ? "Dominant Seventh Chords are Enabled"
                                   : "Dominant Seventh Chords are Disabled";
        optionsAdapter.add(DOMINANT_CHORD_OPTION);

        AUGMENTED_CHORD_OPTION.name = (useAugmentedChords)
                                    ? "Augmented Triad Chords are Enabled"
                                    : "Augmented Triad Chords are Disabled";
        optionsAdapter.add(AUGMENTED_CHORD_OPTION);

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
        MainActivity.getOptions().setChordTypeUse(0, useMajorChords);
        MainActivity.getOptions().setChordTypeUse(1, useMinorChords);
        MainActivity.getOptions().setChordTypeUse(2, useDominantChords);
        MainActivity.getOptions().setChordTypeUse(3, useAugmentedChords);
        finish();
    }

    /**
     * The SettingsOption for using major chords.
     */
    private final SettingsOption MAJOR_CHORD_OPTION = new SettingsOption("Major Triad Chords are Enabled")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Major Triad Chords are Disabled") {
                this.name = "Major Triad Chords are Enabled";
                useMajorChords = true;
            } else {
                useMajorChords = false;
                if (isValidSelection())
                    this.name = "Major Triad Chords are Disabled";
                else
                    useMajorChords = true;
            }

            optionsAdapter.notifyDataSetChanged();
        }
    };

    /**
     * The SettingsOption for using minor chords.
     */
    private final SettingsOption MINOR_CHORD_OPTION = new SettingsOption("Minor Triad Chords are Enabled")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Minor Triad Chords are Disabled") {
                this.name = "Minor Triad Chords are Enabled";
                useMinorChords = true;
            } else {
                useMinorChords = false;
                if (isValidSelection())
                    this.name = "Minor Triad Chords are Disabled";
                else
                  useMinorChords = true;
            }

            optionsAdapter.notifyDataSetChanged();
        }
    };

    /**
     * The SettingsOption for using dominant chords.
     */
    private final SettingsOption DOMINANT_CHORD_OPTION = new SettingsOption("Dominant Seventh Chords are Enabled")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Dominant Seventh Chords are Disabled") {
                this.name = "Dominant Seventh Chords are Enabled";
                useDominantChords = true;
            } else {
                useDominantChords = false;
                if (isValidSelection())
                    this.name = "Dominant Seventh Chords are Disabled";
                else
                    useDominantChords = true;
            }

            optionsAdapter.notifyDataSetChanged();
        }
    };


    /**
     * The SettingsOption for using augmented chords.
     */
    private final SettingsOption AUGMENTED_CHORD_OPTION = new SettingsOption("Augmented Triad Chords are Enabled")
    {
        @Override
        public void performAction()
        {
            if (this.name == "Augmented Triad Chords are Disabled") {
                this.name = "Augmented Triad Chords are Enabled";
                useAugmentedChords = true;
            } else {
                useAugmentedChords = false;
                if (isValidSelection())
                    this.name = "Augmented Triad Chords are Disabled";
                else
                    useAugmentedChords = true;
            }

            optionsAdapter.notifyDataSetChanged();
        }
    };

    private boolean isValidSelection() {
        if (useMajorChords || useMinorChords || useDominantChords || useAugmentedChords)
        {
            return true;
        }
        else
        {
            // Show toast
            Toast toast = Toast.makeText(activity, activity.getString(R.string.settingsChordsInvalid), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            return false;
        }
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

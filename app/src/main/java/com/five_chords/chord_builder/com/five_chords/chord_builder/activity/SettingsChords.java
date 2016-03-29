/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.five_chords.chord_builder.Chord;
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

    private static boolean[] chordOptions;

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

        chordOptions = MainActivity.getOptions().chordTypesInUseArray;
        optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        for (final Chord.ChordType type: Chord.ChordType.values())
        {
            optionsAdapter.add(new SettingsOption(type + " Chords are " + ((chordOptions[type.ordinal()]) ? "Enabled" : "Disabled")) {
                @Override
                public void performAction() {
                    if (this.name.contains("Disabled")) {
                        this.name = type + " Chords are Enabled";
                        chordOptions[type.ordinal()] = true;
                    } else {
                        chordOptions[type.ordinal()] = false;
                        if (isValidSelection())
                            this.name = type +" Chords are Disabled";
                        else
                            chordOptions[type.ordinal()] = true;
                    }

                    optionsAdapter.notifyDataSetChanged();
                }
            });
        }

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
        for (Chord.ChordType type: Chord.ChordType.values()) {
            MainActivity.getOptions().setChordTypeUse(type.ordinal(), chordOptions[type.ordinal()]);
            Log.d("this", type.ordinal() + " " + chordOptions[type.ordinal()]);
        }
        finish();
    }

    private boolean isValidSelection() {
        for (boolean chordOption : chordOptions)
            if (chordOption) return true;

        // Show toast
        Toast toast = Toast.makeText(activity, activity.getString(R.string.settingsChordsInvalid), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return false;
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

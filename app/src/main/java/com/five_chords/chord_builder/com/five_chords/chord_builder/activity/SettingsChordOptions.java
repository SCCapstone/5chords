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
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;

public class SettingsChordOptions extends Activity
{
    private ArrayAdapter<SettingsOption> optionsAdapter;
    private Activity activity;
    private static String[] inversionTypes;

    /**
     * Activity Creator
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chord_options);


        // Inflate the layout for this fragment
        ListView list = (ListView) findViewById(R.id.inversion_list);

        // Populate the list
        byte i = 0;
        Options options = MainActivity.getOptions();
        optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);
        inversionTypes = new String[] {"First", "Second", "Third", "Fourth"};

        for (final String type: inversionTypes)
        {
            optionsAdapter.add(new SettingsOption(type + " Inversions are " + (options.chordInversionsToUse.contains(i++) ? "Enabled" : "Disabled")) {
                @Override
                public void performAction() {
                    if (this.name.contains("Disabled")) {
                        this.name = type + " Inversions are Enabled";
                        inversionsChanged(type, true);
                    } else {
                        this.name = type +" Inversions are Disabled";
                        inversionsChanged(type, false);
                    }

                    optionsAdapter.notifyDataSetChanged();
                }
            });
        }

        // Set click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getItemAtPosition(position);

                if (obj instanceof SettingsOption)
                    ((SettingsOption) obj).performAction();
            }
        });

        // Get the list view of settings
        list.setAdapter(optionsAdapter);
    }


    /**
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * The MainActivity call
     */
    public void backToMain(View view)
    {
        finish();
        this.overridePendingTransition(0, 0);
    }



    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param inverionType The inversion to change.
     * @param isSelected  Was it selected?.
     */
    public void inversionsChanged(String inverionType, boolean isSelected)
    {
        Options options = MainActivity.getOptions();

        byte i = 0;
        for (String type : inversionTypes)
        {
            if (type == inverionType)
            {
                if (isSelected)
                    options.addChordInversion(i);
                else
                    options.removeChordInversion(i);
            }
            ++i;
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

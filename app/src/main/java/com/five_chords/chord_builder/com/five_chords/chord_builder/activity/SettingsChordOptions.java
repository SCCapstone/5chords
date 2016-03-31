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

/**
 * Activity containing the App settings user interface.
 * @date 06 November 2015
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsChordOptions extends Activity
{
    /** The inversion type names. */
    private static final String[] INVERSION_TYPES = new String[] {"First", "Second", "Third", "Fourth"};

    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
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
        final ArrayAdapter<SettingsPage.SettingsOption> optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        for (final String type: INVERSION_TYPES)
        {
            optionsAdapter.add(new SettingsPage.SettingsOption(type + " Inversions are " + (options.chordInversionsToUse.contains(i++) ? "Enabled" : "Disabled")) {
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

                if (obj instanceof SettingsPage.SettingsOption)
                    ((SettingsPage.SettingsOption) obj).performAction();
            }
        });

        // Get the list view of settings
        list.setAdapter(optionsAdapter);
    }

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        finish();
        this.overridePendingTransition(0, 0);
    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param inversionType The inversion to change.
     * @param isSelected  Was it selected?.
     */
    public void inversionsChanged(String inversionType, boolean isSelected)
    {
        Options options = MainActivity.getOptions();

        byte i = 0;
        for (String type : INVERSION_TYPES)
        {
            if (type.equals(inversionType))
            {
                if (isSelected)
                    options.addChordInversion(i);
                else
                    options.removeChordInversion(i);
            }
            ++i;
        }
    }
}

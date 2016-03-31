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

/**
 * Activity containing the chord type selection. This Activity ensures that at least one chord type will
 * always be selected.
 * @date 06 November 2015
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsChords extends Activity
{
    /** The chords the user wants to use. */
    private static boolean[] chordOptions;

    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_chords);

        // Inflate the layout for this fragment
        ListView view = (ListView) findViewById(R.id.settings_content);

        chordOptions = MainActivity.getOptions().chordTypesInUseArray;
        final ArrayAdapter<SettingsPage.SettingsOption> optionsAdapter = new ArrayAdapter<>(this, R.layout.centered_list_items);

        for (final Chord.ChordType type: Chord.ChordType.values())
        {
            optionsAdapter.add(new SettingsPage.SettingsOption(type + " are " + ((chordOptions[type.ordinal()]) ? "Enabled" : "Disabled")) {
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

                if (obj instanceof SettingsPage.SettingsOption)
                    ((SettingsPage.SettingsOption) obj).performAction();
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
        for (Chord.ChordType type: Chord.ChordType.values()) {
            MainActivity.getOptions().setChordTypeUse(type.ordinal(), chordOptions[type.ordinal()]);
            Log.d("this", type.ordinal() + " " + chordOptions[type.ordinal()]);
        }
        finish();
        this.overridePendingTransition(0, 0);
    }

    /**
     * Tests whether or not at least one chord type is enabled.
     * @return Whether or not at least one chord type is enabled
     */
    private boolean isValidSelection() {
        for (boolean chordOption : chordOptions)
            if (chordOption) return true;

        // Show toast
        Toast toast = Toast.makeText(this, getString(R.string.settingsChordsInvalid), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return false;
    }
}

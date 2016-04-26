package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * Fragment containing the chord type selection. This Activity ensures that at least one chord type will
 * always be selected.
 * @date 06 November 2015
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsChooseChordFragment extends SettingsPageFragment.SettingsSubFragment
        implements AdapterView.OnItemClickListener
{
    /** The chords the user wants to use. */
    private boolean[] chordOptions;

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_chords, container, false);

        // Get the reference to the chord type usage array
        chordOptions = MainActivity.getOptions().chordTypesInUseArray;

        // Get the chord ListView
        ListView chordListView = (ListView) view.findViewById(R.id.settings_content);
        chordListView.setOnItemClickListener(null);

        // Populate the instrument select spinner
        ChordSelectAdapter chordSelectAdapter =  new ChordSelectAdapter(getActivity(), R.layout.centered_list_items);
        chordSelectAdapter.addAll(Chord.ChordType.values());
        chordListView.setAdapter(chordSelectAdapter);

        // Set click listener
        chordListView.setOnItemClickListener(this);

        // Set Done Button action
        Button doneButton = (Button)view.findViewById(R.id.button_settings_chords_done);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (SettingsChooseChordFragment.this.getSettingsPageFragment() != null)
                    SettingsChooseChordFragment.this.getSettingsPageFragment().
                        setSettingsSubFragment(SettingsPageFragment.SettingsSubFragmentDef.MAIN.ordinal());
            }
        });

        return view;
    }

//    /**
//     * Called to return to the MainActivity.
//     * @param view The calling View
//     */
//    public void backToMain(View view)
//    {
//        // Update chord types in use
//        for (Chord.ChordType type: Chord.ChordType.values())
//        {
//            MainActivity.getOptions().setChordTypeUse(type.ordinal(), chordOptions[type.ordinal()]);
//            Log.d("this", type.ordinal() + " " + chordOptions[type.ordinal()]);
//        }
//
//        finish();
//        this.overridePendingTransition(0, 0);
//    }

    /**
     * Tests whether or not at least one chord type is enabled.
     * @return Whether or not at least one chord type is enabled
     */
    private boolean isValidSelection()
    {
        // If at least one ChordType is enabled return true
        for (boolean chordOption : chordOptions)
            if (chordOption)
                return true;

        // Otherwise show toast
        Toast toast = Toast.makeText(getActivity(), getString(R.string.settingsChordsInvalid), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return false;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // Toggle the chord type
        chordOptions[position] = !chordOptions[position];

        // Make sure there is still at least one type selected
        if (!isValidSelection())
            chordOptions[position] = true;

        // Update the label on the View
        TextView nameView = (TextView)view;
        nameView.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_small));
        nameView.setText(getChordTypeLabel(Chord.ChordType.values()[position], chordOptions[position]));
        nameView.setTypeface(null, chordOptions[position] ? Typeface.BOLD : Typeface.NORMAL);
    }

    /**
     * Gets the label for a Chord type used in the Adapter.
     * @param type The ChordType whose label to get
     * @param enabled Whether or not the ChordType is currently enabled
     * @return The label for a Chord type used in the Adapter
     */
    private String getChordTypeLabel(Chord.ChordType type, boolean enabled)
    {
        return type.toString() + " are " + (enabled ? "Enabled" : "Disabled");
    }

    /**
     * ArrayAdapter containing ChordTypes.
     */
    private class ChordSelectAdapter extends ArrayAdapter<Chord.ChordType>
    {
        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ChordSelectAdapter(Context context, int resource)
        {
            super(context, resource);
        }

        /**
         * Get the view that displays the data at the specified position.
         * @param position The position of the item
         * @param convertView The old view
         * @param parent The parent viewgroup
         * @return The view that displays the data
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View view;

            // Reuse old view if possible
            if (convertView != null && convertView.getId() == R.id.textview_centered_list_items)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.centered_list_items, parent, false);

            // Get components on View
            Chord.ChordType item = getItem(position);
            TextView nameView = (TextView)view;

            // Check whether the current chord type is enabled
            boolean enabled = MainActivity.getOptions().chordTypesInUseArray[item.ordinal()];

            // Set Text
            nameView.setTextSize(getResources().getDimension(R.dimen.text_size_small));
            nameView.setText(getChordTypeLabel(item, enabled));
            nameView.setTypeface(null, enabled ? Typeface.BOLD : Typeface.NORMAL);

            return view;
        }

        /**
         * Gets the label for a Chord type used in the Adapter.
         * @param type The ChordType whose label to get
         * @param enabled Whether or not the ChordType is currently enabled
         * @return The label for a Chord type used in the Adapter
         */
        private String getChordTypeLabel(Chord.ChordType type, boolean enabled)
        {
            return type.toString() + " are " + (enabled ? "Enabled" : "Disabled");
        }
    }
}

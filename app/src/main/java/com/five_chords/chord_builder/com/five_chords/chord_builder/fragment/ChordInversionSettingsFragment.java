package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * A Fragment containing the Chord inversion settings
 * @author tstone95
 */
public class ChordInversionSettingsFragment extends Fragment
{
    /** The inversion type names. */
    private static final String[] INVERSION_TYPES = new String[] {"First", "Second", "Third", "Fourth"};

    /**
     * Required empty public constructor.
     */
    public ChordInversionSettingsFragment()
    {   }

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
        View view =  inflater.inflate(R.layout.fragment_chord_inversion_settings, container, false);

        // Inflate the layout for this fragment
        ListView list = (ListView) view.findViewById(R.id.inversion_list);

        // Populate the list
        byte i = 0;
        Options options = MainActivity.getOptions();
        final ArrayAdapter<SettingsPageFragment.SettingsOption> optionsAdapter =
                new ArrayAdapter<>(getActivity(), R.layout.centered_list_items);

        for (final String type: INVERSION_TYPES)
        {
            optionsAdapter.add(new SettingsPageFragment.SettingsOption(
                    type + " Inversions are " + (options.chordInversionsToUse.contains(i++) ? "Enabled" : "Disabled")) {
                @Override
                public void performAction()
                {
                    if (this.name.contains("Disabled"))
                    {
                        this.name = type + " Inversions are Enabled";
                        inversionsChanged(type, true);
                    }
                    else
                    {
                        this.name = type +" Inversions are Disabled";
                        inversionsChanged(type, false);
                    }

                    optionsAdapter.notifyDataSetChanged();
                }
            });
        }

        // Set click listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object obj = parent.getItemAtPosition(position);

                if (obj instanceof SettingsPageFragment.SettingsOption)
                    ((SettingsPageFragment.SettingsOption) obj).performAction();
            }
        });


        // Get the list view of settings
        list.setAdapter(optionsAdapter);

        return view;
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

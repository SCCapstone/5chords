package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;

/**
 * A Fragment containing the settings page content.
 * @author tstone95
 */
public class SettingsFragment extends Fragment
{
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
            Score.resetScores(getActivity());
        }
    };

    /**
     * Required empty public constructor.
     */
    public SettingsFragment()
    {   }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        ListView view = (ListView)inflater.inflate(R.layout.fragment_settings_content, container, false);

        // Populate the list
        ArrayAdapter<SettingsOption> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        adapter.add(CHOOSE_CHORDS_OPTIONS);
        adapter.add(EDIT_HINTS_OPTIONS);
        adapter.add(CLEAR_SCORES_OPTIONS);

        // Set click listener
        view.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object obj = parent.getItemAtPosition(position);

                if (obj instanceof SettingsOption)
                    ((SettingsOption)obj).performAction();
            }
        });

        // Get the list view of settings
        view.setAdapter(adapter);

        // Return the View
        return view;
    }

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

    /**
     * Class representing an object in the SettingsFragment list of options.
     */
    public abstract class SettingsOption
    {
        /** The name of the SettingsOption. */
        public final String name;

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

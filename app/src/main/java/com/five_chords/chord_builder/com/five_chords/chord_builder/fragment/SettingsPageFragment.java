package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.five_chords.chord_builder.R;

/**
 * Fragment containing the App settings user interface. This interface contains buttons
 * to navigate to several sub activities where particular settings can be changed.
 * @version 1.0
 * @date 2 April 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsPageFragment extends Fragment
{
    /** The tag of this class. */
    private static final String TAG = "SettingsPageFragment";

    /** The TextView containing the title of this Fragment. */
    private TextView titleView;

    /** The definition of the currently selected sub fragment on the Settings page. */
    private SettingsSubFragmentDef currentSubFragmentDef;

    /** The currently selected sub fragment on the Settings page. */
    private SettingsSubFragment currentSubFragment;

    /** Enum containing the sub fragments available from the Settings Fragment. */
    public enum SettingsSubFragmentDef
    {
        MAIN (R.string.settings, SettingsMainSubFragment.class),
        CHOOSE_CHORDS (R.string.settings_choose_chords, SettingsChooseChordFragment.class),
        CHORD_OPTIONS (R.string.settings_chord_options, SettingsChordOptionsFragment.class),
        CHECK_OPTIONS (R.string.settings_check_options, CheckSettingsFragment.class),
        CHANGE_INSTRUMENT (R.string.settings_change_instrument, SettingsPickInstrumentFragment.class);

        /** String resource id for the name of this Drawer */
        public final int NAME_RES_ID;

        // Fragment represented by this drawer
        private final Class<? extends SettingsSubFragment> FRAGMENT_CLASS;

        /**
         * Creates a DrawerFragment.
         */
        SettingsSubFragmentDef(@StringRes int nameResId, Class<? extends SettingsSubFragment> fragmentClass)
        {
            NAME_RES_ID = nameResId;
            FRAGMENT_CLASS = fragmentClass;
        }
    }

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
        View view = inflater.inflate(R.layout.fragment_settings_page, container, false);

        // Get the title view
        titleView = (TextView) view.findViewById(R.id.textview_settings_title);

        // Set the default sub fragment
        setSettingsSubFragment(SettingsSubFragmentDef.MAIN.ordinal());

        return view;
    }

    /**
     * Called when this Fragment is paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Destroy the current sub fragment, if applicable
        if (currentSubFragment != null)
        {
            try
            {
                getFragmentManager().beginTransaction().remove(currentSubFragment).commit();
            }
            catch (Exception e)
            {/* Ignore */}

            currentSubFragment.setSettingsPageFragment(null);
            currentSubFragment = null;
        }
    }

//    /**
//     * Called when the view of this Fragment is destroyed.
//     */
//    @Override
//    public void onDestroyView()
//    {
////        // Destroy the current sub fragment, if applicable
////        if (currentSubFragment != null)
////        {
////            try
////            {
////                getFragmentManager().beginTransaction().remove(currentSubFragment).commit();
////            }
////            catch (Exception e)
////            {/* Ignore */}
////
////            currentSubFragment.setSettingsPageFragment(null);
////            currentSubFragment = null;
////        }
//
//        super.onDestroyView();
//    }

    /**
     * Sets the settings sub fragment of the given index.
     * @param index The index of the sub fragment
     */
    public void setSettingsSubFragment(int index)
    {
        // Select the new Fragment to show based on selected position
        SettingsSubFragment fragment;
        SettingsSubFragmentDef previousFragmentDef = currentSubFragmentDef;

        try
        {
            currentSubFragmentDef = SettingsSubFragmentDef.values()[index];
            fragment = currentSubFragmentDef.FRAGMENT_CLASS.newInstance();
        }
        catch (Exception e)
        {
            currentSubFragmentDef = previousFragmentDef;
            Log.e(TAG, "Error setting settings sub fragment: " + e.getMessage());
            return;
        }

        // Set reference
        currentSubFragment = fragment;
        currentSubFragment.setSettingsPageFragment(this);

        // Set Title
        if (titleView != null)
            titleView.setText(getString(SettingsSubFragmentDef.values()[index].NAME_RES_ID));

        // Insert the fragment by replacing any existing fragments
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.settings_content, fragment);

//        if (previousFragmentDef != SettingsSubFragmentDef.MAIN)
//            transaction.addToBackStack(null);

        try
        {
            transaction.commit();
        }
        catch (Exception e)
        {/* Ignore */}
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

    /**
     * Parent class for the sub fragments contained in the Settings Page.
     */
    public static class SettingsSubFragment extends Fragment
    {
        /** The SettingsPageFragment containing this SettingsSubFragment. */
        private SettingsPageFragment settingsPageFragment;

        /**
         * Gets the SettingsPageFragment containing this SettingsSubFragment.
         * @return The SettingsPageFragment containing this SettingsSubFragment
         */
        public SettingsPageFragment getSettingsPageFragment()
        {
            return settingsPageFragment;
        }

        /**
         * Sets the SettingsPageFragment containing this SettingsSubFragment.
         * @param settingsPageFragment The SettingsPageFragment containing this SettingsSubFragment
         */
        private void setSettingsPageFragment(SettingsPageFragment settingsPageFragment)
        {
            this.settingsPageFragment = settingsPageFragment;
        }
    }

    /**
     * The Fragment containing the main content of the settings page.
     */
    public static class SettingsMainSubFragment extends SettingsSubFragment
    {
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
            View view = inflater.inflate(R.layout.fragment_settings_main, container, false);

            // Inflate the layout for this fragment
            if (view instanceof ListView)
            {
                ListView listView = (ListView) view;

                // Populate the list
                ArrayAdapter<String> optionsAdapter =
                        new ArrayAdapter<>(getActivity(), R.layout.centered_list_items);

                for (SettingsSubFragmentDef f: SettingsSubFragmentDef.values())
                {
                    if (f != SettingsSubFragmentDef.MAIN)
                        optionsAdapter.add(getString(f.NAME_RES_ID));
                }

                // Set click listener
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        getSettingsPageFragment().setSettingsSubFragment(position + 1);
                    }
                });

                // Get the list view of settings
                listView.setAdapter(optionsAdapter);
            }

            return view;
        }
    }
}

package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * Activity containing the App settings user interface.
 * @date 06 November 2015
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsChordOptionsFragment extends SettingsPageFragment.SettingsSubFragment
{
    /** The PitchBendSettingsFragment attached to this SettingsChordOptionsFragment. */
    private PitchBendSettingsFragment pitchBendSettingsFragment;

    /** The ChordInversionSettingsFragment attached to this SettingsChordOptionsFragment. */
    private ChordInversionSettingsFragment chordInversionSettingsFragment;

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
        View view = inflater.inflate(R.layout.fragment_settings_chord_options, container, false);

        // Create Fragments
        chordInversionSettingsFragment = new ChordInversionSettingsFragment();
        pitchBendSettingsFragment = new PitchBendSettingsFragment();

        // Add Fragments to layout
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_pitch_select, pitchBendSettingsFragment)
                .add(R.id.fragment_inversion_select, chordInversionSettingsFragment).commit();

        // Set Done Button action
        Button doneButton = (Button)view.findViewById(R.id.button_settings_chord_options_done);
        doneButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (SettingsChordOptionsFragment.this.getSettingsPageFragment() != null)
                    SettingsChordOptionsFragment.this.getSettingsPageFragment().
                            setSettingsSubFragment(SettingsPageFragment.SettingsSubFragmentDef.MAIN.ordinal());
            }
        });

        return view;
    }

    /**
     * Called when this Fragment is paused.
     */
    @Override
    public void onPause()
    {
        super.onPause();

        // Destroy Fragments
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (pitchBendSettingsFragment != null)
        {
            transaction.detach(pitchBendSettingsFragment);
            transaction.remove(pitchBendSettingsFragment);
        }

        if (chordInversionSettingsFragment != null)
        {
            transaction.detach(chordInversionSettingsFragment);
            transaction.remove(pitchBendSettingsFragment);
        }

        try
        {
            transaction.commit();
        }
        catch (Exception e)
        {/* Ignore */}
    }

    /**
     * Called when this Fragment's view is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        // Set Fragments to null
        pitchBendSettingsFragment = null;
        chordInversionSettingsFragment = null;

        super.onDestroyView();
    }
}

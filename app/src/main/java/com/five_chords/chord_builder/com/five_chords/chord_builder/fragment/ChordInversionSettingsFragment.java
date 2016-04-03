package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.five_chords.chord_builder.R;

/**
 * A Fragment containing the Chord inversion settings
 * @author tstone95
 */
public class ChordInversionSettingsFragment extends Fragment
{
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chord_inversion_settings, container, false);
    }
}

package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.setUpGUI;

/**
 * A Fragment containing the chord select sliders.
 */
public class SliderFragment extends Fragment
{
    /**
     * Required empty public constructor.
     */
    public SliderFragment()
    {   }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View sliders = inflater.inflate(R.layout.fragment_sliders, container, false);

        sliders.findViewById(R.id.slider_option).setEnabled(false);
        sliders.findViewById(R.id.textview_option).setEnabled(false);

        new setUpGUI(getActivity(), sliders);

        return sliders;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }
}

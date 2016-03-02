package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.soundHandler;

/**
 * A Fragment containing the chord select slider and the instrument select slider.
 * @author tstone95
 */
public class ChordInstrumentSelectFragment extends Fragment
{
    /** The available instruments names */
    public static final String[] INSTRUMENT_NAMES = {"Trumpet", "Piano", "Organ", "Guitar", "Violin", "Flute"};

    /** Reference to the Spinner for selecting chords contained in this Fragment. */
    private Spinner chordSelectSpinner;

    /** Reference to the Spinner for selecting instruments contained in this Fragment. */
    private Spinner instrumentSelectSpinner;

    /**
     * Required empty public constructor.
     */
    public ChordInstrumentSelectFragment()
    {   }

    /**
     * Updates the types of chords available in the Chord spinner on this Fragment.
     * @param activity The current Activity
     * @param chordIndices The indices of the new chords for the Spinner
     */
    public void updateChordSpinner(final Activity activity, int[] chordIndices)
    {
        // Populate the chord select spinner
        ChordDisplayItemAdapter adapter =
                new ChordDisplayItemAdapter(activity, android.R.layout.simple_spinner_dropdown_item);

        // Load items
        if (chordIndices == null) // Assumed to mean load all chords
        {
            for (int i = 0; i < 36; ++i) // TODO get constant for 36
                adapter.add(new ChordDisplayItem(Score.scores[i]));
        }
        else
        {
            // Make sure scores are loaded
            Score.loadScores(activity, false);

            for (int i: chordIndices)
            {
                adapter.add(new ChordDisplayItem(Score.scores[i]));
            }
        }

        // Set the adapter
        chordSelectSpinner.setAdapter(adapter);
    }

    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragmet's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the View
        View view = inflater.inflate(R.layout.fragment_chord_select, container, false);

        // Get the Spinners
        chordSelectSpinner = (Spinner)view.findViewById(R.id.spinner_chord_select);
        instrumentSelectSpinner = (Spinner)view.findViewById(R.id.spinner_instrument);

        // Set the OnItemSelectedListener for the spinners
        chordSelectSpinner.setMinimumWidth((int) getResources().getDimension(R.dimen.min_chord_select_slider_size));
        chordSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                // Update the selected chord
                chordHandler.setSelectedChord(chordSelectSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });

        // Set the OnItemSelectedListener for the spinner
        instrumentSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                // Update the selected chord
                soundHandler.switchInstrument(instrumentSelectSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });

        // Populate the instrument select spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                ChordInstrumentSelectFragment.INSTRUMENT_NAMES);
        instrumentSelectSpinner.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Wrapper class containing a information to be displayed with the Chord names.
     */
    private static class ChordDisplayItem
    {
        /** The chord description matrix */
        public static String[] CHORD_DESCRIPTION_MATRIX = new String[] {"Not Attempted", "Beginner", "Shows Promise",
                            "Difficult", "Good", "Very Good", "Challenging", "Very Good", "Expert", "Mastered"};

        /** The name of the Chord */
        private String chordName;

        /** The chord description */
        private String chordDescription;

        /**
         * Creates a new ChordDisplayItem from a ScoreWrapper.
         * @param wrapper The ScoreWrapper
         */
        public ChordDisplayItem(Score.ScoreWrapper wrapper)
        {
            chordName = wrapper.CHORD_NAME;
            chordDescription = getChordDescription(wrapper);
        }

        /**
         * Gets the appropriate chord description for the Chord with the given ScoreWrapper.
         * @param wrapper The ScoreWrapper
         * @return The appropriate chord description for the Chord with the given ScoreWrapper
         */
        private static String getChordDescription(Score.ScoreWrapper wrapper)
        {
            if (wrapper.getNumTotalGuesses() == 0)
                return CHORD_DESCRIPTION_MATRIX[0];
            else
            {
                // Compute matrix index
                int i, j;

                // Row
                int guesses = wrapper.getNumTotalGuesses();

                if (guesses < 10)
                    i = 0;
                else if (guesses < 50)
                    i = 1;
                else
                    i = 2;

                // Column
                float percent = wrapper.getNumCorrectGuesses()  / (float) wrapper.getNumTotalGuesses();

                if (percent < 0.25f)
                    j = 0;
                else if (percent < 0.75f)
                    j = 1;
                else if (percent < 0.95f)
                    j = 2;
                else
                    return CHORD_DESCRIPTION_MATRIX[CHORD_DESCRIPTION_MATRIX.length - 1];

                // Get value
                return CHORD_DESCRIPTION_MATRIX[3 * j + i + 1];
            }
        }
    }

    /**
     * Implementation of an ArrayAdapter containing views for displaying the chord names.
     */
    public static class ChordDisplayItemAdapter extends ArrayAdapter<ChordDisplayItem>
    {
        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ChordDisplayItemAdapter(Context context, int resource)
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
            if (convertView != null && convertView.getId() == R.id.component_chord_display_item)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.component_chord_display_item, parent, false);

            // Get components on View
            ChordDisplayItem item = getItem(position);
            TextView nameView = (TextView)view.findViewById(R.id.chord_display_chord_name);
            TextView descriptionView = (TextView)view.findViewById(R.id.chord_display_chord_description);

            nameView.setText(item.chordName);
            descriptionView.setText(item.chordDescription);

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = getView(position, convertView, parent);
            view.setMinimumWidth((int) getContext().getResources().getDimension(R.dimen.min_chord_dropdown_menu_size));
            return view;
        }
    }
}
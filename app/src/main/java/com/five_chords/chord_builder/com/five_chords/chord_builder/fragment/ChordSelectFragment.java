package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.soundHandler;

/**
 * A Fragment containing the chord select slider and the instrument select slider.
 * @author tstone95
 */
public class ChordSelectFragment extends Fragment
{
    /** Reference to the Spinner for selecting chords contained in this Fragment. */
    private Spinner chordSelectSpinner;

    /** The button for playing the currently selected chord. */
    private Button playSelectedChordButton;

    /** Every button gets a soundHandler **/
    private static soundHandler[] soundHandlers;

    /**
     * Required empty public constructor.
     */
    public ChordSelectFragment()
    {   }

    /**
     * Sets the Chord that should be displayed on the Chord spinner.
     * @param chord The Chord that should be displayed on the Chord spinner
     */
    public void setDisplayedChord(Chord chord, boolean random)
    {
        if (random) {
            chordSelectSpinner.setSelection(chordSelectSpinner.getCount() - 1);
            return;
        }

        // Get the Chord's position in the spinner
        int position = -1;

        for (int i = 0; position == -1 && i < chordSelectSpinner.getCount(); ++i)
        {
            if (chord.ID == ((ChordDisplayItem)chordSelectSpinner.getItemAtPosition(i)).chord.ID)
                position = i;
        }

        if (position != -1)
            chordSelectSpinner.setSelection(position);
    }

    /**
     * Updates the types of chords available in the Chord spinner on this Fragment.
     * @param activity The current Activity
     */
    public void updateAvailableChordTypes(final Activity activity)
    {
        // Populate the chord select spinner
        ChordDisplayItemAdapter adapter =
                new ChordDisplayItemAdapter(activity, android.R.layout.simple_spinner_dropdown_item);

        // Make sure scores are loaded
        Score.loadScores(activity, false);

        // Load items
        for (Chord.ChordType type: MainActivity.getOptions().getChordTypesInUse())
        {
            for (int i = 0; i < Note.NUM_NOTES; ++i)
            {
                adapter.add(new ChordDisplayItem(chordHandler.getChord(i, type)));
            }
        }

        // Add random chord placeholder
        adapter.add(new ChordDisplayItem(new Chord()));

        // Set the adapter
        chordSelectSpinner.setAdapter(adapter);
    }

    /**
     * Called to play the selected chord programmatically.
     * @param play Whether or not to play the chord
     */
    public void playSelectedChord(boolean play)
    {
        MainActivity.pressButton(playSelectedChordButton, play);
    }

    /**
     * Silence sound from all buttons
     */
    public void silenceButtons() {
        for (soundHandler sH : soundHandlers)
            sH.stopSound();
    }

    /**
     * Called when the Activity containing this Fragment is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();

        // Update the available chord types
        updateAvailableChordTypes(getActivity());
    }

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
        // Inflate the View
        View view = inflater.inflate(R.layout.fragment_chord_select, container, false);

        // Initialize Buttons
        final Button playSelectedChord = (Button) view.findViewById(R.id.button_select_chord_play);
        playSelectedChordButton = playSelectedChord;
        final Button selectRandomChord = (Button) view.findViewById(R.id.button_select_random_chord);
        soundHandlers = new soundHandler[2];
        soundHandlers[0] = new soundHandler(getActivity(), "playButton");
        soundHandlers[1] = new soundHandler(getActivity(), "randomButton");

        // Set the function of the play button
        playSelectedChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    soundHandlers[0].playChord(getActivity(), chordHandler.getCurrentSelectedChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    soundHandlers[0].stopSound();
                }
                return false;
            }
        });

        // Set the function of the random button
        selectRandomChord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    MainActivity.stopAllSound();
                    MainActivity.blockAllSound(true);
                    chordHandler.getRandomChord();
                    soundHandlers[1].playChord(getActivity(), chordHandler.getCurrentSelectedChordSpelling(),
                            chordHandler.getCurrentSelectedChord().getNumNotes());
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    MainActivity.blockAllSound(false);
                    soundHandlers[1].stopSound();
                }

                return false;
            }
        });

        // Initialize Chord Select Spinner
        chordSelectSpinner = (Spinner)view.findViewById(R.id.spinner_chord_select);
        chordSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if (position == chordSelectSpinner.getCount() - 1) return;

                // Update the selected chord
                ChordDisplayItem item = (ChordDisplayItem)parentView.getItemAtPosition(position);
                chordHandler.setSelectedChord(item.chord, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            { /* Ignore */ }
        });

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

        /** The Chord to display. */
        private Chord chord;

        /** The chord description */
        private String chordDescription;

        /**
         * Creates a new ChordDisplayItem from a ScoreWrapper.
         * @param chord The Chord to display
         */
        public ChordDisplayItem(Chord chord)
        {
            this.chord = chord;
            update();
        }

        /**
         * Updates this ChordDisplayItem.
         */
        public void update()
        {
            chordDescription = getChordDescription(Score.getScore(chord).getOverallValue());
        }

        /**
         * Gets the appropriate chord description for the Chord with the given ScoreWrapper.
         * @param value The value of the Score
         * @return The appropriate chord description for the Chord with the given ScoreWrapper
         */
        private static String getChordDescription(Score.ScoreValue value)
        {
            if (value.numTotalGuesses == 0)
                return CHORD_DESCRIPTION_MATRIX[0];
            else
            {
                // Compute matrix index
                int i, j;

                // Row
                int guesses = value.numTotalGuesses;

                if (guesses < 10)
                    i = 0;
                else if (guesses < 50)
                    i = 1;
                else
                    i = 2;

                // Column
                float percent = value.numCorrectGuesses  / (float) value.numTotalGuesses;

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
            ChordDisplayItem item = (position == getCount()) ? getItem(position - 1) : getItem(position);
            TextView nameView = (TextView)view.findViewById(R.id.chord_display_chord_name);
            TextView descriptionView = (TextView)view.findViewById(R.id.chord_display_chord_description);

            nameView.setText((position == getCount() - 1) ? "Randomized!" : item.chord.toString());
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

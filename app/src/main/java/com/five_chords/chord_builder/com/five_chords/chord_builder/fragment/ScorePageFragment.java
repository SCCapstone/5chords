package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.chordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;

/**
 * A Fragment containing the score page.
 * @author tstone95
 */
public class ScorePageFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    /** The bundle id for the displayed type. */
    private static final String BUNDLE_ID_DISPLAYED_TYPE = "ScorePageFragment.displayedType";

    /** Handle to the View of this ScorePageFragment */
    private View view;

    /** The current displayed chordType. */
    private Chord.ChordType currentType;

    /**
     * Required empty public constructor.
     */
    public ScorePageFragment()
    {   }

    /**
     * Refreshes the list view containing scores.
     */
    public void refreshListView()
    {
        if (view == null)
        {
            Log.e(getClass().getSimpleName(), "Error adding scores: view is null");
            return;
        }

        // Populate list view of scores
        ListView listView = (ListView)view.findViewById(R.id.score_page_scorelist);
        ScoreItemHistoryAdapter adapter = new ScoreItemHistoryAdapter(getActivity(), android.R.layout.simple_list_item_1);

        // Get the current type if needed
        if (currentType == null)
        {
            Bundle arguments = getArguments();
            if (arguments != null)
                currentType = Chord.ChordType.values()[arguments.getInt(BUNDLE_ID_DISPLAYED_TYPE, 0)];
            else
                currentType = Chord.ChordType.MAJOR;
        }

        // Loop over each note
        for (int i = 0; i < Note.NUM_NOTES; ++i)
            adapter.add(Score.getScore(chordHandler.getChord(i, currentType)));

        // Set the adapter
        listView.setAdapter(adapter);
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
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_score_page, container, false);

        // Populate ChordType Spinner
        Spinner chordSelectSpinner = (Spinner)view.findViewById(R.id.spinner_score_page);
        ArrayAdapter<Chord.ChordType> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        for (Chord.ChordType type: Chord.ChordType.values())
            adapter.add(type);

        chordSelectSpinner.setAdapter(adapter);
        chordSelectSpinner.setOnItemSelectedListener(this);

        // Return the created View
        return view;
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        // Get the new displayed type
        currentType = Chord.ChordType.values()[position];

        // Save arguments
        Bundle arguments = getArguments();
        if (arguments != null)
            arguments.putInt(BUNDLE_ID_DISPLAYED_TYPE, currentType.ordinal());

        refreshListView();

        // Refresh
        ListView listView = (ListView)ScorePageFragment.this.view.findViewById(R.id.score_page_scorelist);
        listView.postInvalidateDelayed(10L);
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {    }

    /**
     * Implementation of an ArrayAdapter containing views for displaying the history of a chord's scores.
     */
    public static class ScoreItemHistoryAdapter extends ArrayAdapter<Score>
    {
        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public ScoreItemHistoryAdapter(Context context, int resource)
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
            if (convertView != null && convertView.getId() == R.id.component_score_history_item)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.component_score_history_item, parent, false);

            // Get components on View
            Score item = getItem(position);
            TextView nameView = (TextView)view.findViewById(R.id.score_history_chord_name);
            TextView descriptionView = (TextView)view.findViewById(R.id.score_history_chord_description);
            ScoreProgressView progressView = (ScoreProgressView)view.findViewById(R.id.score_history_progress_view);

            // Set chord text
            nameView.setText(chordHandler.getChord(item.CHORD_ID).toString());
            descriptionView.setText(getLabel(item));

            // Set size
            progressView.setWidthPixels(ScoreProgressView.calculateWidth(item.getHistory()));
            progressView.setHeightPixels(ScoreProgressView.calculateHeight());
            progressView.setMinimumWidth(progressView.getWidthPixels());
            progressView.setMinimumHeight(progressView.getHeightPixels());
            progressView.setHistory(item.getHistory());

            // Scroll the progress bar all the way over to the right
            final HorizontalScrollView scrollView = (HorizontalScrollView)view.findViewById(R.id.score_history_progress_scrollview);
            scrollView.setVisibility(View.INVISIBLE);
            scrollView.post(new Runnable()
            {
                public void run()
                {
                    scrollView.scrollTo(10000, 0);
                    scrollView.setVisibility(View.VISIBLE);
                }
            });

            return view;
        }

        /**
         * Gets a label for a CurrentScoreWrapper.
         * @param score The Score
         * @return A label for the CurrentScoreWrapper
         */
        private String getLabel(Score score)
        {
            return score.getOverallValue().numTotalGuesses == 0 ? getContext().getString(R.string.not_attempted) :
                   score.getOverallValue().getDisplayPercentageString() + " Overall";
        }
    }
}

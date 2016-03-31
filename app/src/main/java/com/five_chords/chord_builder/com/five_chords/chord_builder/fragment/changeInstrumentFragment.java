package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

/**
 * A Fragment containing the chord select slider and the instrument select slider.
 * @author tstone95
 */
public class changeInstrumentFragment extends Fragment
{
    /** The available instruments names */
    public static final String[] INSTRUMENT_NAMES = {"Trumpet", "Piano", "Organ", "Violin", "Flute"};

    /** The available instrument icons */
    public static final int[] INSTRUMENT_ICONS = {R.drawable.trumpet, R.drawable.piano, R.drawable.organ,
                                                  R.drawable.violin, R.drawable.flute};

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
        View view = inflater.inflate(R.layout.fragment_change_instrument, container, false);

        ListView instrumentListView = (ListView)view.findViewById(R.id.instruments);

        // Populate the instrument select spinner
        CustomList adapter = new CustomList(getActivity(), R.layout.list_item_text_with_image);
        instrumentListView.setAdapter(adapter);

        // Set click listener
        instrumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.getOptions().changeInstrument(position);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * A Custom list to contain images and text.
     */
    public class CustomList extends ArrayAdapter<String>{

        /**
         * Constructor.
         *
         * @param context  The current context
         * @param resource The resource ID for a layout file containing a TextView to use
         */
        public CustomList(Context context, int resource)
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
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;

            // Reuse old view if possible
            if (convertView != null)
                view = convertView;
            else
                view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_text_with_image, parent, false);

            // Get components on View
            TextView txtTitle = (TextView) view.findViewById(R.id.txt);
            ImageView imageView = (ImageView) view.findViewById(R.id.img);

            // Set component values
            txtTitle.setText(INSTRUMENT_NAMES[position]);
            imageView.setImageResource(INSTRUMENT_ICONS[position]);

            // Make the current instrument name bold
            if (MainActivity.getOptions().instrument == position)
                txtTitle.setTypeface(null, Typeface.BOLD);
            else
                txtTitle.setTypeface(null, Typeface.NORMAL);

            return view;
        }
    }

}

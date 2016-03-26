package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.five_chords.chord_builder.MainActivity;
import com.five_chords.chord_builder.R;
/**
 * A Fragment containing the chord select slider and the instrument select slider.
 * @author tstone95
 */
public class changeInstrumentFragment extends DialogFragment
{

    private int instrument;

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

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.change_instrument);

        ListView instrumentListView = (ListView)view.findViewById(R.id.instruments);

        // Populate the instrument select spinner
        CustomList adapter = new CustomList(this.getActivity(), INSTRUMENT_NAMES, INSTRUMENT_ICONS);
        instrumentListView.setAdapter(adapter);

        // Set click listener
        instrumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.getOptions().changeInstrument(position);
                closeFragment();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    /**
     * Create a new instance of changeInstrumentFragment.
     * @return A new instance of changeInstrumentFragment
     */
    public static changeInstrumentFragment newInstance()
    {
        changeInstrumentFragment f = new changeInstrumentFragment();
        return f;
    }

    public void closeFragment() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }


    public class CustomList extends ArrayAdapter<String>{

        private final Activity context;
        private final String[] text;
        private final int[] imageId;
        public CustomList(Activity context, String[] text, int[] imageId) {
            super(context, R.layout.list_item_text_with_image, text);
            this.context = context;
            this.text = text;
            this.imageId = imageId;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.list_item_text_with_image, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            txtTitle.setText(text[position]);

            imageView.setImageResource(imageId[position]);
            return rowView;
        }
    }

}

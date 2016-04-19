package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
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

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.soundHandler;

/**
 * Activity containing the instrument selection.
 * @date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class SettingsInstruments extends Activity implements AdapterView.OnItemClickListener
{
    /** The available instruments names */
    public static final String[] INSTRUMENT_NAMES = {"Trumpet", "Piano", "Organ", "Violin", "Flute"};

    /** The available instrument icons */
    public static final int[] INSTRUMENT_ICONS = {R.drawable.trumpet, R.drawable.piano, R.drawable.organ,
            R.drawable.violin, R.drawable.flute};

    /** Chord for previewing instruments. */
    private static final Note[] PREVIEW_CHORD = new Note[3];

    static
    {
        for (int i = 0; i < 3; ++i)
        {
            PREVIEW_CHORD[i] = new Note();
            PREVIEW_CHORD[i].set(Chord.ChordType.MAJOR.offsets[i]);
        }
    }

    /** The array of instrument name view handles. */
    private TextView[] instrumentNames;

    /** SoundHandler to use to preview instruments. */
    private soundHandler instrumentPreviewer;

    /** Thread for previewing instrument sounds. */
    private Thread previewThread;

    /** Runnable for previewing instrument sounds. */
    private Runnable previewRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            instrumentPreviewer.playChord(SettingsInstruments.this, PREVIEW_CHORD, 3);

            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException e)
            {/* Ignore */}

            instrumentPreviewer.stopSound();
        }
    };

    /**
     * Called when this Activity is created.
     * @param savedInstanceState Bundle containing the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create the instrument name array
        instrumentNames = new TextView[INSTRUMENT_NAMES.length];

        // Create the previewer
        instrumentPreviewer = new soundHandler(this, "instrumentPreview");

        // Set content view
        setContentView(R.layout.activity_settings_instrument);

        // Populate the instrument select spinner
        populateInstrumentSelector();
    }

    /**
     * Called when this Activity is destroyed.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        instrumentPreviewer.stopSound();
    }

    /**
     * Called to return to the MainActivity.
     * @param view The calling View
     */
    public void backToMain(View view)
    {
        finish();
        this.overridePendingTransition(0, 0);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
    {
        MainActivity.getOptions().changeInstrument(position);
        populateInstrumentSelector();

        if (previewThread != null && previewThread.isAlive())
            previewThread.interrupt();

        previewThread = new Thread(previewRunnable);
        previewThread.start();
    }

    /**
     * Populates the instrument selector.
     */
    private void populateInstrumentSelector()
    {
        ListView instrumentListView = (ListView) findViewById(R.id.instruments);
        instrumentListView.setOnItemClickListener(null);

        // Populate the instrument select spinner
        CustomList adapter = new CustomList(this, R.layout.list_item_text_with_image);
        adapter.addAll(INSTRUMENT_NAMES);
        instrumentListView.setAdapter(adapter);

        // Set click listener
        instrumentListView.setOnItemClickListener(this);
    }

    /**
     * A Custom list to contain images and text.
     */
    public class CustomList extends ArrayAdapter<String>
    {
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
            instrumentNames[position] = txtTitle;

            // Set component values
            txtTitle.setText(INSTRUMENT_NAMES[position]);
            imageView.setImageResource(INSTRUMENT_ICONS[position]);

            // Make the name of the currently selected instrument bold
            txtTitle.setTypeface(null, (position == MainActivity.getOptions().instrument) ? Typeface.BOLD : Typeface.NORMAL);

            return view;
        }
    }

}

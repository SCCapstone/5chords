/*************************************************************************************************
 * Activity containing the App settings user interface.
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 */
package com.five_chords.chord_builder.com.five_chords.chord_builder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.R;

public class SettingsInstruments extends Activity
{
    /** The available instruments names */
    public static final String[] INSTRUMENT_NAMES = {"Trumpet", "Piano", "Organ", "Violin", "Flute"};

    /** The available instrument icons */
    public static final int[] INSTRUMENT_ICONS = {R.drawable.trumpet, R.drawable.piano, R.drawable.organ,
            R.drawable.violin, R.drawable.flute};


    /**
     * Activity Creator
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_instrument);

        ListView instrumentListView = (ListView) findViewById(R.id.instruments);

        // Populate the instrument select spinner
        CustomList adapter = new CustomList(this, INSTRUMENT_NAMES, INSTRUMENT_ICONS);
        instrumentListView.setAdapter(adapter);

        // Set click listener
        instrumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.getOptions().changeInstrument(position);
                backToMain(view);
            }
        });
    }


    /**
     * Goes back to mainActivity on Call
     * @ param  Button Call
     * The MainActivity call
     */
    public void backToMain(View view)
    {
        finish();
        this.overridePendingTransition(0, 0);
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

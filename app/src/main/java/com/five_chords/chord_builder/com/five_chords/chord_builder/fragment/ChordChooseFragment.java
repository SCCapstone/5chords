package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.five_chords.chord_builder.Chord;
import com.five_chords.chord_builder.Score;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Options;
import com.five_chords.chord_builder.com.five_chords.chord_builder.view.ScoreProgressView;

import java.util.Arrays;

/**
 * A Fragment containing the Checkboxes to select which ChordTypes to use.
 * @author tstone95
 */
public class ChordChooseFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener//AdapterView.OnItemClickListener
{
    /** The array checkboxes for each ChordType. */
    private CheckBox[] chordTypeCheckBoxes;

    /**
     * Required empty public constructor.
     */
    public ChordChooseFragment()
    {   }

    /**
     * Create a new instance of ChordChooseFragment.
     * @return A new instance of ChordChooseFragment
     */
    public static ChordChooseFragment newInstance()
    {
        return new ChordChooseFragment();
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
        View view = inflater.inflate(R.layout.fragment_chord_choose_settings, container, false);

        // Set title
        if (getDialog() != null)
            getDialog().setTitle(R.string.choose_chords);

        // Initialize ListView
        chordTypeCheckBoxes = new CheckBox[Chord.ChordType.values().length];
        CheckBox checkBox;
        LinearLayout listView = (LinearLayout)view.findViewById(R.id.layout_fragment_choose_chord_types);

        // Create Checkboxes
        for (Chord.ChordType type: Chord.ChordType.values())
        {
            checkBox = createChordTypeCheckBox(type);
            listView.addView(checkBox);
            chordTypeCheckBoxes[type.ordinal()] = checkBox;
        }

        // Set default value of CheckBoxes
        boolean[] chordTypesInUse = MainActivity.getOptions().chordTypesInUseArray;
        for (int i = 0; i < chordTypeCheckBoxes.length; ++i)
            chordTypeCheckBoxes[i].setChecked(chordTypesInUse[i]);

//        ChordTypeAdapter adapter = new ChordTypeAdapter(getActivity(), android.R.layout.simple_list_item_1);
//
//        for (Chord.ChordType type: Chord.ChordType.values())
//            adapter.add(type);
//
//        listView.setAdapter(adapter);
////        listView.setOnItemClickListener(this);

        // Return the View
        return view;
    }

    /**
     * Creates a CheckBox for the given ChordType.
     * @param type The ChordType for which to create the CheckBox
     * @return A CheckBox for the given ChordType
     */
    private CheckBox createChordTypeCheckBox(Chord.ChordType type)
    {
        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setText(type.name);
        checkBox.setTag(type.ordinal());
        checkBox.setOnCheckedChangeListener(ChordChooseFragment.this);
        return checkBox;
    }

//    /**
//     * Callback method to be invoked when an item in this AdapterView has
//     * been clicked.
//     * <p/>
//     * Implementers can call getItemAtPosition(position) if they need
//     * to access the data associated with the selected item.
//     *
//     * @param parent   The AdapterView where the click happened.
//     * @param view     The view within the AdapterView that was clicked (this
//     *                 will be a view provided by the adapter)
//     * @param position The position of the view in the adapter.
//     * @param id       The row id of the item that was clicked.
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//    {
//        Log.e("AGGG", "dfsldkfj");
//        CheckBox checkBox = chordTypeCheckBoxes[position];
//        checkBox.setChecked(!checkBox.isChecked());
//        updateEnables();
//        MainActivity.getOptions().setChordTypeUse(position, checkBox.isChecked());
//    }

    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        // A CheckBox was changed
        if (buttonView instanceof CheckBox)
        {
            updateEnables();
            int index = (int)buttonView.getTag();
            MainActivity.getOptions().setChordTypeUse(index, isChecked);
            Log.e("DEBUG", Arrays.toString(MainActivity.getOptions().chordTypesInUseArray));
//            MainActivity.getOptions().changeChordSelections(useMajors(), useMinors(), useDominants());
        }
    }

    /**
     * Updates the enabled state of each check box, determined by the number of checks.
     */
    private void updateEnables()
    {
        // Count the number of checks
        int numChecks = 0;
        CheckBox stillChecked = null;

        for (CheckBox checkBox: chordTypeCheckBoxes)
        {
            checkBox.setEnabled(true);

            if (checkBox.isChecked())
            {
                ++numChecks;
                stillChecked = checkBox;
            }
        }

        // Disable or enable checkboxes to prevent all of them from being unchecked
       if (numChecks <= 1 && stillChecked != null)
           stillChecked.setEnabled(false);
    }

//    /**
//     * Implementation of an ArrayAdapter containing views for displaying ChordTypes.
//     */
//    public class ChordTypeAdapter extends ArrayAdapter<Chord.ChordType>
//    {
//        /**
//         * Constructor.
//         *
//         * @param context  The current context
//         * @param resource The resource ID for a layout file containing a TextView to use
//         */
//        public ChordTypeAdapter(Context context, int resource)
//        {
//            super(context, resource);
//        }
//
//        /**
//         * Get the view that displays the data at the specified position.
//         * @param position The position of the item
//         * @param convertView The old view
//         * @param parent The parent viewgroup
//         * @return The view that displays the data
//         */
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent)
//        {
//            View view;
//
//            // Reuse old view if possible
//            if (convertView != null && convertView.getId() == R.id.component_chord_type_display_item)
//                view = convertView;
//            else
//                view = LayoutInflater.from(getContext()).inflate(R.layout.component_chord_type_display_item, parent, false);
//
//            // Get components on View
//            Chord.ChordType item = getItem(position);
//            CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkbox_chord_type);
//            checkBox.setTag(item.ordinal());
//            checkBox.setOnCheckedChangeListener(ChordChooseFragment.this);
//            chordTypeCheckBoxes[position] = checkBox;
//
//            // Set chord type name
//            checkBox.setText(getLabel(item));
//
//            // Set checked
//            checkBox.setChecked(MainActivity.getOptions().isUsingChordType(item));
//
//            return view;
//        }
//
//        /**
//         * Gets a label for a ChordType.
//         * @param type The ChordType
//         * @return A label for a ChordType
//         */
//        private String getLabel(Chord.ChordType type)
//        {
//            return type.name + " Chords";
//        }
//    }
}

package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;


/**
 * The Help page Fragment.
 * date 31 March 2016
 * @author Drea,Steven,Zach,Kevin,Bo,Theodore
 */
public class HelpPageFragment extends Fragment
{
    /**
     * Called when the View containing this Fragment has been created.
     * @param inflater The inflater to use to inflate the Fragment
     * @param container The ViewGroup container
     * @param savedInstanceState The saved instance state
     * @return This Fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help_page, container, false);

        // Set up Buttons
        Button button = (Button) view.findViewById(R.id.contact_dev);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"5chordcontactus@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Of Matter");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                emailIntent.setType("message/rfc822");
                //startActivity(Intent.createChooser(emailIntent, "Choose your email client:"));
                try
                {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(HelpPageFragment.this.getActivity(),
                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button = (Button) view.findViewById(R.id.contact_pro);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"promusicconactus@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Of Matter");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");

                emailIntent.setType("message/rfc822");
                //standard ARPA starting
                //startActivity(Intent.createChooser(emailIntent, "Email us with your preferential source"));
                try
                {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(HelpPageFragment.this.getActivity(),
                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        // Set Done Button action
//        Button doneButton = (Button) view.findViewById(R.id.button_help_page_done);
//        doneButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Activity activity = HelpPageFragment.this.getActivity();
//
//                if (activity instanceof MainActivity)
//                    ((MainActivity)activity).setCurrentDrawer(MainActivity.DrawerFragment.MAIN.ordinal());
//            }
//        });

        return view;
    }
}

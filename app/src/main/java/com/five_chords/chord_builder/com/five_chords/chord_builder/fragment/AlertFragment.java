package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.five_chords.chord_builder.R;

/**
 * A Fragment containing an alert message and yes and no buttons.
 * @author tstone95
 */
public class AlertFragment extends DialogFragment implements View.OnClickListener
{
    /** The id of the title resource of this fragment. */
    private int titleResourceId;

    /** The id of the message resource of this fragment. */
    private int messageResourceId;

    /** The action to occur when yes is selected. */
    private Runnable yesAction;

    /** The action to occur when no is selected. */
    private Runnable noAction;

    /** The action to occur when the fragment is dismissed. */
    private Runnable dismissAction;

    /**
     * Required empty public constructor.
     */
    public AlertFragment()
    {   }

    /**
     * Create a new instance of AlertFragment, providing the title and message string resource ids
     * as arguments.
     */
    public static AlertFragment newInstance(int titleResourceId, int messageResourceId)
    {
        AlertFragment f = new AlertFragment();

        // Supple argument
        Bundle args = new Bundle();
        args.putInt("message", messageResourceId);
        args.putInt("title", titleResourceId);
        f.setArguments(args);

        return f;
    }

    /**
     * Sets the yes action of this AlertFragment.
     * @param r The yes action
     */
    public void setYesAction(Runnable r)
    {
        yesAction = r;
    }

    /**
     * Sets the no action of this AlertFragment.
     * @param r The no action
     */
    public void setNoAction(Runnable r)
    {
        noAction = r;
    }

    /**
     * Sets the dismiss action of this AlertFragment.
     * @param r The dismiss action
     */
    public void setDismissAction(Runnable r)
    {
        dismissAction = r;
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
        View view = inflater.inflate(R.layout.fragment_alert_dialog, container, false);

        // Get resources
        Bundle args = getArguments();

        if (args != null)
        {
            int title = args.getInt("title");
            int message = args.getInt("message");

            if (getDialog() != null)
                getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

            ((TextView) view.findViewById(R.id.textview_alert_dialog_title)).setText(title);
            ((TextView) view.findViewById(R.id.textview_alert_dialog_message)).setText(message);
        }

        // Assign listeners
        view.findViewById(R.id.button_alert_dialog_no).setOnClickListener(this);
        view.findViewById(R.id.button_alert_dialog_yes).setOnClickListener(this);

        return view;
    }

    /**
     * Called when this fragment is destroyed.
     */
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();

        if (dismissAction != null)
            dismissAction.run();
    }

    /**
     * Called when this fragment is resumed.
     */
    @Override
    public void onResume()
    {
        super.onResume();

        // Set size
        if (getDialog() != null)
        {
            getDialog().getWindow().setLayout(getResources().getDimensionPixelSize(R.dimen.alert_dialog_width),
                    getResources().getDimensionPixelSize(R.dimen.alert_dialog_height));
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.button_alert_dialog_yes)
        {
            if (yesAction != null)
                yesAction.run();
        }
        else if (v.getId() == R.id.button_alert_dialog_no)
        {
            if (noAction != null)
                noAction.run();
        }

        dismiss();
    }
}

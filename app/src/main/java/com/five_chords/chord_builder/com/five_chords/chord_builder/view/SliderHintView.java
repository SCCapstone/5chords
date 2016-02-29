package com.five_chords.chord_builder.com.five_chords.chord_builder.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * An overridden LinearLayout to contain the chord sliders and a mechanism for drawing hints on top of the sliders.
 * Created by Theodore on 2/28/2016.
 */
public class SliderHintView extends LinearLayout
{
    /**
     * Constructs a new SliderHintView with the given Context.
     * @param context The Context
     */
    public SliderHintView(Context context)
    {
        super (context);
    }

    /**
     * Constructs a new SliderHintView with the given Context and AttributeSet.
     * @param context The Context
     * @param attrs The AttributeSet
     */
    public SliderHintView(Context context, AttributeSet attrs)
    {
        super (context, attrs);
    }

    /**
     * Constructs a new SliderHintView with the given Context, AttributeSet, and default style attribute.
     * @param context The Context
     * @param attrs The AttributeSet
     * @param defStyleAttr The default style attribute
     */
    public SliderHintView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super (context, attrs, defStyleAttr);
    }
}

/***************************************************************************************************
 * VerticalSeekBar.java
 * This class creates a vertical scroll bar instead of horizontal
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder.com.five_chords.chord_builder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.five_chords.chord_builder.chordHandler;

public class VerticalSeekBar extends SeekBar
{
    /** The number of notes per slider */
    public static final int NUM_NOTES_PER_SLIDER = 16;

    /** The number of intermediate slider increments per note */
    public static final int NUM_INCREMENTS_PER_NOTE = 32*2;

    /** The maximum number of increments per Slider */
    private static final int MAX_SLIDER_PROGRESS = NUM_NOTES_PER_SLIDER * NUM_INCREMENTS_PER_NOTE;

    /** The check threshold */
    public static final int CHECK_THRESHOLD = NUM_INCREMENTS_PER_NOTE / 8;

    /** Indicates whether or not this SeekBar is currently touched by the user */
    private boolean isTouched;

    /** The Value of this VerticalSeekBar */
    private Value value;

    public VerticalSeekBar(Context context)
    {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    /**
     * Initializes this VerticalSeekBar.
     */
    public void initialize()
    {
        value = new Value();
        setMax(MAX_SLIDER_PROGRESS);
        setProgress(0);
    }

    /**
     * Gets whether or not this VerticalSeekBar is currently being touched by the user.
     * @return Whether or not this VerticalSeekBar is currently being touched by the user
     */
    public boolean isTouched()
    {
        return isTouched;
    }

    /**
     * Gets the note that is most closely selected.
     * @return The nearest note
     */
    public int getNearestNote()
    {
        value.set(getProgress());

        if (value.fractionSteps > NUM_INCREMENTS_PER_NOTE / 2)
            return Math.min(value.lowerChord + 1, NUM_NOTES_PER_SLIDER - 1);
        else
            return Math.max(value.lowerChord, 0);
    }

    /**
     * Gets the note that is most closely selected, replacing it with a dummy value if the selection is further than
     * the given threshold (as a fraction).
     * @param threshold The threshold value
     * @return The nearest note
     */
    public int getNearestNote(int threshold)
    {
        if (getDistanceToNearestNote() > CHECK_THRESHOLD)
            return -1;
        else
            return getNearestNote();
    }

    /**
     * Gets the distance to the note that is most closely selected.
     * @return The distance to the nearest note
     */
    public int getDistanceToNearestNote()
    {
        value.set(getProgress());

        if (value.fractionSteps > NUM_INCREMENTS_PER_NOTE / 2)
            return NUM_INCREMENTS_PER_NOTE - value.fractionSteps;
        else
            return value.fractionSteps;
    }

    /**
     * Gets the Value of this VerticalSeekBar.
     * @return The Value of this VerticalSeekBar
     */
    public Value getValue()
    {
        value.set(getProgress());
        return value;
    }

    /**
     * Sets the touch flag of this VerticalSeekBar.
     * @param touched The new value of the touch flag
     */
    public void setTouched(boolean touched)
    {
        isTouched = touched;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c)
    {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                isTouched = true;
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                chordHandler.builtChordChanged();
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                break;

            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                break;
        }
        return true;
    }

    /**
     * Class representing the Value of this VerticalSeekBar.
     */
    public class Value
    {
        /** The lower chord value */
        public int lowerChord;

        /** The number of steps above the lower chord */
        public int fractionSteps;

        /** The fractional value above the lower chord */
        public double fraction;

        /**
         * Sets this Value.
         * @param progress The progress of the bar
         */
        private void set(int progress)
        {
            lowerChord = progress / NUM_INCREMENTS_PER_NOTE;
            fractionSteps = progress % NUM_INCREMENTS_PER_NOTE;
            fraction = fractionSteps / (double) NUM_INCREMENTS_PER_NOTE;
        }
    }
}
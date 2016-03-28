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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.five_chords.chord_builder.chordHandler;

public class VerticalSeekBar extends SeekBar
{
    /** Indicates whether or not this SeekBar is currently touched by the user */
    private boolean isTouched;

    /**
     * Constructs a new VerticalSeekBar.
     * @param context The Context to set
     */
    public VerticalSeekBar(Context context)
    {
        super(context);
    }

    /**
     * Constructs a new VerticalSeekBar.
     * @param context The Context to set
     * @param attrs The attributes to set
     * @param defStyle The default style
     */
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Constructs a new VerticalSeekBar.
     * @param context The Context to set
     * @param attrs The attributes to set
     */
    public VerticalSeekBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Initializes this VerticalSeekBar.
     */
    public void initialize()
    {
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
     * Sets the touch flag of this VerticalSeekBar.
     * @param touched The new value of the touch flag
     */
    public void setTouched(boolean touched)
    {
        isTouched = touched;
    }

    /**
     * Called when this VerticalSeekBar is measured.
     * @param widthMeasureSpec The measured width
     * @param heightMeasureSpec The measrued height
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    /**
     * Called when this VerticalSeekBar is drawn.
     * @param c The draw Canvas
     */
    protected void onDraw(Canvas c)
    {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    /**
     * Called when the size of this VerticalSeekBar changes.
     * @param w The new width
     * @param h The new height
     * @param oldw The old width
     * @param oldh The old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    /**
     * Called on a touch event.
     * @param event The MotionEvent
     * @return Whether the state of this VerticalSeekBar changed as a result of the event
     */
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
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                break;

            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                break;
        }
        return true;
    }
}
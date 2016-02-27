/***************************************************************************************************
 * VerticalSeekBar.java
 * This class creates a vertical scroll bar instead of horizontal
 * @version 1.0
 * @date 06 November 2015
 * @author: Drea,Steven,Zach,Kevin,Bo
 **/
package com.five_chords.chord_builder;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar
{
    /** Indicates whether or not this SeekBar is currently touched by the user */
    private boolean isTouched;

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

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    /**
     * Gets whether or not this VerticalSeekBar is currently being touched by the user.
     * @return Whether or not this VerticalSeekBar is currently being touched by the user
     */
    protected boolean isTouched()
    {
        return isTouched;
    }

    /**
     * Sets the touch flag of this VerticalSeekBar.
     * @param touched The new value of the touch flag
     */
    public void setTouched(boolean touched)
    {
        isTouched = false;
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
                onSizeChanged(getWidth(), getHeight(), 0, 0);

                break;

            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                break;
        }
        return true;
    }
}
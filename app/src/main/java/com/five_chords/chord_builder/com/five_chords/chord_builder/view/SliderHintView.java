package com.five_chords.chord_builder.com.five_chords.chord_builder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.five_chords.chord_builder.R;

import java.util.List;

/**
 * An overridden LinearLayout to contain the chord sliders and a mechanism for drawing hints on top of the sliders.
 * Created by Theodore on 2/28/2016.
 */
public class SliderHintView extends LinearLayout
{
    /** A Paint handle for convenience */
    private static final Paint PAINT = new Paint();

    /** The delay between hint updates in milliseconds */
    private static final long HINT_UPDATE_DELAY = 16L;

    // Setup Paint
    static
    {
        PAINT.setAntiAlias(true);
    }

    /** The current on this SliderHintView */
    private Hint hint;

    /** The HintUpdater to use */
    private HintUpdater updater;

    /** The lock to use for synchronization */
    private final Object HINT_LOCK = new Object();

    /**
     * Constructs a new SliderHintView with the given Context.
     * @param context The Context
     */
    public SliderHintView(Context context)
    {
        super(context);
        initialize();
    }

    /**
     * Constructs a new SliderHintView with the given Context and AttributeSet.
     * @param context The Context
     * @param attrs The AttributeSet
     */
    public SliderHintView(Context context, AttributeSet attrs)
    {
        super (context, attrs);
        initialize();
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
        initialize();
    }

    /**
     * Sets the Hint on this SliderHintView.
     * @param hint The Hint
     */
    public void setHint(Hint hint)
    {
        // TODO
        Log.e("SHV", "Set Hint");

        synchronized (HINT_LOCK)
        {
            this.hint = hint;
            HINT_LOCK.notify();
        }
    }

    /**
     * Overridden dispatchDraw method implemented to draw a hints if needed.
     * @param canvas The Canvas on which to draw
     */
    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);

        // Draw hint
        synchronized (HINT_LOCK)
        {
            if (hint != null && hint.isVisible())
            {
                hint.draw(canvas);
            }
        }
    }

    /**
     * Called to initialize this SliderHintView.
     */
    private void initialize()
    {
        setWillNotDraw(false);
        updater = new HintUpdater();
        updater.isRunning = true;

        new Thread(updater).start();
    }

    /**
     * Gets the thumb position and size of the slider in this SliderHintView.
     * @param pos Will be filled with the new position, must be size three (size is stored at index two)
     */
    private void getThumbPosition(int[] pos)
    {
        // Calculate own position
        int mx, my;
        getLocationInWindow(pos);

        mx = pos[0];
        my = pos[1];

        // Calculate SeekBar position
        VerticalSeekBar bar = (VerticalSeekBar)findViewWithTag(getContext().getString(R.string.seek_bar_tag));
        bar.getLocationInWindow(pos);
        pos[0] -= mx;
        pos[1] -= my;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            Rect bounds = bar.getThumb().getBounds();
            pos[0] += bounds.centerY();
            pos[1] += bar.getHeight() - bounds.centerX();
            pos[2] = bounds.width();
        }
        else
        {
            // TODO there is some slight error in this calculation
            // Calculate thumb position
            mx = bar.getWidth(); // Reuse mx
            my = bar.getHeight() - bar.getPaddingTop() - bar.getPaddingBottom(); // Reuse my

            // Set position
            pos[0] += mx / 2;
            pos[1] += bar.getPaddingBottom() + Math.round(my * (1.0f - (float) bar.getProgress() / bar.getMax()));
            pos[2] = mx / 2;
        }
    }

    /**
     * Class to handle updating any hints on the SliderHintView.
     */
    private class HintUpdater implements Runnable
    {
        /** Denotes whether or not this Runnable is running */
        private boolean isRunning;

        /**
         * Starts executing the active part of the class' code. This method is
         * called when a thread is started that has been created with a class which
         * implements {@code Runnable}.
         */
        @Override
        public void run()
        {
            while (isRunning)
            {
                synchronized (HINT_LOCK)
                {
                    while (hint == null)
                    {
                        try
                        {
                            HINT_LOCK.wait();
                        }
                        catch (InterruptedException e)
                        {/* Ignore */}
                    }
                }

                synchronized (HINT_LOCK)
                {
                    if (hint.isDone())
                        hint = null;
                    else
                        hint.update();
                }

                post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        invalidate();
                    }
                });

                // Sleep
                try
                {
                    Thread.sleep(HINT_UPDATE_DELAY);
                }
                catch (InterruptedException e)
                {/* Ignore */}
            }
        }
    }

    /**
     * A circular Hint.
     */
    public class CircleHint extends Hint
    {
        /** The radius of this CircleHint. */
        private float radius;

        /**
         * Default constructor.
         */
        public CircleHint()
        {
            getThumbPosition(position);
            radius = position[2] * 0.5f;
        }

        /**
         * Override to update this Hint.
         */
        @Override
        public void update()
        {
            if (timer < 10)
                alpha = timer / 10.0f;
            else
            {
                alpha -= 0.0625f;

                if (alpha <= 0.0f)
                    timer = Integer.MIN_VALUE;
            }

            radius += position[2] * 0.025f;
            timer++;
        }

        /**
         * Override to draw this Hint.
         * @param canvas The drawing Canvas
         */
        public void draw(Canvas canvas)
        {
            PAINT.setColor(Color.WHITE);
            PAINT.setAlpha(Math.round(alpha * 255.0f));
            PAINT.setStyle(Paint.Style.FILL);
            canvas.drawCircle(position[0], position[1], radius, PAINT);
        }
    }

    /**
     * Class representing a hint which will be drawn on the SliderHintView and fade out.
     */
    public abstract class Hint
    {
        /** A timer for drawing */
        protected int timer;

        /** The current transparency value of this Hint */
        protected float alpha;

        /** The position of this Hint */
        protected int[] position;

        /**
         * Default constructor.
         */
        public Hint()
        {
            timer = 0;
            alpha = 1.0f;
            position = new int[3];
        }

        /**
         * Tests whether or not this Hint is visible.
         * @return Whether or not this Hint is visible
         */
        public boolean isVisible()
        {
            return alpha > 0.0f;
        }

        /**
         * Tests whether or not this Hint is done and should be removed.
         * @return Whether or not this Hint is done and should be removed
         */
        public boolean isDone()
        {
            return timer < 0;
        }

        /**
         * Override to update this Hint.
         */
        public abstract void update();

        /**
         * Override to draw this Hint.
         * @param canvas The drawing Canvas
         */
        public abstract void draw(Canvas canvas);
    }
}

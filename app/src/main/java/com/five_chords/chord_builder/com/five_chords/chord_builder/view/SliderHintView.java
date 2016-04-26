package com.five_chords.chord_builder.com.five_chords.chord_builder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.five_chords.chord_builder.Note;
import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.ChordHandler;
import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;
import com.five_chords.chord_builder.com.five_chords.chord_builder.fragment.SliderFragment;

/**
 * An overridden LinearLayout to contain the chord sliders and a mechanism for drawing hints on top of the sliders.
 * @author tstone95
 */
public class SliderHintView extends LinearLayout
{
    /** A Paint handle for convenience */
    private static final Paint PAINT = new Paint();

    /** The delay between hint updates in milliseconds */
    private static final long HINT_UPDATE_DELAY = 12L;

    // Setup Paint
    static
    {
        PAINT.setAntiAlias(true);
    }

    /** The current on this SliderHintView */
    private Hint hint;

    /** The Thread to handle updating and drawing hints. */
    private HintUpdater updater;

    /** The current SliderFragment. */
    private SliderFragment sliderFragment;

    /** The lock to use for synchronization */
    private static final Object HINT_LOCK = new Object();

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
     * Called when this View is detached from the Window.
     */
    @Override
    public void onDetachedFromWindow()
    {
        // Delete updater thread if needed
        if (updater != null)
        {
            updater.isRunning = false;
            updater.interrupt();
            updater = null;
        }

        synchronized (HINT_LOCK)
        {
            HINT_LOCK.notifyAll();
        }

        super.onDetachedFromWindow();
    }

    /**
     * Sets the Hint on this SliderHintView.
     * @param type The type of hint
     * @param testNote The test Note
     * @param actualNote The actual Note
     * @param sliderFragment The current SliderFragment in use
     * @param delay The delay to display the hint, in milliseconds
     */
    public void setHint(final byte type, final Note testNote,
                        final Note actualNote, final SliderFragment sliderFragment, long delay)
    {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setHint(type, testNote, actualNote, sliderFragment);
            }
        }, delay);
    }

    /**
     * Sets the Hint on this SliderHintView.
     * @param type The type of hint
     * @param testNote The test Note
     * @param actualNote The actual Note
     */
    private void setHint(byte type, Note testNote, Note actualNote, SliderFragment sliderFragment)
    {
        this.sliderFragment = sliderFragment;

        synchronized (HINT_LOCK)
        {
            double diff = testNote.getFractionalIndex() - actualNote.getFractionalIndex();

            if (Math.abs(diff) < MainActivity.getOptions().allowableCheckError)
                diff = 0.0;

            if (type == ChordHandler.HINT_ONE)
                hint = new CircleHint(testNote, diff == 0.0 ? Color.GREEN : Color.RED);
            else if (type == ChordHandler.HINT_TWO)
            {
                if (diff == 0.0)
                    hint = new CircleHint(testNote, Color.GREEN);
                else
                    hint = new TriangleHint(testNote, diff < 0.0);
            }
            else if (type == ChordHandler.HINT_THREE)
                hint = new CircleHint(actualNote, diff == 0.0 ? Color.GREEN : Color.BLUE);

            HINT_LOCK.notifyAll();
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

        if (updater != null)
        {
            updater.isRunning = false;
            updater.interrupt();
            synchronized (HINT_LOCK)
            {
                HINT_LOCK.notifyAll();
            }
        }

        updater = new HintUpdater();
        updater.isRunning = true;
        updater.start();
    }

    /**
     * Gets the thumb position and size of the slider in this SliderHintView.
     * @param pos Will be filled with the new position, must be size three (size is stored at index two)
     * @param desiredProgress The desired thumb progress
     */
    private void getThumbPosition(int[] pos, int desiredProgress)
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

        // Calculate thumb position
        mx = bar.getWidth(); // Reuse mx
        my = bar.getHeight() - bar.getPaddingTop() - bar.getPaddingBottom() - mx; // Reuse my

        // Set position
        mx /= 2;
        pos[0] += mx;
        pos[1] += mx + Math.round(my * (1.0f - (float) desiredProgress / bar.getMax()));
        pos[2] = mx;
    }


    /**
     * Class to handle updating any hints on the SliderHintView.
     */
    private class HintUpdater extends Thread
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
                // Wait until there is a hint to draw
                synchronized (HINT_LOCK)
                {
                    while (hint == null && isRunning)
                    {
                        try
                        {
                            HINT_LOCK.wait();
                        }
                        catch (InterruptedException e)
                        {
                            if (!isRunning)
                                return;
                        }
                    }
                }

                // While there is a hint, and while that hint is not done, update the hint
                synchronized (HINT_LOCK)
                {
                    if (hint != null)
                    {
                        if (hint.isDone())
                            hint = null;
                        else
                            hint.update();
                    }
                }

                if (!isRunning)
                    return;

                // Invalidate the fragment so that it redraws
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
                {
                    if (!isRunning)
                        return;
                }
            }
        }
    }

    /**
     * A circular Hint.
     */
    private class CircleHint extends Hint
    {
        /** The color of this CircleHint */
        protected int color;

        /** The radius of this CircleHint. */
        protected float radius;

        /**
         * Constructs a new CircleHint
         * @param note The Note on which this Hint will appear
         * @param color The color of this CircleHint
         */
        public CircleHint(Note note, int color)
        {
            super (note);
            this.color = color;
            radius = position[2] * 0.5f;
        }

        /**
         * Updates this Hint.
         */
        @Override
        public void update()
        {
            super.update();
            radius += position[2] * 0.025f;
        }

        /**
         * Override to draw this Hint.
         * @param canvas The drawing Canvas
         */
        public void draw(Canvas canvas)
        {
            PAINT.setColor(color);
            PAINT.setAlpha(Math.round(alpha * 255.0f));
            PAINT.setStyle(Paint.Style.FILL);
            canvas.drawCircle(position[0], position[1], radius, PAINT);
        }
    }

    /**
     * A triangular Hint. Can face up or down.
     */
    private class TriangleHint extends Hint
    {
        /** The Triangle Path */
        private final Path TRIANGLE;

        /**
         * Constructs a new TriangleHint
         * @param note The Note on which this Hint will appear
         * @param up Whether or not this TriangleHint faces up
         */
        public TriangleHint(Note note, boolean up)
        {
            super(note, 20, 40);

            final float sign = up ? 1.0f : -1.0f;
            final float x = position[0];
            final float radius = position[2];
            final float y = position[1] - radius * sign;

            TRIANGLE = new Path();
            TRIANGLE.setFillType(Path.FillType.EVEN_ODD);
            TRIANGLE.moveTo(x - radius, y);
            TRIANGLE.lineTo(x, y - radius * sign);
            TRIANGLE.lineTo(x + radius, y);
            TRIANGLE.close();
        }

        /**
         * Override to draw this Hint.
         * @param canvas The drawing Canvas
         */
        public void draw(Canvas canvas)
        {
            PAINT.setColor(Color.RED);
            PAINT.setAlpha(Math.round(alpha * 255.0f));
            PAINT.setStyle(Paint.Style.FILL);
            canvas.drawPath(TRIANGLE, PAINT);
        }
    }

    /**
     * Class representing a hint which will be drawn on the SliderHintView and fade out.
     */
    public abstract class Hint
    {
        /** A timer for drawing */
        private int timer;

        /** The time to grow */
        private final int GROW_TIME;

        /** The time to fade */
        private final int FADE_TIME;

        /** The current transparency value of this Hint */
        protected float alpha;

        /** The position of this Hint */
        protected int[] position;

        /**
         * Constructs a new Hint.
         * @param note The Note on which this Hint will appear
         */
        public Hint(Note note)
        {
            this (note, 30, 50);
        }

        /**
         * Constructs a new Hint.
         * @param note The Note on which this Hint will appear
         * @param growTime The grow time
         * @param fadeTime The fade time
         */
        public Hint(Note note, int growTime, int fadeTime)
        {
            GROW_TIME = growTime;
            FADE_TIME = fadeTime;
            timer = 0;
            alpha = 1.0f;
            position = new int[3];

            getThumbPosition(position, sliderFragment.getSliderProgressForNote(note));
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
         * Updates this Hint.
         */
        public void update()
        {
            if (timer < GROW_TIME)
                alpha = timer / (float) GROW_TIME;
            else
            {
                alpha = 1.0f - (timer - GROW_TIME) / (float)FADE_TIME;

                if (alpha <= 0.0f)
                    timer = Integer.MIN_VALUE;
            }
            timer++;
        }

        /**
         * Override to draw this Hint.
         * @param canvas The Canvas to draw on
         */
        public abstract void draw(Canvas canvas);
    }
}

package com.five_chords.chord_builder.com.five_chords.chord_builder.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.five_chords.chord_builder.R;
import com.five_chords.chord_builder.Score;

import java.util.Date;
import java.util.LinkedList;

/**
 * Extension of View overridden to draw a progress graph for a chords score history.
 */
public class ScoreProgressView extends View
{
    /** A Paint handle for convenience */
    private static final Paint PAINT = new Paint();

    /** A Rect handle for convenience */
    private static final Rect BOUNDS = new Rect();

    /** Keeps track of whether or not the Paint in this class has been initialized */
    private static boolean isInitialized = false;

    /** The size of the border to use for the history tag names */
    private static int historyTagBorder;

    /** The height of the history tag names */
    private static int historyTagHeight;

    /** The height of the history tag names */
    private static int historyTagWidth;

    /** The width of this ScoreProgressView */
    private int width;

    /** The height of this ScoreProgressView */
    private int height;

    /** The array of ScorePoints wrapping the score history. */
    private ScorePoint[] points;

    /**
     * Constructs a new ScoreProgressView with the given Context.
     * @param context The Context
     */
    public ScoreProgressView(Context context)
    {
        super (context);
    }

    /**
     * Constructs a new ScoreProgressView with the given Context and AttributeSet.
     * @param context The Context
     * @param attrs The AttributeSet
     */
    public ScoreProgressView(Context context, AttributeSet attrs)
    {
        super (context, attrs);
    }

    /**
     * Constructs a new ScoreProgressView with the given Context, AttributeSet, and default style attribute.
     * @param context The Context
     * @param attrs The AttributeSet
     * @param defStyleAttr The default style attribute
     */
    public ScoreProgressView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super (context, attrs, defStyleAttr);
    }

    /**
     * Called to initialize the Paint used in instances of ScoreProgressView.
     * @param activity The current Activity
     */
    public static void initializePaint(Activity activity)
    {
        if (isInitialized)
            return;

        isInitialized = true;

        PAINT.setAntiAlias(true);
        PAINT.setTextAlign(Paint.Align.CENTER);
        PAINT.setTextSize(activity.getResources().getDimensionPixelSize(R.dimen.text_size_small));

        // Compute history tag width and height
        String tag;
        historyTagHeight = 0;
        historyTagWidth = 0;
        for (int i = 0; i < Score.HISTORY_TAGS.length; ++i)
        {
            tag = Score.HISTORY_TAGS[i];
            PAINT.getTextBounds(tag, 0, tag.length() - 1, BOUNDS);

            if (BOUNDS.width() > historyTagWidth)
                historyTagWidth = BOUNDS.width();

            if (BOUNDS.height() > historyTagHeight)
                historyTagHeight = BOUNDS.height();
        }

        // Set the border size
        historyTagBorder = (int)(historyTagWidth * 0.75f);

        // Double the width
        historyTagWidth *= 2;

        // Double the height
        historyTagHeight *= 2;
    }

    /**
     * Calculates the width needed to fit the given history.
     * @param history The Score.DiscreteScoreHistory
     * @return The width needed to fit the given history
     */
    public static int calculateWidth(Score.DiscreteScoreHistory history)
    {
        return historyTagBorder + historyTagWidth * history.size;
    }

    /**
     * Calculates the height needed for ScoreProgressView.
     * @return The height needed for ScoreProgressView
     */
    public static int calculateHeight()
    {
        return historyTagHeight * 5; // 2 times to fit text, 3 times for content
    }

    /**
     * Sets the width in pixels of this ScoreProgressView.
     * @param width The width in pixels of this ScoreProgressView.
     */
    public void setWidthPixels(int width)
    {
        this.width = width;
    }

    /**
     * Gets the width in pixels of this ScoreProgressView.
     * @return The width in pixels of this ScoreProgressView.
     */
    public int getWidthPixels()
    {
        return width;
    }

    /**
     * Sets the height in pixels of this ScoreProgressView.
     * @param height The height in pixels of this ScoreProgressView.
     */
    public void setHeightPixels(int height)
    {
        this.height = height;
    }

    /**
     * Gets the height in pixels of this ScoreProgressView.
     * @return The height in pixels of this ScoreProgressView.
     */
    public int getHeightPixels()
    {
        return height;
    }

    /**
     * Sets the Score.DiscreteScoreHistory displayed by this ScoreProgressView.
     * @param history The Score.DiscreteScoreHistory to be displayed
     */
    public void setHistory(Score.DiscreteScoreHistory history)
    {
        if (history.size == 0)
        {
            points = null;
            return;
        }

        // Allocate array
        points = new ScorePoint[history.size];

        // Add points
        int index = 0;
        int x = historyTagWidth;
        float yBase = historyTagHeight * 1.5f / height;
        float yScale = 1.0f - 2 * yBase;
        ScorePoint point;
        Score.ScoreValue value;

        for (int j = 0; j < history.values.length; ++j)
        {
            value = history.values[j];

            if (value != null)
            {
                point = new ScorePoint();
                point.percent = value.numTotalGuesses == 0 ? 0.0f : (float) value.numCorrectGuesses / value.numTotalGuesses;
                point.x = 1.0f - (float)x / width;
                point.y = yBase + (1.0f - point.percent) * yScale;
                point.label = Score.HISTORY_TAGS[j];
                points[index++] = point;
                x += historyTagWidth;
            }
        }
    }

    /**
     * Overridden onDraw method implemented to draw a progress graph.
     * @param canvas The Canvas on which to draw
     */
    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();

        // Clear the canvas
        canvas.drawColor(0x00000000);

        if (points != null && points.length > 0)
        {
            float radius = historyTagHeight * 0.125f;

            // Draw points
            float x, y;
            String text;
            for (int i = 0; i < points.length; ++i)
            {
                // Get position of point
                x = points[i].x * w;
                y = points[i].y * h;

                // Draw circles on each point
                PAINT.setStrokeWidth(2.0f);
                PAINT.setStyle(Paint.Style.FILL);
                PAINT.setColor(Color.LTGRAY);
                canvas.drawCircle(x, y, radius, PAINT);

                // Draw lines between every two points
                if (i > 0)
                {
                    PAINT.setStyle(Paint.Style.STROKE);
                    PAINT.setColor(Color.LTGRAY);
                    canvas.drawLine(x, y, points[i - 1].x * w, points[i - 1].y * h, PAINT);
                }

                // Draw percent above point
                PAINT.setStrokeWidth(1.0f);
                PAINT.setStyle(Paint.Style.FILL);
                PAINT.setColor(Color.LTGRAY);
                text = "" + Math.round(points[i].percent * 100.0f) + " %";
                drawText(canvas, text, x, 0.0f);

                // Draw history tags beneath points
                PAINT.setColor(Color.LTGRAY);
                drawText(canvas, points[i].label, x, h);
            }
        }
    }

    /**
     * Draws Text on the given Canvas, constraining it always be on the Canvas. Uses the global Paint in this class.
     * @param canvas The Canvas on which to draw
     * @param text The text to draw
     * @param x The x coordinate of the text position on the Canvas
     * @param y The y coordinate of the text position on the Canvas
     */
    private void drawText(Canvas canvas, String text, float x, float y)
    {
        PAINT.getTextBounds(text, 0, text.length() - 1, BOUNDS);

        int width = (BOUNDS.width() + 4);
        int height = (BOUNDS.height() + 4);

        canvas.drawText(text,
                Math.max(Math.min(x, canvas.getWidth() - width * 0.5f), width * 0.5f),
                Math.max(Math.min(y, canvas.getHeight() - height * 0.5f), height), PAINT);
    }

    /**
     * Class wrapping a single score.
     */
    private class ScorePoint
    {
        /** The x coordinate of the point. */
        private float x;

        /** The y coordinate of the point. */
        private float y;

        /** The percent value of the point. */
        private float percent;

        /** The label of this point */
        private String label;
    }
}

package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    /** The size of the border to use for the history tag names */
    private static int historyTagBorder;

    /** The height of the history tag names */
    private static int historyTagHeight;

    /** Array of the widths of the History tag strings */
    private static int[] historyTagWidths = new int[Score.HISTORY_TAGS.length];

    // Setup Paint
    static
    {
        PAINT.setAntiAlias(true);
        PAINT.setTextAlign(Paint.Align.CENTER);
        PAINT.setTextSize(18.0f);

        // Compute history tag widths
        String tag;
        historyTagHeight = 0;
        for (int i = 0; i < historyTagWidths.length; ++i)
        {
            tag = Score.HISTORY_TAGS[i];
            PAINT.getTextBounds(tag, 0, tag.length() - 1, BOUNDS);
            historyTagWidths[i] = BOUNDS.width();

            if (BOUNDS.height() > historyTagHeight)
                historyTagHeight = BOUNDS.height();
        }

        // Set the border size (for now use half the size of 'Earlier this Month')
        historyTagBorder = historyTagWidths[0] / 2;

        // Double the height
        historyTagHeight *= 2;
    }

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
     * Calculates the width needed to fit the given history.
     * @param history The Score.DiscreteScoreHistory
     * @return The width needed to fit the given history
     */
    public static int calculateWidth(Score.DiscreteScoreHistory history)
    {
        int width = historyTagBorder * 2;

        int i = 0;
        for (Score.ScoreValue value: history.values)
        {
            if (value != null)
                width += historyTagWidths[i];
            ++i;
        }

        return width;
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
        // Allocate array
        points = new ScorePoint[history.size];

        // Add points
        int index = 0;
        int x = historyTagBorder;
        ScorePoint point;
        Score.ScoreValue value;

        for (int j = 0; j < history.values.length; ++j)
        {
            value = history.values[j];

            if (value != null)
            {
                point = new ScorePoint();
                point.percent = value.numTotalGuesses == 0 ? 0.0f : (float) value.numCorrectGuesses / value.numTotalGuesses;
                point.x = (float)x / width;
                point.y = 1.0f - point.percent;
                points[index++] = point;
                x += historyTagWidths[j];
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

//        // Draw background
//        PAINT.setColor(Color.WHITE);
//        PAINT.setStyle(Paint.Style.FILL);
//        canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, PAINT);

        if (points != null && points.length > 0)
        {
            float wBorder = w * 0.0625f;
            float xScale = w - 2.0f * wBorder ;
            float yScale = h - 2.0f * historyTagHeight;
            float radius = historyTagHeight * 0.125f;

            // Draw points
            float x, y;
            String text;
            for (int i = 0; i < points.length; ++i)
            {
                // Get position of point
                x = wBorder + points[i].x * xScale;
                y = historyTagHeight + points[i].y * yScale;

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
                    canvas.drawLine(x, y, wBorder + points[i - 1].x * xScale,
                            historyTagHeight + points[i - 1].y * yScale, PAINT);
                }

                // Draw percent above point
                PAINT.setStrokeWidth(1.0f);
                PAINT.setStyle(Paint.Style.FILL);
                PAINT.setColor(Color.DKGRAY);
                text = "" + Math.round(points[i].percent * 100.0f) + " %";
                drawText(canvas, text, x, 0.0f);

                // Draw history tags beneath points
                PAINT.setColor(Color.LTGRAY);
                drawText(canvas, Score.HISTORY_TAGS[i], x, h);

                // TODO draw other strings over previous points, such as 'Earlier today', 'last week', etc
                // TODO Possibly make the spacing between points constant and put the whole view in a
                // TODO horizontal scroll view
            }
        }

//        // Draw Border
//        PAINT.setColor(Color.DKGRAY);
//        PAINT.setStyle(Paint.Style.STROKE);
//        canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, PAINT);
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
    }
}

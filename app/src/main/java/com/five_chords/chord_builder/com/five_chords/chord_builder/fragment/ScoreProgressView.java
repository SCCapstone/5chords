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

    // Setup Paint
    static
    {
        PAINT.setAntiAlias(true);
        PAINT.setTextAlign(Paint.Align.CENTER);
        PAINT.setTextSize(18.0f);
    }

    /** The CurrentScoreWrapper attached to this ScoreProgressView. */
    private Score.CurrentScoreWrapper score;

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
     * Sets the CurrentScoreWrapper attached to this ScoreProgressView.
     * @param score The new CurrentScoreWrapper
     */
    public void setScore(Score.CurrentScoreWrapper score)
    {
        this.score = score;

        // Load points (if possible)
        if (score.getHistory() != null)
            createScorePointArray(score.getHistory());
        else
            points = null;
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

        // Draw background
        PAINT.setColor(Color.WHITE);
        PAINT.setStyle(Paint.Style.FILL);
        canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, PAINT);

        if (points != null && points.length > 0)
        {
            float wBorder = w * 0.0625f;
            float hBorder = h * 0.0625f;
            float xScale = w - 2.0f * wBorder ;
            float yScale = h - 2.0f * hBorder;
            float radius = w / (96.0f);

            // Draw points
            float x, y;
            String text;
            for (int i = 0; i < points.length; ++i)
            {
                // Get position of point
                x = wBorder + points[i].x * xScale;
                y = hBorder + points[i].y * yScale;

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
                            hBorder + points[i - 1].y * yScale, PAINT);
                }

                // Draw percent above point
                PAINT.setStrokeWidth(1.0f);
                PAINT.setStyle(Paint.Style.FILL);
                PAINT.setColor(Color.DKGRAY);
                text = "" + Math.round(points[i].percent * 100.0f) + " %";
                drawText(canvas, text, x, y);

                // Draw 'Today' over last point
                if (i == points.length - 1)
                {
                    PAINT.setColor(Color.LTGRAY);
                    drawText(canvas, "Today", x, points[i].y > 0.0f ? 0.0f : h);
                }

                // TODO draw other strings over previous points, such as 'Earlier today', 'last week', etc
                // TODO Possibly make the spacing between points constant and put the whole view in a
                // TODO horizontal scroll view
            }
        }

        // Draw Border
        PAINT.setColor(Color.DKGRAY);
        PAINT.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, PAINT);
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
        canvas.drawText(text,
                Math.max(Math.min(x, canvas.getWidth() - BOUNDS.width()), BOUNDS.width()),
                Math.max(Math.min(y, canvas.getHeight() - BOUNDS.height()), BOUNDS.height()), PAINT);
    }

    /**
     * Creates the ScorePoint array.
     * @param scores The list of ScoreWrappers to use to create the array
     */
    private void createScorePointArray(LinkedList<Score.ScoreWrapper> scores)
    {
        // Allocate array
        points = new ScorePoint[scores.size()];

        if (points.length == 0)
        {
            Log.e("ScoreProgressView", "Zero length history for " + score.CHORD_NAME);
            return;
        }

        // First calculate total time span
        long startTime = scores.getLast().time;
        long timeSpan = scores.getFirst().time - startTime;

        // Add points
        int i = 0;
        ScorePoint point;
        Log.w("SPV", "Points for " + score.CHORD_NAME + ":");
        for (Score.ScoreWrapper wrapper: scores)
        {
            point = new ScorePoint();
            point.percent = wrapper.numTotalGuesses == 0 ? 0.0f : (float)wrapper.numCorrectGuesses / wrapper.numTotalGuesses;
            point.x = timeSpan == 0L ? 0.0f : (float)((wrapper.time - startTime) / (double)timeSpan);
            point.y = 1.0f - point.percent;
            points[points.length - 1 - (i++)] = point;

            Log.w("\tPoint", "Pos = " + point.x + ", " + point.y + " time = " + new Date(wrapper.time).toString());
        }
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

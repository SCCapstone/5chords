package com.five_chords.chord_builder.com.five_chords.chord_builder.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.five_chords.chord_builder.Score;

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
            float xScale = 2.0f * (w - 2.0f * wBorder) / (float) points.length;
            float radius = w / (96.0f);

            // Draw points
            float x, y;
            String text;
            for (int i = 0; i < points.length; ++i)
            {
                // Gte position of point
                x = wBorder + points[i].x * xScale;
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
                    canvas.drawLine(x, y, wBorder + points[i - 1].x * xScale, points[i - 1].y * h, PAINT);
                }

                // Draw percent above point
                PAINT.setStrokeWidth(1.0f);
                PAINT.setStyle(Paint.Style.STROKE);
                PAINT.setColor(Color.DKGRAY);
                text = "" + Math.round(points[i].percent * 100.0f) + " %";
                PAINT.getTextBounds(text, 0, text.length() - 1, BOUNDS);
                canvas.drawText(text, x, Math.max(Math.min(y - BOUNDS.height() * 0.5f,
                        h - BOUNDS.height()), BOUNDS.height()), PAINT);
            }
        }

        // Draw Border
        PAINT.setColor(Color.DKGRAY);
        PAINT.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0.0f, 0.0f, (float) w, (float) h, PAINT);
    }

    /**
     * Creates the ScorePoint array.
     * @param scores The list of ScoreWrappers to use to create the array
     */
    private void createScorePointArray(LinkedList<Score.ScoreWrapper> scores)
    {
        // Allocate array
        points = new ScorePoint[score.getHistory().size()];

        if (points.length == 0)
            return;

        // First calculate total time span
        long startTime = scores.getLast().time;
        long timeSpan = scores.getFirst().time - startTime;

        // Add points
        int i = 0;
        ScorePoint point;
        for (Score.ScoreWrapper wrapper: scores)
        {
            point = new ScorePoint();
            point.percent = (float)wrapper.numCorrectGuesses / wrapper.numTotalGuesses;
            point.x = timeSpan == 0L ? 0.0f : (float)((wrapper.time - startTime) / timeSpan);
            point.y = 1.0f - point.percent;
            points[points.length - 1 - (i++)] = point;
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

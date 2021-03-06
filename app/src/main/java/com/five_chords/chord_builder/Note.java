package com.five_chords.chord_builder;

/**
 * Class representing a Note with an integer in the range [0, 11]. Notes also have an optional double value which
 * represents a fractional distance to the integer note value. This distance is typically in the range [-1, 1].
 * @author tstone95
 */
public class Note
{
    /** The number of notes in an octave. */
    public static final int NUM_NOTES = 12;

    /** The array of note names. */
    public static final String[] NOTE_NAMES = new String[] {"C", "C#", "D", "E\u266D",
            "E", "F", "F#", "G",
            "A\u266D", "A", "B\u266D", "B"};

    /** The index of this Note. */
    public int index;
    /** The fractional distance to the index, in the range [-1, 1]. */
    public double distanceToIndex;

    /**
     * Sets the value of this Note.
     * @param index The index of this Note
     */
    public void set(int index)
    {
        set(index, 0.0);
    }

    /**
     * Sets the value of this Note.
     * @param index The index of this Note
     * @param distanceToIndex The fractional distance to the index, in the range [-1, 1]
     */
    public void set(int index, double distanceToIndex)
    {
        this.index = index;
        this.distanceToIndex = distanceToIndex;
    }

    /**
     * Gets the fractional index of this Note.
     * @return The fractional index of this Note
     */
    public double getFractionalIndex()
    {
        return index + distanceToIndex;
    }

    /**
     * Gets a String representation of this Note.
     * @return A String representation of this Note
     */
    @Override
    public String toString()
    {
        return "Note: index = " + index + ", distance = " + distanceToIndex;
    }
}

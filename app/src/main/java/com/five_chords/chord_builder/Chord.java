package com.five_chords.chord_builder;

import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Class representing a single Chord.
 * Created by Theodore on 3/24/2016.
 */
public class Chord
{
    /** The unique id of this Chord. */
    public final long ID;

    /** The fundamental note of this Chord. */
    public final int FUNDAMENTAL;

    /** The type of this Chord. */
    public final ChordType TYPE;

    /**
     * Enum containing the note offsets for various types of chords.
     */
    public enum ChordType
    {
        MAJOR("Major", "Maj", 0, 4, 7),
        MINOR("Minor", "Min", 0, 3, 7),
        DOMINANT("Dominant", "Dom", 0, 4, 7, 10),
        AUGMENTED_TRIAD("Augmented Triad", "Aug", 0, 4, 8);

        /** The name of the ChordType. */
        public final String name;
        /** The notation of this ChordType. */
        public final String notation;
        /** The note offsets of this ChordType. */
        public final int[] offsets;

        /**
         * Constructor for ChordType.
         * @param name The name of the ChordType
         * @param notation The notation of the ChordType
         * @param offsets The note offsets of the ChordType
         */
        ChordType(String name, String notation, int ... offsets)
        {
            this.name = name;
            this.notation = notation;
            this.offsets = offsets;
        }

        /**
         * Gets a String representation of this Chord.
         * @return A String representation of this Chord
         */
        @Override
        public String toString()
        {
            return name;
        }
    }

    /**
     * Constructs a new Chord.
     * @param fundamental The fundamental note of the Chord
     * @param type The type of the Chord
     */
    public Chord(int fundamental, ChordType type)
    {
        this.ID = getChordId(fundamental, type);
        this.FUNDAMENTAL = fundamental;
        this.TYPE = type;
    }

    /**
     * Constructs a new Chord.
     * @param id The id of the Chord
     */
    public Chord(long id)
    {
        ID = id;
        FUNDAMENTAL = (int)id;
        TYPE = ChordType.values()[(int)(id >>> Integer.SIZE)];
    }

    /**
     * Gets a unique value from the fundamental note - ChordType pair.
     * @param fundamental The fundamental note
     * @param type The ChordType
     * @return A unique value from the fundamental note - ChordType pair
     */
    public static long getChordId(int fundamental, ChordType type)
    {
        return (long)fundamental | (((long)type.ordinal()) << Integer.SIZE);
    }

    /**
     * Compares two Chords represented by int arrays.
     * @param chordA The first chord to compare
     * @param chordB The second chord to compare
     * @param length The number of Notes to compare in each chord
     * @return Whether or not the two Chords are the same
     */
    public static boolean compareChords(Note[] chordA, Note[] chordB, int length)
    {
        double maxErr = MainActivity.getOptions().allowableCheckError;

        for (int i = 0; i < length; ++i)
            if (Math.abs(chordA[i].getFractionalIndex() - chordB[i].getFractionalIndex()) > maxErr)
                return false;

        return true;
    }

    /**
     * Shuffles the indices beyond the given position in the array.
     * @param array The array to shuffle
     * @param start The start index; indices larger than this will be shuffled
     * @param result An array in which to put the result, should be the same size as array
     */
    private static void shuffle(int[] array, int start, int[] result)
    {
        // Copy the indices
        System.arraycopy(array, 0, result, 0, array.length);

        // Shuffle the indices
        Random random = new Random();

        // Using the Fisher–Yates shuffle
        int temp, index;
        for (int i = array.length - 1; i > start; i--)
        {
            index = start + random.nextInt(i + 1 - start);

            // Simple swap
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Gets the default root position of this Chord, as defined in the ChordType.
     * @param word The array into which to put the given position of this Chord
     */
    public void getRootPosition(Note[] word)
    {
        for (int i = 0; i < TYPE.offsets.length; ++i)
            word[i].set(FUNDAMENTAL + TYPE.offsets[i]);

        if (TYPE.offsets.length == 3)
            word[3].set(-1);
    }

    /**
     * Gets the root position of this Chord, randomly ordering the notes after the root.
     * @param word The array into which to put the given position of this Chord
     */
    public void getRandomRootPosition(Note[] word)
    {
//        // Shuffle the offsets other than the root TODO
//        int[] shuffled = new int[TYPE.offsets.length];
//        shuffle(TYPE.offsets, 1, shuffled);
//
//        // Increment offsets that are out of order
//        for (int i = 2; i < word.length; ++i)
//            while (word[i].index < word[i - 1].index)
//                word[i].index += Note.NUM_NOTES;
//
//        // Add fundamental position
//        for (int i = 0; i < word.length; ++i)
//        {
//            word[i].index += FUNDAMENTAL;
//            word[i].distanceToIndex = 0.0;
//        }
//
//        if (TYPE.offsets.length == 3)
//            word[3].set(-1);
    }

    /**
     * Gets a random spelling of this Chord.
     * @param word The array into which to put the given position of this Chord
     */
    public void getRandomSpelling(Note[] word)
    {
//        // Shuffle the offsets TODO
//        shuffle(TYPE.offsets, 0, word);
//
//        // Increment offsets that are out of order
//        for (int i = 1; i < word.length; ++i)
//            while (word[i].index < word[i - 1].index)
//                word[i].index += Note.NUM_NOTES;
//
//        // Add fundamental position
//        for (int i = 0; i < word.length; ++i)
//        {
//            word[i].index += FUNDAMENTAL;
//            word[i].distanceToIndex = 0.0;
//        }
//
//        if (TYPE.offsets.length == 3)
//            word[3].set(-1);
    }

    /**
     * Gets a String representation of this Chord.
     * @return A String representation of this Chord
     */
    @Override
    public String toString()
    {
        return Note.NOTE_NAMES[FUNDAMENTAL] + " " + TYPE.notation;
    }
}

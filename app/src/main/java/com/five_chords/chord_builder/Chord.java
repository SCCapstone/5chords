package com.five_chords.chord_builder;


import com.five_chords.chord_builder.com.five_chords.chord_builder.activity.MainActivity;

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
        AUGMENTED_TRIAD("Augmented Triad", "Aug", 0, 4, 8),
        DIMINISHED_TRIAD("Diminished Triad", "Dim", 0, 3, 6),
        DIMINISHED_SEVENTH("Diminished Seventh", "Dim7", 0, 3, 6, 9),
        MINOR_SEVENTH("Minor Seventh", "Min7", 0, 3, 7, 10),
        MINOR_MAJOR_SEVENTH("Minor Major Seventh", "mM7", 0, 3, 7, 11),
        MAJOR_SEVENTH("Major Seventh", "Maj7", 0, 4, 7, 11),
        AUGMENTED_SEVENTH("Augmented Seventh", "Aug7", 0, 4, 8, 10),
        AUGMENTED_MAJOR_SEVENTH("Augmented Major Seventh", "AugMaj7", 0, 4, 8, 11);

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
            return name + " Chords";
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
     * Constructs a new Chord of type 0 and fundamental 0.
     */
    public Chord()
    {
        this (0L);
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
     * Compares two Chords represented by Note arrays.
     * @param chordA The first chord to compare
     * @param chordB The second chord to compare
     * @param length The number of Notes to compare in each chord
     * @return Whether or not the two Chords are the same
     */
    public static boolean compareChords(Note[] chordA, Note[] chordB, int length)
    {
        double maxErr = MainActivity.getOptions().allowableCheckError;

        // Check that each note of chordA is represented somewhere in chordB, up to length
        boolean foundNote;

        // Loop over each note of chordA
        for (int i = 0; i < length; ++i)
        {
            foundNote = false;

            // Loop over the notes of chordB, looking for the current note in chordA
            for (int j = 0; !foundNote && j < length; ++j)
            {
                if (Math.abs(chordB[i].getFractionalIndex() - chordA[j].getFractionalIndex()) <= maxErr)
                    // The note was found, within the allowable error margin
                    foundNote = true;
            }

            // If the note was not found, the chords do not match
            if (!foundNote)
                return false;
        }

//        for (int i = 0; i < length; ++i)
//            if (Math.abs(chordA[i].getFractionalIndex() - chordB[i].getFractionalIndex()) > maxErr)
//                return false;

        return true;
    }

    /**
     * Gets the number of notes of this Chord.
     * @return The number of notes of this Chord
     */
    public int getNumNotes()
    {
        return TYPE.offsets.length;
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
     * Gets a random spelling of this Chord.
     * @param word The array into which to put the given position of this Chord
     */

    /**
     * Gets the given inversion for this Chord. Currently this implementation keeps the Note order
     * and only changes the octaves of the appropriate note.
     * @param chord A Note array to use to store the inversion
     * @param inversion The inversion number, must be less than the length of the chord and larger than one
     */
    public void getInversion(Note[] chord, int inversion)
    {
        // Copy the offsets
        for (int i = 0; i < TYPE.offsets.length; ++i)
            chord[i].set(TYPE.offsets[i]);

        // Increment the appropriate note octaves
        for (int i = 0; i < inversion; ++i)
            chord[i].index += Note.NUM_NOTES;
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

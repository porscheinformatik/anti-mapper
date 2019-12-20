package at.porscheinformatik.antimapper;

public enum Hint
{

    /**
     * Tells the collection and map transformer and merger methods to return an empty object, even if the source is
     * null. Using this hint, these methods will never return null.
     */
    OR_EMPTY,

    /**
     * Tells the collection and map transformer and merger methods to keep null entries.
     */
    KEEP_NULL,

    /**
     * Tells the collection and map mergers to keep items, that are missing in the source (do not delete them).
     */
    KEEP_MISSING,
    
    /**
     * Tells the collection and map transformer methods to create unmodifiable collections and maps. Tells the
     * collection and map merger methods to assume and create unmodifiable collections and maps.
     */
    UNMODIFIABLE
}

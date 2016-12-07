package at.porscheinformatik.antimapper;

public enum Hint
{

    /**
     * Tells the collection and map transformer and merger methods to keep null entries.
     */
    KEEP_NULL,

    /**
     * Tells the collection and map transformer methods to create unmodifiable collections and maps. Tells the
     * collection and map merger methods to assume and create unmodifiable collections and maps.
     */
    UNMODIFIABLE
}

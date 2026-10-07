package app.interfaces;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generic search contract. T can be any type (VideoGame, Integer, String, ...).
 */
public interface Searchable<T> {

    /** Returns every element for which the matcher returns true. O(n). */
    List<T> linearSearch(Predicate<T> matcher);

    /**
     * Finds one element equal to {@code key} according to {@code comparator}.
     * The data must already be sorted with that same comparator. O(log n).
     * Returns null if nothing matches.
     */
    T binarySearch(T key, Comparator<T> comparator);
}

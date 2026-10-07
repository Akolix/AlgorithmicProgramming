package app.interfaces;

import java.util.Comparator;
import java.util.List;

/**
 * Generic sort contract. The ordering is supplied by the caller as a
 * Comparator, so the algorithms never need to know what T is.
 * For descending order pass {@code comparator.reversed()}.
 */
public interface Sortable<T> {
    void bubbleSort(Comparator<T> comparator);
    void mergeSort(Comparator<T> comparator);
    void selectionSort(Comparator<T> comparator);
    List<T> getAll();
}

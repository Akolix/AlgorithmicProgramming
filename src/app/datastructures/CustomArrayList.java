package app.datastructures;

import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CustomArrayList<T> implements Searchable<T>, Sortable<T> {

    private static final int DEFAULT_CAPACITY = 16;
    private Object[] data;
    private int size;

    public CustomArrayList() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(T item) {
        ensureCapacity();
        data[size++] = item;
    }

    @SuppressWarnings("unchecked")
    public T get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i);
        return (T) data[i];
    }

    public int size() { return size; }

    public void clear() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, size);
            data = bigger;
        }
    }

    // Linear search - O(n)
    @Override
    public List<T> linearSearch(Predicate<T> matcher) {
        List<T> results = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            T item = get(i);
            if (matcher.test(item)) results.add(item);
        }
        return results;
    }

    // Binary search - O(log n). Data must be sorted with the same comparator.
    @Override
    public T binarySearch(T key, Comparator<T> comparator) {
        int low = 0, high = size - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = comparator.compare(get(mid), key);
            if (cmp == 0)      return get(mid);
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // BUBBLE SORT — O(n²)
    @Override
    public void bubbleSort(Comparator<T> comparator) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (comparator.compare(get(j), get(j + 1)) > 0) {
                    swap(j, j + 1);
                }
            }
        }
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(Comparator<T> comparator) {
        T[] arr = toArray();
        mergeSortHelper(arr, 0, arr.length - 1, comparator);
        for (int i = 0; i < size; i++) data[i] = arr[i];
    }

    private void mergeSortHelper(T[] arr, int left, int right, Comparator<T> comparator) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSortHelper(arr, left, mid, comparator);
        mergeSortHelper(arr, mid + 1, right, comparator);
        merge(arr, left, mid, right, comparator);
    }

    @SuppressWarnings("unchecked")
    private void merge(T[] arr, int left, int mid, int right, Comparator<T> comparator) {
        int n1 = mid - left + 1, n2 = right - mid;
        T[] L = (T[]) new Object[n1];
        T[] R = (T[]) new Object[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (comparator.compare(L[i], R[j]) <= 0) arr[k++] = L[i++];
            else                                     arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Selection sort — O(n²)
    @Override
    public void selectionSort(Comparator<T> comparator) {
        for (int i = 0; i < size - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < size; j++) {
                if (comparator.compare(get(targetIdx), get(j)) > 0) {
                    targetIdx = j;
                }
            }
            if (targetIdx != i) swap(i, targetIdx);
        }
    }

    @Override
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < size; i++) result.add(get(i));
        return result;
    }

    private void swap(int i, int j) {
        Object tmp = data[i]; data[i] = data[j]; data[j] = tmp;
    }

    // Generic arrays cannot be created with "new T[n]", so an Object[] is cast.
    // The array never leaves this class, so the cast is safe.
    @SuppressWarnings("unchecked")
    private T[] toArray() {
        T[] arr = (T[]) new Object[size];
        for (int i = 0; i < size; i++) arr[i] = get(i);
        return arr;
    }
}

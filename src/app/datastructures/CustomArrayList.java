package app.datastructures;

import app.dataset.Movie;
import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayList implements Searchable, Sortable {

    private static final int DEFAULT_CAPACITY = 16;
    private Object[] data;
    private int size;

    public CustomArrayList() {
        data = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(Movie movie) {
        ensureCapacity();
        data[size++] = movie;
    }

    public Movie get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i);
        return (Movie) data[i];
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
    public List<Movie> linearSearch(String query) {
        List<Movie> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (int i = 0; i < size; i++) {
            Movie m = get(i);
            if (m.getTitle().toLowerCase().contains(lowerQuery)
                    || m.getGenre().toLowerCase().contains(lowerQuery)) {
                results.add(m);
            }
        }
        return results;
    }

    // Binary search - O(log n)
    @Override
    public Movie binarySearch(String title) {
        int low = 0, high = size - 1;
        String target = title.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = get(mid).getTitle().toLowerCase().compareTo(target);
            if (cmp == 0)      return get(mid);
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // BUBBLE SORT — O(n²)
    @Override
    public void bubbleSort(String field, boolean ascending) {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1 - i; j++) {
                if (shouldSwap(get(j), get(j + 1), field, ascending)) {
                    swap(j, j + 1);
                }
            }
        }
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(String field, boolean ascending) {
        Movie[] arr = toMovieArray();
        mergeSortHelper(arr, 0, arr.length - 1, field, ascending);
        for (int i = 0; i < size; i++) data[i] = arr[i];
    }

    private void mergeSortHelper(Movie[] arr, int left, int right, String field, boolean asc) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSortHelper(arr, left, mid, field, asc);
        mergeSortHelper(arr, mid + 1, right, field, asc);
        merge(arr, left, mid, right, field, asc);
    }

    private void merge(Movie[] arr, int left, int mid, int right, String field, boolean asc) {
        int n1 = mid - left + 1, n2 = right - mid;
        Movie[] L = new Movie[n1], R = new Movie[n2];
        System.arraycopy(arr, left, L, 0, n1);
        System.arraycopy(arr, mid + 1, R, 0, n2);
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (!shouldSwap(L[i], R[j], field, asc)) arr[k++] = L[i++];
            else                                      arr[k++] = R[j++];
        }
        while (i < n1) arr[k++] = L[i++];
        while (j < n2) arr[k++] = R[j++];
    }

    // Selection sort — O(n²)
    @Override
    public void selectionSort(String field, boolean ascending) {
        for (int i = 0; i < size - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < size; j++) {
                if (shouldSwap(get(targetIdx), get(j), field, ascending)) {
                    targetIdx = j;
                }
            }
            if (targetIdx != i) swap(i, targetIdx);
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> result = new ArrayList<>();
        for (int i = 0; i < size; i++) result.add(get(i));
        return result;
    }

    private void swap(int i, int j) {
        Object tmp = data[i]; data[i] = data[j]; data[j] = tmp;
    }

    private boolean shouldSwap(Movie a, Movie b, String field, boolean ascending) {
        int cmp;
        switch (field.toLowerCase()) {
            case "year":      cmp = Integer.compare(a.getYear(), b.getYear()); break;
            case "rating":    cmp = Double.compare(a.getRating(), b.getRating()); break;
            case "boxoffice": cmp = Double.compare(a.getBoxOffice(), b.getBoxOffice()); break;
            default:          cmp = a.getTitle().compareToIgnoreCase(b.getTitle()); break;
        }
        return ascending ? cmp > 0 : cmp < 0;
    }

    private Movie[] toMovieArray() {
        Movie[] arr = new Movie[size];
        for (int i = 0; i < size; i++) arr[i] = get(i);
        return arr;
    }
}
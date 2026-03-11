package app.datastructures;

import app.dataset.Movie;
import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.List;

public class CustomBinarySearchTree implements Searchable, Sortable {

    private static class BSTNode {
        Movie data;
        BSTNode left, right;
        BSTNode(Movie data) { this.data = data; }
    }

    private BSTNode root;
    private int size;
    private String sortField = "rating";

    public CustomBinarySearchTree() { root = null; size = 0; }

    public void insert(Movie movie) {
        root = insertRec(root, movie);
        size++;
    }

    private BSTNode insertRec(BSTNode node, Movie movie) {
        if (node == null) return new BSTNode(movie);
        double movieVal = getFieldValue(movie, sortField);
        double nodeVal  = getFieldValue(node.data, sortField);
        if (movieVal <= nodeVal) node.left  = insertRec(node.left, movie);
        else                     node.right = insertRec(node.right, movie);
        return node;
    }

    public void add(Movie movie) { insert(movie); }
    public int size() { return size; }
    public void clear() { root = null; size = 0; }

    // Linear search — O(n)
    @Override
    public List<Movie> linearSearch(String query) {
        List<Movie> results = new ArrayList<>();
        linearSearchRec(root, query.toLowerCase(), results);
        return results;
    }

    private void linearSearchRec(BSTNode node, String query, List<Movie> results) {
        if (node == null) return;
        linearSearchRec(node.left, query, results);
        if (node.data.getTitle().toLowerCase().contains(query)
                || node.data.getGenre().toLowerCase().contains(query)) {
            results.add(node.data);
        }
        linearSearchRec(node.right, query, results);
    }

    // Binary search - O(log n)
    @Override
    public Movie binarySearch(String title) {
        List<Movie> allMovies = getAllMovies();
        allMovies.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        int low = 0, high = allMovies.size() - 1;
        String target = title.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = allMovies.get(mid).getTitle().toLowerCase().compareTo(target);
            if (cmp == 0)      return allMovies.get(mid);
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // Bubble sort — O(n²)
    @Override
    public void bubbleSort(String field, boolean ascending) {
        Movie[] arr = toMovieArray();
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (shouldSwap(arr[j], arr[j + 1], field, ascending)) {
                    Movie tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                }
            }
        }
        rebuildFromArray(arr, field);
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(String field, boolean ascending) {
        Movie[] arr = toMovieArray();
        mergeSortHelper(arr, 0, arr.length - 1, field, ascending);
        rebuildFromArray(arr, field);
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
        Movie[] arr = toMovieArray();
        for (int i = 0; i < arr.length - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (shouldSwap(arr[targetIdx], arr[j], field, ascending)) {
                    targetIdx = j;
                }
            }
            if (targetIdx != i) {
                Movie tmp = arr[i]; arr[i] = arr[targetIdx]; arr[targetIdx] = tmp;
            }
        }
        rebuildFromArray(arr, field);
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(BSTNode node, List<Movie> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }

    private double getFieldValue(Movie m, String field) {
        switch (field.toLowerCase()) {
            case "year":      return m.getYear();
            case "rating":    return m.getRating();
            case "boxoffice": return m.getBoxOffice();
            default:          return 0;
        }
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
        return getAllMovies().toArray(new Movie[0]);
    }

    private void rebuildFromArray(Movie[] arr, String field) {
        clear();
        this.sortField = field;
        for (Movie m : arr) { root = insertRec(root, m); size++; }
    }
}
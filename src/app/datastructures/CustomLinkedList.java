package app.datastructures;

import app.dataset.Movie;
import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.List;

public class CustomLinkedList implements Searchable, Sortable {

    private static class Node {
        Movie data;
        Node prev, next;
        Node(Movie data) { this.data = data; }
    }

    private Node head, tail;
    private int size;

    public CustomLinkedList() { head = null; tail = null; size = 0; }

    public void add(Movie movie) {
        Node newNode = new Node(movie);
        if (tail == null) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public Movie get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i);
        Node cur = head;
        for (int k = 0; k < i; k++) cur = cur.next;
        return cur.data;
    }

    public int size() { return size; }

    public void clear() { head = tail = null; size = 0; }

    // Linear — O(n)
    @Override
    public List<Movie> linearSearch(String query) {
        List<Movie> results = new ArrayList<>();
        String lower = query.toLowerCase();
        Node cur = head;
        while (cur != null) {
            if (cur.data.getTitle().toLowerCase().contains(lower)
                    || cur.data.getGenre().toLowerCase().contains(lower)) {
                results.add(cur.data);
            }
            cur = cur.next;
        }
        return results;
    }

    // Binary search — O(log n)
    @Override
    public Movie binarySearch(String title) {
        Movie[] arr = toMovieArray();
        int low = 0, high = arr.length - 1;
        String target = title.toLowerCase();
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = arr[mid].getTitle().toLowerCase().compareTo(target);
            if (cmp == 0)      return arr[mid];
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // Bubble sort — O(n²)
    @Override
    public void bubbleSort(String field, boolean ascending) {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node cur = head;
            while (cur != null && cur.next != null) {
                if (shouldSwap(cur.data, cur.next.data, field, ascending)) {
                    Movie tmp = cur.data; cur.data = cur.next.data; cur.next.data = tmp;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(String field, boolean ascending) {
        Movie[] arr = toMovieArray();
        mergeSortHelper(arr, 0, arr.length - 1, field, ascending);
        clear();
        for (Movie m : arr) add(m);
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
        Node outer = head;
        while (outer != null) {
            Node target = outer;
            Node inner  = outer.next;
            while (inner != null) {
                if (shouldSwap(target.data, inner.data, field, ascending)) {
                    target = inner;
                }
                inner = inner.next;
            }
            if (target != outer) {
                Movie tmp = outer.data; outer.data = target.data; target.data = tmp;
            }
            outer = outer.next;
        }
    }

    @Override
    public List<Movie> getAllMovies() {
        List<Movie> result = new ArrayList<>();
        Node cur = head;
        while (cur != null) { result.add(cur.data); cur = cur.next; }
        return result;
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
        int i = 0; Node cur = head;
        while (cur != null) { arr[i++] = cur.data; cur = cur.next; }
        return arr;
    }
}
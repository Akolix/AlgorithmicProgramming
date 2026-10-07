package app.datastructures;

import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CustomLinkedList<T> implements Searchable<T>, Sortable<T> {

    private static class Node<T> {
        T data;
        Node<T> prev, next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head, tail;
    private int size;

    public CustomLinkedList() { head = null; tail = null; size = 0; }

    public void add(T item) {
        Node<T> newNode = new Node<>(item);
        if (tail == null) {
            head = tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public T get(int i) {
        if (i < 0 || i >= size) throw new IndexOutOfBoundsException("Index: " + i);
        Node<T> cur = head;
        for (int k = 0; k < i; k++) cur = cur.next;
        return cur.data;
    }

    public int size() { return size; }

    public void clear() { head = tail = null; size = 0; }

    // Linear — O(n)
    @Override
    public List<T> linearSearch(Predicate<T> matcher) {
        List<T> results = new ArrayList<>();
        Node<T> cur = head;
        while (cur != null) {
            if (matcher.test(cur.data)) results.add(cur.data);
            cur = cur.next;
        }
        return results;
    }

    // Binary search — O(log n). Data must be sorted with the same comparator.
    @Override
    public T binarySearch(T key, Comparator<T> comparator) {
        T[] arr = toArray();
        int low = 0, high = arr.length - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = comparator.compare(arr[mid], key);
            if (cmp == 0)      return arr[mid];
            else if (cmp < 0)  low = mid + 1;
            else               high = mid - 1;
        }
        return null;
    }

    // Bubble sort — O(n²)
    @Override
    public void bubbleSort(Comparator<T> comparator) {
        if (size <= 1) return;
        boolean swapped;
        do {
            swapped = false;
            Node<T> cur = head;
            while (cur != null && cur.next != null) {
                if (comparator.compare(cur.data, cur.next.data) > 0) {
                    T tmp = cur.data; cur.data = cur.next.data; cur.next.data = tmp;
                    swapped = true;
                }
                cur = cur.next;
            }
        } while (swapped);
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(Comparator<T> comparator) {
        T[] arr = toArray();
        mergeSortHelper(arr, 0, arr.length - 1, comparator);
        clear();
        for (T item : arr) add(item);
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
        Node<T> outer = head;
        while (outer != null) {
            Node<T> target = outer;
            Node<T> inner  = outer.next;
            while (inner != null) {
                if (comparator.compare(target.data, inner.data) > 0) {
                    target = inner;
                }
                inner = inner.next;
            }
            if (target != outer) {
                T tmp = outer.data; outer.data = target.data; target.data = tmp;
            }
            outer = outer.next;
        }
    }

    @Override
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        Node<T> cur = head;
        while (cur != null) { result.add(cur.data); cur = cur.next; }
        return result;
    }

    // Generic arrays cannot be created with "new T[n]", so an Object[] is cast.
    @SuppressWarnings("unchecked")
    private T[] toArray() {
        T[] arr = (T[]) new Object[size];
        int i = 0; Node<T> cur = head;
        while (cur != null) { arr[i++] = cur.data; cur = cur.next; }
        return arr;
    }
}

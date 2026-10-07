package app.datastructures;

import app.interfaces.Searchable;
import app.interfaces.Sortable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CustomBinarySearchTree<T> implements Searchable<T>, Sortable<T> {

    private static class BSTNode<T> {
        T data;
        BSTNode<T> left, right;
        BSTNode(T data) { this.data = data; }
    }

    private BSTNode<T> root;
    private int size;
    // Defines how the tree is ordered. Replaced whenever a sort rebuilds the tree.
    private Comparator<T> ordering;

    public CustomBinarySearchTree(Comparator<T> ordering) {
        this.ordering = ordering;
        root = null;
        size = 0;
    }

    public void insert(T item) {
        BSTNode<T> node = new BSTNode<>(item);
        size++;
        if (root == null) { root = node; return; }

        BSTNode<T> cur = root;
        while (true) {
            if (ordering.compare(item, cur.data) <= 0) {
                if (cur.left == null)  { cur.left = node;  return; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = node; return; }
                cur = cur.right;
            }
        }
    }

    public void add(T item) { insert(item); }
    public int size() { return size; }
    public void clear() { root = null; size = 0; }

    /** Empties the tree and sets the ordering used by subsequent inserts. */
    public void reset(Comparator<T> newOrdering) {
        clear();
        this.ordering = newOrdering;
    }

    // Linear search — O(n)
    @Override
    public List<T> linearSearch(Predicate<T> matcher) {
        List<T> results = new ArrayList<>();
        inOrder(matcher, results);
        return results;
    }

    // Binary search - O(log n) on the sorted copy.
    @Override
    public T binarySearch(T key, Comparator<T> comparator) {
        T[] arr = toArray();
        mergeSortHelper(arr, 0, arr.length - 1, comparator);
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
        T[] arr = toArray();
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (comparator.compare(arr[j], arr[j + 1]) > 0) {
                    T tmp = arr[j]; arr[j] = arr[j + 1]; arr[j + 1] = tmp;
                }
            }
        }
        rebuildFromArray(arr, comparator);
    }

    // Merge sort — O(n log n)
    @Override
    public void mergeSort(Comparator<T> comparator) {
        T[] arr = toArray();
        mergeSortHelper(arr, 0, arr.length - 1, comparator);
        rebuildFromArray(arr, comparator);
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
        T[] arr = toArray();
        for (int i = 0; i < arr.length - 1; i++) {
            int targetIdx = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (comparator.compare(arr[targetIdx], arr[j]) > 0) {
                    targetIdx = j;
                }
            }
            if (targetIdx != i) {
                T tmp = arr[i]; arr[i] = arr[targetIdx]; arr[targetIdx] = tmp;
            }
        }
        rebuildFromArray(arr, comparator);
    }

    @Override
    public List<T> getAll() {
        List<T> result = new ArrayList<>();
        inOrder(null, result);
        return result;
    }

    /**
     * Iterative in-order traversal (left, node, right) using an explicit stack,
     * so a very deep tree cannot overflow the call stack.
     * Only elements accepted by the filter are collected (null = accept all).
     */
    @SuppressWarnings("unchecked")
    private void inOrder(Predicate<T> filter, List<T> out) {
        Object[] stack = new Object[64];
        int top = 0;
        BSTNode<T> cur = root;
        while (cur != null || top > 0) {
            while (cur != null) {
                if (top == stack.length) {
                    Object[] bigger = new Object[stack.length * 2];
                    System.arraycopy(stack, 0, bigger, 0, top);
                    stack = bigger;
                }
                stack[top++] = cur;
                cur = cur.left;
            }
            cur = (BSTNode<T>) stack[--top];
            if (filter == null || filter.test(cur.data)) out.add(cur.data);
            cur = cur.right;
        }
    }

    // Generic arrays cannot be created with "new T[n]", so an Object[] is cast.
    @SuppressWarnings("unchecked")
    private T[] toArray() {
        List<T> all = getAll();
        T[] arr = (T[]) new Object[all.size()];
        for (int i = 0; i < arr.length; i++) arr[i] = all.get(i);
        return arr;
    }

    // The array is already sorted, so inserting it one by one would build a chain
    private void rebuildFromArray(T[] sorted, Comparator<T> comparator) {
        this.ordering = comparator;
        this.root = buildBalanced(sorted, 0, sorted.length - 1);
        this.size = sorted.length;
    }

    private BSTNode<T> buildBalanced(T[] sorted, int low, int high) {
        if (low > high) return null;
        int mid = (low + high) >>> 1;
        BSTNode<T> node = new BSTNode<>(sorted[mid]);
        node.left  = buildBalanced(sorted, low, mid - 1);
        node.right = buildBalanced(sorted, mid + 1, high);
        return node;
    }
}

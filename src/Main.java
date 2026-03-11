import app.dataset.Movie;
import app.dataset.MovieDataset;
import app.datastructures.CustomArrayList;
import app.datastructures.CustomBinarySearchTree;
import app.datastructures.CustomLinkedList;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  DATASET & ALGORITHM EXPLORER — Console Test");
        System.out.println("=".repeat(60));

        List<Movie> movies = MovieDataset.getSubset(20);

        testArrayList(movies);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("=".repeat(60));
    }

    // ── CustomArrayList ────────────────────────────────────────────────────

    static void testArrayList(List<Movie> movies) {
        System.out.println("  CustomArrayList");

        CustomArrayList list = new CustomArrayList();
        for (Movie m : movies) list.add(m);
        System.out.println("Loaded " + list.size() + " movies.\n");

        // Linear search
        String query = "the";
        long start = System.nanoTime();
        List<Movie> results = list.linearSearch(query);
        long elapsed = System.nanoTime() - start;
        System.out.println("Linear Search: \"" + query + "\" → " + results.size() + " result(s)  (" + formatNano(elapsed) + ")");
        results.forEach(m -> System.out.println("  " + m.toDisplayString()));

        // Bubble sort → Binary search
        System.out.println("\nBubble Sort by title ASC:");
        start = System.nanoTime();
        list.bubbleSort("title", true);
        elapsed = System.nanoTime() - start;
        System.out.println("  Done in " + formatNano(elapsed));
        printFirst3(list.getAllMovies());

        String searchTitle = list.get(0).getTitle(); // grab first title after sort
        start = System.nanoTime();
        Movie found = list.binarySearch(searchTitle);
        elapsed = System.nanoTime() - start;
        System.out.println("\nBinary Search: \"" + searchTitle + "\" → "
                + (found != null ? "FOUND" : "NOT FOUND") + "  (" + formatNano(elapsed) + ")");

        // Merge sort
        System.out.println("\nMerge Sort by rating DESC:");
        start = System.nanoTime();
        list.mergeSort("rating", false);
        elapsed = System.nanoTime() - start;
        System.out.println("  Done in " + formatNano(elapsed));
        printFirst3(list.getAllMovies());

        // Selection sort
        System.out.println("\nSelection Sort by year ASC:");
        start = System.nanoTime();
        list.selectionSort("year", true);
        elapsed = System.nanoTime() - start;
        System.out.println("  Done in " + formatNano(elapsed));
        printFirst3(list.getAllMovies());
    }



    // ── Helpers ────────────────────────────────────────────────────────────

    static void printFirst3(List<Movie> movies) {
        int limit = Math.min(3, movies.size());
        for (int i = 0; i < limit; i++) {
            System.out.println("  [" + (i + 1) + "] " + movies.get(i).toDisplayString());
        }
        if (movies.size() > 3) {
            System.out.println("  ... and " + (movies.size() - 3) + " more.");
        }
    }

    static String formatNano(long nanos) {
        double ms = nanos / 1_000_000.0, sec = ms / 1000.0;
        if (sec >= 0.1) return String.format("%.1f s", sec);
        if (ms  >= 1.0) return String.format("%.1f ms", ms);
        return String.format("%.0f µs", nanos / 1000.0);
    }
}
package app.gui;

import app.dataset.Movie;
import app.dataset.MovieCSVLoader;
import app.dataset.MovieDataset;
import app.datastructures.CustomArrayList;
import app.datastructures.CustomBinarySearchTree;
import app.datastructures.CustomLinkedList;
import app.interfaces.Searchable;
import app.interfaces.Sortable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

import static app.gui.UIFactory.*;

/**
 * MainWindow — the application frame.
 *
 * Responsibilities:
 *   - Assembles TitleBarPanel, TablePanel, ControlPanel, StatusBar into a BorderLayout
 *   - Wires all button callbacks from ControlPanel to the three data structures
 *   - Owns the three data structure instances
 *   - Contains zero layout or styling code (all delegated to panel classes / UIFactory)
 *
 * Layout:
 *   NORTH  → TitleBarPanel  (title + dataset size slider)
 *   CENTER → JSplitPane [ TablePanel (left) | ControlPanel (right) ]
 *   SOUTH  → StatusBar    (status message + timing)
 */
public class MainWindow extends JFrame {

    // ── Data structures ────────────────────────────────────────────────────
    private final CustomArrayList        arrayList  = new CustomArrayList();
    private final CustomLinkedList       linkedList = new CustomLinkedList();
    private final CustomBinarySearchTree bst        = new CustomBinarySearchTree();

    // ── Panels ─────────────────────────────────────────────────────────────
    private final TitleBarPanel titleBar;
    private final TablePanel    tablePanel;
    private final ControlPanel  controls;
    private final StatusBar     statusBar;
    private List<Movie> currentMovies = null;

    private int currentDatasetSize = 50;

    public MainWindow() {
        super("Dataset & Algorithm Explorer — Movies");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 780));

        applyDarkLookAndFeel();

        // Build panels
        titleBar   = new TitleBarPanel(this::loadDataset,this::uploadCSV);
        tablePanel = new TablePanel();
        controls   = new ControlPanel();
        statusBar  = new StatusBar();

        // Wire button callbacks
        wireActions();

        // Assemble layout
        JSplitPane center = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, controls);
        center.setDividerLocation(700);
        center.setBackground(BG_DARK);
        center.setBorder(null);
        center.setDividerSize(6);

        getContentPane().setBackground(BG_DARK);
        add(titleBar,  BorderLayout.NORTH);
        add(center,    BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        // Load full dataset on startup
        loadDataset(50);

        setVisible(true);
    }

    // ── Wiring ─────────────────────────────────────────────────────────────

    private void wireActions() {
        controls.searchCard.onLinearSearch(this::runLinearSearch);
        controls.searchCard.onBinarySearch(this::runBinarySearch);

        controls.sortCard.onBubbleSort(   () -> runSort("bubble"));
        controls.sortCard.onMergeSort(    () -> runSort("merge"));
        controls.sortCard.onSelectionSort(() -> runSort("selection"));

        controls.viewCard.onViewAll(this::showAll);
        controls.viewCard.onReset(() -> loadDataset(currentDatasetSize));
    }

    // ── Actions ────────────────────────────────────────────────────────────

    /**
     * Loads (or reloads) the dataset into all three data structures.
     * Called on startup and whenever the slider Apply button is clicked.
     */
    private void loadDataset(int count) {
        List<Movie> movies;

        if (currentMovies != null) {
            // Use CSV dataset
            movies = currentMovies.subList(0, Math.min(count, currentMovies.size()));
        } else {
            // Use built-in dataset
            movies = MovieDataset.getSubset(count);
        }

        arrayList.clear();
        linkedList.clear();
        bst.clear();

        for (Movie m : movies) {
            arrayList.add(m);
            linkedList.add(m);
            bst.insert(m);
        }

        currentDatasetSize = count;
        showAll();
        tablePanel.log("Loaded " + movies.size() + " movies.");
        statusBar.setStatus("Dataset loaded: " + movies.size() + " movies.", TEXT_MAIN);
    }

    /**
     * Loads CSV dataset
     */
    private void uploadCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            List<Movie> loaded = MovieCSVLoader.loadFromCSV(file);

            if (loaded.isEmpty()) {
                warn("CSV file is empty or invalid.");
                return;
            }

            currentMovies = loaded;
            titleBar.setMaxDatasetSize(loaded.size());

            int currentSliderValue = titleBar.getCurrentSize();
            int sizeToLoad = Math.min(currentSliderValue, loaded.size());

            if (currentSliderValue > loaded.size()) {
                titleBar.setCurrentSize(loaded.size());
                sizeToLoad = loaded.size();
                currentDatasetSize = loaded.size();
            }

            loadDataset(sizeToLoad);

            tablePanel.log("Loaded CSV: " + file.getName());
            statusBar.setStatus("CSV dataset loaded (" + loaded.size() + " movies)", SUCCESS);
        }
    }

    private void resetToDefaultDataset() {
        currentMovies = null;
        titleBar.setMaxDatasetSize(50);
        titleBar.setCurrentSize(50);
        loadDataset(50);

        tablePanel.log("Reset to built-in dataset");
        statusBar.setStatus("Using default movie dataset", TEXT_MAIN);
    }

    /**
     * LINEAR SEARCH — O(n)
     * Searches by title substring or genre on the currently selected data structure.
     */
    private void runLinearSearch() {
        String query = controls.searchCard.getQuery();
        if (query.isEmpty()) { warn("Please enter a search term."); return; }

        long start      = System.nanoTime();
        List<Movie> results = getSearchable().linearSearch(query);
        String time     = formatNano(System.nanoTime() - start);

        tablePanel.populate(results);
        tablePanel.log("🔍 Linear Search [" + getDSName() + "]  query=\"" + query
                + "\"  →  " + results.size() + " result(s)  (" + time + ")");
        statusBar.setTiming("Linear Search", time);
        statusBar.setStatus("Linear Search: " + results.size()
                + " match(es) for \"" + query + "\"", SUCCESS);
    }

    /**
     * BINARY SEARCH — O(log n)
     * Requires sorted data — performs a merge sort by title first,
     * then searches for an exact title match.
     * Uses linear search first to resolve a partial query to a full title,
     * so the user does not need to type the complete title exactly.
     */
    private void runBinarySearch() {
        String query = controls.searchCard.getQuery();
        if (query.isEmpty()) { warn("Please enter a movie title to search for."); return; }

        // Binary search finds one movie by title — it cannot search by genre
        String[] genres = {"Action", "Drama", "Sci-Fi", "Comedy", "Horror",
                "Adventure", "Thriller", "Animation", "Romance"};
        for (String genre : genres) {
            if (query.equalsIgnoreCase(genre)) {
                warn("Binary Search finds one movie by title.\n"
                        + "Use Linear Search to search by genre.");
                return;
            }
        }

        // Sort by title so binary search works correctly
        getSortable().mergeSort("title", true);

        // Use linear search to resolve the partial query to a full exact title
        List<Movie> candidates = getSearchable().linearSearch(query);
        if (candidates.isEmpty()) {
            tablePanel.populate(List.of());
            tablePanel.log("🔎 Binary Search [" + getDSName() + "]  query=\"" + query
                    + "\"  →  NOT FOUND  (no candidates from linear pre-search)");
            statusBar.setTiming("Binary Search", "—");
            statusBar.setStatus("Binary Search: no match found for \"" + query + "\"", WARNING);
            return;
        }

        // Binary search using the first candidate's exact full title
        String exactTitle = candidates.get(0).getTitle();
        long start   = System.nanoTime();
        Movie result = getSearchable().binarySearch(exactTitle);
        String time  = formatNano(System.nanoTime() - start);

        if (result != null) {
            tablePanel.populate(List.of(result));
            statusBar.setStatus("Binary Search: found \"" + result.getTitle() + "\"", SUCCESS);
        } else {
            tablePanel.populate(List.of());
            statusBar.setStatus("Binary Search: no exact match for \"" + query + "\"", WARNING);
        }

        tablePanel.log("🔎 Binary Search [" + getDSName() + "]  query=\"" + query
                + "\"  →  exact title used: \"" + exactTitle + "\""
                + "  →  " + (result != null ? "FOUND" : "NOT FOUND") + "  (" + time + ")");
        statusBar.setTiming("Binary Search", time);
    }

    /**
     * Runs the chosen sort algorithm on the active data structure,
     * then refreshes the table and logs the timing.
     */
    private void runSort(String algorithm) {
        String  field = controls.sortCard.getSortField();
        boolean asc   = controls.sortCard.isAscending();

        long start = System.nanoTime();
        Sortable s = getSortable();
        switch (algorithm) {
            case "bubble":    s.bubbleSort(field, asc);    break;
            case "merge":     s.mergeSort(field, asc);     break;
            case "selection": s.selectionSort(field, asc); break;
        }
        String time = formatNano(System.nanoTime() - start);

        String algoName = algorithm.equals("bubble")    ? "Bubble Sort"
                : algorithm.equals("merge")     ? "Merge Sort"
                : "Selection Sort";
        String dir = asc ? "ASC" : "DESC";

        showAll();
        tablePanel.log("⚙  " + algoName + " [" + getDSName() + "]"
                + "  by=" + field + "  " + dir + "  (" + time + ")");
        statusBar.setTiming(algoName, time);
        statusBar.setStatus(algoName + " complete — sorted by "
                + field + " " + dir, SUCCESS);
    }

    /** Shows all movies currently in the active data structure. */
    private void showAll() {
        List<Movie> all = getSortable().getAllMovies();
        tablePanel.populate(all);
        statusBar.setStatus("Showing all " + all.size()
                + " movies from " + getDSName(), TEXT_MAIN);
    }

    // ── Data structure routing ─────────────────────────────────────────────

    private Searchable getSearchable() {
        switch (controls.dataStructureCard.getSelectedIndex()) {
            case 1:  return linkedList;
            case 2:  return bst;
            default: return arrayList;
        }
    }

    private Sortable getSortable() {
        switch (controls.dataStructureCard.getSelectedIndex()) {
            case 1:  return linkedList;
            case 2:  return bst;
            default: return arrayList;
        }
    }

    private String getDSName() {
        return controls.dataStructureCard.getSelectedName();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input required",
                JOptionPane.WARNING_MESSAGE);
    }
}
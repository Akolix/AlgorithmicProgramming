package app.gui;

import app.dataset.GameCSVLoader;
import app.dataset.GameQueries;
import app.dataset.VideoGame;
import app.datastructures.CustomArrayList;
import app.datastructures.CustomBinarySearchTree;
import app.datastructures.CustomLinkedList;
import app.interfaces.Searchable;
import app.interfaces.Sortable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;
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
    private final CustomArrayList<VideoGame>        arrayList  = new CustomArrayList<>();
    private final CustomLinkedList<VideoGame>       linkedList = new CustomLinkedList<>();
    private final CustomBinarySearchTree<VideoGame> bst        = new CustomBinarySearchTree<>(GameQueries.BY_NAME);

    // ── Panels ─────────────────────────────────────────────────────────────
    private final TitleBarPanel titleBar;
    private final TablePanel    tablePanel;
    private final ControlPanel  controls;
    private final StatusBar     statusBar;

    /** Every row of the loaded dataset (bundled vgsales.csv, or an uploaded CSV). */
    private List<VideoGame> allGames = List.of();

    private static final int DEFAULT_DATASET_SIZE = 1000;
    private int currentDatasetSize = DEFAULT_DATASET_SIZE;

    public MainWindow() {
        super("Dataset & Algorithm Explorer — Video Game Sales");
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

        // Load the bundled dataset (vgsales.csv) on startup
        useDefaultDataset();

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
        List<VideoGame> games = allGames.subList(0, Math.min(count, allGames.size()));

        arrayList.clear();
        linkedList.clear();
        bst.reset(GameQueries.BY_NAME);   // back to the default ordering

        for (VideoGame g : games) {
            arrayList.add(g);
            linkedList.add(g);
            bst.insert(g);
        }

        currentDatasetSize = count;
        showAll();
        tablePanel.log("Loaded " + games.size() + " games.");
        statusBar.setStatus("Dataset loaded: " + games.size() + " games.", TEXT_MAIN);
    }

    /**
     * Loads CSV dataset
     */
    private void uploadCSV() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            List<VideoGame> loaded = GameCSVLoader.loadFromCSV(file);

            if (loaded.isEmpty()) {
                warn("CSV file is empty or invalid.");
                return;
            }

            allGames = loaded;
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
            statusBar.setStatus("CSV dataset loaded (" + loaded.size() + " games)", SUCCESS);
        }
    }

    /** Loads the bundled vgsales.csv and shows the first DEFAULT_DATASET_SIZE rows. */
    private void useDefaultDataset() {
        allGames = GameCSVLoader.loadDefault();
        if (allGames.isEmpty()) {
            warn("Could not load the bundled vgsales.csv.\nUse \"Upload CSV\" to choose the file.");
            return;
        }
        int size = Math.min(DEFAULT_DATASET_SIZE, allGames.size());
        titleBar.setMaxDatasetSize(allGames.size());
        titleBar.setCurrentSize(size);
        loadDataset(size);
        tablePanel.log("Loaded bundled dataset: vgsales.csv (" + allGames.size() + " rows available)");
    }

    /**
     * LINEAR SEARCH — O(n)
     * Searches by name, platform, genre or publisher on the currently selected data structure.
     */
    private void runLinearSearch() {
        String query = controls.searchCard.getQuery();
        if (query.isEmpty()) { warn("Please enter a search term."); return; }

        long start      = System.nanoTime();
        List<VideoGame> results = getSearchable().linearSearch(GameQueries.matching(query));
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
     * Requires sorted data — performs a merge sort by name first,
     * then searches for an exact match.
     * Uses linear search first to resolve a partial query to a full game,
     * so the user does not need to type the complete name exactly.
     */
    private void runBinarySearch() {
        String query = controls.searchCard.getQuery();
        if (query.isEmpty()) { warn("Please enter a game name to search for."); return; }

        // Sort by name so binary search works correctly
        getSortable().mergeSort(GameQueries.BY_NAME);

        // Binary search finds one game by name (it cannot search by genre, platform
        // or publisher), so the partial query is resolved against names only.
        List<VideoGame> candidates = getSearchable().linearSearch(GameQueries.nameContaining(query));
        if (candidates.isEmpty()) {
            tablePanel.populate(List.of());
            tablePanel.log("🔎 Binary Search [" + getDSName() + "]  query=\"" + query
                    + "\"  →  NOT FOUND  (no game name contains this text; "
                    + "use Linear Search for genre, platform or publisher)");
            statusBar.setTiming("Binary Search", "—");
            statusBar.setStatus("Binary Search: no match found for \"" + query + "\"", WARNING);
            return;
        }

        // Binary search using the first candidate as the exact key
        VideoGame key       = candidates.get(0);
        String    exactName = key.getName();
        long start       = System.nanoTime();
        VideoGame result = getSearchable().binarySearch(key, GameQueries.BY_NAME);
        String time  = formatNano(System.nanoTime() - start);

        if (result != null) {
            tablePanel.populate(List.of(result));
            statusBar.setStatus("Binary Search: found \"" + result.getName() + "\" (" + result.getPlatform() + ")", SUCCESS);
        } else {
            tablePanel.populate(List.of());
            statusBar.setStatus("Binary Search: no exact match for \"" + query + "\"", WARNING);
        }

        tablePanel.log("🔎 Binary Search [" + getDSName() + "]  query=\"" + query
                + "\"  →  exact name used: \"" + exactName + "\" (" + key.getPlatform() + ")"
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
        Comparator<VideoGame> comparator = GameQueries.comparatorFor(field, asc);

        long start = System.nanoTime();
        Sortable<VideoGame> s = getSortable();
        switch (algorithm) {
            case "bubble":    s.bubbleSort(comparator);    break;
            case "merge":     s.mergeSort(comparator);     break;
            case "selection": s.selectionSort(comparator); break;
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

    /** Shows all games currently in the active data structure. */
    private void showAll() {
        List<VideoGame> all = getSortable().getAll();
        tablePanel.populate(all);
        statusBar.setStatus("Showing all " + all.size()
                + " games from " + getDSName(), TEXT_MAIN);
    }

    // ── Data structure routing ─────────────────────────────────────────────

    private Searchable<VideoGame> getSearchable() {
        switch (controls.dataStructureCard.getSelectedIndex()) {
            case 1:  return linkedList;
            case 2:  return bst;
            default: return arrayList;
        }
    }

    private Sortable<VideoGame> getSortable() {
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
# AlgorithmicProgramming
A Java Swing application that loads a built-in movie dataset into three
custom-built data structures and provides a graphical interface for
demonstrating and benchmarking search and sort algorithms. All data
structures and algorithms are implemented from scratch without using
Java's standard collection classes.

---

## How to Run

**Requirements:** Java Development Kit (JDK) 11 or higher.

1. Open IntelliJ IDEA and select **File > Open**, then choose the project folder.
2. Right-click the `src` folder and select **Mark Directory as > Sources Root**
   if it is not already marked.
3. Open `src/Main.java` and click the green Run button, or right-click and
   select **Run 'Main'**.
4. Use the built-in 50 movies or the csv file found in the dataset folder and adjust the slider on how many entities you want to search in.

---

## Dataset

The application uses a built-in dataset of 50 movies defined in
`MovieDataset.java`. No external file or network connection is required.
The dataset can be loaded in full or as a subset using the slider in the
application header.

Each movie contains the following fields:

| Field | Type | Description |
|---|---|---|
| `title` | String | Full movie title |
| `year` | int | Year of release |
| `genre` | String | Genre category (Action, Drama, Sci-Fi, etc.) |
| `rating` | double | Rating on a scale of 1.0 to 10.0 |
| `boxOffice` | double | Box office revenue in millions of USD |

---

## Interfaces

Two interfaces define the contracts that all three data structures implement.
This allows `MainWindow` to operate on any structure through a common API.

### Searchable
Defined in `app.interfaces.Searchable`.

- `linearSearch(String query)` — returns a List of all matching Movie objects
- `binarySearch(String title)` — returns a single Movie or null if not found

### Sortable
Defined in `app.interfaces.Sortable`.

- `bubbleSort(String field, boolean ascending)`
- `mergeSort(String field, boolean ascending)`
- `selectionSort(String field, boolean ascending)`
- `getAllMovies()` — returns all stored movies as a flat List

The `field` parameter accepts `"title"`, `"year"`, `"rating"`, or `"boxoffice"`.

---

## Data Structures

All three structures implement both `Searchable` and `Sortable`.

### CustomArrayList
A dynamic array backed by a raw `Object[]`. When capacity is reached the
internal array is replaced with one of double the size and all elements are
copied across.

| Operation | Complexity | Notes |
|---|---|---|
| `add(Movie)` | O(1) amortized | Occasional O(n) resize |
| `get(int i)` | O(1) | Direct index access |
| `clear()` | O(1) | Resets to default capacity |

### CustomLinkedList
A doubly linked list. Each `Node` holds a Movie reference and `prev`/`next`
pointers. Head and tail pointers are maintained for O(1) insertion at either
end.

| Operation | Complexity | Notes |
|---|---|---|
| `add(Movie)` | O(1) | Appends via tail pointer |
| `get(int i)` | O(n) | Traverses from head |
| `clear()` | O(1) | Sets head and tail to null |

### CustomBinarySearchTree
A binary search tree where each `BSTNode` holds a Movie and left/right child
pointers. Movies are inserted ordered by a configurable field (default:
rating), so an in-order traversal always yields movies sorted by that field.
When a sort is performed the tree is flattened, sorted, and rebuilt.

| Operation | Complexity | Notes |
|---|---|---|
| `insert(Movie)` | O(log n) avg | O(n) worst case on a skewed tree |
| `getAllMovies()` | O(n) | In-order traversal; result is sorted |
| `clear()` | O(1) | Sets root to null |

---

## Algorithms

All sort methods accept a `field` and `ascending` flag. A shared
`shouldSwap()` helper in each structure handles field-based comparison.

### Search Algorithms

#### Linear Search — O(n)
Iterates through every element and collects all entries whose title or genre
contains the query string (case-insensitive substring match). Works on
unsorted data and returns all matches.

- **CustomArrayList** — iterates index 0 to size - 1
- **CustomLinkedList** — traverses from head to tail via next pointers
- **CustomBST** — performs a recursive in-order traversal of all nodes

#### Binary Search — O(log n)
Locates a single movie by exact title. The data must be sorted by title
first; `MainWindow` automatically runs a merge sort before executing this
search. The algorithm maintains low/high bounds and repeatedly checks the
midpoint, halving the search space each iteration.

Because binary search requires an exact title, the GUI first runs a linear
search to resolve the user's partial query to a full title. Binary search is
then run on that exact title so the timing reflects only the binary search
itself.

- **CustomArrayList** — bisects the internal array directly
- **CustomLinkedList** — converts the node chain to a temporary array, then bisects
- **CustomBST** — collects an in-order list sorted by title, then bisects

### Sort Algorithms

#### Bubble Sort — O(n²)
Repeatedly compares adjacent pairs and swaps them if out of order. Each pass
moves the largest unsorted element to its correct position. Terminates early
if a full pass completes with no swaps.

#### Merge Sort — O(n log n)
Recursively splits the dataset in half, sorts each half independently, then
merges the two sorted halves by comparing their front elements. Used
internally as the pre-sort step before binary search.

#### Selection Sort — O(n²)
On each pass, scans the unsorted region to find the minimum (or maximum)
element and swaps it into the correct position at the front of that region.
Performs at most one swap per pass.

---

## Graphical User Interface

The GUI is built with Java Swing and split into focused panel classes.
Shared styling is centralised in `UIFactory.java`.

| Class | Responsibility |
|---|---|
| `MainWindow` | Main JFrame. Assembles all panels, owns the three data structure instances, and wires all button callbacks to algorithm methods. |
| `UIFactory` | Shared colour constants, fonts, and factory methods for styled buttons, labels, combo boxes, and card panels. |
| `TitleBarPanel` | Application title and dataset-size slider with an Apply button. |
| `TablePanel` | Results table and execution log. The log records every algorithm call with its measured execution time. |
| `ControlPanel` | Right-side container that stacks the four control cards using a BoxLayout. |
| `DataStructureCard` | Dropdown for selecting the active data structure with a description of the selected structure. |
| `SearchCard` | Text input and buttons for Linear Search and Binary Search. |
| `SortCard` | Field and direction dropdowns with buttons for all three sort algorithms. |
| `ViewCard` | View All Movies and Reset Dataset utility buttons. |
| `StatusBar` | Shows the current status message and the most recent algorithm execution time. |

### Execution Timing
Every algorithm call is timed with `System.nanoTime()`. The elapsed time is
displayed in the most readable unit — seconds, milliseconds, or microseconds
— rounded to the nearest tenth. Timing appears in both the status bar and the
execution log for direct comparison between algorithms and structures.

### Dataset Size Control
The slider in the title bar controls how many movies are loaded into all
three data structures simultaneously, allowing observation of how algorithm
performance scales with input size.


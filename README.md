# AlgorithmicProgramming
A Java Swing application that loads the **Video Game Sales** dataset
(`vgsales.csv`, 16,598 rows) into three custom-built data structures and
provides a graphical interface for demonstrating and benchmarking search and
sort algorithms. The data structures (array list, linked list, binary search
tree) and all search/sort algorithms are implemented from scratch, and they are
fully generic (`<T>`), so they work with any element type, not just video
games. `java.util` is only used for the `List` returned by search/`getAll` and
for the functional interfaces `Comparator` and `Predicate`.

---

## How to Run

**Requirements:** Java Development Kit (JDK) 11 or higher.

1. Open IntelliJ IDEA and select **File > Open**, then choose the project folder.
2. Right-click the `src` folder and select **Mark Directory as > Sources Root**
   if it is not already marked.
3. Open `src/Main.java` and click the green Run button, or right-click and
   select **Run 'Main'**.
4. The bundled `vgsales.csv` loads automatically. Adjust the slider to choose how many games (taken in file order) are loaded into the data structures, or use **Upload CSV** to load another file with the same columns.

> The loader reads `vgsales.csv` from the classpath and falls back to `src/app/dataset/vgsales.csv`, so run the project from its root folder (the IntelliJ default).

---

## Dataset

The application uses the **Video Game Sales** dataset located in `src/app/dataset/`. It contains 16,598 games. No network
connection is required. The slider in the application header loads the first
*n* rows into all three data structures.

Each game (`VideoGame`) contains the following fields:

| Field | Type | Description |
|---|---|---|
| `rank` | int | Rank by global sales |
| `name` | String | Game title |
| `platform` | String | Platform (Wii, PS2, X360, ...) |
| `year` | int | Release year (`0` when the file says `N/A`) |
| `genre` | String | Genre category (Sports, Racing, ...) |
| `publisher` | String | Publisher (`Unknown` when missing) |
| `naSales`, `euSales`, `jpSales`, `otherSales`, `globalSales` | double | Sales in millions of copies |

`GameCSVLoader` parses the file with a quote-aware reader (names such as
`"Ratatouille, The Game"` contain commas). Each row is handled on its own, so a
malformed row is skipped instead of aborting the load, and `N/A` values for
year/publisher are replaced with placeholders.

---

## Generic Programming

All data structures and algorithms are generic and know nothing about `VideoGame`.
Anything type-specific is passed in from the outside:

- **Ordering** is a `Comparator<T>`. Descending order is `comparator.reversed()`.
- **Search matching** is a `Predicate<T>`.
- **VideoGame-specific logic** (which field to sort by, what counts as a match)
  lives only in `app.dataset.GameQueries`.

`app.demo.GenericDemo` runs the same three structures and every sort/search
algorithm on `Integer` and `String` (console only, no GUI) to demonstrate this.

---

## Interfaces

Two generic interfaces define the contracts that all three data structures implement.
This allows `MainWindow` to operate on any structure through a common API.

### Searchable&lt;T&gt;
Defined in `app.interfaces.Searchable`.

- `List<T> linearSearch(Predicate<T> matcher)` — returns all matching elements
- `T binarySearch(T key, Comparator<T> comparator)` — returns the matching element or null if not found

### Sortable&lt;T&gt;
Defined in `app.interfaces.Sortable`.

- `bubbleSort(Comparator<T> comparator)`
- `mergeSort(Comparator<T> comparator)`
- `selectionSort(Comparator<T> comparator)`
- `getAll()` — returns all stored elements as a flat List

---

## Data Structures

All three structures are generic and implement both `Searchable<T>` and `Sortable<T>`.

### CustomArrayList&lt;T&gt;
A dynamic array backed by a raw `Object[]`. When capacity is reached the
internal array is replaced with one of double the size and all elements are
copied across.

| Operation | Complexity | Notes |
|---|---|---|
| `add(T)` | O(1) amortized | Occasional O(n) resize |
| `get(int i)` | O(1) | Direct index access |
| `clear()` | O(1) | Resets to default capacity |

### CustomLinkedList&lt;T&gt;
A doubly linked list. Each `Node<T>` holds an element reference and `prev`/`next`
pointers. Head and tail pointers are maintained for O(1) insertion at either
end.

| Operation | Complexity | Notes |
|---|---|---|
| `add(T)` | O(1) | Appends via tail pointer |
| `get(int i)` | O(n) | Traverses from head |
| `clear()` | O(1) | Sets head and tail to null |

### CustomBinarySearchTree&lt;T&gt;
A binary search tree where each `BSTNode<T>` holds an element and left/right child
pointers. Elements are inserted according to a `Comparator<T>` given to the
constructor (the app uses game name), so an in-order traversal always yields the
elements in that order. When a sort is performed the tree is flattened, sorted,
and rebuilt as a *balanced* tree using the sort's comparator. Insert and
traversal are iterative, so sorted input (which makes a tree degenerate into a
chain) cannot overflow the call stack.

| Operation | Complexity | Notes |
|---|---|---|
| `insert(T)` | O(log n) avg | O(n) worst case on a skewed tree |
| `getAll()` | O(n) | In-order traversal; result is sorted |
| `clear()` | O(1) | Sets root to null |

---

## Algorithms

All sort methods accept a `Comparator<T>`; two elements are swapped when
`comparator.compare(a, b) > 0`. The GUI builds the comparator from the chosen
field and direction via `GameQueries.comparatorFor(field, ascending)`.

### Search Algorithms

#### Linear Search — O(n)
Iterates through every element and collects all entries accepted by the given
`Predicate<T>`. For games, `GameQueries.matching(query)` supplies a predicate
that does a case-insensitive substring match on name, platform, genre or publisher. Works on
unsorted data and returns all matches.

- **CustomArrayList** — iterates index 0 to size - 1
- **CustomLinkedList** — traverses from head to tail via next pointers
- **CustomBST** — performs an (iterative) in-order traversal of all nodes

#### Binary Search — O(log n)
Locates a single element equal to the key according to the given comparator.
For games this is the exact name (ties between platforms are broken by platform and rank, so the order is total). The data must be sorted by that comparator
first; `MainWindow` automatically runs a merge sort before executing this
search. The algorithm maintains low/high bounds and repeatedly checks the
midpoint, halving the search space each iteration.

Because binary search requires an exact key, the GUI first runs a linear
search on game names to resolve the user's partial query to a full game. Binary
search is then run with that game as the key so the timing reflects only the
binary search itself.

- **CustomArrayList** — bisects the internal array directly
- **CustomLinkedList** — converts the node chain to a temporary array, then bisects
- **CustomBST** — collects the elements in order, sorts them with the given comparator, then bisects

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
| `ViewCard` | View All Games and Reset Dataset utility buttons. |
| `StatusBar` | Shows the current status message and the most recent algorithm execution time. |

### Execution Timing
Every algorithm call is timed with `System.nanoTime()`. The elapsed time is
displayed in the most readable unit — seconds, milliseconds, or microseconds
— rounded to the nearest tenth. Timing appears in both the status bar and the
execution log for direct comparison between algorithms and structures.

### Dataset Size Control
The slider in the title bar controls how many games are loaded into all
three data structures simultaneously, allowing observation of how algorithm
performance scales with input size.


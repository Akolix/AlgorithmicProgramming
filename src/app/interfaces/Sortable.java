package app.interfaces;

import app.dataset.Movie;
import java.util.List;

public interface Sortable {
    void bubbleSort(String field, boolean ascending);
    void mergeSort(String field, boolean ascending);
    void selectionSort(String field, boolean ascending);
    List<Movie> getAllMovies();
}
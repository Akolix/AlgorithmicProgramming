package app.interfaces;

import app.dataset.Movie;
import java.util.List;

public interface Searchable {
    List<Movie> linearSearch(String query);
    Movie binarySearch(String title);
}
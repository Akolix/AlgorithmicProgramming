package app.dataset;

public class Movie implements Comparable<Movie> {
    private String title;
    private int year;
    private String genre;
    private double rating;
    private double boxOffice;

    public Movie(String title, int year, String genre, double rating, double boxOffice) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
        this.boxOffice = boxOffice;
    }

    @Override
    public int compareTo(Movie other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return String.format("%-45s %d  %-15s  %.1f  $%.0fM", title, year, genre, rating, boxOffice);
    }

    public String toDisplayString() {
        return String.format("[%s] (%d) | %s | ★ %.1f | $%.0fM",
                title, year, genre, rating, boxOffice);
    }

    public String getTitle()     { return title; }
    public int getYear()         { return year; }
    public String getGenre()     { return genre; }
    public double getRating()    { return rating; }
    public double getBoxOffice() { return boxOffice; }
}
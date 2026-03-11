package app.dataset;

import java.util.ArrayList;
import java.util.List;

public class MovieDataset {

    public static List<Movie> getAll() {
        List<Movie> movies = new ArrayList<>();

        // Action
        movies.add(new Movie("The Dark Knight",               2008, "Action",    9.0, 1005.0));
        movies.add(new Movie("Mad Max: Fury Road",            2015, "Action",    8.1,  375.4));
        movies.add(new Movie("Die Hard",                      1988, "Action",    8.2,  140.8));
        movies.add(new Movie("Top Gun: Maverick",             2022, "Action",    8.3, 1490.0));
        movies.add(new Movie("John Wick",                     2014, "Action",    7.4,   88.8));
        movies.add(new Movie("Mission: Impossible - Fallout", 2018, "Action",    7.7,  791.1));
        movies.add(new Movie("The Matrix",                    1999, "Action",    8.7,  463.5));
        movies.add(new Movie("Gladiator",                     2000, "Action",    8.5,  460.5));

        // Drama
        movies.add(new Movie("The Shawshank Redemption",      1994, "Drama",     9.3,   16.0));
        movies.add(new Movie("Forrest Gump",                  1994, "Drama",     8.8,  678.2));
        movies.add(new Movie("Schindler's List",              1993, "Drama",     9.0,  321.3));
        movies.add(new Movie("The Godfather",                 1972, "Drama",     9.2,  246.1));
        movies.add(new Movie("Good Will Hunting",             1997, "Drama",     8.3,  225.9));
        movies.add(new Movie("A Beautiful Mind",              2001, "Drama",     8.2,  313.5));
        movies.add(new Movie("12 Angry Men",                  1957, "Drama",     9.0,    4.4));

        // Sci-Fi
        movies.add(new Movie("Interstellar",                  2014, "Sci-Fi",    8.6,  677.5));
        movies.add(new Movie("Inception",                     2010, "Sci-Fi",    8.8,  836.8));
        movies.add(new Movie("Blade Runner 2049",             2017, "Sci-Fi",    8.0,  259.2));
        movies.add(new Movie("The Martian",                   2015, "Sci-Fi",    8.0,  630.2));
        movies.add(new Movie("Ex Machina",                    2014, "Sci-Fi",    7.7,   36.9));
        movies.add(new Movie("Arrival",                       2016, "Sci-Fi",    7.9,  203.4));
        movies.add(new Movie("2001: A Space Odyssey",         1968, "Sci-Fi",    8.3,   56.7));

        // Comedy
        movies.add(new Movie("The Grand Budapest Hotel",      2014, "Comedy",    8.1,  174.8));
        movies.add(new Movie("Superbad",                      2007, "Comedy",    7.6,  169.8));
        movies.add(new Movie("Home Alone",                    1990, "Comedy",    7.7,  476.7));
        movies.add(new Movie("Groundhog Day",                 1993, "Comedy",    8.0,   70.9));
        movies.add(new Movie("The Big Lebowski",              1998, "Comedy",    8.1,   17.5));

        // Horror
        movies.add(new Movie("The Shining",                   1980, "Horror",    8.4,   44.0));
        movies.add(new Movie("Get Out",                       2017, "Horror",    7.7,  255.4));
        movies.add(new Movie("Hereditary",                    2018, "Horror",    7.3,   44.1));
        movies.add(new Movie("A Quiet Place",                 2018, "Horror",    7.5,  340.9));
        movies.add(new Movie("It",                            2017, "Horror",    7.3,  700.4));

        // Adventure
        movies.add(new Movie("The Lord of the Rings: FOTR",   2001, "Adventure", 8.8,  898.2));
        movies.add(new Movie("Indiana Jones: Raiders",        1981, "Adventure", 8.4,  389.9));
        movies.add(new Movie("Jurassic Park",                 1993, "Adventure", 8.2, 1029.2));
        movies.add(new Movie("Avatar",                        2009, "Adventure", 7.9, 2923.7));
        movies.add(new Movie("Pirates of the Caribbean",      2003, "Adventure", 8.0,  654.3));

        // Thriller
        movies.add(new Movie("Parasite",                      2019, "Thriller",  8.5,  258.8));
        movies.add(new Movie("Gone Girl",                     2014, "Thriller",  8.1,  369.3));
        movies.add(new Movie("Prisoners",                     2013, "Thriller",  8.1,  122.1));
        movies.add(new Movie("Se7en",                         1995, "Thriller",  8.6,  327.3));
        movies.add(new Movie("Zodiac",                        2007, "Thriller",  7.7,   84.8));

        // Animation
        movies.add(new Movie("Spirited Away",                 2001, "Animation", 8.6,  395.8));
        movies.add(new Movie("The Lion King",                 1994, "Animation", 8.5,  968.5));
        movies.add(new Movie("Up",                            2009, "Animation", 8.3,  735.1));
        movies.add(new Movie("WALL-E",                        2008, "Animation", 8.4,  521.3));
        movies.add(new Movie("Toy Story",                     1995, "Animation", 8.3,  373.6));

        // Romance
        movies.add(new Movie("Titanic",                       1997, "Romance",   7.9, 2264.7));
        movies.add(new Movie("La La Land",                    2016, "Romance",   8.0,  446.1));
        movies.add(new Movie("Pride & Prejudice",             2005, "Romance",   7.8,  121.6));

        return movies;
    }

    public static List<Movie> getSubset(int count) {
        List<Movie> all = getAll();
        return all.subList(0, Math.min(count, all.size()));
    }
}
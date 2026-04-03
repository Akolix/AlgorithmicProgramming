package app.dataset;

import java.io.*;
import java.util.*;

public class MovieCSVLoader {

    public static List<Movie> loadFromCSV(File file) {
        List<Movie> movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            // Skip header if present
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (parts.length < 5) continue;

                String title      = parts[0].trim();
                int year          = Integer.parseInt(parts[1].trim());
                String genre      = parts[2].trim();
                double rating     = Double.parseDouble(parts[3].trim());
                double boxOffice  = Double.parseDouble(parts[4].trim());

                movies.add(new Movie(title, year, genre, rating, boxOffice));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }
}
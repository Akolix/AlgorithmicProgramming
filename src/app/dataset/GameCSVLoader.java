package app.dataset;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the Video Game Sales dataset (vgsales.csv).
 *
 * Expected columns:
 *   Rank, Name, Platform, Year, Genre, Publisher, NA_Sales, EU_Sales, JP_Sales, Other_Sales, Global_Sales
 *
 * The parser is quote-aware (names such as "Ratatouille, The Game" contain commas)
 * and every row is handled on its own: a bad row is skipped, it never aborts the
 * whole load. Missing years / publishers ("N/A") are kept with placeholder values.
 */
public final class GameCSVLoader {

    public static final String DEFAULT_RESOURCE = "vgsales.csv";

    private static final int COLUMNS = 11;

    private GameCSVLoader() {}

    /** Loads the bundled vgsales.csv. Returns an empty list if it cannot be found. */
    public static List<VideoGame> loadDefault() {
        try (InputStream in = GameCSVLoader.class.getResourceAsStream(DEFAULT_RESOURCE)) {
            if (in != null) return load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Fallback when resources are not copied to the classpath: look next to the sources.
        for (String path : new String[]{"src/app/dataset/" + DEFAULT_RESOURCE, DEFAULT_RESOURCE}) {
            File f = new File(path);
            if (f.isFile()) return loadFromCSV(f);
        }
        return new ArrayList<>();
    }

    public static List<VideoGame> loadFromCSV(File file) {
        try (InputStream in = new FileInputStream(file)) {
            return load(in);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static List<VideoGame> load(InputStream in) throws IOException {
        List<VideoGame> games = new ArrayList<>();
        int skipped = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

            readRecord(reader);                       // header row
            String[] f;
            while ((f = readRecord(reader)) != null) {
                if (f.length == 1 && f[0].isBlank()) continue;   // empty line
                VideoGame game = toGame(f);
                if (game == null) skipped++; else games.add(game);
            }
        }

        if (skipped > 0) System.err.println("GameCSVLoader: skipped " + skipped + " invalid row(s).");
        return games;
    }

    /** Converts one CSV record to a VideoGame, or returns null if the row is unusable. */
    private static VideoGame toGame(String[] f) {
        if (f.length < COLUMNS) return null;
        try {
            String name = f[1].trim();
            if (name.isEmpty()) return null;

            return new VideoGame(
                    Integer.parseInt(f[0].trim()),
                    name,
                    f[2].trim(),
                    parseYear(f[3]),
                    f[4].trim(),
                    parsePublisher(f[5]),
                    Double.parseDouble(f[6].trim()),
                    Double.parseDouble(f[7].trim()),
                    Double.parseDouble(f[8].trim()),
                    Double.parseDouble(f[9].trim()),
                    Double.parseDouble(f[10].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static int parseYear(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {          // "N/A"
            return VideoGame.UNKNOWN_YEAR;
        }
    }

    private static String parsePublisher(String s) {
        String p = s.trim();
        return (p.isEmpty() || p.equalsIgnoreCase("N/A")) ? VideoGame.UNKNOWN_PUBLISHER : p;
    }

    /**
     * Reads one CSV record. Handles quoted fields, escaped quotes ("") and line
     * breaks inside quotes. Returns null at end of file.
     */
    private static String[] readRecord(BufferedReader in) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false, readAnything = false;
        int c;

        while ((c = in.read()) != -1) {
            readAnything = true;
            char ch = (char) c;

            if (inQuotes) {
                if (ch == '"') {
                    in.mark(1);
                    int next = in.read();
                    if (next == '"') {
                        cur.append('"');
                    } else {
                        inQuotes = false;
                        if (next != -1) in.reset();
                    }
                } else {
                    cur.append(ch);
                }
            } else if (ch == '"') {
                inQuotes = true;
            } else if (ch == ',') {
                fields.add(cur.toString());
                cur.setLength(0);
            } else if (ch == '\n') {
                fields.add(cur.toString());
                return fields.toArray(new String[0]);
            } else if (ch != '\r') {
                cur.append(ch);
            }
        }

        if (!readAnything) return null;
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }
}

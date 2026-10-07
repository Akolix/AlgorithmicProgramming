package app.dataset;

import java.util.Comparator;
import java.util.function.Predicate;

/**
 * All VideoGame-specific knowledge lives here: which field to sort on and what
 * counts as a search match. The data structures and algorithms only ever see a
 * generic Comparator<T> / Predicate<T>, so they stay independent of VideoGame.
 */
public final class GameQueries {

    private GameQueries() {}

    /**
     * Name order (case-insensitive). The same game can exist on several platforms,
     * so platform and rank break ties; this makes the order total, which means
     * binary search finds exactly the game it was given.
     */
    public static final Comparator<VideoGame> BY_NAME =
            Comparator.comparing(VideoGame::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(VideoGame::getPlatform)
                    .thenComparingInt(VideoGame::getRank);

    /** Unknown years (stored as 0) are placed after all known years in ascending order. */
    private static final Comparator<VideoGame> BY_YEAR =
            Comparator.comparingInt(g -> g.getYear() == VideoGame.UNKNOWN_YEAR
                    ? Integer.MAX_VALUE : g.getYear());

    /** Comparator for a field name coming from the GUI sort dropdown. */
    public static Comparator<VideoGame> comparatorFor(String field, boolean ascending) {
        Comparator<VideoGame> cmp;
        switch (field.toLowerCase()) {
            case "platform":     cmp = Comparator.comparing(VideoGame::getPlatform);                    break;
            case "year":         cmp = BY_YEAR;                                                         break;
            case "genre":        cmp = Comparator.comparing(VideoGame::getGenre);                       break;
            case "publisher":    cmp = Comparator.comparing(VideoGame::getPublisher,
                    String.CASE_INSENSITIVE_ORDER);             break;
            case "global sales": cmp = Comparator.comparingDouble(VideoGame::getGlobalSales);           break;
            case "na sales":     cmp = Comparator.comparingDouble(VideoGame::getNaSales);               break;
            case "eu sales":     cmp = Comparator.comparingDouble(VideoGame::getEuSales);               break;
            case "jp sales":     cmp = Comparator.comparingDouble(VideoGame::getJpSales);               break;
            default:             cmp = BY_NAME;                                                         break;
        }
        return ascending ? cmp : cmp.reversed();
    }

    /** Linear search: case-insensitive substring match on name, platform, genre or publisher. */
    public static Predicate<VideoGame> matching(String query) {
        String q = query.toLowerCase();
        return g -> g.getName().toLowerCase().contains(q)
                || g.getPlatform().toLowerCase().contains(q)
                || g.getGenre().toLowerCase().contains(q)
                || g.getPublisher().toLowerCase().contains(q);
    }

    /** Case-insensitive substring match on the name only (used to resolve a binary search key). */
    public static Predicate<VideoGame> nameContaining(String query) {
        String q = query.toLowerCase();
        return g -> g.getName().toLowerCase().contains(q);
    }
}

package app.dataset;

/**
 * One row of the Video Game Sales dataset (vgsales.csv).
 * Sales figures are in millions of copies sold.
 */
public class VideoGame {

    /** Used when the dataset has no release year ("N/A" in the CSV). */
    public static final int UNKNOWN_YEAR = 0;
    /** Used when the dataset has no publisher ("N/A" in the CSV). */
    public static final String UNKNOWN_PUBLISHER = "Unknown";

    private final int    rank;
    private final String name;
    private final String platform;
    private final int    year;
    private final String genre;
    private final String publisher;
    private final double naSales;
    private final double euSales;
    private final double jpSales;
    private final double otherSales;
    private final double globalSales;

    public VideoGame(int rank, String name, String platform, int year, String genre,
                     String publisher, double naSales, double euSales, double jpSales,
                     double otherSales, double globalSales) {
        this.rank        = rank;
        this.name        = name;
        this.platform    = platform;
        this.year        = year;
        this.genre       = genre;
        this.publisher   = publisher;
        this.naSales     = naSales;
        this.euSales     = euSales;
        this.jpSales     = jpSales;
        this.otherSales  = otherSales;
        this.globalSales = globalSales;
    }

    public int    getRank()        { return rank; }
    public String getName()        { return name; }
    public String getPlatform()    { return platform; }
    public int    getYear()        { return year; }
    public String getGenre()       { return genre; }
    public String getPublisher()   { return publisher; }
    public double getNaSales()     { return naSales; }
    public double getEuSales()     { return euSales; }
    public double getJpSales()     { return jpSales; }
    public double getOtherSales()  { return otherSales; }
    public double getGlobalSales() { return globalSales; }

    @Override
    public String toString() {
        return String.format("%s (%s, %s) - %.2fM", name, platform,
                year == UNKNOWN_YEAR ? "N/A" : String.valueOf(year), globalSales);
    }
}

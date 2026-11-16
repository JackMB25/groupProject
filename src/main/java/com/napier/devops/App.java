
package com.napier.devops;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Entry point for the population reporting system.
 * Connects to the world database, runs the reports and prints the results.
 */
public class App
{
    /**
     * Connection to MySQL database.
     */
    private static Connection con = null;

    /**
     * Setup Logger.
     */
    private static final Logger log = Logger.getLogger(App.class.getName());

    /**
     * Starts the app: connects to the database, runs the reports,
     * then disconnects.
     *
     * @param args optional: args[0] = database location (e.g. "db:3306"),
     *             args[1] = delay in milliseconds before connecting.
     *             With no args the app connects to "localhost:33060"
     *             for running in IntelliJ.
     */
    public static void main(String[] args)
    {
        App app = new App();

        // Connect to the database.
        // No arguments = running locally in IntelliJ.
        // Docker passes the database location and connection delay.
        if (args.length < 1)
        {
            app.connect("localhost:33060", 0);
        }
        else
        {
            app.connect(args[0], Integer.parseInt(args[1]));
        }

        // Create objects for each type of report.
        LanguageReport lr = new LanguageReport(con);
        CityReport cir = new CityReport(con);
        CountryReport ctryr = new CountryReport(con);
        PopulationReport pr = new PopulationReport(con);

        // -------------------------------------------------
        // Example report methods.
        // Uncomment a method when you want to run that report.
        // -------------------------------------------------

        // Getting population of the world
        // pr.getWorldPopulation();

        // Getting population of a continent
        // pr.getContinentPopulation("North America");

        // Getting population of a region
        // pr.getRegionPopulation("Caribbean");

        // Getting population of a country
        // pr.getCountryPopulation("Cuba");

        // Getting population of a district
        // pr.getDistrictPopulation("Herat");

        // Getting population in and out of cities by country
        // pr.getInOutPopulationByCountry("Cuba");

        // Getting population in and out of cities by region
        // pr.getInOutPopulationByRegion("Caribbean");

        // Getting N populated cities in a country
        // cir.getTopCitiesInCountry("Poland", 5);

        // Getting N populated cities in the world
        // cir.getTopNCitiesWorld(10);

        // Getting all capitals by population
        // pr.getAllCapitalsByPopulation();

        // Getting population of a city
        // pr.getCityPopulation("Edinburgh");

        // Getting population in and out of cities by continent
        // pr.getInOutPopulationByContinent("Europe");

        // Getting top N populated capital cities in the world
        // pr.getTopCapitalsWorld(5);

        // Getting top N populated cities in a continent
        // cir.getTopCitiesByContinent("Europe", 5);

        // Getting top N populated cities in a region
        // cir.getTopCitiesByRegion("British Islands", 5);

        // Getting top N populated cities in a district
        // cir.getTopCitiesByDistrict("Scotland", 3);

        // Getting top N populated capital cities in a continent
        // pr.getTopCapitalsByContinent("Europe", 5);

        // Getting top N populated capital cities in a region
        // pr.getTopCapitalsByRegion("Caribbean", 5);

        // Getting top N populated countries in a region
        // ctryr.getTopCountriesByRegion("Western Europe", 5);

        // Run the language speakers report.
        ResultSet languageSpeakers = lr.getLanguageSpeakers();

        // Output the report as a Markdown file.
        // The file will be created inside the ./reports directory.
        outputReport(languageSpeakers, "LanguageSpeakers.md");

        // Disconnect from the database.
        app.disconnect();
    }

    /**
     * Connect to the MySQL database.
     *
     * @param location host and port of the database,
     *                 e.g. "localhost:33060" or "db:3306"
     * @param delay milliseconds to wait before each connection attempt
     *              to give the database time to start
     */
    public void connect(String location, int delay)
    {
        try
        {
            // Load Database driver.
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Could not load SQL driver");
                System.exit(-1);
            }
        }

        int retries = 10;

        for (int i = 0; i < retries; ++i)
        {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Connecting to database...");
            }

            try
            {
                // Wait a bit for the database to start.
                Thread.sleep(delay);

                // Connect to database.
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );

                if (log.isLoggable(Level.FINE)) {
                    log.fine("Successfully connected");
                }

                break;
            }
            catch (SQLException sqle)
            {
                // Temporary diagnostic to reveal the actual JDBC connection error.
//                System.err.println("Database connection failed: " + sqle.getMessage());

                if (log.isLoggable(Level.FINE)) {
                    log.fine("Failed to connect to database attempt " + Integer.toString(i));
                    log.fine(sqle.getMessage());
                }
            }
            catch (InterruptedException ie)
            {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Thread interrupted? Should not happen.");
                }
            }
        }
    }

    /**
     * Gets the current database connection,
     * so tests can pass it to the report classes.
     *
     * @return the connection, or null if connect() has not succeeded
     */
    public static Connection getCon()
    {
        return con;
    }

    /**
     * Disconnect from the MySQL database.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                // Close connection.
                con.close();
            }
            catch (Exception e)
            {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("Error closing connection to database");
                }
            }
        }
    }

    /**
     * Prints any report as a table:
     * column headings, a separator line, then one line per row.
     * Prints "No results" if the report returned null.
     *
     * @param rs the result returned by a report method
     */
    public static void printReport(ResultSet rs)
    {
        if (rs == null)
        {
            if (log.isLoggable(Level.FINE)) {
                log.fine("No results");
            }
            return;
        }

        try
        {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column headings.
            for (int i = 1; i <= columnCount; i++)
            {
                if (log.isLoggable(Level.FINE)) {
                    log.fine(metaData.getColumnLabel(i));
                }

                if (i < columnCount)
                {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine(" | ");
                    }
                }
            }

            // Print a separator.
            for (int i = 1; i <= columnCount; i++)
            {
                if (log.isLoggable(Level.FINE)) {
                    log.fine("------------");
                }

                if (i < columnCount)
                {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("+");
                    }
                }
            }

            // Print each row.
            while (rs.next())
            {
                for (int i = 1; i <= columnCount; i++)
                {
                    Object value = rs.getObject(i);

                    if (log.isLoggable(Level.FINE)) {
                        log.fine((value == null ? "NULL" : value).toString());
                    }

                    if (i < columnCount)
                    {
                        if (log.isLoggable(Level.FINE)) {
                            log.fine(" | ");
                        }
                    }
                }
            }
        }
        catch (SQLException e)
        {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Error printing report: " + e.getMessage());
            }
        }
    }

    /**
     * Outputs a database report as a Markdown table in the reports directory.
     *
     * @param rs the ResultSet containing the report data
     * @param filename the name of the Markdown file to create
     */
    public static void outputReport(ResultSet rs, String filename)
    {
        // Check whether the report returned any results.
        if (rs == null)
        {
            System.out.println("No results");
            return;
        }

        // StringBuilder collects the Markdown content before writing it.
        StringBuilder sb = new StringBuilder();

        try
        {
            // Get the column information from the ResultSet.
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Add the Markdown table headings.
            sb.append("|");

            for (int i = 1; i <= columnCount; i++)
            {
                sb.append(" ")
                        .append(metaData.getColumnLabel(i))
                        .append(" |");
            }

            sb.append("\n|");

            // Add the Markdown table separator.
            for (int i = 1; i <= columnCount; i++)
            {
                sb.append(" --- |");
            }

            sb.append("\n");

            // Add each database row to the Markdown table.
            while (rs.next())
            {
                sb.append("|");

                for (int i = 1; i <= columnCount; i++)
                {
                    Object value = rs.getObject(i);

                    sb.append(" ")
                            .append(value == null ? "NULL" : value.toString())
                            .append(" |");
                }

                sb.append("\n");
            }

            // Create the reports directory if it does not already exist.
            File reportsDirectory = new File("./reports/");

            if (!reportsDirectory.exists() && !reportsDirectory.mkdirs())
            {
                throw new IOException("Could not create reports directory");
            }

            // Create the Markdown file and write the report content.
            File reportFile = new File(reportsDirectory, filename);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile)))
            {
                writer.write(sb.toString());
            }

            System.out.println("Report created: " + reportFile.getPath());
        }
        catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }
    }
}


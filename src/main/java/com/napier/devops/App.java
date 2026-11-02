package com.napier.devops;

import java.sql.*;

/**
 * Entry point for the population reporting system. Connects to the world database,
 * runs the reports and prints the results.
 */
public class App
{
    /**
     * Connection to MySQL database.
     */
    private static Connection con = null;

    /**
     * Starts the app: connects to the database, runs the reports, then disconnects.
     *
     * @param args optional: args[0] = database location (e.g. "db:3306"),
     *             args[1] = delay in milliseconds before connecting. With no args the app
     *             connects to "localhost:33060" straight away (for running in IntelliJ).
     */
    public static void main(String[] args)
    {
        App app = new App();

        // Connect to the database.
        // No arguments = running locally (IntelliJ), so use the db container's
        // mapped port with no wait. Docker passes "db:3306" and a delay instead.
        if (args.length < 1)
        {
            app.connect("localhost:33060", 0);
        }
        else
        {
            app.connect(args[0], Integer.parseInt(args[1]));
        }

        // create objects for each type of reports
        LanguageReport lr = new LanguageReport(con);

        CityReport cir = new CityReport(con);

        CountryReport ctryr = new CountryReport(con);

        PopulationReport pr = new PopulationReport(con);


//        // Getting Population of the world
//
//        pr.getWorldPopulation();
//
//        // Getting Population of a continent
//
//        pr.getContinentPopulation("North America"); // Replace "..." with input reader
//
//        // Getting Population of a region
//
//        pr.getRegionPopulation("Caribbean");    // Replace "..." with input reader
//
//        // Getting Population of a country
//
//        pr.getCountryPopulation("Cuba");    // Replace "..." with input reader
//
//        // Getting Population of a district
//
//        pr.getDistrictPopulation("Herat");  // Replace "..." with input reader
//
//        // Getting Population in and out of cities by country
//
//        pr.getInOutPopulationByCountry("Cuba"); // Replace "..." with input reader
//
//        // Getting Population in and out of cities by region
//
//        //Getting N populated Cities in Country
//        cir.getTopCitiesInCountry("Poland", 5);
//        //Getting N populated Cities in World
//        cir.getTopNCitiesWorld(10);
//
//
//        pr.getInOutPopulationByRegion("Caribbean"); // Replace "..." with input reader
//        // test language report function
//        lr.getLanguageSpeakers();
//        pr.getAllCapitalsByPopulation();
//
//        // Getting Population of a city
//        pr.getCityPopulation("Edinburgh");
//
//        // Getting Population in and out of cities by continent
//        pr.getInOutPopulationByContinent("Europe");
//
//        // Getting top N populated capital cities in the world
//        pr.getTopCapitalsWorld(5);
//
//        // Getting top N populated cities in a continent
//        cir.getTopCitiesByContinent("Europe", 5);
//
//        // Getting top N populated cities in a region
//        cir.getTopCitiesByRegion("British Islands", 5);
//
//        // Getting top N populated cities in a district
//        cir.getTopCitiesByDistrict("Scotland", 3);
//
//        // Getting top N populated capital cities in a continent
//        pr.getTopCapitalsByContinent("Europe", 5);
//
//        // Getting top N populated capital cities in a region
//        pr.getTopCapitalsByRegion("Caribbean", 5);
//
//        // Getting top N populated countries in a region
//        ctryr.getTopCountriesByRegion("Western Europe", 5);


        ResultSet languageSpeakers = lr.getLanguageSpeakers();
        printReport(languageSpeakers);
        // Database operations will go here later

        app.disconnect();
    }

    /**
     * Connect to the MySQL database.
     *
     * @param location host and port of the database, e.g. "localhost:33060" or "db:3306"
     * @param delay    milliseconds to wait before each connection attempt (gives the db time to start)
     */
    public void connect(String location, int delay)
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(delay);

                // Connect to database
                con = DriverManager.getConnection(
                        "jdbc:mysql://" + location + "/world?allowPublicKeyRetrieval=true&useSSL=false",
                        "root",
                        "example"
                );

                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Gets the current database connection, so tests can pass it to the report classes.
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
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }


    /**
     * Prints any report as a table: column headings, a separator line, then one line per row.
     * Prints "No results" if the report returned null.
     *
     * @param rs the result returned by a report method
     */
    public static void printReport(ResultSet rs) {
        if (rs == null) {
            System.out.println("No results");
            return;
        }

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column headings
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i));

                if (i < columnCount) {
                    System.out.print(" | ");
                }
            }
            System.out.println();

            // Print a separator
            for (int i = 1; i <= columnCount; i++) {
                System.out.print("------------");

                if (i < columnCount) {
                    System.out.print("+");
                }
            }
            System.out.println();

            // Print each row
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);

                    System.out.print(value == null ? "NULL" : value);

                    if (i < columnCount) {
                        System.out.print(" | ");
                    }
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error printing report: " + e.getMessage());
        }
    }



}
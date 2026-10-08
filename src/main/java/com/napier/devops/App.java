package com.napier.devops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App
{
    /**
     * Connection to MySQL database.
     */
    private static Connection con = null;

    public static void main(String[] args)
    {
        App app = new App();

        app.connect();

        // create objects for each type of reports
        LanguageReport lr = new LanguageReport(con);

        CityReport cir = new CityReport(con);

        CountryReport ctryr = new CountryReport(con);

        PopulationReport pr = new PopulationReport(con);


        // Getting Population of the world

        pr.getWorldPopulation();

        // Getting Population of a continent

        pr.getContinentPopulation("North America"); // Replace "..." with input reader

        // Getting Population of a region

        pr.getRegionPopulation("Caribbean");    // Replace "..." with input reader

        // Getting Population of a country

        pr.getCountryPopulation("Cuba");    // Replace "..." with input reader

        // Getting Population of a district

        pr.getDistrictPopulation("Herat");  // Replace "..." with input reader

        // Getting Population in and out of cities by country

        pr.getInOutPopulationByCountry("Cuba"); // Replace "..." with input reader

        // Getting Population in and out of cities by region

        //Getting N populated Cities in Country
//        cir.getTopCitiesInCountry("Italy", 5);


        pr.getInOutPopulationByRegion("Caribbean"); // Replace "..." with input reader
        // test language report function
        lr.getLanguageSpeakers();
        pr.getAllCapitalsByPopulation();

        // Getting Population of a city
        pr.getCityPopulation("Edinburgh");


        // Database operations will go here later

        app.disconnect();
    }

    /**
     * Connect to the MySQL database.
     */
    public void connect()
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
                Thread.sleep(30000);

                // Connect to database
                con = DriverManager.getConnection(
                        "jdbc:mysql://db:3306/world?allowPublicKeyRetrieval=true&useSSL=false",
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
}
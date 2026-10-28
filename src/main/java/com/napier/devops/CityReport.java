package com.napier.devops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CityReport {

    private Connection con;

    public CityReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets the population of each city in the world
     */
    // getAllCities()
    public ResultSet getAllCitiesWorld() {
        try {
            ResultSet rs = executeStatement("SELECT c.Name AS cName, c.Population AS cPop FROM city as c ORDER BY cPop DESC;");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }
    /** KSalawa
     * Gets the top N most populated cities in the world.
     *
     * @param n the number of cities to display
     */
    public ResultSet getTopNCitiesWorld(int n) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop " +
                            "FROM city AS c " +
                            "ORDER BY cPop DESC " +
                            "LIMIT " + n + ";"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets the population of all cities in the continent chosen by the user
     * @param Continent user selected continent for query
     */
    // getAllCitiesByContinent()
    public ResultSet getAllCitiesByContinent(String Continent) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c, country AS ctry WHERE c.CountryCode = ctry.Code AND ctry.Continent ='" + Continent + "' ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets the population of all cities in the region chosen by the user
     * @param Region user selected region for query
     */
    // getAllCitiesByRegion()
    public ResultSet getAllCitiesByRegion(String Region) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c, country AS ctry WHERE c.CountryCode = ctry.Code AND ctry.Region ='" + Region + "' ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets the population of all cities in the district chosen by the user
     * @param District user selected district for query
     */
    // getAllCitiesByDistrict
    public ResultSet getAllCitiesByDistrict(String District) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c WHERE ctry.District ='" + District + "' ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }


    /**
     * Takes the parameter 'Country' and returns all cities in that country by population
     * @param Country the name of the country, e.g. "Italy"
     */

    public ResultSet getCitiesInCountryByPopulation(String Country) {
        try {
            ResultSet rs = executeStatement(
                    "Select city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'Population' From city JOIN country ON city.CountryCode = country.Code WHERE country.Name ='" + Country + "'  ORDER BY city.Population DESC");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * returns all capital cities and their population from a continent
     * @param Name this is the name of the continent from which to find the Capital cities
     */
    public ResultSet getCapitalsInContinentByPopulation(String Name) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'City Population' FROM city JOIN country on city.ID = country.Capital WHERE country.Continent = '" + Name + "' ORDER BY city.Population DESC");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Returns all Capital cities and their population within a given region
     * @param Name names the region to conduct the search within
     */
    public ResultSet getCapitalsInRegionByPopulation(String Name){
        try {
            ResultSet rs = executeStatement(
                    "SELECT city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'City Population'  FROM city JOIN country on city.ID = country.Capital  WHERE country.Region ='" + Name + "' ORDER BY city.Population DESC");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    // Top N cities in a country #20
    public ResultSet getTopCitiesInCountry(String country, int n){
        try {
            ResultSet rs = executeStatement(
                    "SELECT city.Name AS cName, country.Name AS countryName, city.Population AS cPop FROM city JOIN country ON city.CountryCode = country.Code WHERE country.Name = '" + country + "' ORDER BY city.Population DESC LIMIT " + n + ";"            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated cities in a continent.
     * @param continent is the name of the continent
     * @param n is the number of cities to show
     */
    public ResultSet getTopCitiesByContinent(String continent, int n) {
        try {
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE country.Continent = '" + continent + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated cities in a region.
     * @param region is the name of the region
     * @param n is the number of cities to show
     */
    public ResultSet getTopCitiesByRegion(String region, int n) {
        try {
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE country.Region = '" + region + "'"
                    + "ORDER BY city.Population DESC LIMIT "+ n +";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated cities in a district.
     * @param district is the name of the district
     * @param n is the number of cities to show
     */
    public ResultSet getTopCitiesByDistrict(String district, int n) {
        try {
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE city.District = '" + district + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public ResultSet executeStatement(String query) throws SQLException {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }
}

package com.napier.devops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * country reports class
 */
public class CountryReport {

    /**
     * database connection
     */
    private Connection con;

    /**
     * country reports constructor
     * @param con database connection to be used
     */
    public CountryReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets the population of each country in the world
     * @return returns the result set from the query
     */
    public ResultSet getAllCountriesWorld() {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }


    /**
     * Gets the population of all countries in a selected continent
     * @param continent user selected continent for query
     * @return returns the result set from the query
     */
    public ResultSet getAllCountriesByContinent(String continent) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Continent='" + continent +"' ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets the population of all countries in a selected region
     * @param region user selected region for query
     * @return returns the result set from the query
     */
    public ResultSet getAllCountriesByRegion(String region) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Region='" + region +"' ORDER BY cPop DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated countries in a region.
     * @param region is the name of the region
     * @param n is the number of countries to show
     * @return returns the result set from the query
     */
    public ResultSet getTopCountriesByRegion(String region, int n) {
        try {
            ResultSet rs = executeStatement("SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS 'Capital' "
                    + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                    + "WHERE country.Region = '" + region + "'"
                    + "ORDER BY country.Population DESC LIMIT " + n + ";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Prints the top N countries in the world by Population
     * @param Number how many countries to be shown
     * @return returns the result set from the query
     */
    public ResultSet getTopNCountriesInWorld(int Number) {
        try {
        ResultSet rs = executeStatement(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country ORDER By country.Population DESC LIMIT " + Number );

        return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Returns a number of top Countries by population in a Continent
     * @param Continent the name of the continent to search within
     * @param Number the Number of top Countries to return
     * @return returns the result set from the query
     */
    public ResultSet getTopNCountriesByContinent(String Continent, int Number) {
        try {
            ResultSet rs = executeStatement(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country WHERE country.Continent ='" + Continent + "' ORDER BY country.Population DESC LIMIT " + Number);

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * @param query the sql query that will be run
     * @return @return returns the result set from the query
     * @throws SQLException throws error if any issues
     */
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

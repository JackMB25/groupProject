package com.napier.devops;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CountryReport {

    private Connection con;

    public CountryReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets the population of each country in the world
     */
    // getAllCountriesWorld()
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
     */
    // getAllCountriesByContinent()
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
     */
    // getAllCountriesByRegion()
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
    // getTopCountriesWorld()
    // getTopCountriesByContinent()

    /**
     * Gets and displays the top N populated countries in a region.
     * @param region is the name of the region
     * @param n is the number of countries to show
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
     */

    public ResultSet getTopNCountriesInWorld(int Number) {
        try {
        ResultSet rs = executeStatement(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country ORDER By country.Population DESC LIMIT " + Number );

        return rs;
    } catch (
    SQLException e) {
        System.out.println(e);
        return null;
    }
}

    /**
     * Returns a number of top Countries by population in a Continent
     * @param Continent the name of the continent to search within
     * @param Number the Number of top Countries to return
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

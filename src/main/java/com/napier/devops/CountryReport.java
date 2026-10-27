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
    public void getAllCountriesWorld() {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c ORDER BY cPop DESC;"
            );

            System.out.println("Name | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("cName") + " - " +
                                rs.getInt("cPop")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    /**
     * Gets the population of all countries in a selected continent
     * @param continent user selected continent for query
     */
    // getAllCountriesByContinent()
    public void getAllCountriesByContinent(String continent) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Continent='" + continent +"' ORDER BY cPop DESC;"
            );

            System.out.println("Name | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("cName") + " - " +
                                rs.getInt("cPop")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets the population of all countries in a selected region
     * @param region user selected region for query
     */
    // getAllCountriesByRegion()
    public void getAllCountriesByRegion(String region) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Region='" + region +"' ORDER BY cPop DESC;"
            );

            System.out.println("Name | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("cName") + " - " +
                                rs.getInt("cPop")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    // getTopCountriesWorld()
    // getTopCountriesByContinent()

    /**
     * Gets and displays the top N populated countries in a region.
     * @param region is the name of the region
     * @param n is the number of countries to show
     */
    public void getTopCountriesByRegion(String region, int n) {
        try {
            ResultSet rs = executeStatement("SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS 'Capital' "
                    + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                    + "WHERE country.Region = " + region
                    + "ORDER BY country.Population DESC LIMIT " + n + ";");
            System.out.println("Top " + n + " countries in " + region);
            System.out.println("Code | Name | Continent | Region | Population | Capital");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Code") + " | " +
                                rs.getString("Name") + " | " +
                                rs.getString("Continent") + " | " +
                                rs.getString("Region") + " | " +
                                rs.getInt("Population") + " | " +
                                rs.getString("Capital")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Prints the top N countries in the world by Population
     * @param Number how many countries to be shown
     */

    public void getTopNCountriesInWorld(int Number) {
        try {
        ResultSet rs = executeStatement(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country ORDER By country.Population DESC LIMIT " + Number );

        while (rs.next()) {
            System.out.println(
                    rs.getString("Country Name") + " - " +
                            rs.getInt("Country Population")
            );
        }
    } catch (
    SQLException e) {
        System.out.println(e);
    }
}

    /**
     * Returns a number of top Countries by population in a Continent
     * @param Continent the name of the continent to search within
     * @param Number the Number of top Countries to return
     */


    public void getTopNCountriesByContinent(String Continent, int Number) {
        try {
            ResultSet rs = executeStatement(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country WHERE country.Continent ='" + Continent + "' ORDER BY country.Population DESC LIMIT " + Number);

            while (rs.next()) {
                System.out.println(
                    rs.getString("Country Name") + " - " +
                            rs.getInt("Country Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ResultSet executeStatement(String query) {
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

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
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
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
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
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
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
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
            // left join city on the capital ID to get the capital's name
            // left join so a country with no capital still shows up
            // biggest first, LIMIT ? = top n
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS 'Capital' "
                            + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                            + "WHERE country.Region = ? "
                            + "ORDER BY country.Population DESC LIMIT ?;"
            );
            stmt.setString(1, region);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
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

}
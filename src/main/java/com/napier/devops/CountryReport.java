package com.napier.devops;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CountryReport {

    private Connection con;

    public CountryReport(Connection con) {
        this.con = con;
    }

    // getAllCountriesWorld()


    // getAllCountriesByContinent()

    // getAllCountriesByRegion()

    // getTopCountriesByContinent()

    // getTopCountriesByRegion()

    /**
     * Prints the top N countries in the world by Population
     * @param Number how many countries to be shown
     */

    public void getTopNCountriesInWorld(int Number) {
        try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
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
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT country.Name as 'Country Name', country.Population as 'Country Population' FROM country WHERE country.Continent ='" + Continent + "' ORDER BY country.Population DESC LIMIT " + Number);

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
}






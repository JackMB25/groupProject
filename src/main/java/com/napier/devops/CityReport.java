package com.napier.devops;

import java.sql.Connection;
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
    public void getAllCitiesWorld() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c ORDER BY cPop DESC;"
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
     * Gets the population of all cities in the continent chosen by the user
     * @param Continent user selected continent for query
     */
    // getAllCitiesByContinent()
    public void getAllCitiesByContinent(String Continent) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c, country AS ctry WHERE c.CountryCode = ctry.Code AND ctry.Continent ='" + Continent + "' ORDER BY cPop DESC;"
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
     * Gets the population of all cities in the region chosen by the user
     * @param Region user selected region for query
     */
    // getAllCitiesByRegion()
    public void getAllCitiesByRegion(String Region) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c, country AS ctry WHERE c.CountryCode = ctry.Code AND ctry.Region ='" + Region + "' ORDER BY cPop DESC;"
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
     * Gets the population of all cities in the district chosen by the user
     * @param District user selected district for query
     */
    // getAllCitiesByDistrict
    public void getAllCitiesByDistrict(String District) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name AS cName, c.Population AS cPop FROM city as c WHERE ctry.District ='" + District + "' ORDER BY cPop DESC;"
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

    // getTopCapitalsWorld()

    // getTopCapitalsByContinent()

    // getTopCapitalsByRegion()
}

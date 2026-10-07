package com.napier.devops;

import java.sql.*;

public class CityReport {

    private Connection con;

    public CityReport(Connection con) {
        this.con = con;
    }
    /**
     * Prints all cities in the world, ordered by population (largest first).
     */
    public void getAllCitiesWorld() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code ORDER BY city.Population DESC;"
            );

            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    /**
     * Prints all cities in a continent, ordered by population (largest first).
     * @param continent the continent to report on, e.g. "Europe"
     */
    public void getAllCitiesByContinent(String continent) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code WHERE country.Continent = ? ORDER BY city.Population DESC;"
            );
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    /**
     * Prints all cities in a region, ordered by population (largest first).
     * @param region the region to report on, e.g. "British Islands"
     */
    public void getAllCitiesByRegion(String region) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code WHERE country.Region = ? ORDER BY city.Population DESC;"
            );
            stmt.setString(1, region);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    /**
     * Prints all cities in a country, ordered by population (largest first).
     * @param country the country to report on, e.g. "United Kingdom"
     */
    public void getAllCitiesByCountry(String country) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code WHERE country.Name = ? ORDER BY city.Population DESC;"
            );
            stmt.setString(1, country);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    /**
     * Prints all cities in a district, ordered by population (largest first).
     * @param district the district to report on, e.g. "Scotland"
     */
    public void getAllCitiesByDistrict(String district) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code WHERE city.District = ? ORDER BY city.Population DESC;"
            );
            stmt.setString(1, district);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    /**
     * Prints the top N most populated cities in the world.
     * @param n the number of cities to show
     */
    public void getTopCitiesWorld(int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name, country.Name AS Country, city.District, city.Population FROM city JOIN country ON city.CountryCode = country.Code ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setInt(1, n);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " - " +
                                rs.getString("Country") + " - " +
                                rs.getString("District") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
    // getAllCapitalsWorld()

    // getAllCapitalsByContinent()

    // getAllCapitalsByRegion()

    // getTopCapitalsWorld()

    // getTopCapitalsByContinent()

    // getTopCapitalsByRegion()
}

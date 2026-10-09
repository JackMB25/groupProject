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
    /** KSalawa
     * Gets the top N most populated cities in the world.
     *
     * @param n the number of cities to display
     */
    public void getTopNCitiesWorld(int n) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT c.Name AS cName, c.Population AS cPop " +
                            "FROM city AS c " +
                            "ORDER BY cPop DESC " +
                            "LIMIT " + n + ";"
            );

            System.out.println("========================================");
            System.out.println("Top " + n + " Cities in the World by Population");
            System.out.println("========================================");
            System.out.printf("%-25s %15s%n", "City", "Population");
            System.out.println("----------------------------------------");

            while (rs.next()) {
                System.out.printf(
                        "%-25s %,15d%n",
                        rs.getString("cName"),
                        rs.getInt("cPop")
                );
            }

            rs.close();
            stmt.close();

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

    // getCitiesInCountryByPopulation()


    /**
     * Takes the parameter 'Country' and returns all cities in that country by population
     * @param Country the name of the country, e.g. "Italy"
     */

    public void getCitiesInCountryByPopulation(String Country) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "Select city.Name as 'City Name', country.Name as 'Country Name', city.Population From city JOIN country ON city.CountryCode = Country.Code WHERE country.Name ='" + Country + "'  ORDER BY city.Population DESC;");

            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " - " +
                                rs.getString("Country Name") + " - " +
                                rs.getInt("cities.Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    // Top N cities in a country #20
    public void getTopCitiesInCountry(String country, int n){
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT city.Name AS cName, country.Name AS countryName, city.Population AS cPop FROM city JOIN country ON city.CountryCode = country.Code WHERE country.Name = '" + country + "' ORDER BY city.Population DESC LIMIT " + n + ";"            );

            System.out.println("========================================");
            System.out.println("Top " + n + " Cities in " + country + " by Population");
            System.out.println("========================================");
            System.out.printf("%-20s %-15s %15s%n", "City", "Country", "Population");
            System.out.println("------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf(
                        "%-20s %-15s %,15d%n",
                        rs.getString("cName"),
                        rs.getString("countryName"),
                        rs.getInt("cPop")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated cities in a continent.
     * @param continent is the name of the continent
     * @param n is the number of cities to show
     */
    public void getTopCitiesByContinent(String continent, int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                            + "FROM city JOIN country ON city.CountryCode = country.Code "
                            + "WHERE country.Continent = ? "
                            + "ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setString(1, continent);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top " + n + " cities in " + continent);
            System.out.println("City | Country | District | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City") + " | " +
                                rs.getString("Country") + " | " +
                                rs.getString("District") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated cities in a region.
     * @param region is the name of the region
     * @param n is the number of cities to show
     */
    public void getTopCitiesByRegion(String region, int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                            + "FROM city JOIN country ON city.CountryCode = country.Code "
                            + "WHERE country.Region = ? "
                            + "ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setString(1, region);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top " + n + " cities in " + region);
            System.out.println("City | Country | District | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City") + " | " +
                                rs.getString("Country") + " | " +
                                rs.getString("District") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated cities in a district.
     * @param district is the name of the district
     * @param n is the number of cities to show
     */
    public void getTopCitiesByDistrict(String district, int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                            + "FROM city JOIN country ON city.CountryCode = country.Code "
                            + "WHERE city.District = ? "
                            + "ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setString(1, district);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top " + n + " cities in " + district);
            System.out.println("City | Country | District | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City") + " | " +
                                rs.getString("Country") + " | " +
                                rs.getString("District") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

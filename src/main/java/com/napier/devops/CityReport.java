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

    /**
     * Takes the parameter 'Country' and returns all cities in that country by population
     * @param Country the name of the country, e.g. "Italy"
     */
    public void getCitiesInCountryByPopulation(String Country) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "Select city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'Population' From city JOIN country ON city.CountryCode = country.Code WHERE country.Name ='" + Country + "'  ORDER BY city.Population DESC");

            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " - " +
                                rs.getString("Country Name") + " - " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * returns all capital cities and their population from a continent
     * @param Name this is the name of the continent from which to find the Capital cities
     */
    public void getCapitalsInContinentByPopulation(String Name) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'City Population' FROM city JOIN country on city.ID = country.Capital WHERE country.Continent = '" + Name + "' ORDER BY city.Population DESC");

            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " - " +
                                rs.getString("Country Name") + " - " +
                                rs.getInt("City Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Returns all Capital cities and their population within a given region
     * @param Name names the region to conduct the search within
     */
    public void getCapitalsInRegionByPopulation(String Name){
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT city.Name as 'City Name', country.Name as 'Country Name', city.Population as 'City Population'  FROM city JOIN country on city.ID = country.Capital  WHERE country.Region ='" + Name + "' ORDER BY city.Population DESC");

            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " - " +
                                rs.getString("Country Name") + " - " +
                                rs.getInt("City Population")
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
}
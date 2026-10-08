package com.napier.devops;

import java.sql.*;

public class PopulationReport {

    private Connection con;

    public PopulationReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets and displays the total population of the world
     * from the World database.
     */
    public void getWorldPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(       // Sending SQL Query to the World database
                    "SELECT SUM(Population) as 'sumPopulation' FROM country;"
            );
            System.out.println("Population of the world");      // Printing out heading
            while (rs.next()) {
                System.out.println(
                        rs.getLong("sumPopulation")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the continent
     * from the World database.
     */
    public void getContinentPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Continent, SUM(Population) as sumPopulation FROM country GROUP BY Continent ORDER BY Continent ASC;"
            );
            System.out.println("Continent name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Continent") + " | " +
                                rs.getLong("sumPopulation")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the region
     * from the World database.
     */
    public void getRegionPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Region, SUM(Population) as sumPopulation FROM country GROUP BY Region ORDER BY Region ASC;"
            );
            System.out.println("Region name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Region") + " | " +
                                rs.getLong("sumPopulation")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the country
     * from the World database.
     */
    public void getCountryPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Name as CountryName, SUM(Population) as sumPopulation FROM country GROUP BY Name ORDER BY Name ASC;"
            );
            System.out.println("Country name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("CountryName") + " | " +
                                rs.getLong("sumPopulation")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the district
     * from the World database.
     */
    public void getDistrictPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT District, SUM(Population) as sumPopulation FROM city GROUP BY District ORDER BY District ASC;"
            );
            System.out.println("District name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("District") + " | " +
                                rs.getLong("sumPopulation")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays population that live in and out of cities by country
     * from the World database.
     */
    public void getInOutPopulationByCountry() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT country.Name AS 'Country', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheCountry' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode GROUP BY country.Name ORDER BY country.Name ASC;"
            );

            System.out.println("Country Name | Population out of cities | Population in cities | Total population of the country");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Country") + " | " +
                                rs.getLong("PopulationOutOfCities") + " | " +
                                rs.getLong("PopulationInCities") + " | " +
                                rs.getLong("TotalPopulationOfTheCountry")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays population that live in and out of cities by region
     * from the World database.
     */
    public void getInOutPopulationByRegion() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT country.region AS 'Region', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheRegion' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode GROUP BY country.region ORDER BY country.Region ASC;"
            );

    public void getAllCapitalsByPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "Select country.Name as 'Country Name', city.Name as 'City Name',city.Population FROM city JOIN country on city.ID = country.Capital ORDER BY city.population DESC;"
            );

            while (rs.next()) {
                System.out.println(
                        rs.getString("Country Name") + " - " +
                                rs.getString("City Name")  + " - " +
                                rs.getInt("city.Population")
                );
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

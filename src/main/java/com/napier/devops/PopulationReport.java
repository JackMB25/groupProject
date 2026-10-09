package com.napier.devops;

import java.sql.*;

public class PopulationReport {

    private Connection con;

    public PopulationReport(Connection con) {
        this.con = con;
    }

    /**
     * Gets and displays the total population of the world from the World database.
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
     * \Gets and displays the total population of the continent from the World database.
     * @param continent Is the name of the continent.
     */
    public void getContinentPopulation(String continent) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Name, Population FROM country WHERE Continent='" + continent + "' ORDER BY Name ASC;"
            );
            System.out.println("Name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " | " +
                                rs.getLong("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the region from the World database.
     * @param region is the name of the region
     */
    public void getRegionPopulation(String region) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Region, SUM(Population) as sumPopulation FROM country WHERE Region='" + region + "';"
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
     * Gets and displays the total population of the country from the World database.
     * @param country is the name of the country.
     */
    public void getCountryPopulation(String country) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT Name, Population FROM country WHERE Name='" + country + "';"
            );
            System.out.println("Country name | Total population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " | " +
                                rs.getLong("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the total population of the district from the World database.
     * @param district is the name of the district
     */
    public void getDistrictPopulation(String district) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT District, SUM(Population) as sumPopulation FROM city WHERE District = '" + district + "' GROUP BY District ORDER BY District ASC;"
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
     * Gets and displays population that live in and out of cities by country from the World database.
     * @param country is the name of the country
     */
    public void getInOutPopulationByCountry(String country) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT country.Name AS 'Country', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheCountry' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Name = '" + country + "' GROUP BY country.Name ORDER BY country.Name ASC;"
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
     * Gets and displays population that live in and out of cities by region from the World database.
     * @param region is the name of the region
     */
    public void getInOutPopulationByRegion(String region) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT country.region AS 'Region', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheRegion' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Region ='" + region + "' GROUP BY country.Region ORDER BY country.Region ASC;"
            );

            System.out.println("Region | Population out of cities | Population in cities | Total population in the region");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Region") + " | " +
                                rs.getLong("PopulationOutOfCities") + " | " +
                                rs.getLong("PopulationInCities") + " | " +
                                rs.getLong("TotalPopulationOfTheRegion")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    /**
     * Prints all Capital Cities ordered by their population
     */
    public void getAllCapitalsByPopulation() {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "Select country.Name as 'Country Name', city.Name as 'City Name',city.Population FROM city JOIN country on city.ID = country.Capital ORDER BY city.population DESC;"
            );

            while (rs.next()) {
                System.out.println(
                        rs.getString("Country Name") + " - " +
                                rs.getString("City Name") + " - " +
                                rs.getInt("city.Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the population of a city from the World database.
     * @param city is the name of the city
     */
    public void getCityPopulation(String city) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT Name, Population FROM city WHERE Name = ?;"
            );
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();
            System.out.println("City name | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Name") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    /**
     * Gets and displays population that live in and out of cities by continent from the World database,
     * including the percentage of people living in and out of cities.
     * @param continent is the name of the continent
     */
    public void getInOutPopulationByContinent(String continent) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Continent AS 'Continent', "
                            + "sum(country.Population) AS 'TotalPopulationOfTheContinent', "
                            + "sum(city.CityPopulation) AS 'PopulationInCities', "
                            + "ROUND(sum(city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentInCities', "
                            + "sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', "
                            + "ROUND(sum(country.Population - city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentOutOfCities' "
                            + "FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city "
                            + "ON country.Code = city.CountryCode "
                            + "WHERE country.Continent = ? "
                            + "GROUP BY country.Continent ORDER BY country.Continent ASC;"
            );
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Continent | Total population | In cities | % in cities | Out of cities | % out of cities");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Continent") + " | " +
                                rs.getLong("TotalPopulationOfTheContinent") + " | " +
                                rs.getLong("PopulationInCities") + " | " +
                                rs.getDouble("PercentInCities") + "% | " +
                                rs.getLong("PopulationOutOfCities") + " | " +
                                rs.getDouble("PercentOutOfCities") + "%"
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated capital cities in the world.
     * @param n is the number of capital cities to show
     */
    public void getTopCapitalsWorld(int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT country.Name AS 'Country Name', city.Name AS 'City Name', city.Population FROM city JOIN country ON city.ID = country.Capital ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setInt(1, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Capital | Country | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " | " +
                                rs.getString("Country Name") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated capital cities in a continent.
     * @param continent is the name of the continent
     * @param n is the number of capital cities to show
     */
    public void getTopCapitalsByContinent(String continent, int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                            + "FROM city JOIN country ON city.ID = country.Capital "
                            + "WHERE country.Continent = ? "
                            + "ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setString(1, continent);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top " + n + " capital cities in " + continent);
            System.out.println("Capital | Country | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " | " +
                                rs.getString("Country Name") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Gets and displays the top N populated capital cities in a region.
     * @param region is the name of the region
     * @param n is the number of capital cities to show
     */
    public void getTopCapitalsByRegion(String region, int n) {
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                            + "FROM city JOIN country ON city.ID = country.Capital "
                            + "WHERE country.Region = ? "
                            + "ORDER BY city.Population DESC LIMIT ?;"
            );
            stmt.setString(1, region);
            stmt.setInt(2, n);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Top " + n + " capital cities in " + region);
            System.out.println("Capital | Country | Population");
            while (rs.next()) {
                System.out.println(
                        rs.getString("City Name") + " | " +
                                rs.getString("Country Name") + " | " +
                                rs.getInt("Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

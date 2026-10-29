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
    public ResultSet getWorldPopulation() {
        try {
            ResultSet rs = executeStatement(       // Sending SQL Query to the World database
                    "SELECT SUM(Population) as 'sumPopulation' FROM country;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * \Gets and displays the total population of the continent from the World database.
     * @param continent Is the name of the continent.
     */
    public ResultSet getContinentPopulation(String continent) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT Name, Population FROM country WHERE Continent='" + continent + "' ORDER BY Name ASC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the total population of the region from the World database.
     * @param region is the name of the region
     */
    public ResultSet getRegionPopulation(String region) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT Region, SUM(Population) as sumPopulation FROM country WHERE Region='" + region + "';"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the total population of the country from the World database.
     * @param country is the name of the country.
     */
    public ResultSet getCountryPopulation(String country) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT Name, Population FROM country WHERE Name='" + country + "';"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the total population of the district from the World database.
     * @param district is the name of the district
     */
    public ResultSet getDistrictPopulation(String district) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT District, SUM(Population) as sumPopulation FROM city WHERE District = '" + district + "' GROUP BY District ORDER BY District ASC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays population that live in and out of cities by country from the World database.
     * @param country is the name of the country
     */
    public ResultSet getInOutPopulationByCountry(String country) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT country.Name AS 'Country', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheCountry' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Name = '" + country + "' GROUP BY country.Name ORDER BY country.Name ASC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays population that live in and out of cities by region from the World database.
     * @param region is the name of the region
     */
    public ResultSet getInOutPopulationByRegion(String region) {
        try {
            ResultSet rs = executeStatement(
                    "SELECT country.region AS 'Region', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheRegion' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Region ='" + region + "' GROUP BY country.Region ORDER BY country.Region ASC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }


    /**
     * Prints all Capital Cities ordered by their population
     */
    public ResultSet getAllCapitalsByPopulation() {
        try {
            ResultSet rs = executeStatement(
                    "Select country.Name as 'Country Name', city.Name as 'City Name',city.Population FROM city JOIN country on city.ID = country.Capital ORDER BY city.population DESC;"
            );

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the population of a city from the World database.
     * @param city is the name of the city
     */
    public ResultSet getCityPopulation(String city) {
        // PreparedStatement: the name is sent as data, not glued into the SQL,
        // so names with apostrophes work and SQL injection is not possible
        return executePrepared("SELECT Name, Population FROM city WHERE Name = ?;", city);
    }
    
    /**
     * Gets and displays population that live in and out of cities by continent from the World database,
     * including the percentage of people living in and out of cities.
     * @param continent is the name of the continent
     */
    public ResultSet getInOutPopulationByContinent(String continent) {
        try {
            ResultSet rs = executeStatement("SELECT country.Continent AS 'Continent', "
                    + "sum(country.Population) AS 'TotalPopulationOfTheContinent', "
                    + "sum(city.CityPopulation) AS 'PopulationInCities', "
                    + "ROUND(sum(city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentInCities', "
                    + "sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', "
                    + "ROUND(sum(country.Population - city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentOutOfCities' "
                    + "FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city "
                    + "ON country.Code = city.CountryCode "
                    + "WHERE country.Continent = '" + continent + "'"
                    + "GROUP BY country.Continent ORDER BY country.Continent ASC;");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated capital cities in the world.
     * @param n is the number of capital cities to show
     */
    public ResultSet getTopCapitalsWorld(int n) {
        try {
            ResultSet rs = executeStatement("SELECT country.Name AS 'Country Name', city.Name AS 'City Name', city.Population FROM city JOIN country ON city.ID = country.Capital ORDER BY city.Population DESC LIMIT "+ n +";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated capital cities in a continent.
     * @param continent is the name of the continent
     * @param n is the number of capital cities to show
     */
    public ResultSet getTopCapitalsByContinent(String continent, int n) {
        try {
            ResultSet rs = executeStatement("SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                    + "FROM city JOIN country ON city.ID = country.Capital "
                    + "WHERE country.Continent = '" + continent + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets and displays the top N populated capital cities in a region.
     * @param region is the name of the region
     * @param n is the number of capital cities to show
     */
    public ResultSet getTopCapitalsByRegion(String region, int n) {
        try {

            ResultSet rs = executeStatement("SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                    + "FROM city JOIN country ON city.ID = country.Capital "
                    + "WHERE country.Region = '" + region + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");

            return rs;
        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Runs a query with ? placeholders, filling them in safely with a PreparedStatement.
     * @param query  the SQL with a ? for each value
     * @param params the values for the ?s, in order
     * @return the results, or null if the query failed
     */
    public ResultSet executePrepared(String query, Object... params) {
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeQuery();
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

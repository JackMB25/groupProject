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
            ResultSet rs = executeStatement("SELECT c.Name AS cName, c.Population AS cPop FROM city as c ORDER BY cPop DESC;");

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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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


    /**
     * Takes the parameter 'Country' and returns all cities in that country by population
     * @param Country the name of the country, e.g. "Italy"
     */

    public void getCitiesInCountryByPopulation(String Country) {
        try {
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement(
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
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE country.Continent = '" + continent + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");
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
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE country.Region = '" + region + "'"
                    + "ORDER BY city.Population DESC LIMIT "+ n +";");
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
            ResultSet rs = executeStatement("SELECT city.Name AS 'City', country.Name AS 'Country', city.District, city.Population "
                    + "FROM city JOIN country ON city.CountryCode = country.Code "
                    + "WHERE city.District = '" + district + "'"
                    + "ORDER BY city.Population DESC LIMIT " + n + ";");
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

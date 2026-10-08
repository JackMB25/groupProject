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

    // getAllCapitalsWorld()



    // getAllCapitalsByRegion()

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
}

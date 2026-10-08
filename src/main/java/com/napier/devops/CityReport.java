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

    // getAllCapitalsByContinent()

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
}

package com.napier.devops;

import java.sql.*;

public class PopulationReport {

    private Connection con;

    public PopulationReport(Connection con) {
        this.con = con;
    }
    // getPopulationByContinent()

    // getPopulationByRegion()

    // getPopulationByCountry()

    // getWorldPopulation()

    // getContinentPopulation()

    // getRegionPopulation()

    // getCountryPopulation()

    // getDistrictPopulation()

    // getCityPopulation()


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
                                rs.getString("City Name")  + " - " +
                                rs.getInt("city.Population")
                );
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }
}

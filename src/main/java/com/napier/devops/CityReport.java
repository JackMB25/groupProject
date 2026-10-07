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


    // getAllCapitalsWorld()

    // getAllCapitalsByContinent()

    // getAllCapitalsByRegion()

    // getTopCapitalsWorld()

    // getTopCapitalsByContinent()

    // getTopCapitalsByRegion()
}

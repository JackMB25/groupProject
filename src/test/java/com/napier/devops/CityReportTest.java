package com.napier.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

/**
 * Unit tests for CityReport, using a fake database so no real connection is needed
 */
public class CityReportTest {

    // Fake database objects
    Connection con;
    Statement stmt;
    ResultSet rs;
    CityReport cityReport;


    /**
     * Sets up the fake database for all tests to run from before each test
     */
    @BeforeEach
    void init() throws SQLException {
        con = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);
        when(con.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);


        cityReport = new CityReport(con);
    }

    /**
     * Checks the continent passed in is used to filter the SQL
     */
    @Test
    void getCapitalsInContinentByPopulationTest() throws SQLException{
        // Run the test searching through Asia
        cityReport.getCapitalsInContinentByPopulation("Asia");

        // Check the SQL includes the Asia filter
        verify(stmt).executeQuery(contains("country.Continent = 'Asia'"));
    }

    /**
     * Checks that the Region successfully filters the SQL
     */
    @Test
    void getCapitalsInRegionByPopulationTest() throws SQLException{
        // Run the test searching with Lothian
        cityReport.getCapitalsInRegionByPopulation("Lothian");

        //Check the SQL includes the test Region
        verify(stmt).executeQuery(contains("country.Region ='Lothian'"));
    }

    /**
     * Checks that the country filters the SQL
     */
    @Test
    void getCitiesInCountryByPopulationTest() throws SQLException {
        // Run the test searching with France
        cityReport.getCitiesInCountryByPopulation("France");

        // Check the SQL includes the test String
        verify(stmt).executeQuery(contains("country.Name ='France'"));
    }

    /**
     * Checks that the order of cities is returned in descending order
     */
    @Test
    void getAllCitiesWorldTest() throws SQLException {
        // run the method
        cityReport.getAllCitiesWorld();

        // there is no WHERE clause, so will test it orders the cities correctly
        verify(stmt).executeQuery(contains("ORDER BY cPop DESC"));

    }

    /**
     * Checks that the LIMIT filter applies to the SQL
     */
    @Test
    void getTopCitiesWorldTest() throws SQLException{
        // Run the method searching with 3
        cityReport.getTopNCitiesWorld(3);

        //Check the SQL will output only 3 cities
        verify(stmt).executeQuery(contains("LIMIT 3"));
    }

    /**
     * Checks that the continent filters the SQL
     */
    @Test
    void getAllCitiesByContinentTest() throws SQLException{

        cityReport.getAllCitiesByContinent("Asia");

        //Check the SQL received the continent correctly
        verify(stmt).executeQuery(contains("ctry.Continent ='Asia'"));

    }

    /**
     * Checks that the region filters the SQL
     */
    @Test
    void getAllCitiesByRegionTest() throws SQLException {
        // Run the method with a test Region
        cityReport.getAllCitiesByRegion("Miyajima");

        // Check that SQl received the test Region
        verify(stmt).executeQuery(contains("ctry.Region ='Miyajima'"));
    }

    /**
     * Checks that the district filters the SQL
     */
    @Test
    void getAllCitiesByDistrictTest() throws SQLException{
        // Run the method with a test District
        cityReport.getAllCitiesByDistrict("Racoon City");

        // Check that SQL received the test District
        verify(stmt).executeQuery(contains("c.District ='Racoon City'"));

    }

    /**
     * Checks that the country and LIMIT filters the SQL
     */
    @Test
    void getTopCitiesInCountryTest() throws SQLException{
        // Run the method with Japan and 5 being the test data for country and N
        cityReport.getTopCitiesInCountry("Japan", 5);

        // Check that SQL received Japan
        verify(stmt).executeQuery(contains("country.Name = 'Japan'"));
        //Check that SQL received the Limit of 5
        verify(stmt).executeQuery(contains("LIMIT 5"));
    }

    /**
     * Checks that the continent and LIMIT filters the SQL
     */
    @Test
    void getTopCitiesByContinentTest() throws SQLException{
        // Run the method with Japan and 5 being the test data for country and N
        cityReport.getTopCitiesByContinent("Africa", 2);

        // Check that SQL received Africa
        verify(stmt).executeQuery(contains("country.Continent = 'Africa'"));
        //Check that SQL received the Limit of 2
        verify(stmt).executeQuery(contains("LIMIT 2"));
    }

    /**
     * Checks that the region and LIMIT filters the SQL
     */
    @Test
    void getTopCitiesByRegionTest() throws SQLException{
        //Run the Method with test data
        cityReport.getTopCitiesByRegion("Springfield", 8);

        //Check that SQl received Springfield
        verify(stmt).executeQuery(contains("country.Region = 'Springfield'"));
        //Check that SQL received the Limit of 8
        verify(stmt).executeQuery(contains("LIMIT 8"));
    }

    /**
     * Checks that the district and LIMIT filters the SQL
     */
    @Test
    void getTopCitiesByDistrictTest() throws SQLException{
        //Run the Method with test data
        cityReport.getTopCitiesByDistrict("Montpelier", 6);

        //Check that SQl received Montpelier
        verify(stmt).executeQuery(contains("city.District = 'Montpelier'"));
        //Check that SQL received the Limit of 6
        verify(stmt).executeQuery(contains("LIMIT 6"));
    }

}

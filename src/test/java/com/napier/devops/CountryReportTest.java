package com.napier.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CountryReportTest {
    Connection con;
    Statement stmt;
    ResultSet rs;
    CountryReport report;

    /**
     * Creates and executes moc database objects before each test
     *
     * Configures the behaviour of the moc database
     *
     * Creates the object to run the tests on
     * @throws SQLException - exception if database fails
     */
    @BeforeEach
    void setUp() throws SQLException {

        // Create mock database objects
        con = mock(Connection.class);
        stmt = mock(Statement.class);
        rs = mock(ResultSet.class);

        // Configure mock behaviour
        when(con.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(anyString())).thenReturn(rs);

        // Create the object to test
        report = new CountryReport(con);
    }
    /**
     * Tests that the countries world test method returns the mocked ResultSet and executes the expected SQL query.
     * @throws SQLException
     */
    @Test
    void getAllCountriesWorldTest () throws SQLException {

        // Call the method being tested
        ResultSet result = report.getAllCountriesWorld();

        // Call the method being tested
        assertSame(rs, result);

        // Verify that the correct SQL query was executed
        verify(stmt).executeQuery(
                "SELECT c.Name AS cName, c.Population AS cPop FROM country as c ORDER BY cPop DESC;"
        );
    }

    /**
     * Tests that getAllCountriesByContinent returns the mocked ResultSet
     * and executes the expected SQL query for the specified continent.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void getAllCountriesByContinentTest() throws SQLException {

        // Call the method being tested
        ResultSet result = report.getAllCountriesByContinent("Europe");

        // Call the method being tested
        assertSame(rs, result);

        // Verify that the correct SQL query was executed
        verify(stmt).executeQuery(
                "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Continent='Europe' ORDER BY cPop DESC;"
        );
    }

    /**
     * Tests that getAllCountriesByRegion returns the mocked ResultSet
     * and executes the expected SQL query for the specified region.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void getAllCountriesByRegionTest()  throws SQLException {

        // Call the method being tested
        ResultSet result = report.getAllCountriesByRegion("British Islands");

        // Call the method being tested
        assertSame(rs, result);

        // Verify that the correct SQL query was executed
        verify(stmt).executeQuery(
                "SELECT c.Name AS cName, c.Population AS cPop FROM country as c WHERE c.Region='British Islands' ORDER BY cPop DESC;"
        );
    }

    /**
     * Tests that getAllCountriesByRegion returns the mocked ResultSet
     * and executes the expected SQL query for the specified region.
     *
     * @throws SQLException if a database error occurs
     */
    @Test
    void getTopCountriesByRegionTest()  throws SQLException {

        // Call the method being tested
        ResultSet result = report.getTopCountriesByRegion("British Islands", 4);

        // Call the method being tested
        assertSame(rs, result);

        // Verify that the correct SQL query was executed
        verify(stmt).executeQuery("SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, city.Name AS 'Capital' "
                + "FROM country LEFT JOIN city ON country.Capital = city.ID "
                + "WHERE country.Region = 'British Islands'"
                + "ORDER BY country.Population DESC LIMIT 4;");
    }
}

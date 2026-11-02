package com.napier.devops;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PopulationReport class
 *
 * Mockito was used to simulate database
 */
public class PopulationReportTest {
    Connection con;
    Statement stmt;
    ResultSet rs;
    PopulationReport report;

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
        report = new PopulationReport(con);
    }
    /**
     * Tests that the world population method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getWorldPopulationTest() throws SQLException {

        ResultSet result = report.getWorldPopulation();

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT SUM(Population) as 'sumPopulation' FROM country;"
        );
    }
    /**
     * Tests that the continent population method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getContinentPopulationTest() throws SQLException {

        ResultSet result = report.getContinentPopulation("Europe");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT Name, Population FROM country WHERE Continent='Europe' ORDER BY Name ASC;"
        );
    }

    /**
     * Tests that the region population method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getRegionPopulationTest() throws SQLException {

        ResultSet result = report.getRegionPopulation("British Islands");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT Region, SUM(Population) as sumPopulation FROM country WHERE Region='British Islands';"
        );
    }
    /**
     * Tests that the country population method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getCountryPopulationTest() throws SQLException {

        ResultSet result = report.getCountryPopulation("France");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT Name, Population FROM country WHERE Name='France';"
        );
    }

    /**
     * Tests that the distinct population method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getDistrictPopulationTest() throws SQLException {

        ResultSet result = report.getDistrictPopulation("England");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT District, SUM(Population) as sumPopulation FROM city WHERE District = 'England' GROUP BY District ORDER BY District ASC;"
        );
    }

    /**
     * Tests that the population in and out of the country method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getInOutPopulationByCountryTest() throws SQLException {

        ResultSet result = report.getInOutPopulationByCountry("France");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT country.Name AS 'Country', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheCountry' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Name = 'France' GROUP BY country.Name ORDER BY country.Name ASC;"
        );
    }

    /**
     * Tests that the population in and out of the region method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getInOutPopulationByRegionTest() throws SQLException {

        ResultSet result = report.getInOutPopulationByRegion("British Islands");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT country.region AS 'Region', sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', sum(city.CityPopulation) AS 'PopulationInCities', sum(country.Population) AS 'TotalPopulationOfTheRegion' FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city ON country.Code = city.CountryCode WHERE country.Region ='British Islands' GROUP BY country.Region ORDER BY country.Region ASC;"
        );
    }

    /**
     * Tests that the method displaying the capitals by population returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getAllCapitalsByPopulationTest() throws SQLException {

        ResultSet result = report.getAllCapitalsByPopulation();

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "Select country.Name as 'Country Name', city.Name as 'City Name',city.Population FROM city JOIN country on city.ID = country.Capital ORDER BY city.population DESC;"
        );
    }

    /**
     * Tests that the method displaying the cities by population returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getCityPopulationTest() throws SQLException {

        // getCityPopulation uses a PreparedStatement, so the name is passed as a parameter
        PreparedStatement pstmt = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeQuery()).thenReturn(rs);

        ResultSet result = report.getCityPopulation("London");

        assertSame(rs, result);

        verify(con).prepareStatement("SELECT Name, Population FROM city WHERE Name = ?;");
        verify(pstmt).setObject(1, "London");
    }

    /**
     * Tests that the population in and out of the continent method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getInOutPopulationByContinentTest() throws SQLException {

        ResultSet result = report.getInOutPopulationByContinent("Europe");

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT country.Continent AS 'Continent', "
                        + "sum(country.Population) AS 'TotalPopulationOfTheContinent', "
                        + "sum(city.CityPopulation) AS 'PopulationInCities', "
                        + "ROUND(sum(city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentInCities', "
                        + "sum(country.Population - city.CityPopulation) AS 'PopulationOutOfCities', "
                        + "ROUND(sum(country.Population - city.CityPopulation) / sum(country.Population) * 100, 2) AS 'PercentOutOfCities' "
                        + "FROM country JOIN ( SELECT CountryCode, SUM(Population) AS CityPopulation FROM city GROUP BY CountryCode) city "
                        + "ON country.Code = city.CountryCode "
                        + "WHERE country.Continent = 'Europe'"
                        + "GROUP BY country.Continent ORDER BY country.Continent ASC;"
        );
    }

    /**
     * Tests that the top capitals in the world method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getTopCapitalsWorldTest() throws SQLException {

        ResultSet result = report.getTopCapitalsWorld(10);

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT country.Name AS 'Country Name', city.Name AS 'City Name', city.Population FROM city JOIN country ON city.ID = country.Capital ORDER BY city.Population DESC LIMIT 10;"
        );
    }

    /**
     * Tests that the top capitals in the continent method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getTopCapitalsByContinentTest() throws SQLException {

        ResultSet result = report.getTopCapitalsByContinent("Europe", 5);

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                        + "FROM city JOIN country ON city.ID = country.Capital "
                        + "WHERE country.Continent = 'Europe'"
                        + "ORDER BY city.Population DESC LIMIT 5;"
        );
    }

    /**
     * Tests that the top capitals by region method returns the mocked ResultSet and executes the expected SQL query.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void getTopCapitalsByRegionTest() throws SQLException {

        ResultSet result = report.getTopCapitalsByRegion("British Islands", 5);

        assertSame(rs, result);

        verify(stmt).executeQuery(
                "SELECT city.Name AS 'City Name', country.Name AS 'Country Name', city.Population "
                        + "FROM city JOIN country ON city.ID = country.Capital "
                        + "WHERE country.Region = 'British Islands'"
                        + "ORDER BY city.Population DESC LIMIT 5;"
        );
    }

    /**
     * Tests that executeStatement returns the mocked ResultSet and passes the supplied SQL query to the database statement.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test
    void executeStatementTest() throws SQLException {

        String query = "SELECT * FROM country;";

        ResultSet result = report.executeStatement(query);

        assertSame(rs, result);

        verify(con).createStatement();

        verify(stmt).executeQuery(query);
    }

    /**
     * Tests that executeStatement returns null when the mocked database statement throws an SQLException.
     *
     * @throws SQLException - exception if SQL error occurs
     */
    @Test

    void executeStatementSQLExceptionTest() throws SQLException {

        // Simulate an SQL error

        when(stmt.executeQuery(anyString()))

                .thenThrow(new SQLException("Database error"));

        ResultSet result = report.executeStatement(
                "SELECT * FROM country;"
        );

        // Current executeStatement() catches SQLException and returns null
        assertNull(result);
    }

}
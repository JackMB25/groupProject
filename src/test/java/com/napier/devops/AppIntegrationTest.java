package com.napier.devops;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the reports. These connect to the real world database in the db container
 * (localhost:33060) and check the reports give the right answers from world.sql.
 * Run: docker compose up -d db, then run this class in IntelliJ or
 * mvn -Dtest=com.napier.devops.AppIntegrationTest test
 * Depth over breadth: fewer reports, each checked for the right values, sort order, row counts,
 * filters, edge cases (N = 0, N = 1, big N, unknown / empty / null names) and bad input.
 */
public class AppIntegrationTest
{
    /**
     * The app, used to connect to and disconnect from the database.
     */
    static App app;

    /**
     * City reports to test.
     */
    static CityReport cityReport;

    /**
     * Country reports to test.
     */
    static CountryReport countryReport;

    /**
     * Population and capital city reports to test.
     */
    static PopulationReport populationReport;

    /**
     * Language report to test.
     */
    static LanguageReport languageReport;

    // ---------- Setup and cleanup ----------

    /**
     * Connects to the database once before all the tests and sets up the reports.
     */
    @BeforeAll
    static void init()
    {
        app = new App();
        app.connect("localhost:33060", 0);
        assertNotNull(App.getCon(), "Could not connect to the db on localhost:33060 - is the container running?");

        cityReport = new CityReport(App.getCon());
        countryReport = new CountryReport(App.getCon());
        populationReport = new PopulationReport(App.getCon());
        languageReport = new LanguageReport(App.getCon());
    }

    /**
     * Disconnects from the database after all the tests.
     */
    @AfterAll
    static void tearDown()
    {
        app.disconnect();
    }

    // ---------- Helpers ----------

    /**
     * Reads one column from every row into a list, then closes the result.
     * @param rs is the result from a report
     * @param col is the column number, starting at 1
     * @return the values in that column
     * @throws SQLException if the result can't be read
     */
    private static List<String> column(ResultSet rs, int col) throws SQLException
    {
        List<String> values = new ArrayList<>();
        while (rs.next())
        {
            values.add(rs.getString(col));
        }
        rs.getStatement().close();
        return values;
    }

    /**
     * Reads a number column (e.g. population) from every row into a list, then closes the result.
     * @param rs is the result from a report
     * @param col is the column number, starting at 1
     * @return the numbers in that column
     * @throws SQLException if the result can't be read
     */
    private static List<Long> numbers(ResultSet rs, int col) throws SQLException
    {
        List<Long> values = new ArrayList<>();
        while (rs.next())
        {
            values.add(rs.getLong(col));
        }
        rs.getStatement().close();
        return values;
    }

    /**
     * Checks a list of numbers is sorted from largest to smallest.
     * @param values is the list to check
     */
    private static void assertDescending(List<Long> values)
    {
        for (int i = 1; i < values.size(); i++)
        {
            assertTrue(values.get(i - 1) >= values.get(i),
                    "Not sorted largest to smallest at row " + (i + 1));
        }
    }

    // ---------- CityReport - getTopNCitiesWorld(n) ----------

    /**
     * Tests that top 5 cities in the world gives exactly 5 rows.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCitiesWorldReturnsExactlyN() throws SQLException
    {
        ResultSet rs = cityReport.getTopNCitiesWorld(5);
        assertNotNull(rs);
        assertEquals(5, column(rs, 1).size());
    }

    /**
     * Tests the top cities are Mumbai (10,500,000) then Seoul, sorted largest first.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCitiesWorldHasKnownFirstRowsInOrder() throws SQLException
    {
        List<String> names = column(cityReport.getTopNCitiesWorld(3), 1);
        assertEquals("Mumbai (Bombay)", names.get(0));
        assertEquals("Seoul", names.get(1));

        List<Long> pops = numbers(cityReport.getTopNCitiesWorld(10), 2);
        assertEquals(10500000L, pops.get(0));
        assertDescending(pops);
    }

    /**
     * Tests that N = 1 only gives Mumbai.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCitiesWorldWithOneReturnsOnlyTheLargest() throws SQLException
    {
        assertEquals(List.of("Mumbai (Bombay)"), column(cityReport.getTopNCitiesWorld(1), 1));
    }

    /**
     * Tests that N = 0 gives no rows instead of an error.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCitiesWorldWithZeroReturnsNoRows() throws SQLException
    {
        ResultSet rs = cityReport.getTopNCitiesWorld(0);
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests that N = 5000 gives all 4079 cities, with Adamstown (42) last.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCitiesWorldWithNBiggerThanTableReturnsEveryCity() throws SQLException
    {
        List<Long> pops = numbers(cityReport.getTopNCitiesWorld(5000), 2);
        assertEquals(4079, pops.size());
        assertDescending(pops);
        assertEquals(42L, pops.get(pops.size() - 1));
    }

    /**
     * Tests that N = -1 doesn't crash the app (the method returns null).
     * The SQL error printed when the tests run comes from this test.
     */
    @Test
    void topNCitiesWorldWithNegativeNDoesNotCrash()
    {
        assertDoesNotThrow(() -> cityReport.getTopNCitiesWorld(-1));
        assertNull(cityReport.getTopNCitiesWorld(-1));
    }

    // ---------- CityReport - districts, continents, countries ----------

    /**
     * Tests the top 3 cities in Scotland are Glasgow, Edinburgh, Aberdeen.
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesByDistrictReturnsNInOrder() throws SQLException
    {
        List<String> names = column(cityReport.getTopCitiesByDistrict("Scotland", 3), 1);
        assertEquals(List.of("Glasgow", "Edinburgh", "Aberdeen"), names);
    }

    /**
     * Tests that asking for 10 cities in Scotland gives 4, as Scotland only has 4.
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesByDistrictWithNBiggerThanDistrictReturnsAll() throws SQLException
    {
        assertEquals(4, column(cityReport.getTopCitiesByDistrict("Scotland", 10), 1).size());
    }

    /**
     * Tests all cities in Scotland gives 4 rows with Glasgow first.
     * This found a bug: the query used ctry.District but ctry wasn't in the FROM.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesByDistrictWorks() throws SQLException
    {
        ResultSet rs = cityReport.getAllCitiesByDistrict("Scotland");
        assertNotNull(rs, "getAllCitiesByDistrict returned null - SQL error in the query");
        List<String> names = column(rs, 1);
        assertEquals(4, names.size());
        assertEquals("Glasgow", names.get(0));
    }

    /**
     * Tests an empty district name. world.sql has 4 cities with a blank district,
     * so this gives those 4 with Taiping first.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesByEmptyDistrictMatchesBlankDistricts() throws SQLException
    {
        List<String> names = column(cityReport.getAllCitiesByDistrict(""), 1);
        assertEquals(4, names.size());
        assertEquals("Taiping", names.get(0));
    }

    /**
     * Tests Oceania gives exactly 55 cities, sorted largest first.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesByContinentOnlyHasThatContinent() throws SQLException
    {
        List<Long> pops = numbers(cityReport.getAllCitiesByContinent("Oceania"), 2);
        assertEquals(55, pops.size());
        assertDescending(pops);
    }

    /**
     * Tests a country that doesn't exist gives no rows.
     * @throws SQLException if the query fails
     */
    @Test
    void citiesInUnknownCountryReturnsNoRows() throws SQLException
    {
        ResultSet rs = cityReport.getCitiesInCountryByPopulation("Atlantis");
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    // ---------- CountryReport ----------

    /**
     * Tests all 239 countries come back, sorted largest first.
     * @throws SQLException if the query fails
     */
    @Test
    void allCountriesWorldReturnsEveryCountrySorted() throws SQLException
    {
        List<Long> pops = numbers(countryReport.getAllCountriesWorld(), 2);
        assertEquals(239, pops.size());
        assertDescending(pops);
    }

    /**
     * Tests that all the country populations added up equal the world population report.
     * @throws SQLException if the query fails
     */
    @Test
    void allCountriesAddUpToWorldPopulation() throws SQLException
    {
        long total = 0;
        for (long p : numbers(countryReport.getAllCountriesWorld(), 2))
        {
            total += p;
        }
        assertEquals(numbers(populationReport.getWorldPopulation(), 1).get(0), total);
    }

    /**
     * Tests the top 5 countries are China, India, United States, Indonesia, Brazil.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCountriesWorldHasKnownTopFive() throws SQLException
    {
        List<String> names = column(countryReport.getTopNCountriesInWorld(5), 1);
        assertEquals(List.of("China", "India", "United States", "Indonesia", "Brazil"), names);
    }

    /**
     * Tests that N = 0 gives no countries instead of an error.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCountriesWorldWithZeroReturnsNoRows() throws SQLException
    {
        ResultSet rs = countryReport.getTopNCountriesInWorld(0);
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests the top 3 in the Caribbean are Cuba, Dominican Republic, Haiti
     * and every row is really in the Caribbean.
     * @throws SQLException if the query fails
     */
    @Test
    void topCountriesByRegionFiltersAndSorts() throws SQLException
    {
        ResultSet rs = countryReport.getTopCountriesByRegion("Caribbean", 3);
        assertNotNull(rs);
        List<String> names = new ArrayList<>();
        while (rs.next())
        {
            names.add(rs.getString("Name"));
            assertEquals("Caribbean", rs.getString("Region"));
        }
        rs.getStatement().close();
        assertEquals(List.of("Cuba", "Dominican Republic", "Haiti"), names);
    }

    /**
     * Tests a region that doesn't exist gives no rows.
     * @throws SQLException if the query fails
     */
    @Test
    void topCountriesByUnknownRegionReturnsNoRows() throws SQLException
    {
        ResultSet rs = countryReport.getTopCountriesByRegion("Atlantis", 5);
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    // ---------- PopulationReport - world and city populations ----------

    /**
     * Tests the world population is 6,078,749,450.
     * @throws SQLException if the query fails
     */
    @Test
    void worldPopulationIsKnownTotal() throws SQLException
    {
        assertEquals(List.of(6078749450L), numbers(populationReport.getWorldPopulation(), 1));
    }

    /**
     * Tests the population of Edinburgh is 450,180.
     * @throws SQLException if the query fails
     */
    @Test
    void cityPopulationOfEdinburgh() throws SQLException
    {
        assertEquals(List.of(450180L), numbers(populationReport.getCityPopulation("Edinburgh"), 2));
    }

    /**
     * Tests a city that doesn't exist gives no rows.
     * @throws SQLException if the query fails
     */
    @Test
    void cityPopulationOfMissingCityReturnsNoRows() throws SQLException
    {
        ResultSet rs = populationReport.getCityPopulation("Atlantis");
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests a null city name gives no rows instead of crashing.
     * @throws SQLException if the query fails
     */
    @Test
    void cityPopulationWithNullNameReturnsNoRows() throws SQLException
    {
        ResultSet rs = populationReport.getCityPopulation(null);
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests a name with an apostrophe (St John's) doesn't break the SQL.
     * Before the PreparedStatement fix this gave a SQL error.
     * @throws SQLException if the query fails
     */
    @Test
    void cityPopulationWithApostropheDoesNotBreakQuery() throws SQLException
    {
        ResultSet rs = populationReport.getCityPopulation("St John's");
        assertNotNull(rs, "Apostrophe in the name broke the SQL - use a PreparedStatement");
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests SQL injection doesn't work. Before the PreparedStatement fix,
     * x' OR '1'='1 gave back all 4079 cities.
     * @throws SQLException if the query fails
     */
    @Test
    void cityPopulationIsSafeFromSqlInjection() throws SQLException
    {
        ResultSet rs = populationReport.getCityPopulation("x' OR '1'='1");
        assertNotNull(rs);
        assertEquals(0, column(rs, 1).size(), "SQL injection - the input was run as SQL");
    }

    // ---------- PopulationReport - capital cities ----------

    /**
     * Tests the biggest capital city is Seoul with 9,981,619.
     * @throws SQLException if the query fails
     */
    @Test
    void topCapitalsWorldHasSeoulFirst() throws SQLException
    {
        ResultSet rs = populationReport.getTopCapitalsWorld(3);
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Seoul", rs.getString("City Name"));
        assertEquals(9981619L, rs.getLong("Population"));
        rs.getStatement().close();
    }

    /**
     * Tests that N = 1000 gives all 232 capitals, sorted largest first
     * (232 of the 239 countries have a capital).
     * @throws SQLException if the query fails
     */
    @Test
    void topCapitalsWorldWithLargeNReturnsEveryCapital() throws SQLException
    {
        List<Long> pops = numbers(populationReport.getTopCapitalsWorld(1000), 3);
        assertEquals(232, pops.size());
        assertDescending(pops);
    }

    /**
     * Tests the top 3 capitals in Europe are Moscow, London, Berlin.
     * @throws SQLException if the query fails
     */
    @Test
    void topCapitalsByContinentEurope() throws SQLException
    {
        List<String> names = column(populationReport.getTopCapitalsByContinent("Europe", 3), 1);
        assertEquals(List.of("Moscow", "London", "Berlin"), names);
    }

    // ---------- PopulationReport - people living in and out of cities ----------

    /**
     * Tests the in/out of cities numbers for Europe: total 730,074,600, in cities 241,942,813,
     * 33.14% in cities, and in + out adds up to the total.
     * @throws SQLException if the query fails
     */
    @Test
    void inOutPopulationByContinentEurope() throws SQLException
    {
        ResultSet rs = populationReport.getInOutPopulationByContinent("Europe");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(730074600L, rs.getLong("TotalPopulationOfTheContinent"));
        assertEquals(241942813L, rs.getLong("PopulationInCities"));
        assertEquals(33.14, rs.getDouble("PercentInCities"), 0.001);
        assertEquals(rs.getLong("TotalPopulationOfTheContinent"),
                rs.getLong("PopulationInCities") + rs.getLong("PopulationOutOfCities"));
        assertFalse(rs.next(), "Should be exactly one row");
        rs.getStatement().close();
    }

    /**
     * Tests a continent that doesn't exist gives no rows.
     * @throws SQLException if the query fails
     */
    @Test
    void inOutPopulationForUnknownContinentReturnsNoRows() throws SQLException
    {
        ResultSet rs = populationReport.getInOutPopulationByContinent("Atlantis");
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    // ---------- LanguageReport ----------

    /**
     * Tests Chinese is first in the language report and it's sorted largest first.
     * @throws SQLException if the query fails
     */
    @Test
    void languageSpeakersHasChineseFirstAndSorted() throws SQLException
    {
        ResultSet rs = languageReport.getLanguageSpeakers();
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals("Chinese", rs.getString(1));
        rs.getStatement().close();

        assertDescending(numbers(languageReport.getLanguageSpeakers(), 2));
    }
}

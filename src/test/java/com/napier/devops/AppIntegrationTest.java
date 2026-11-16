package com.napier.devops;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    /**
     * Counts the rows in a table straight from the database.
     * @param table is the table name
     * @return the number of rows
     * @throws SQLException if the query fails
     */
    private static long countRows(String table) throws SQLException
    {
        try (Statement stmt = App.getCon().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table))
        {
            rs.next();
            return rs.getLong(1);
        }
    }

    // ---------- Database connection (runs first) ----------

    /**
     * Tests the app is connected and the connection is open. Runs first: if this fails,
     * every other test would fail too, so the real problem shows up straight away.
     * @throws SQLException if the connection can't be checked
     */
    @Test
    @Order(1)
    void connectionIsOpen() throws SQLException
    {
        Connection con = App.getCon();
        assertNotNull(con, "No connection - is the db container running on localhost:33060?");
        assertFalse(con.isClosed());
    }

    /**
     * Tests the database actually answers, not just that a connection object exists.
     * @throws SQLException if the connection can't be checked
     */
    @Test
    @Order(2)
    void connectionIsValid() throws SQLException
    {
        assertTrue(App.getCon().isValid(5), "Connection exists but the database isn't responding");
    }

    /**
     * Tests we're connected to the world database and not another one on the same server.
     * @throws SQLException if the connection can't be checked
     */
    @Test
    @Order(3)
    void connectedToWorldDatabase() throws SQLException
    {
        assertEquals("world", App.getCon().getCatalog());
    }

    /**
     * Tests world.sql has fully loaded: the three tables exist and have the right number of rows
     * (4079 cities, 239 countries, 984 country languages). Catches an empty or half-loaded db.
     * @throws SQLException if the query fails
     */
    @Test
    @Order(4)
    void worldTablesAreLoaded() throws SQLException
    {
        assertEquals(4079L, countRows("city"));
        assertEquals(239L, countRows("country"));
        assertEquals(984L, countRows("countrylanguage"));
    }

    /**
     * Tests connecting to a wrong address doesn't crash the app, and doesn't break the
     * connection that's already working.
     * @throws SQLException if the connection can't be checked
     */
    @Test
    @Order(5)
    void connectToWrongLocationDoesNotCrash() throws SQLException
    {
        assertDoesNotThrow(() -> new App().connect("localhost:1", 0));
        assertTrue(App.getCon().isValid(5));
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
        assertEquals(10500000L, pops.get(0).longValue());
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
        assertEquals(42L, pops.get(pops.size() - 1).longValue());
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

    // ---------- CityReport - all cities, regions, countries, continents (CR4) ----------

    /**
     * Tests all cities in the world gives all 4079, Mumbai first, sorted largest first.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesWorldReturnsEveryCitySorted() throws SQLException
    {
        ResultSet rs = cityReport.getAllCitiesWorld();
        assertNotNull(rs);
        List<String> names = column(rs, 1);
        assertEquals(4079, names.size());
        assertEquals("Mumbai (Bombay)", names.get(0));
        assertDescending(numbers(cityReport.getAllCitiesWorld(), 2));
    }

    /**
     * Tests the British Islands region gives 83 cities with London (7,285,000) first, sorted.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesByRegionBritishIslands() throws SQLException
    {
        List<Long> pops = numbers(cityReport.getAllCitiesByRegion("British Islands"), 2);
        assertEquals(83, pops.size());
        assertEquals(7285000L, pops.get(0).longValue());
        assertDescending(pops);
        assertEquals("London", column(cityReport.getAllCitiesByRegion("British Islands"), 1).get(0));
    }

    /**
     * Tests a region that doesn't exist gives no cities.
     * @throws SQLException if the query fails
     */
    @Test
    void allCitiesByUnknownRegionReturnsNoRows() throws SQLException
    {
        ResultSet rs = cityReport.getAllCitiesByRegion("Atlantis");
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests the top 3 cities in the United Kingdom are London, Birmingham, Glasgow
     * and every row really is in the United Kingdom.
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesInCountryUnitedKingdom() throws SQLException
    {
        ResultSet rs = cityReport.getTopCitiesInCountry("United Kingdom", 3);
        assertNotNull(rs);
        List<String> names = new ArrayList<>();
        while (rs.next())
        {
            names.add(rs.getString("cName"));
            assertEquals("United Kingdom", rs.getString("countryName"));
        }
        rs.getStatement().close();
        assertEquals(List.of("London", "Birmingham", "Glasgow"), names);
    }

    /**
     * Tests that N = 0 gives no cities for a country instead of an error.
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesInCountryWithZeroReturnsNoRows() throws SQLException
    {
        ResultSet rs = cityReport.getTopCitiesInCountry("United Kingdom", 0);
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
    }

    /**
     * Tests the top 3 cities in Europe are Moscow, London, St Petersburg.
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesByContinentEurope() throws SQLException
    {
        List<String> names = column(cityReport.getTopCitiesByContinent("Europe", 3), 1);
        assertEquals(List.of("Moscow", "London", "St Petersburg"), names);
    }

    /**
     * Tests asking for the top 100 cities in Oceania gives all 55, the same number as
     * the all cities in a continent report (cross-check between two reports).
     * @throws SQLException if the query fails
     */
    @Test
    void topCitiesByContinentMatchesAllCitiesByContinent() throws SQLException
    {
        List<Long> top = numbers(cityReport.getTopCitiesByContinent("Oceania", 100), 4);
        List<Long> all = numbers(cityReport.getAllCitiesByContinent("Oceania"), 2);
        assertEquals(55, top.size());
        assertEquals(all, top);
    }

    /**
     * Tests all capitals in Europe gives 46, sorted, and the top 3 match the
     * top capitals in a continent report (Moscow, London, Berlin).
     * @throws SQLException if the query fails
     */
    @Test
    void capitalsInContinentEurope() throws SQLException
    {
        List<Long> pops = numbers(cityReport.getCapitalsInContinentByPopulation("Europe"), 3);
        assertEquals(46, pops.size());
        assertDescending(pops);
        List<String> names = column(cityReport.getCapitalsInContinentByPopulation("Europe"), 1);
        assertEquals(column(populationReport.getTopCapitalsByContinent("Europe", 3), 1), names.subList(0, 3));
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

    // ---------- CountryReport - continents (CR4) ----------

    /**
     * Tests all countries in Europe gives 46 with the Russian Federation first, sorted,
     * and they add up to Europe's population (730,074,600).
     * @throws SQLException if the query fails
     */
    @Test
    void allCountriesByContinentEurope() throws SQLException
    {
        List<String> names = column(countryReport.getAllCountriesByContinent("Europe"), 1);
        assertEquals(46, names.size());
        assertEquals("Russian Federation", names.get(0));

        List<Long> pops = numbers(countryReport.getAllCountriesByContinent("Europe"), 2);
        assertDescending(pops);
        long total = 0;
        for (long p : pops)
        {
            total += p;
        }
        assertEquals(730074600L, total);
    }

    /**
     * Tests the top 3 countries in Asia are China, India, Indonesia.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCountriesByContinentAsia() throws SQLException
    {
        List<String> names = column(countryReport.getTopNCountriesByContinent("Asia", 3), 1);
        assertEquals(List.of("China", "India", "Indonesia"), names);
    }

    /**
     * Tests that N = 0 gives no countries for a continent instead of an error.
     * @throws SQLException if the query fails
     */
    @Test
    void topNCountriesByContinentWithZeroReturnsNoRows() throws SQLException
    {
        ResultSet rs = countryReport.getTopNCountriesByContinent("Asia", 0);
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

    // ---------- PopulationReport - continent, region, country totals (CR4) ----------

    /**
     * Tests the population of Europe is one row with the total, 730,074,600.
     * This found a bug: the query listed every country in Europe instead of adding them up.
     * @throws SQLException if the query fails
     */
    @Test
    void continentPopulationEurope() throws SQLException
    {
        ResultSet rs = populationReport.getContinentPopulation("Europe");
        assertNotNull(rs, "getContinentPopulation returned null - SQL error in the query");
        assertEquals(List.of(730074600L), numbers(rs, 2));
    }

    /**
     * Tests the population of the British Islands region is one row with the total, 63,398,500.
     * This found a bug: SUM without GROUP BY is rejected by MySQL 8, so it returned null.
     * @throws SQLException if the query fails
     */
    @Test
    void regionPopulationBritishIslands() throws SQLException
    {
        ResultSet rs = populationReport.getRegionPopulation("British Islands");
        assertNotNull(rs, "getRegionPopulation returned null - SQL error in the query");
        assertEquals(List.of(63398500L), numbers(rs, 2));
    }

    /**
     * Tests the population of the United Kingdom is 59,623,400.
     * @throws SQLException if the query fails
     */
    @Test
    void countryPopulationUnitedKingdom() throws SQLException
    {
        assertEquals(List.of(59623400L), numbers(populationReport.getCountryPopulation("United Kingdom"), 2));
    }

    /**
     * Tests a country that doesn't exist gives no rows.
     * @throws SQLException if the query fails
     */
    @Test
    void countryPopulationOfUnknownCountryReturnsNoRows() throws SQLException
    {
        ResultSet rs = populationReport.getCountryPopulation("Atlantis");
        assertNotNull(rs);
        assertTrue(column(rs, 1).isEmpty());
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

    /**
     * Tests all capitals in the world gives 232 with Seoul first, sorted largest first,
     * the same number as the top capitals report with a large N (cross-check).
     * @throws SQLException if the query fails
     */
    @Test
    void allCapitalsByPopulationReturnsEveryCapital() throws SQLException
    {
        ResultSet rs = populationReport.getAllCapitalsByPopulation();
        assertNotNull(rs);
        List<String> names = column(rs, 2);
        assertEquals(232, names.size());
        assertEquals("Seoul", names.get(0));

        List<Long> pops = numbers(populationReport.getAllCapitalsByPopulation(), 3);
        assertDescending(pops);
        assertEquals(numbers(populationReport.getTopCapitalsWorld(1000), 3), pops);
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

    /**
     * Tests the in/out of cities numbers for the United Kingdom: total 59,623,400 (the same as
     * the country population report), in cities 22,436,673, and in + out adds up to the total.
     * @throws SQLException if the query fails
     */
    @Test
    void inOutPopulationByCountryUnitedKingdom() throws SQLException
    {
        ResultSet rs = populationReport.getInOutPopulationByCountry("United Kingdom");
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(59623400L, rs.getLong("TotalPopulationOfTheCountry"));
        assertEquals(22436673L, rs.getLong("PopulationInCities"));
        assertEquals(rs.getLong("TotalPopulationOfTheCountry"),
                rs.getLong("PopulationInCities") + rs.getLong("PopulationOutOfCities"));
        assertFalse(rs.next(), "Should be exactly one row");
        rs.getStatement().close();
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

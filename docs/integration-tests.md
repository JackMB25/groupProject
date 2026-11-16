# Integration tests

These go with issue #82 (my sub-issue #84 for CR3, then more tests for CR4). The tests are in `src/test/java/com/napier/devops/AppIntegrationTest.java`.

The unit tests check each report method on its own with a mocked database. The integration tests check the app and the real MySQL database actually work together. Each test calls a report method, which runs its SQL against the world database in the `db` container, and then checks what comes back against values I looked up in `db/world.sql`.

There are 52 tests. 5 check the database connection first, and the other 47 cover 26 of the 32 report methods (81%) across all four classes (city, country, population and language). For CR3 it was 30 tests on 14 methods; for CR4 I added the connection tests and 12 more report methods, still testing each one properly rather than just checking it runs. Depending on the report, the tests check the actual values (e.g. the biggest city is Mumbai (Bombay) with 10,500,000), that the list is sorted biggest to smallest, that top N gives exactly N rows, and that filters only return the right rows (every row in the Caribbean report is actually in the Caribbean). There are also edge cases like N = 0, N = 1, an N bigger than the whole table, a negative N, a name that doesn't exist, a null name, a name with an apostrophe and an SQL injection attempt. Some tests check two reports agree, e.g. every country's population added up matches the world population report, and all capitals in the world matches the top capitals report with a big N.

## Connection tests (run first)

Andreas's feedback at CR3 was to check the database is actually connected before testing anything else. So the class uses `@TestMethodOrder` and the first five tests (`@Order(1)` to `@Order(5)`) check:

1. there is a connection and it's open
2. the database actually answers (`isValid`)
3. it's the `world` database
4. `world.sql` has fully loaded: 4079 cities, 239 countries, 984 country languages
5. connecting to a wrong address doesn't crash the app or break the working connection

If the database is down, these fail first with a clear message, instead of 47 report tests failing with confusing errors.

## Running them

You need Docker Desktop running. Start the database from the project folder:

```
docker compose up -d --build db
```

It's mapped to `localhost:33060`. The first time, give it about a minute to load `world.sql`. Then run `AppIntegrationTest` in IntelliJ (green arrow next to the class name), or from the terminal:

```
mvn test -Dtest=com.napier.devops.AppIntegrationTest
```

All 52 should pass. Run `docker compose down` when you're finished.

If it says it couldn't connect to `localhost:33060`, the container isn't up yet or the data is still loading, so wait and try again. You'll also see two red `SQLSyntaxErrorException ... near '-1'` lines in the output. That's expected, it's the negative N test checking bad input doesn't crash anything.

To let the tests connect, `App.connect()` now takes the database location and a delay. The tests use `localhost:33060` with no delay. Docker passes `db:3306` and 30 seconds through the `Dockerfile`, so `docker compose up` works the same as before. `App.getCon()` gives the tests the connection so they can create the report objects.

## On GitHub Actions

The integration tests have their own job, `IntegrationTests`. It builds and starts the database container, waits until the `city` table can be queried, runs the tests, then removes the container even if a test failed. The unit test job skips `AppIntegrationTest` because there's no database in that job, and the build job only runs once both test jobs have passed.

## Bugs the tests found

For CR3, three tests failed the first time, and I fixed the code:

- `getAllCitiesByDistrict` always returned null. The query used `ctry.District`, but `ctry` was never in the FROM. Changed it to `c.District`.
- `getCityPopulation` was open to SQL injection. Searching for `x' OR '1'='1` returned all 4079 cities. It now uses a PreparedStatement through a new `executePrepared()` method in `PopulationReport`.
- A city name with an apostrophe, like "St John's", broke the same query. The PreparedStatement fixed that too.

I also updated two of the unit tests that were checking the old SQL for those two methods, so they match the fixes.

For CR4, two more:

- `getContinentPopulation` listed every country in the continent (46 rows for Europe) instead of giving the continent's total. It now adds them up with `SUM` and `GROUP BY Continent`, so Europe gives one row: 730,074,600.
- `getRegionPopulation` used `SUM` without a `GROUP BY`. MySQL 8 rejects that, so it returned null. Added `GROUP BY Region`; the British Islands now give 63,398,500.

The two unit tests that check the SQL text for these methods were updated to match.

## Things to know

The other reports still put the user's input straight into the SQL string, so they have the same apostrophe and injection problem `getCityPopulation` had. Moving them to `executePrepared()` would fix it. Six report methods still don't have integration tests (all the "by region" ones apart from cities and countries, plus district population and in/out by region). The tests also rely on the exact data in `world.sql`, so if that file ever changes, some of the expected numbers will need updating.

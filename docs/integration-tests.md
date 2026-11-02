# Integration tests

These go with issue #82 (and my sub-issue #84). The tests are in `src/test/java/com/napier/devops/AppIntegrationTest.java`.

The unit tests check each report method on its own with a mocked database. The integration tests check the app and the real MySQL database actually work together. Each test calls a report method, which runs its SQL against the world database in the `db` container, and then checks what comes back against values I looked up in `db/world.sql`.

I didn't write one test for every report. I picked reports from all four classes (city, country, population and language) and tested those properly instead, 30 tests in total. Depending on the report, the tests check the actual values (e.g. the biggest city is Mumbai (Bombay) with 10,500,000), that the list is sorted biggest to smallest, that top N gives exactly N rows, and that filters only return the right rows (every row in the Caribbean report is actually in the Caribbean). There are also edge cases like N = 0, N = 1, an N bigger than the whole table, a negative N, a name that doesn't exist, a null name, a name with an apostrophe and an SQL injection attempt. One test adds up every country's population and checks it matches the world population report.

## Running them

You need Docker Desktop running. Start the database from the project folder:

```
docker compose up -d --build db
```

It's mapped to `localhost:33060`. The first time, give it about a minute to load `world.sql`. Then run `AppIntegrationTest` in IntelliJ (green arrow next to the class name), or from the terminal:

```
mvn test -Dtest=com.napier.devops.AppIntegrationTest
```

All 30 should pass. Run `docker compose down` when you're finished.

If it says it couldn't connect to `localhost:33060`, the container isn't up yet or the data is still loading, so wait and try again. You'll also see two red `SQLSyntaxErrorException ... near '-1'` lines in the output. That's expected, it's the negative N test checking bad input doesn't crash anything.

To let the tests connect, `App.connect()` now takes the database location and a delay. The tests use `localhost:33060` with no delay. Docker passes `db:3306` and 30 seconds through the `Dockerfile`, so `docker compose up` works the same as before. `App.getCon()` gives the tests the connection so they can create the report objects.

## On GitHub Actions

The integration tests have their own job, `IntegrationTests`. It builds and starts the database container, waits until the `city` table can be queried, runs the tests, then removes the container even if a test failed. The unit test job skips `AppIntegrationTest` because there's no database in that job, and the build job only runs once both test jobs have passed.

## Bugs the tests found

Three tests failed the first time, and I fixed the code:

- `getAllCitiesByDistrict` always returned null. The query used `ctry.District`, but `ctry` was never in the FROM. Changed it to `c.District`.
- `getCityPopulation` was open to SQL injection. Searching for `x' OR '1'='1` returned all 4079 cities. It now uses a PreparedStatement through a new `executePrepared()` method in `PopulationReport`.
- A city name with an apostrophe, like "St John's", broke the same query. The PreparedStatement fixed that too.

I also updated two of the unit tests that were checking the old SQL for those two methods, so they match the fixes.

## Things to know

The other reports still put the user's input straight into the SQL string, so they have the same apostrophe and injection problem `getCityPopulation` had. Moving them to `executePrepared()` would fix it. The tests also rely on the exact data in `world.sql`, so if that file ever changes, some of the expected numbers will need updating.

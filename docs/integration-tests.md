# Integration tests

Issue: #84 (sub-issue of #82 – Implement integration tests for MySQL-backed reports)
Test class: [`src/test/java/com/napier/devops/AppIntegrationTest.java`](../src/test/java/com/napier/devops/AppIntegrationTest.java)

## What they are

Unit tests check a single method on its own. **Integration tests check that the app and the MySQL database work together.** Each test:

1. calls a real report method (e.g. `CityReport.getTopNCitiesWorld(5)`)
2. which runs real SQL against the world database in the `db` container
3. then checks the rows that come back against known values from `db/world.sql`

They are kept separate from the unit tests: their own class (`AppIntegrationTest`) and their own GitHub Actions job (`IntegrationTests`).

## Approach: depth over breadth

There are 32 reports. Instead of one shallow test for each, a smaller set of reports from every report class is tested properly (30 tests in total). Each one is checked for:

| Check | Example |
| --- | --- |
| Correct values | Largest city is Mumbai (Bombay), 10,500,000 |
| Sort order | Every list is largest → smallest |
| Exact row counts | Top 5 gives exactly 5 rows |
| Filtering | Every row from the Caribbean report really has Region = Caribbean |
| Edge cases | N = 0, N = 1, N bigger than the table, unknown / empty / null names |
| Bad input | Negative N, a name with an apostrophe, SQL injection |
| Cross-checks | All country populations added up = the world population report |

## How to run

**You need:** Docker Desktop running, JDK 17, and Maven (built into IntelliJ).

1. Start the database (from the project folder):
   ```
   docker compose up -d --build db
   ```
   This maps MySQL to `localhost:33060`. On the first start, wait about a minute while `world.sql` loads.
   (In IntelliJ you can also open `docker-compose.yml` and click the green arrow next to `db:`.)
2. Run the tests, either:
   - **IntelliJ:** open `AppIntegrationTest` → green arrow next to the class name → Run
   - **Terminal:** `mvn -Dtest=com.napier.devops.AppIntegrationTest test`
3. You should see **30 tests passed**.
4. Stop the database when you're done: `docker compose down`

### On GitHub Actions

The `IntegrationTests` job in `.github/workflows/main.yml`:

1. builds the db image from `db/` and runs it on port 33060
2. waits until `SELECT COUNT(*) FROM world.city` works (so `world.sql` has finished loading), then 15 more seconds
3. runs `mvn -Dtest=com.napier.devops.AppIntegrationTest test`
4. removes the container, even if the tests failed

The `build` job uses `mvn -DskipTests package`, because it has no database. Without `-DskipTests`, Maven would run these tests there and fail.

### How the test connects

`App.connect(String location, int delay)` takes the database address and how long to wait before connecting:

| Where the app runs | Location | Delay |
| --- | --- | --- |
| Tests / IntelliJ | `localhost:33060` | 0 ms |
| Docker (`docker compose up`) | `db:3306` (set in the `Dockerfile`) | 30000 ms |

`App.getCon()` gives the tests the connection so they can build the report objects.

## What's tested

| Report method | Tests |
| --- | --- |
| `CityReport.getTopNCitiesWorld` | N = 5 gives 5 rows · Mumbai then Seoul, 10,500,000, sorted · N = 1 gives only Mumbai · N = 0 gives no rows · N = 5000 gives all 4079 cities, last is Adamstown (42) · N = -1 doesn't crash |
| `CityReport.getTopCitiesByDistrict` | Scotland top 3 = Glasgow, Edinburgh, Aberdeen · top 10 gives 4 (Scotland only has 4) |
| `CityReport.getAllCitiesByDistrict` | Scotland gives 4 rows, Glasgow first · "" gives the 4 cities with a blank district |
| `CityReport.getAllCitiesByContinent` | Oceania = 55 cities, sorted |
| `CityReport.getCitiesInCountryByPopulation` | "Atlantis" gives no rows |
| `CountryReport.getAllCountriesWorld` | 239 countries, sorted · their populations add up to the world total |
| `CountryReport.getTopNCountriesInWorld` | Top 5 = China, India, United States, Indonesia, Brazil · N = 0 gives no rows |
| `CountryReport.getTopCountriesByRegion` | Caribbean top 3 = Cuba, Dominican Republic, Haiti, every row's Region = Caribbean · "Atlantis" gives no rows |
| `PopulationReport.getWorldPopulation` | 6,078,749,450 |
| `PopulationReport.getCityPopulation` | Edinburgh = 450,180 · "Atlantis" and null give no rows · "St John's" doesn't break the query · SQL injection gives 0 rows |
| `PopulationReport.getTopCapitalsWorld` | Seoul first, 9,981,619 · N = 1000 gives all 232 capitals, sorted |
| `PopulationReport.getTopCapitalsByContinent` | Europe top 3 = Moscow, London, Berlin |
| `PopulationReport.getInOutPopulationByContinent` | Europe: total 730,074,600, in cities 241,942,813, 33.14%, in + out = total, one row · "Atlantis" gives no rows |
| `LanguageReport.getLanguageSpeakers` | Chinese first, sorted |

## Bugs the tests found

| Problem | How the test showed it | Fix |
| --- | --- | --- |
| `getAllCitiesByDistrict` always returned null | The query used `ctry.District` but `ctry` wasn't in the FROM, so the SQL failed | `ctry.District` → `c.District` in `CityReport` |
| SQL injection in `getCityPopulation` | Searching for `x' OR '1'='1` returned all 4079 cities | PreparedStatement through a new `executePrepared(sql, params...)` helper in `PopulationReport` |
| Apostrophes broke `getCityPopulation` | "St John's" ended the SQL string early, causing a syntax error (null) | Same PreparedStatement fix |

Each of these tests failed first and passed after the fix.

**Data quirk:** `world.sql` has 4 cities with a blank District (Taiping, Taiwan is the largest), so `""` is a real district value.

## Troubleshooting

| What you see | Why / what to do |
| --- | --- |
| `Could not connect to the db on localhost:33060 - is the container running?` | The db container isn't up, or `world.sql` is still loading. Run `docker compose up -d db`, wait a minute, then run the tests again |
| Two red `SQLSyntaxErrorException ... near '-1'` lines in the output | **Expected.** They come from the N = -1 test, which checks that bad input is handled without crashing |
| JUnit imports are red in IntelliJ | Maven panel (the "m" on the right) → Reload |
| Port 33060 already in use | Another MySQL container is running. Stop it in Docker Desktop, or run `docker compose down` in the other project |

## Known limitations (for the team)

- The other report methods still glue user input into the SQL (`'" + region + "'`), so they have the same apostrophe / injection problem `getCityPopulation` had. Switching them to `executePrepared` would fix it.
- `executeStatement` is copied into all four report classes and never closes its Statement.
- The tests depend on the exact data in `world.sql`. If that file changes, the expected values need updating.

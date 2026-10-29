# groupProject

Group members:
- Jack
- Yurii
- Kamil
- Reuben



Things to do (all):

- setup connection for github
- each person sets up database on their machine

To do (Individual):
- write database connection code - Reuben/Yurii
- 




## Running the integration tests

The integration tests (`src/test/java/com/napier/devops/AppIntegrationTest.java`) run the real report
methods against the world database and check the results against known values from `db/world.sql`.
They are kept separate from the unit tests (own class, own GitHub Actions job).

1. Start the database container (Docker Desktop must be running):
   `docker compose up -d --build db`
   It is mapped to `localhost:33060`. The first start takes a minute or two while `world.sql` loads.
2. Run the tests:
   - IntelliJ: right-click `AppIntegrationTest` > Run, or
   - Terminal: `mvn -Dtest=com.napier.devops.AppIntegrationTest test`
3. Stop the database when finished: `docker compose down`

You should see 30 tests passed. On GitHub Actions the `IntegrationTests` job builds the db image,
waits for `world.sql` to load, runs the same Maven command, then removes the container.

Full details (what each test checks, bugs the tests found, troubleshooting):
[docs/integration-tests.md](docs/integration-tests.md)

CODE OF CONDUCT:
- All members must contribute to assigned work or be docked points as detailed by Dr Kevin Sim.
- All members must attend weekly team meetings or owe team members a hot chocolate unless --> If missing a meeting, advise the team in advance with notes on your contribution.
- Treat other team members with respect and build each other's confidence. (forms of bullying acceptable)
- Always leave tags and comments when committing changes.
- When committing code notify team members to avoid confusion when pulling from github. 
- Protect group intellectual property, avoid sharing sensitive credentials or API keys in public channels, and use approved software tools only.

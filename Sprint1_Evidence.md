# Sprint 1 Evidence

**Project:** World Database Population Reporting System  
**Sprint:** Sprint 1  
**Sprint dates:** 21 September – 12 October 2026  
**Team:** Kamil, Jack, Yuri, Reuben and Preston

## 1. Sprint Goal

The goal of Sprint 1 was to establish the project workflow, organise requirements and tasks, configure the development environment, and begin implementing population reports for Code Review 2 (CR2).

## 2. Sprint Planning and Task Allocation

The team held a sprint meeting to discuss the requirements for CR2 and allocate work. GitHub Issues were used to organise tasks, while the Kanban board tracked progress. User stories and use cases helped define the required functionality.

| Team member | Work completed |
|---|---|
| **Kamil** | Worked on assigned project issues and fixed db connection in Docker compose allowing working directly on linked World.sql database |
| **Jack** | Worked on assigned project issues and contributed to the application implementation, PR reviews. |
| **Yuri** | Worked on assigned project issues as part of the sprint implementation. |
| **Reuben** | Worked on diagrams and use cases to document the system requirements, and assigned project issues |
| **Preston** | Worked on assigned project issues. |

## 3. Work Completed

- **Project setup:** Configured Maven, Docker Compose and the database connection.
- **City reporting:** Implemented a report for the top N most populated cities worldwide.
- **Population reports:** Added reports for individual city populations and population distribution within and outside cities by continent.
- **Capital cities:** Implemented a report displaying the top N most populated capital cities.
- **Requirements:** Used user stories, use cases and GitHub Issues to organise the project work.
- **Diagrams:** Worked on use-case documentation and diagrams.
- **Continuous integration:** Used GitHub Actions to check changes submitted through pull requests.

## 4. Testing and Code Review

The application was built using Maven and run with Docker Compose against the MySQL World database.

The following checks were completed:

- The application connected successfully to the database.
- The top N populated cities report produced results ordered by population.
- The city population report returned Edinburgh's population as 450,180.
- The European population report displayed a total population of 730,074,600, with 241,942,813 people living in cities and 488,131,787 living outside cities.
- The corresponding percentages were 33.14% and 66.86%, adding up to 100%.
- The top five capital cities were displayed in descending population order.
- The application completed its Docker run and exited with code 0.
- These checks provide evidence of successful application execution and manual verification. A complete automated unit-test run has not been confirmed.

## 5. Challenges and Improvements

### Challenges
- The Docker image initially used an older JAR, so the application needed to be rebuilt to test the latest changes.
- Care was needed when switching branches and integrating changes.
- Automated test coverage still needs to be verified.

### Improvements for the next sprint
- Rebuild the application and Docker image before testing changes.
- Follow the agreed feature-branch and pull-request workflow consistently.
- Add or verify automated tests for the reporting functionality.
- Keep issue assignments, board statuses and contribution records up to date.

## 6. Sprint Retrospective

Sprint 1 established the project's technical foundation and task-management workflow. The application successfully connected to the database and ran through Docker Compose. Several population reports were implemented, and changes were reviewed and integrated.

For the next sprint, the team should focus on improving automated test coverage, maintaining consistent branch practices, and ensuring that completed work is clearly linked to its requirements and evidence.

## 7. Checklist
- [x] **GitHub Issues:** Issues have been created and used to organise project tasks.

- [x] **User stories:** User stories define the project requirements.

- [x] **GitHub Projects integration:** Issues are integrated with the project board.

- [x] **Kanban board:** The board is used to track task progress.

- [x] **Sprints:** Sprint planning, task allocation and retrospective are documented.

- [x] **Full use cases:** Use-case documentation is present in the project folder.

- [x] **Use-case diagram:** The diagram is present in the project folder.

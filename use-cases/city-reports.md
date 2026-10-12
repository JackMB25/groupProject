# USE CASE: 2 Produce City Reports

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to produce reports of cities organised by largest population to smallest* so that *I can compare cities in the world, a continent, a region, a country or a district.*

### Reports Covered

1. All the cities in the world organised by largest population to smallest.
2. All the cities in a continent organised by largest population to smallest.
3. All the cities in a region organised by largest population to smallest.
4. All the cities in a country organised by largest population to smallest.
5. All the cities in a district organised by largest population to smallest.
6. The top N populated cities in the world where N is provided by the user.
7. The top N populated cities in a continent where N is provided by the user.
8. The top N populated cities in a region where N is provided by the user.
9. The top N populated cities in a country where N is provided by the user.
10. The top N populated cities in a district where N is provided by the user.

Report columns: Name, Country, District, Population.

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current city data. For continent, region, country or district reports, we know the name of the area.

### Success End Condition

A city report is available for the analyst to provide to the organisation.

### Failed End Condition

No report is produced.

### Primary Actor

Analyst.

### Trigger

A request for city population information is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests a city report.
2. Analyst chooses the area for the report: world, continent, region, country or district.
3. Analyst enters the name of the area if needed.
4. Analyst extracts the cities in that area ordered by largest population to smallest.
5. Analyst provides the report to the organisation.

## EXTENSIONS

3. **Area does not exist**:
    1. Analyst informs the organisation that no such continent, region, country or district exists.

## SUB-VARIATIONS

4. **Top N cities** (extends this use case):
    1. Analyst enters a value for N.
    2. Only the first N cities are included in the report.
    3. If N is larger than the number of cities, all cities are shown.

## SCHEDULE

**DUE DATE**: Release 1.0

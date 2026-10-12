# USE CASE: 1 Produce Country Reports

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to produce reports of countries organised by largest population to smallest* so that *I can compare countries in the world, a continent or a region.*

### Reports Covered

1. All the countries in the world organised by largest population to smallest.
2. All the countries in a continent organised by largest population to smallest.
3. All the countries in a region organised by largest population to smallest.
4. The top N populated countries in the world where N is provided by the user.
5. The top N populated countries in a continent where N is provided by the user.
6. The top N populated countries in a region where N is provided by the user.

Report columns: Code, Name, Continent, Region, Population, Capital.

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current country data. For continent or region reports, we know the continent or region.

### Success End Condition

A country report is available for the analyst to provide to the organisation.

### Failed End Condition

No report is produced.

### Primary Actor

Analyst.

### Trigger

A request for country population information is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests a country report.
2. Analyst chooses the area for the report: world, continent or region.
3. Analyst enters the name of the continent or region if needed.
4. Analyst extracts the countries in that area ordered by largest population to smallest.
5. Analyst provides the report to the organisation.

## EXTENSIONS

3. **Continent or region does not exist**:
    1. Analyst informs the organisation that no such continent or region exists.

## SUB-VARIATIONS

4. **Top N countries** (extends this use case):
    1. Analyst enters a value for N.
    2. Only the first N countries are included in the report.
    3. If N is larger than the number of countries, all countries are shown.

## SCHEDULE

**DUE DATE**: Release 1.0

# USE CASE: 3 Produce Capital City Reports

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to produce reports of capital cities organised by largest population to smallest* so that *I can compare capital cities in the world, a continent or a region.*

### Reports Covered

1. All the capital cities in the world organised by largest population to smallest.
2. All the capital cities in a continent organised by largest population to smallest.
3. All the capital cities in a region organised by largest population to smallest.
4. The top N populated capital cities in the world where N is provided by the user.
5. The top N populated capital cities in a continent where N is provided by the user.
6. The top N populated capital cities in a region where N is provided by the user.

Report columns: Name, Country, Population.

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current country and city data, including which city is each country's capital. For continent or region reports, we know the continent or region.

### Success End Condition

A capital city report is available for the analyst to provide to the organisation.

### Failed End Condition

No report is produced.

### Primary Actor

Analyst.

### Trigger

A request for capital city population information is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests a capital city report.
2. Analyst chooses the area for the report: world, continent or region.
3. Analyst enters the name of the continent or region if needed.
4. Analyst extracts the capital cities in that area ordered by largest population to smallest.
5. Analyst provides the report to the organisation.

## EXTENSIONS

3. **Continent or region does not exist**:
    1. Analyst informs the organisation that no such continent or region exists.

## SUB-VARIATIONS

4. **Top N capital cities** (extends this use case):
    1. Analyst enters a value for N.
    2. Only the first N capital cities are included in the report.
    3. If N is larger than the number of capital cities, all capital cities are shown.

## SCHEDULE

**DUE DATE**: Release 1.0

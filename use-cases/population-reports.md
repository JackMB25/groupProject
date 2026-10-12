# USE CASE: 4 Produce Population Reports

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to produce reports of the population living in cities and not living in cities* so that *I can understand how urbanised each continent, region and country is.*

### Reports Covered

1. The population of people, people living in cities, and people not living in cities in each continent.
2. The population of people, people living in cities, and people not living in cities in each region.
3. The population of people, people living in cities, and people not living in cities in each country.

Report columns: Name of the continent/region/country, Total population, Population living in cities (with %), Population not living in cities (with %).

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current country and city population data.

### Success End Condition

A population report is available for the analyst to provide to the organisation.

### Failed End Condition

No report is produced.

### Primary Actor

Analyst.

### Trigger

A request for urban and rural population information is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests a population report.
2. Analyst chooses the level for the report: continent, region or country.
3. Analyst extracts the total population for each area at that level.
4. Analyst extracts the population living in cities for each area.
5. Analyst calculates the population not living in cities and both percentages.
6. Analyst provides the report to the organisation.

## EXTENSIONS

3. **No population data for an area**:
    1. The area is shown with a population of 0 and no percentages.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0

# USE CASE: 5 Look Up a Population

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to look up the total population of a given area* so that *I can quickly answer questions about population.*

### Reports Covered

1. The population of the world.
2. The population of a continent.
3. The population of a region.
4. The population of a country.
5. The population of a district.
6. The population of a city.

Report columns: Name of the area, Population.

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current population data. Unless looking up the world, we know the name of the area.

### Success End Condition

The population of the requested area is available for the analyst to provide to the organisation.

### Failed End Condition

No population figure is produced.

### Primary Actor

Analyst.

### Trigger

A request for the population of an area is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests the population of an area.
2. Analyst chooses the type of area: world, continent, region, country, district or city.
3. Analyst enters the name of the area if needed.
4. Analyst extracts the total population of that area.
5. Analyst provides the population figure to the organisation.

## EXTENSIONS

3. **Area does not exist**:
    1. Analyst informs the organisation that no such area exists.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0

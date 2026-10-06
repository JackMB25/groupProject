# USE CASE: 6 Produce a Language Report

## CHARACTERISTIC INFORMATION

### Goal in Context

As an *analyst* I want *to produce a report on the number of people who speak major world languages* so that *I can understand the global reach of each language.*

### Reports Covered

1. The number of people who speak Chinese, English, Hindi, Spanish and Arabic, from greatest number to smallest, including the percentage of the world population.

Report columns: Language, Number of speakers, Percentage of world population.

### Scope

Organisation.

### Level

Primary task.

### Preconditions

Database contains current country population data and the percentage of each country's population that speaks each language.

### Success End Condition

A language report is available for the analyst to provide to the organisation.

### Failed End Condition

No report is produced.

### Primary Actor

Analyst.

### Trigger

A request for language information is sent to the analyst.

## MAIN SUCCESS SCENARIO

1. Organisation requests a language report.
2. Analyst extracts the number of speakers of Chinese, English, Hindi, Spanish and Arabic in each country.
3. Analyst totals the speakers of each language across all countries.
4. Analyst calculates each total as a percentage of the world population.
5. Analyst orders the languages from greatest number of speakers to smallest.
6. Analyst provides the report to the organisation.

## EXTENSIONS

None.

## SUB-VARIATIONS

None.

## SCHEDULE

**DUE DATE**: Release 1.0

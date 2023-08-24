Feature: Execute CompactionService

  Background:
    Given a view with the following properties
      | viewName                    | pageSize |
      | mobility-hindrances/by-page | 10       |
   Scenario: Execution Compaction
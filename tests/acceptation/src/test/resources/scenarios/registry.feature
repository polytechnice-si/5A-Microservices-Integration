Feature: Citizen Registry

  Background:
    Given an empty registry deployed on localhost:9080
      And a citizen named John added to the registry
      And a citizen named Jane added to the registry

  Scenario: Registering a citizen

    Given A citizen identified as 111-555-111
      And with last_name  set to Foo
      And with first_name set to Bar
      And with zip_code   set to 55555
      And with address    set to nowhere, middle of
      And with birth_year set to 1970
    When the REGISTER message is sent
    Then the citizen is registered
      And there are 3 citizens in the registry
      And the last_name   is equals to Foo
      And the first_name  is equals to Bar
      And the zip_code    is equals to 55555
      And the address     is equals to nowhere, middle of
      And the birth_year  is equals to 1970

  Scenario: Removing a citizen
    Given a POI identified as 111-555-001
      And the DELETE message is sent
    Then the citizen is removed
      And there is 1 citizen in the registry

  Scenario: Purging the database
    Given the caution safe word
    When the PURGE message is sent
    Then there is 0 citizen in the registry

   Scenario: Retrieving a citizen
     Given a POI identified as 111-555-001
     When the RETRIEVE message is sent
     Then the citizen exists
      And the last_name   is equals to Doe
      And the first_name  is equals to John
      And the zip_code    is equals to 55555
      And the address     is equals to nowhere, middle of
      And the birth_year  is equals to 1970

  Scenario: Looking for citizens #1
    Given a filter set to "D.*"
    When the LIST message is sent
    Then the answer contains 2 results

  Scenario: Looking for citizens #2
    Given a filter set to ".*o.*"
    When the LIST message is sent
    Then the answer contains 1 result

  Scenario: Looking for citizens #3
    Given a filter set to "c.*"
    When the LIST message is sent
    Then the answer contains 0 results

  Scenario: Getting all the citizens
    When the DUMP message is sent
    Then the answer contains 2 results
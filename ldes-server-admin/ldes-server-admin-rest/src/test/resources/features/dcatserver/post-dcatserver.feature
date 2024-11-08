Feature: POST a dcatserver to the server

  Scenario: POST a valid dcatserver
    Given a db containing no dcatserver
    When the client makes a post request with a valid model
    Then the client receives HTTP status 201 for dcatserverrequest
    And the client receives an UUID

  Scenario: POST an invalid dcatserver
    Given a db containing no dcatserver
    When the client makes a post request with a invalid model
    Then the client receives HTTP status 400 for dcatserverrequest

  Scenario: POST a valid dcatserver when there is already one configured
    Given a db containing one dcatserver
    When the client makes a post request with a valid model
    Then the client receives HTTP status 400 for dcatserverrequest

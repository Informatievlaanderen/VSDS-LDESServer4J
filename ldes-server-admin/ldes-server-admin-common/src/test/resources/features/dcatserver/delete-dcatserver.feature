Feature: delete a dcatserver from the server

  Scenario Outline: DELETE a valid dcatserver
    Given a db containing <dcatNumber>
    When the client makes a delete serverdcat request
    Then the client receives HTTP status <status> for dcatserverrequest
    Examples:
      | dcatNumber     | status |
      | one dcatserver | 200    |
      | no dcatserver  | 200    |
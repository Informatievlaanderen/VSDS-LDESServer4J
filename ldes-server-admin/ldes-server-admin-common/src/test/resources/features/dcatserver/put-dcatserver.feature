Feature: PUT a dcatserver to the server

  Scenario Outline: PUT a valid dcatserver
    Given a db containing <dcatNumber>
    When the client makes a put request with a <isValid> model
    Then the client receives HTTP status <status> for dcatserverrequest
    Examples:
      | dcatNumber     | isValid  | status |
      | one dcatserver | valid    | 200    |
      | no dcatserver  | valid    | 404    |
      | one dcatserver | invalid  | 400    |
      | no dcatserver  | invalid  | 400    |


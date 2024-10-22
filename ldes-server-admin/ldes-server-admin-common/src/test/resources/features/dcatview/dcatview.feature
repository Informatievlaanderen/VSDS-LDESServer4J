Feature: DcatView
  As a user
  I want to interact with the DcatViewRestController to handle data services

  Scenario Outline: Create DCAT Dataservice
    Given I have a <isValid> dcat dataservice
    When I POST this dataservice
    Then The dataservice metadata <isStored> be stored
    And Response with http <httpCode> will be returned

    Examples:
      | isValid | isStored | httpCode |
      | valid   | will     | 201      |
      | invalid | will not | 400      |

  Scenario Outline: Update DCAT Dataservice
    Given I have a <isValid> dcat dataservice
    And The dataservice <isExisting>
    When I PUT this dataservice
    Then The dataservice metadata <isStored> be stored
    And Response with http <httpCode> will be returned

    Examples:
      | isValid | isStored | httpCode | isExisting         |
      | valid   | will     | 200      | already exists     |
      | valid   | will not | 404      | does not yet exist |
      | invalid | will not | 400      | already exists     |

    Scenario Outline: Delete DCAT Dataservice
      Given I have a <isValid> dcat dataservice
      When I DELETE this dataservice
      Then The dataservice metadata will be deleted
      And Response with http 200 will be returned

      Examples:
        | isValid |
        | valid   |
        | invalid |
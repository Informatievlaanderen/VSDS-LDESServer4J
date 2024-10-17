Feature: DcatDataset
  As a user
  I want to interact with the DcatDatasetRestController to handle datasets

  Scenario Outline: Create DCAT dataset
    Given I have a <isValid> dcat dataset
    When I POST this dataset
    Then The dataset <isStored> be stored
    And Response with http <httpCode> will be returned for dataset

    Examples:
      | isValid | isStored | httpCode |
      | valid   | will     | 201      |
      | invalid | will not | 400      |

  Scenario Outline: Update DCAT dataset
    Given I have a <isValid> dcat dataset
    And The dataset <isExisting>
    When I PUT this dataset
    Then The dataset <isStored> be stored
    And Response with http <httpCode> will be returned for dataset

    Examples:
      | isValid | isStored | httpCode | isExisting         |
      | valid   | will     | 200      | already exists     |
      | valid   | will not | 404      | does not yet exist |
      | invalid | will not | 400      | already exists     |

  Scenario Outline: Delete DCAT dataset
    Given I have a <isValid> dcat dataset
    And The dataset <isExisting>
    When I DELETE this dataset
    Then The dataset <isDeleted> be deleted
    And Response with http 200 will be returned for dataset

    Examples:
      | isValid | isExisting         | isDeleted |
      | valid   | already exists     |will       |
      | valid   | does not yet exist |will not   |
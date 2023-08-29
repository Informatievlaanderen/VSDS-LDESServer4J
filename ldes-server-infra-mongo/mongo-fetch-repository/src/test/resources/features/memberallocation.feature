Feature: AllocationRepository
  As a user
  I want to interact with the AllocationRepository to save, retrieve and delete MemberAllocation

  Background:
    Given The following MemberAllocations
      | id                                   | collectionName      | viewName   | fragmentId                                | memberId |
      | bc8b4fb6-6ae7-4108-a1a2-de1375cc3538 | mobility-hindrances | by-page    | /mobility-hindrances/by-page?pageNumber=1 | member-1 |
      | 8100886e-ab0f-4064-b3e1-ced75f07958a | mobility-hindrances | by-page    | /mobility-hindrances/by-page?pageNumber=1 | member-2 |
      | e93b036e-d169-40a0-8b11-8cb54ee67f10 | mobility-hindrances | by-version | /mobility-hindrances/by-version?version=1 | member-1 |
      | e9c89017-894d-4196-b2e8-ecb9ff98fda8 | mobility-hindrances | by-version | /mobility-hindrances/by-version?version=2 | member-2 |
      | 7deb5ce5-e57b-469b-9966-35150862d890 | parcels             | by-page    | /parcels/by-page?pageNumber=1             | member-1 |
      | 618019d0-b18c-4e2e-92d8-1e169baead6c | parcels             | by-page    | /parcels/by-page?pageNumber=1             | member-2 |
      | d6a71f1d-83f0-4987-b064-539314a43056 | parcels             | by-page    | /parcels/by-page?pageNumber=1             | member-3 |
    And They are ingested using the AllocationRepository

  Scenario: FIND ALL BY FRAGMENT_ID
    Then Querying by the fragment id has the following results
      | fragmentId                                | ids                                                                                                            |
      | /mobility-hindrances/by-page?pageNumber=1 | bc8b4fb6-6ae7-4108-a1a2-de1375cc3538,8100886e-ab0f-4064-b3e1-ced75f07958a                                      |
      | /mobility-hindrances/by-version?version=1 | e93b036e-d169-40a0-8b11-8cb54ee67f10                                                                           |
      | /mobility-hindrances/by-version?version=2 | e9c89017-894d-4196-b2e8-ecb9ff98fda8                                                                           |
      | /parcels/by-page?pageNumber=1             | 7deb5ce5-e57b-469b-9966-35150862d890,618019d0-b18c-4e2e-92d8-1e169baead6c,d6a71f1d-83f0-4987-b064-539314a43056 |
      | /not-existing                             | [blank]                                                                                                        |

  Scenario Outline: DELETE BY MEMBER_ID AND COLLECTION_NAME AND VIEW_NAME
    When Deleting by the member id <memberId> and the collection name <collectionName> and the view name <viewName>
    Then Querying by the fragment id <fragmentId> has the following results <ids>
    And There are 6 remaining MemberAllocations in the MemberAllocationRepository
    Examples:
      | memberId | collectionName      | viewName   | fragmentId                                | ids                                                                       |
      | member-1 | mobility-hindrances | by-page    | /mobility-hindrances/by-page?pageNumber=1 | 8100886e-ab0f-4064-b3e1-ced75f07958a                                      |
      | member-2 | mobility-hindrances | by-page    | /mobility-hindrances/by-page?pageNumber=1 | bc8b4fb6-6ae7-4108-a1a2-de1375cc3538                                      |
      | member-1 | mobility-hindrances | by-version | /mobility-hindrances/by-version?version=1 | [blank]                                                                   |
      | member-2 | mobility-hindrances | by-version | /mobility-hindrances/by-version?version=2 | [blank]                                                                   |
      | member-1 | parcels             | by-page    | /parcels/by-page?pageNumber=1             | 618019d0-b18c-4e2e-92d8-1e169baead6c,d6a71f1d-83f0-4987-b064-539314a43056 |
      | member-2 | parcels             | by-page    | /parcels/by-page?pageNumber=1             | 7deb5ce5-e57b-469b-9966-35150862d890,d6a71f1d-83f0-4987-b064-539314a43056 |
      | member-3 | parcels             | by-page    | /parcels/by-page?pageNumber=1             | 7deb5ce5-e57b-469b-9966-35150862d890,618019d0-b18c-4e2e-92d8-1e169baead6c |

  Scenario Outline: DELETE BY COLLECTION_NAME AND VIEW_NAME
    When Deleting by the collection name <collectionName> and the view name <viewName>
    Then Querying by the fragment ids <fragmentIds> returns empty list
    And There are <remainingMemberAllocations> remaining MemberAllocations in the MemberAllocationRepository
    Examples:
      | collectionName      | viewName   | fragmentIds                                                                         | remainingMemberAllocations |
      | mobility-hindrances | by-page    | /mobility-hindrances/by-page?pageNumber=1                                           | 5                          |
      | mobility-hindrances | by-version | /mobility-hindrances/by-version?version=1,/mobility-hindrances/by-version?version=2 | 5                          |
      | parcels             | by-page    | /parcels/by-page?pageNumber=1                                                       | 4                          |

  Scenario Outline: DELETE BY COLLECTION_NAME
    When Deleting by the collection name <collectionName>
    Then Querying by the fragment ids <fragmentIds> returns empty list
    And There are <remainingMemberAllocations> remaining MemberAllocations in the MemberAllocationRepository
    Examples:
      | collectionName      | fragmentIds                                                                                                                   | remainingMemberAllocations |
      | mobility-hindrances | /mobility-hindrances/by-page?pageNumber=1,/mobility-hindrances/by-version?version=1,/mobility-hindrances/by-version?version=2 | 3                          |
      | parcels             | /parcels/by-page?pageNumber=1                                                                                                 | 4                          |

  Scenario Outline: DELETE BY FRAGMENT ID
    When Deleting by the fragment id <fragmentId>
    Then There are <expectedCount> remaining MemberAllocations in the MemberAllocationRepository
    Examples:
      | fragmentId                                | expectedCount |
      | /mobility-hindrances/by-page?pageNumber=1 | 5             |
      | /mobility-hindrances/by-version?version=1 | 6             |
      | /mobility-hindrances/by-version?version=2 | 6             |
      | /parcels/by-page?pageNumber=1             | 4             |

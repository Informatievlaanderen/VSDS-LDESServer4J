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

  Scenario: FIND ALL BY FRAGMENT ID
    Then Querying using the fragment ids has the following results
      | fragmentId                                | ids                                                                                                            |
      | /mobility-hindrances/by-page?pageNumber=1 | bc8b4fb6-6ae7-4108-a1a2-de1375cc3538,8100886e-ab0f-4064-b3e1-ced75f07958a                                      |
      | /mobility-hindrances/by-version?version=1 | e93b036e-d169-40a0-8b11-8cb54ee67f10                                                                           |
      | /mobility-hindrances/by-version?version=2 | e9c89017-894d-4196-b2e8-ecb9ff98fda8                                                                           |
      | /parcels/by-page?pageNumber=1             | 7deb5ce5-e57b-469b-9966-35150862d890,618019d0-b18c-4e2e-92d8-1e169baead6c,d6a71f1d-83f0-4987-b064-539314a43056 |
      | /not-existing                             | [blank]                                                                                                        |
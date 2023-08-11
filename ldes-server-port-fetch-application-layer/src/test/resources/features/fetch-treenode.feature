Feature: Fetch TreeNodeDto

  Background:
    Given The following EventStream
      | collection          | memberType                                                 | timestampPath                             | versionOfPath                        |
      | mobility-hindrances | https://data.vlaanderen.be/ns/mobiliteit#Mobiliteitshinder | http://www.w3.org/ns/prov#generatedAtTime | http://purl.org/dc/terms/isVersionOf |
    And The following shacl
      | collection          | shacl                   |
      | mobility-hindrances | features/data/shacl.ttl |
    And The following Allocations
      | memberId                                                          | collectionName      | viewName | fragmentId                          |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/1 | mobility-hindrances | by-page  | /mobility-hindrances/by-page?page=1 |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/2 | mobility-hindrances | by-page  | /mobility-hindrances/by-page?page=1 |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/3 | mobility-hindrances | by-page  | /mobility-hindrances/by-page?page=1 |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/4 | mobility-hindrances | by-page  | /mobility-hindrances/by-page?page=2 |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/5 | mobility-hindrances | by-page  | /mobility-hindrances/by-page?page=2 |
    And the following Fragments can be retrieved from the FragmentRepository
      | fragmentId                          | immutable | relations                           |
      | /mobility-hindrances/by-page        | false     | /mobility-hindrances/by-page?page=1 |
      | /mobility-hindrances/by-page?page=1 | true      | /mobility-hindrances/by-page?page=2 |
      | /mobility-hindrances/by-page?page=2 | false     | [blank]                             |
    And the following dcat can be retrieved from the DcatViewService
      | viewName                    | dcat                   |
      | mobility-hindrances/by-page | features/data/dcat.ttl |
    And the following Members can be retrieved from the MemberRepository
      | memberId                                                          | collectionName      | model                     |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/1 | mobility-hindrances | features/data/member1.ttl |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/2 | mobility-hindrances | features/data/member2.ttl |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/3 | mobility-hindrances | features/data/member3.ttl |
    And the following Members can be retrieved from the MemberRepository
      | memberId                                                          | collectionName      | model                     |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/4 | mobility-hindrances | features/data/member4.ttl |
      | mobility-hindrances/http://localhost:8080/mobility-hindrances/1/5 | mobility-hindrances | features/data/member5.ttl |


  Scenario Outline: Fetch TreeNodeDTO
    When The TreeNodeDTO with for LdesFragmentRequest with viewName <viewName> and fragmentPairs <fragmentPairs> is fetched
    Then The Model of the TreeNodeDTO is the same as in <expectedModel>

    Examples:
      | viewName                    | fragmentPairs | expectedModel               |
      | mobility-hindrances/by-page | null          | features/expected/view.ttl  |
      | mobility-hindrances/by-page | page,1        | features/expected/page1.ttl |
      | mobility-hindrances/by-page | page,2        | features/expected/page2.ttl |
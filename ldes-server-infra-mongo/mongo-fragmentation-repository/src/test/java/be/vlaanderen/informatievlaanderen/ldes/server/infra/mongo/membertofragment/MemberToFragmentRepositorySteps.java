// package
// be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.membertofragment;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.ViewName;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Member;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.MongoFragmentationIntegrationTest;
// import io.cucumber.datatable.DataTable;
// import io.cucumber.java.DataTableType;
// import io.cucumber.java.en.And;
// import io.cucumber.java.en.Given;
// import io.cucumber.java.en.Then;
// import io.cucumber.java.en.When;
// import org.apache.jena.rdf.model.ModelFactory;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// TODO TVB: 31/07/23 remove
// public class MemberToFragmentRepositorySteps extends
// MongoFragmentationIntegrationTest {
//
// private final Map<String, List<ViewName>> viewsPerCollection = new
// HashMap<>();
// private Optional<Member> nextMemberToFragment;
//
// @Given("I have the following collections")
// public void iHaveTheFollowingCollections(DataTable dataTable) {
// List<Map<String, String>> tableMap = dataTable.asMaps();
// tableMap.forEach(entry -> {
// String collectionName = entry.get("collectionName");
// List<String> views = List.of(entry.get("views").split(","));
// List<ViewName> viewNames = views.stream().map(view -> new
// ViewName(collectionName, view)).toList();
// viewsPerCollection.put(collectionName, viewNames);
// });
// }
//
// @DataTableType(replaceWithEmptyString = "[blank]")
// public Member extractMember(Map<String, String> row) {
// return new Member(
// row.get("memberId"),
// ModelFactory.createDefaultModel(),
// Long.parseLong(row.get("sequenceNr")));
// }
//
// @And("I create the following members")
// public void iCreateTheFollowingMembers(DataTable dataTable) {
// List<Map<String, String>> tableMap = dataTable.asMaps();
// tableMap.forEach(entry -> {
// List<ViewName> views = viewsPerCollection.get(entry.get("collectionName"));
// memberToFragmentRepository.create(views, extractMember(entry));
// });
// }
//
// @When("I request the next member for view {string}")
// public void iRequestTheNextMember(String viewName) {
// nextMemberToFragment =
// memberToFragmentRepository.getNextMemberToFragment(ViewName.fromString(viewName));
// }
//
// @Then("I find the member with id {string} and sequenceNr {int}")
// public void iFindTheMemberWithIdAndSequenceNr(String memberId, long
// sequenceNr) {
// assertTrue(nextMemberToFragment.isPresent());
// Member member = nextMemberToFragment.get();
// assertEquals(sequenceNr, member.sequenceNr());
// assertEquals(memberId, member.id());
// }
//
// @Then("I do not find a member")
// public void iDoNotFindAMember() {
// assertTrue(nextMemberToFragment.isEmpty());
// }
//
// @And("I delete the member with view {string} and sequenceNr {int}")
// public void iDeleteTheMemberWithViewAndSequenceNr(String viewNameString, long
// sequenceNr) {
// ViewName viewName = ViewName.fromString(viewNameString);
// memberToFragmentRepository.delete(viewName, sequenceNr);
// }
// }

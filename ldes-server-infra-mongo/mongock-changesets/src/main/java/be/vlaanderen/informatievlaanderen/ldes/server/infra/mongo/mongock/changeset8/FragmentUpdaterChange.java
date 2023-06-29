//package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.changeset8;
//
//import io.mongock.api.annotations.ChangeUnit;
//import io.mongock.api.annotations.Execution;
//import org.springframework.data.mongodb.core.MongoTemplate;
//
//@ChangeUnit(id = "fragment-updater-changeset-8", order = "8", author = "VSDS")
//public class FragmentUpdaterChange {
//	private final MongoTemplate mongoTemplate;
//
//	public FragmentUpdaterChange(MongoTemplate mongoTemplate) {
//		this.mongoTemplate = mongoTemplate;
//	}
//
//	/**
//	 * This is the method with the migration code
//	 **/
//	@Execution
//	public void changeSet() {
//		mongoTemplate.indexOps("ldesfragment").dropIndex("softDeleted");
//	}
//
//}
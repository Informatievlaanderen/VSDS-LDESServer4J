package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.fragmentation.resultchecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.result.UpdateResult;

public class ResultChecker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResultChecker.class);

	private ResultChecker() {
	}

	public static void expect(UpdateResult result, long expectAffected) {
		if (result.getModifiedCount() != expectAffected) {
			LOGGER.warn("Expected {} rows to be updated but {} were updated.", expectAffected,
					result.getModifiedCount());
		}
	}
}

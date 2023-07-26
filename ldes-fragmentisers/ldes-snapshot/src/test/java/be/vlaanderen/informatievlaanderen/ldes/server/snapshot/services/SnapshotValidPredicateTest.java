package be.vlaanderen.informatievlaanderen.ldes.server.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.snapshot.exception.SnapshotCreationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotValidPredicateTest {

	private final SnapshotValidPredicate snapshotValidPredicate = new SnapshotValidPredicate();

	@Nested
	class ValidMember {

		@Test
		void test_validMember() {
			Member member = new Member("id", null, "versionOf", LocalDateTime.now());

			assertTrue(snapshotValidPredicate.test(member));
		}
	}

	@Nested
	class InvalidMember {

		@ParameterizedTest
		@ArgumentsSource(InvalidMembersProvider.class)
		void test_invalidMember(Member member) {
			SnapshotCreationException snapshotCreationException = assertThrows(SnapshotCreationException.class,
					() -> snapshotValidPredicate.test(member));
			assertEquals("Unable to create snapshot.\n" +
					"Cause: Member id does not have a valid timestampPath or versionOfPath",
					snapshotCreationException.getMessage());
		}

		static class InvalidMembersProvider implements ArgumentsProvider {

			@Override
			public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
				return Stream.of(
						Arguments.of(
								new Member("id", null, null, LocalDateTime.now()),
								Arguments.of(new Member("id", null, "versionOf", null))));
			}
		}
	}
}
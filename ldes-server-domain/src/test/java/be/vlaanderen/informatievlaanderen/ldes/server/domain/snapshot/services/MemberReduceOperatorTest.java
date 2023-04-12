package be.vlaanderen.informatievlaanderen.ldes.server.domain.snapshot.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemberReduceOperatorTest {

	MemberReduceOperator memberReduceOperator = new MemberReduceOperator();

	@ParameterizedTest
	@ArgumentsSource(ContentTypeRdfFormatLangArgumentsProvider.class)
	void test_MemberReduceOperator(Member member, Member member2, String expectedId) {
		Member chosenMember = memberReduceOperator.apply(member, member2);
		assertEquals(expectedId, chosenMember.getLdesMemberId());
	}

	static class ContentTypeRdfFormatLangArgumentsProvider implements
			ArgumentsProvider {
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
			return Stream.of(
					Arguments.of(getMember("v1", 1), getMember("v2", 2), "v2"),
					Arguments.of(getMember("v1", 2), getMember("v1", 1), "v1"));
		}

		private Member getMember(String id, int minute) {
			return new Member("collectionName", id, null, LocalDateTime.of(1, 1, 1, 1, minute), null, List.of());
		}
	}
}
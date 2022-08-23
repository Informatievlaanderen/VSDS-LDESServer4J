package be.vlaanderen.informatievlaanderen.ldes.server.domain.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.services.TimestampPathComparator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimestampPathComparatorTest {

	@Test
	@DisplayName("Verify correct sorting of LdesMembers based on generatedAtTime property")
	void when_LdesMembersHaveGeneratedAtTimeProperty_TimestampPathComparatorCanSortLdesMembers() {
		LdesMember firstLdesMember = createLdesMember("\"2020-12-28T09:36:37.127Z\"");
		LdesMember secondLdesMember = createLdesMember("\"2020-12-28T09:37:37.17Z\"");
		LdesMember thirdLdesMember = createLdesMember("\"2020-12-28T09:38:37.1Z\"");
		LdesMember fourthLdesMember = createLdesMember("\"2022-05-25T06:46:26Z\"");
		LdesMember maxLdesMember = Stream.of(fourthLdesMember, thirdLdesMember, firstLdesMember, secondLdesMember)
				.max(new TimestampPathComparator()).get();
		LdesMember minLdesMember = Stream.of(firstLdesMember, fourthLdesMember, thirdLdesMember, secondLdesMember)
				.min(new TimestampPathComparator()).get();
		assertEquals(fourthLdesMember, maxLdesMember);
		assertEquals(firstLdesMember, minLdesMember);

	}

	private LdesMember createLdesMember(String generatedAtTime) {

		Model model = RdfModelConverter.fromString(
				"<https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483> <http://www.w3.org/ns/prov#generatedAtTime> "
						+ generatedAtTime + "^^<http://www.w3.org/2001/XMLSchema#dateTime> .\n",
				Lang.NQUADS);
		return new LdesMember("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10228622/483",
				model);
	}

}
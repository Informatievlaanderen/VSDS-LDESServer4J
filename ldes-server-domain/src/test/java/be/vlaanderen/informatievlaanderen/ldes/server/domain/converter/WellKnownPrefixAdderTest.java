package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.collections.WellKnownPrefixesConfig;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WellKnownPrefixAdderTest {
	@Mock
	private WellKnownPrefixesConfig wellKnownPrefixes;
	@InjectMocks
	private WellKnownPrefixAdder wellKnownPrefixAdder;

	@Test
	void test_AddPrefixes() {

		final Model model = mock();

		wellKnownPrefixAdder.addPrefixesToModel(model);

		verify(model).setNsPrefixes(anyMap());
	}
}
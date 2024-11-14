package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.collections.Prefixes;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

@Component
public class WellKnownPrefixAdder implements PrefixAdder {
	private final Prefixes wellKnownPrefixes;

	public WellKnownPrefixAdder(Prefixes wellKnownPrefixes) {
		this.wellKnownPrefixes = wellKnownPrefixes;
	}

	@Override
	public Model addPrefixesToModel(Model model) {
		model.setNsPrefixes(wellKnownPrefixes.getPrefixes());
		return model;
	}
}

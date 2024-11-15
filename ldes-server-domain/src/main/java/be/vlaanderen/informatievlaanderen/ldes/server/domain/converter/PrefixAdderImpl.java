package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.collections.Prefixes;
import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrefixAdderImpl implements PrefixAdder {
	private final List<Prefixes> prefixes;

	public PrefixAdderImpl(List<Prefixes> prefixes) {
		this.prefixes = prefixes;
	}

	@Override
	public Model addPrefixesToModel(Model model) {
		prefixes.stream().map(Prefixes::getPrefixes).forEach(model::setNsPrefixes);
		return model;
	}
}

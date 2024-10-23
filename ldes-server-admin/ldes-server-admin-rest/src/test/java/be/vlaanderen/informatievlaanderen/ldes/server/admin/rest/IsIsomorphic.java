package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest;

import org.apache.jena.atlas.web.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RDFParser;
import org.mockito.ArgumentMatcher;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class IsIsomorphic implements ResultMatcher, ArgumentMatcher<Model> {
	private final Model model;

	private IsIsomorphic(Model model) {
		this.model = model;
	}

	public static IsIsomorphic with(Model model) {
		return new IsIsomorphic(model);
	}

	@Override
	public void match(MvcResult result) {
		final MockHttpServletResponse response = result.getResponse();
		final ByteArrayInputStream bytes = new ByteArrayInputStream(response.getContentAsByteArray());
		final Lang lang = Optional.ofNullable(response.getContentType())
				.map(ContentType::create)
				.map(RDFLanguages::contentTypeToLang)
				.orElse(Lang.TURTLE);
		final Model actualModel = RDFParser.source(bytes).lang(lang).toModel();
		AssertionErrors.assertTrue("Result should be isomorphic with provided model",
				actualModel.isIsomorphicWith(model));
	}

	@Override
	public boolean matches(Model actualModel) {
		return actualModel.isIsomorphicWith(model);
	}

}

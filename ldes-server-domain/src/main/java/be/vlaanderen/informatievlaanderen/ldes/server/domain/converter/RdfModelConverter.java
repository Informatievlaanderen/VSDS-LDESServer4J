package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RelativeUrlException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig.USE_RELATIVE_URL_KEY;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConstants.RELATIVE_URL_INCOMPATIBLE_LANGS;
import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.TURTLE;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

@Component
public class RdfModelConverter {

	@Value(USE_RELATIVE_URL_KEY)
	private boolean useRelativeUrl;

	public Lang getLang(MediaType contentType, RdfFormatException.RdfFormatContext rdfFormatContext) {
		if (contentType.equals(MediaType.TEXT_HTML)) {
			return TURTLE;
		}
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow(() -> new RdfFormatException(contentType.toString(), rdfFormatContext)));
	}

	public void checkLangForRelativeUrl(Lang lang) {
		if(useRelativeUrl && RELATIVE_URL_INCOMPATIBLE_LANGS.contains(lang)){
			throw new RelativeUrlException(lang);
		}
	}

	public static Model fromString(final String content, final Lang lang) {
		return RDFParserBuilder.create().fromString(content).lang(lang).toModel();
	}

	public static String toString(final Model model, final Lang lang) {
		StringWriter stringWriter = new StringWriter();
		RDFDataMgr.write(stringWriter, model, lang);
		return stringWriter.toString();
	}
}

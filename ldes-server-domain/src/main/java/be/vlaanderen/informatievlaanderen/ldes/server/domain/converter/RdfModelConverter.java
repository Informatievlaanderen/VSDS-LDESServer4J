package be.vlaanderen.informatievlaanderen.ldes.server.domain.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFParserBuilder;
import org.springframework.http.MediaType;

import java.io.StringWriter;

import static java.util.Optional.ofNullable;
import static org.apache.jena.riot.RDFLanguages.nameToLang;

public class RdfModelConverter {
	private RdfModelConverter() {
	}

	public static Lang getLang(MediaType contentType, RdfFormatException.LdesProcessDirection ldesProcessDirection) {
		return ofNullable(nameToLang(contentType.getType() + "/" + contentType.getSubtype()))
				.orElseGet(() -> ofNullable(nameToLang(contentType.getSubtype()))
						.orElseThrow(() -> new RdfFormatException(contentType.toString(), ldesProcessDirection)));
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

package be.vlaanderen.informatievlaanderen.ldes.server.admin.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFWriter;
import org.springframework.http.MediaType;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.RdfFormatContext.REST_ADMIN;

public class ModelConverter {

	public static Model toModel(String content, String contentType) {
		Lang lang = getLang(MediaType.valueOf(contentType), REST_ADMIN);
		return RdfModelConverter.fromString(content, lang);
	}

	public static String toString(Model model, String contentType) {
		Lang lang = getLang(MediaType.valueOf(contentType), REST_ADMIN);
		return RDFWriter.source(model)
				.lang(lang)
				.asString();
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.fromString;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.LdesProcessDirection.INGEST;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFFormat.NQUADS;

public class LdesMemberConverter extends AbstractHttpMessageConverter<Member> {
	private static final String APPLICATION = "application";

	@Autowired
	Environment environment;

	private final LdesConfig ldesConfig;

	public LdesMemberConverter(LdesConfig ldesConfig) {
		super(new MediaType(APPLICATION, "n-quads"), new MediaType(APPLICATION, "n-triples"),
				new MediaType(APPLICATION, "ld+json"));
		this.ldesConfig = ldesConfig;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Member.class);
	}

	@Override
	protected Member readInternal(Class<? extends Member> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()), INGEST);
		Model memberModel = fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);
		String memberId = extractMemberId(memberModel);
		return new Member(memberId, memberModel);
	}

	private String extractMemberId(Model model) {
		return model
				.listStatements(null, RDF_SYNTAX_TYPE, createResource(ldesConfig.getMemberType()))
				.nextOptional()
				.map(statement -> statement.getSubject().toString())
				.orElse(null);
	}

	@Override
	protected void writeInternal(Member member, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException, IOException {
		Model fragmentModel = member.getModel();

		StringWriter outputStream = new StringWriter();

		RDFDataMgr.write(outputStream, fragmentModel, NQUADS);

		OutputStream body = outputMessage.getBody();
		body.write(outputStream.toString().getBytes());
	}

	@PostConstruct
	public void init() {
		String example = "<init-server> <http://www.opengis.net/ont/geosparql#asWKT> \"<http://www.opengis.net/def/crs/EPSG/9.9.1/31370> POINT (0,0)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .";
		fromString(example, Lang.NQUADS);
	}

}

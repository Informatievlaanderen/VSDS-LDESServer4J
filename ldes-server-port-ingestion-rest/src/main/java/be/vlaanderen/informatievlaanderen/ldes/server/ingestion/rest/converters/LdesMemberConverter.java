package be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.CollectionNotFoundException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.ingestion.rest.exceptions.MalformedMemberIdException;
import jakarta.annotation.PostConstruct;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.LiteralImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.fromString;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter.getLang;
import static be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException.LdesProcessDirection.INGEST;
import static org.apache.jena.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.apache.jena.riot.RDFFormat.NQUADS;

public class LdesMemberConverter extends AbstractHttpMessageConverter<Member> {

	private final LdesConfig ldesConfig;
	private final LocalDateTimeConverter localDateTimeConverter = new LocalDateTimeConverter();

	public LdesMemberConverter(LdesConfig ldesConfig) {
		super(MediaType.ALL);
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

		String collectionName = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest().getRequestURI().substring(1);
		LdesSpecification ldesSpecification = ldesConfig.getLdesSpecification(collectionName)
				.orElseThrow(() -> new CollectionNotFoundException(collectionName));

		String memberId = extractMemberId(memberModel, ldesSpecification.getMemberType());
		String versionOf = extractVersionOf(memberModel, ldesSpecification.getVersionOfPath());
		LocalDateTime timestamp = extractTimestamp(memberModel, ldesSpecification.getTimestampPath());

		return new Member(collectionName, memberId, versionOf, timestamp, memberModel, List.of());
	}

	private LocalDateTime extractTimestamp(Model memberModel, String timestampPath) {
		LiteralImpl literalImpl = memberModel
				.listStatements(null, createProperty(timestampPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> (LiteralImpl) statement.getObject())
				.orElse(null);
		if (literalImpl == null) {
			return null;
		}
		return localDateTimeConverter.getLocalDateTime(literalImpl);

	}

	private String extractVersionOf(Model memberModel, String versionOfPath) {
		return memberModel
				.listStatements(null, createProperty(versionOfPath), (RDFNode) null)
				.nextOptional()
				.map(statement -> statement.getObject().toString())
				.orElse(null);
	}

	private String extractMemberId(Model model, String memberType) {
		return model
				.listStatements(null, RDF_SYNTAX_TYPE, createResource(memberType))
				.nextOptional()
				.map(statement -> statement.getSubject().toString())
				.orElseThrow(() -> new MalformedMemberIdException(memberType));
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

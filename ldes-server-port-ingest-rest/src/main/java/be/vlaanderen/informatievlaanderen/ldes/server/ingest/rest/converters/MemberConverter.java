package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.AppConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.MalformedMemberIdException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class MemberConverter extends AbstractHttpMessageConverter<Member> {

	private final AppConfig appConfig;

	public MemberConverter(AppConfig appConfig) {
		super(MediaType.ALL);
		this.appConfig = appConfig;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Member.class);
	}

	@Override
	protected Member readInternal(Class<? extends Member> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = RdfModelConverter.getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()),
				RdfFormatException.RdfFormatContext.INGEST);
		Model memberModel = RdfModelConverter
				.fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);

		String collectionName = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest().getRequestURI().substring(1);
		LdesConfig ldesConfig = appConfig.getLdesConfig(collectionName);

		String memberId = extractMemberId(memberModel, ldesConfig.getMemberType());
		return new Member(memberId, collectionName, null, memberModel);
	}

	private String extractMemberId(Model model, String memberType) {
		return model
				.listStatements(null, RdfConstants.RDF_SYNTAX_TYPE, ResourceFactory.createResource(memberType))
				.nextOptional()
				.map(statement -> statement.getSubject().toString())
				.orElseThrow(() -> new MalformedMemberIdException(memberType));
	}

	@Override
	protected void writeInternal(Member member, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}

}

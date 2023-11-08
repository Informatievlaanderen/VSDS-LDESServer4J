package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamCreatedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.EventStreamDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.MissingResourceException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.EventStream;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.MalformedMemberIdException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.context.event.EventListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.RdfConstants.RDF_SYNTAX_TYPE;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;

@Component
public class MemberConverter extends AbstractHttpMessageConverter<Member> {
	private final Map<String, String> memberTypes = new HashMap<>();

	public MemberConverter() {
		super(MediaType.ALL);
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

		String memberType = memberTypes.get(collectionName);
		if (memberType == null) {
			throw new MissingResourceException("eventstream", collectionName);
		}

		String memberId = extractMemberId(memberModel, memberType, collectionName);
		return new Member(memberId, collectionName, null, memberModel);
	}

	@Override
	protected void writeInternal(Member member, HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}

	@EventListener
	public void handleEventStreamCreatedEvent(EventStreamCreatedEvent event) {
		EventStream eventStream = event.eventStream();
		memberTypes.put(eventStream.getCollection(), eventStream.getMemberType());
	}

	@EventListener
	public void handleEventStreamDeletedEvent(EventStreamDeletedEvent event) {
		memberTypes.remove(event.collectionName());
	}

	private String extractMemberId(Model model, String memberType, String collectionName) {
		return model
				.listStatements(null, RDF_SYNTAX_TYPE, createResource(memberType))
				.nextOptional()
				.map(statement -> collectionName + "/" + statement.getSubject().toString())
				.orElseThrow(() -> new MalformedMemberIdException(memberType));
	}

}

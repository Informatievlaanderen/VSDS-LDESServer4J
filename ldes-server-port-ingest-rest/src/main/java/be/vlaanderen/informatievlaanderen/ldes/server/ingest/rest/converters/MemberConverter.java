package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.MemberIdNotFoundException;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFParser;
import org.jetbrains.annotations.NotNull;
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
import java.util.Objects;

@SuppressWarnings("java:S1075")
@Observed
@Component
public class MemberConverter extends AbstractHttpMessageConverter<Member> {

	private static final String VERSION_OF_PATH = "http://purl.org/dc/terms/isVersionOf";

	private final RdfModelConverter rdfModelConverter;

	public MemberConverter(RdfModelConverter rdfModelConverter) {
		super(MediaType.ALL);
		this.rdfModelConverter = rdfModelConverter;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(Member.class);
	}

	@Override
	protected Member readInternal(@NotNull Class<? extends Member> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		Lang lang = rdfModelConverter.getLang(Objects.requireNonNull(inputMessage.getHeaders().getContentType()),
				RdfFormatException.RdfFormatContext.INGEST);
		Model memberModel = RDFParser.source(inputMessage.getBody()).lang(lang).toModel();

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		String collectionName = request.getRequestURI().replace(request.getContextPath() + "/", "");

		String memberId = extractMemberId(memberModel, collectionName);
		return new Member(memberId, collectionName, null, memberModel);
	}

	@Override
	protected void writeInternal(@NotNull Member member, @NotNull HttpOutputMessage outputMessage)
			throws UnsupportedOperationException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException();
	}

	private String extractMemberId(Model model, String collectionName) {
		final var ids = model.listSubjectsWithProperty(ResourceFactory.createProperty(VERSION_OF_PATH)).toSet();
		if (ids.size() != 1) {
			throw new MemberIdNotFoundException(model);
		} else {
			return collectionName + "/" + ids.iterator().next().toString();
		}
	}

}

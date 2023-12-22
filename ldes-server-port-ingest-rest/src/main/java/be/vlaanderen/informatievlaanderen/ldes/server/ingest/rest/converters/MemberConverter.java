package be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.converters;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.converter.RdfModelConverter;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.RdfFormatException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.rest.exception.MemberIdNotFoundException;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
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
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Observed
@Component
public class MemberConverter extends AbstractHttpMessageConverter<Member> {

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
		Model memberModel = RdfModelConverter
				.fromString(new String(inputMessage.getBody().readAllBytes(), StandardCharsets.UTF_8), lang);

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
		final var ids = model.listSubjects().filterKeep(RDFNode::isURIResource).toSet();
		if (ids.size() != 1) {
			throw new MemberIdNotFoundException(model);
		} else {
			return collectionName + "/" + ids.iterator().next().toString();
		}
	}

}

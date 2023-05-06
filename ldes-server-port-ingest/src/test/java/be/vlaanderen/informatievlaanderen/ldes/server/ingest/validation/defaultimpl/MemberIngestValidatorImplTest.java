package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.Member;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.IngestValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MemberIngestValidatorImplTest {

	private MemberIngestValidatorImpl validator;

	private ModelIngestValidatorFactory factory;

	@BeforeEach
	void setUp() {
		factory = mock(ModelIngestValidatorFactory.class);
		validator = new MemberIngestValidatorImpl(factory);
	}

	@Test
	void validationShouldNotFail_whenThereAreNoValidators() {
		Member member = createBasicMember();

		assertDoesNotThrow(() -> validator.validate(member));
	}

	@Test
    void validationShouldThrowException_whenMemberIsInvalid() {
        when(factory.createValidator(null, "myCollection")).thenReturn(model -> {
            throw new IngestValidationException("invalid");
        });
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));

        Member member = createBasicMember();
        assertThrows(IngestValidationException.class, () -> validator.validate(member));
    }

	@Test
    void validatorShouldBeOverWritten_onChangedEvent() {
        when(factory.createValidator(null, "myCollection"))
                .thenReturn(model -> {throw new IngestValidationException("invalid");})
                .thenReturn(model -> {});
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));

        Member member = createBasicMember();
        assertDoesNotThrow(() -> validator.validate(member));
    }

	private Member createBasicMember() {
		return new Member("id", "myCollection", 0L, null);
	}

}
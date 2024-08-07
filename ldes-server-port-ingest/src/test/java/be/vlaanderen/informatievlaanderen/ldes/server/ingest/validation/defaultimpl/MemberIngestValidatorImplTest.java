package be.vlaanderen.informatievlaanderen.ldes.server.ingest.validation.defaultimpl;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclChangedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ShaclDeletedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.ShaclValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.ingest.entities.IngestedMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        IngestedMember member = createBasicMember();

        assertThatNoException().isThrownBy(() -> validator.validate(member));
    }

    @Test
    void validationShouldThrowException_whenMemberIsInvalid() {
        when(factory.createValidator(null)).thenReturn(model -> {
            throw new ShaclValidationException("invalid", null);
        });
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));

        IngestedMember member = createBasicMember();
        assertThatThrownBy(() -> validator.validate(member))
                .isInstanceOf(ShaclValidationException.class)
                .hasMessage("Shacl validation failed: \n\ninvalid");
    }

    @Test
    void validatorShouldBeOverWritten_onChangedEvent() {
        when(factory.createValidator(null))
                .thenReturn(model -> {
                    throw new ShaclValidationException("invalid", null);
                })
                .thenReturn(model -> {
                });
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));

        IngestedMember member = createBasicMember();
        assertThatNoException().isThrownBy(() -> validator.validate(member));
    }

    @Test
    void validatorShouldBeRemoved_onDeleteEvent() {
        when(factory.createValidator(null))
                .thenReturn(model -> {
                    throw new ShaclValidationException("invalid", null);
                });
        validator.handleShaclChangedEvent(new ShaclChangedEvent("myCollection", null));
        validator.handleShaclDeletedEvent(new ShaclDeletedEvent("myCollection"));

        IngestedMember member = createBasicMember();

        assertThatNoException().isThrownBy(() -> validator.validate(member));
    }

    private IngestedMember createBasicMember() {
        return new IngestedMember("id", "myCollection", "versionOf", LocalDateTime.now(), true, "txId", null);
    }

}
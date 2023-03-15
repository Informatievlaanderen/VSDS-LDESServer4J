package be.vlaanderen.informatievlaanderen.ldes.server.domain.validation;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions.LdesStreamValidationException;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldes.eventstream.valueobjects.EventStream;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class LdesStreamValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return EventStream.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventStream eventStream = (EventStream) target;

    }

    protected void validateEventStream(EventStream eventStream) {
        if(eventStream.collection() == null) {
            throw new LdesStreamValidationException("null");
        }
        if(eventStream.shape() == null) {
            throw new LdesStreamValidationException(eventStream.collection());
        }
    }
}

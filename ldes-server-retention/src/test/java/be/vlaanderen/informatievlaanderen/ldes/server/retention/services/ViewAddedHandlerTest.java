package be.vlaanderen.informatievlaanderen.ldes.server.retention.services;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.events.admin.ViewAddedEvent;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewName;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.model.ViewSpecification;
import be.vlaanderen.informatievlaanderen.ldes.server.retention.repositories.MemberPropertiesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ViewAddedHandlerTest {

    @Mock
    private MemberPropertiesRepository memberPropertiesRepository;

    @InjectMocks
    private ViewAddedHandler viewAddedHandler;

    @Test
    void handleViewAddedEvent() {
        ViewName viewName = ViewName.fromString("col/view");
        ViewSpecification viewSpecification = new ViewSpecification(viewName, List.of(), List.of(), 10);

        viewAddedHandler.handle(new ViewAddedEvent(viewSpecification));

        verify(memberPropertiesRepository).addViewToAll(viewName);
    }

}
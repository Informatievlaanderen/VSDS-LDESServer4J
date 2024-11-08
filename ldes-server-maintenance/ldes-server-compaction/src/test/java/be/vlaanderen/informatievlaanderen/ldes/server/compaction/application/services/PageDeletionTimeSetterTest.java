package be.vlaanderen.informatievlaanderen.ldes.server.compaction.application.services;

import be.vlaanderen.informatievlaanderen.ldes.server.compaction.domain.repository.CompactionPageRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.constants.ServerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PageDeletionTimeSetterTest {

    @Captor
    ArgumentCaptor<LocalDateTime> timeCaptor;
    private final CompactionPageRepository pageRepository = mock(CompactionPageRepository.class);
    private ServerConfig config;
    private PageDeletionTimeSetter pageDeletionTimeSetter;

    @BeforeEach
    void setUp() {
        config = new ServerConfig();
        config.setCompactionDuration("PT1M");
        pageDeletionTimeSetter = new PageDeletionTimeSetter(pageRepository, config);
    }

    @Test
    void when_DeletionTimeSet_Then_CorrectTimeSet() {
        List<Long> ids = List.of(1L, 2L);
        pageDeletionTimeSetter.setDeleteTimeOfFragment(ids);

        verify(pageRepository).setDeleteTime(eq(ids), timeCaptor.capture());
        assertThat(timeCaptor.getValue()).isAfter(LocalDateTime.now()).isBefore(LocalDateTime.now().plusMinutes(2));
    }
}
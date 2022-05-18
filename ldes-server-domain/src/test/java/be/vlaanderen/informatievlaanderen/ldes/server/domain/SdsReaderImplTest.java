package be.vlaanderen.informatievlaanderen.ldes.server.domain;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.entities.LdesMember;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.repositories.LdesMemberRepository;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReader;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.services.SdsReaderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SdsReaderImplTest {

    private final LdesMemberRepository ldesMemberRepository = mock(LdesMemberRepository.class);
    private final SdsReader sdsReader = new SdsReaderImpl(ldesMemberRepository);

    @DisplayName("Correct storing of an LdesMember in a repository")
    @Test
    void when_LdesMemberIsStoredInRepository_CreatedResourceIsReturned() {
        LdesMember originalLdesMember = new LdesMember(new String[] { "original", "ldes", "member" });
        LdesMember expectedStoredLdesMember = new LdesMember(new String[] { "stored", "ldes", "member" });
        when(ldesMemberRepository.saveLdesMember(originalLdesMember)).thenReturn(expectedStoredLdesMember);

        LdesMember actualStoredLdesMember = sdsReader.storeLdesMember(originalLdesMember);

        assertEquals(expectedStoredLdesMember, actualStoredLdesMember);
        verify(ldesMemberRepository, times(1)).saveLdesMember(originalLdesMember);
    }
}
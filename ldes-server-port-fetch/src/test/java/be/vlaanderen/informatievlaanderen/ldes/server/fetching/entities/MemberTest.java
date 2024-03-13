package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {
    @Test
    void shouldThrowException_whenIdHasNoPrefix() {
        Member member = new Member(
                "http://localhost:8080/member/1", null,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                LocalDateTime.parse("2020-12-28T09:36:37.127"), null);

        assertThrows(IllegalStateException.class, member::getMemberIdWithoutPrefix);
    }

    @Test
    void shouldReturnIdWithoutPrefix_whenIdHasPrefix() {
        Member member = new Member(
                "parcels/http://localhost:8080/member/1", null,
                "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
                LocalDateTime.parse("2020-12-28T09:36:37.127"), null);

        assertEquals("http://localhost:8080/member/1", member.getMemberIdWithoutPrefix());
    }
}
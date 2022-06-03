package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LdesMemberTest {

    @Test
    void test() {
        String[] strings = { "a" };
        LdesMember ldesMember = new LdesMember(strings);
        assertEquals(ldesMember.favorites(), strings);
    }

}
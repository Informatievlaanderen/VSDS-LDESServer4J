package be.vlaanderen.informatievlaanderen.ldes.server.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LdesMember {
    private final String[] quads;
}

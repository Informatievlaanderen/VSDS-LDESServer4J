package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesmember")
@Getter
@Setter
@RequiredArgsConstructor
public class LdesMemberEntity {

    @Id
    private final Integer id;

    private final JSONObject ldesMember;
}

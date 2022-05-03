package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("ldesfragment")
@Getter
@Setter
@RequiredArgsConstructor
public class LdesFragmentEntity {

    @Id
    private final Integer id;

    private final JSONObject ldesFragment;
}

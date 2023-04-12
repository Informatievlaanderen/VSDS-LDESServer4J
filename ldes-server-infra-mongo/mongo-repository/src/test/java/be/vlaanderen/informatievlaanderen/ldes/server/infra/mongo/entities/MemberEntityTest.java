// package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.entities;
//
// import
// be.vlaanderen.informatievlaanderen.ldes.server.domain.tree.member.entities.Member;
// import
// be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.member.entity.LdesMemberEntity;
// import org.apache.jena.rdf.model.Model;
// import org.apache.jena.riot.Lang;
// import org.apache.jena.riot.RDFParserBuilder;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
// import java.io.File;
// import java.io.IOException;
// import java.net.URISyntaxException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// class MemberEntityTest {
//
// private Member member;
//
// @BeforeEach
// public void init() throws IOException, URISyntaxException {
// ClassLoader classLoader = getClass().getClassLoader();
// member = readLdesMemberFromFile(classLoader);
// }
//
// @Test
// void testReconstructionOfLdesMember() {
// LdesMemberEntity actualLdesMemberEntity =
// LdesMemberEntity.fromLdesMember(member);
// Member reconstructedMember = actualLdesMemberEntity.toLdesMember();
//
// assertTrue(member.getModel().isIsomorphicWith(reconstructedMember.getModel()));
// assertEquals(member.getLdesMemberId(),
// reconstructedMember.getLdesMemberId());
// assertEquals(member.getVersionOf(), reconstructedMember.getVersionOf());
// assertEquals(member.getTimestamp(), reconstructedMember.getTimestamp());
// assertEquals(member.getTreeNodeReferences(),
// reconstructedMember.getTreeNodeReferences());
// }
//
// private Member readLdesMemberFromFile(ClassLoader classLoader)
// throws URISyntaxException, IOException {
// File file = new
// File(Objects.requireNonNull(classLoader.getResource("example-ldes-member.nq")).toURI());
//
// Model outputModel = RDFParserBuilder.create()
// .fromString(Files.lines(Paths.get(file.toURI())).collect(Collectors.joining())).lang(Lang.NQUADS)
// .toModel();
//
// return new
// Member("https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464/1",
// collection, index,
// "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/10810464",
// LocalDateTime.of(1, 1, 1, 1, 1), outputModel, List.of());
// }
// }

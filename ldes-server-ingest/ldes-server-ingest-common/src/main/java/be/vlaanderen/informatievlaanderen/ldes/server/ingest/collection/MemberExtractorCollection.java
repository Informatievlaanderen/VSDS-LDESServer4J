package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.extractor.MemberExtractor;

import java.util.Optional;

public interface MemberExtractorCollection {
   Optional<MemberExtractor> getMemberExtractor(String collection);
   void addMemberExtractor(String collectionName, MemberExtractor memberExtractor);
   void deleteMemberExtractor(String collectionName);
}

package be.vlaanderen.informatievlaanderen.ldes.server.ingest.collection;

import be.vlaanderen.informatievlaanderen.ldes.server.ingest.transformers.VersionObjectTransformer;

public interface VersionObjectTransformerCollection {
   VersionObjectTransformer getVersionObjectTransformer(String collection);
   void addVersionObjectTransformer(String collectionName, VersionObjectTransformer versionObjectTransformer);
   void deleteVersionObjectTransformer(String collectionName);
}

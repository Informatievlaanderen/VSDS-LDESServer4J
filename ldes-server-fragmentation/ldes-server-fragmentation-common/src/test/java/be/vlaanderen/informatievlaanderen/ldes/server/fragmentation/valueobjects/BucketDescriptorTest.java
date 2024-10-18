package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions.BucketDescriptorParseException;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BucketDescriptorTest {
	final String descriptorPairKey1 = "key1";
	final String descriptorPairValue1 = "value1";
	final String descriptorPairKey2 = "key2";
	final String descriptorPairValue2 = "value#2";

	final String decodedFragmentIdString =  descriptorPairKey1 + "=" + descriptorPairValue1 +
			"&" + descriptorPairKey2 + "=" + descriptorPairValue2;
	final String encodedFragmentIdString = descriptorPairKey1 + "=" + descriptorPairValue1
			+ "&" + descriptorPairKey2 + "=" + URLEncoder.encode(descriptorPairValue2, StandardCharsets.UTF_8);

	final String descriptorIdStringWithEmpty = descriptorPairKey1 + "="
			+ "&" + descriptorPairKey2 + "=" + descriptorPairValue2;
	final String malformedIdString = "?faultyString" + "&" + descriptorPairKey1 + "=" + descriptorPairValue1;

	final List<BucketDescriptorPair> descriptorPairs = List.of(
			new BucketDescriptorPair(descriptorPairKey1, descriptorPairValue1),
			new BucketDescriptorPair(descriptorPairKey2, descriptorPairValue2)
	);

	final BucketDescriptor bucketDescriptor = new BucketDescriptor(descriptorPairs);
	final BucketDescriptor rootBucketDescriptor = new BucketDescriptor(new ArrayList<>());

	@Test
	void when_NonRootDescriptorString_then_CreateDescriptor() {
		assertThat(BucketDescriptor.fromString(decodedFragmentIdString)).isEqualTo(bucketDescriptor);
	}

	@Test
	void when_NonRootDescriptorStringWithEmptyPairValue_then_CreateDescriptor() {
		final BucketDescriptor emptyValueDescriptor = BucketDescriptor.of(new BucketDescriptorPair(descriptorPairKey1, ""), new BucketDescriptorPair(descriptorPairKey2, descriptorPairValue2));
		assertThat(emptyValueDescriptor.asDecodedString()).isEqualTo(descriptorIdStringWithEmpty);
	}

	@Test
	void when_RootDescriptorString_then_CreateDescriptor() {
		assertThat(BucketDescriptor.fromString("")).isEqualTo(BucketDescriptor.empty());
	}

	@Test
	void when_MalformedFragmentIdString_Then_CreateFragmentIdentifier() {
		assertThatThrownBy(() -> BucketDescriptor.fromString(malformedIdString))
				.isInstanceOf(BucketDescriptorParseException.class)
				.hasMessage("BucketDescriptor could not be created from string: %s", malformedIdString);
	}

	@Test
	void when_RootFragmentIdentifier_Then_CreateFragmentIdString() {
		assertThat(rootBucketDescriptor.asDecodedString()).isEmpty();
	}

	@Test
	void when_NonRootFragmentIdentifier_Then_CreateDecodedFragmentIdString() {
		assertThat(bucketDescriptor.asDecodedString()).isEqualTo(decodedFragmentIdString);
	}

	@Test
	void when_NonRootFragmentIdentifier_Then_CreateEncodedFragmentIdString_withOnlyTheParametersBeingEncoded() {
		assertThat(bucketDescriptor.asEncodedString()).isEqualTo(encodedFragmentIdString);
	}
}
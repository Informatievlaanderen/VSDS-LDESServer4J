package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class BucketDescriptor {
	private final List<BucketDescriptorPair> descriptorPairs;

	public BucketDescriptor(List<BucketDescriptorPair> descriptorPairs) {
		this.descriptorPairs = descriptorPairs;
	}

	public List<BucketDescriptorPair> getDescriptorPairs() {
		return descriptorPairs;
	}

	public static BucketDescriptor fromString(String descriptor) {
		final List<BucketDescriptorPair> descriptorPairs = Arrays.stream(descriptor.split("&"))
				.map(pair -> pair.split("=", -1))
				.map(pairArray -> new BucketDescriptorPair(pairArray[0], pairArray[1]))
				.toList();
		return new BucketDescriptor(descriptorPairs);
	}

	public String asDecodedString() {
		return asString(false);
	}

	public String asEncodedString() {
		return asString(true);
	}

	private String asString(boolean encoded) {
		StringBuilder stringBuilder = new StringBuilder();

		if (!descriptorPairs.isEmpty()) {
			stringBuilder.append(descriptorPairs.stream()
					.map(pair -> encoded
							? pair.key() + "=" + encode(pair.value(), UTF_8)
							: pair.key() + "=" + pair.value())
					.collect(Collectors.joining("&")));
		}

		return stringBuilder.toString();
	}

}

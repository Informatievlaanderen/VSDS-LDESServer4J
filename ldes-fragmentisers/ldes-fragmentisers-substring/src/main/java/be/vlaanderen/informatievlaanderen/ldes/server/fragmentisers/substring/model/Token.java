package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import com.apicatalog.jsonld.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;

public class Token {

	private final String part;

	public Token(String part) {
		this.part = part;
	}

	public List<String> getBuckets() {
		return bucketize(part);
	}

	private List<String> bucketize(String substringTarget) {
		final List<String> bucket = new ArrayList<>(List.of(ROOT_SUBSTRING));
		if (StringUtils.isBlank(substringTarget)) {
			return bucket;
		}

		bucket.addAll(
				IntStream
						.rangeClosed(1, substringTarget.length())
						.mapToObj(index -> substringTarget.substring(0, index))
						.toList());

		return bucket;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Token that = (Token) o;
		return part.equals(that.part);
	}

	@Override
	public int hashCode() {
		return Objects.hash(part);
	}
}

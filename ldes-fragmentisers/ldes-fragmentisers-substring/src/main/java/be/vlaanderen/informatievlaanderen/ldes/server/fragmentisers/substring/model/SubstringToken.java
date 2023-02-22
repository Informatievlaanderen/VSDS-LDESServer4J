package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import com.apicatalog.jsonld.StringUtils;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.substring.SubstringFragmentationStrategy.ROOT_SUBSTRING;

public class SubstringToken {

	private final String token;

	public SubstringToken(String token) {
		this.token = token;
	}

	public List<String> getBucket() {
		return bucketize(token);
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
		SubstringToken that = (SubstringToken) o;
		return token.equals(that.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}
}

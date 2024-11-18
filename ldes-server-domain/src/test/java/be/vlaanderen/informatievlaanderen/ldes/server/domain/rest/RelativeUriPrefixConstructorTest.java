package be.vlaanderen.informatievlaanderen.ldes.server.domain.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;

class RelativeUriPrefixConstructorTest {
	private UriPrefixConstructor prefixConstructor;

	@BeforeEach
	void setUp() {
		prefixConstructor = new RelativeUriPrefixConstructor();
	}

	@ParameterizedTest(name = "Request with URI {0} returns {1}")
	@CsvSource(value = {
			"test,..",
			"test/testing,../..",
			"/test/testing,../..",
			"'',''",
			"test/testing/testing/testing,../../../.."})
	void test_BuildPrefix(String requestUri, String expected) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		request.setRequestURI(requestUri);

		String prefix = prefixConstructor.buildPrefix();

		assertThat(prefix).isEqualTo(expected);
	}
}
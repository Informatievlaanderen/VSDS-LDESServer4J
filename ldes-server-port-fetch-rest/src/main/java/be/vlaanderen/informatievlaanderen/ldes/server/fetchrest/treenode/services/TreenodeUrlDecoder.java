package be.vlaanderen.informatievlaanderen.ldes.server.fetchrest.treenode.services;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TreenodeUrlDecoder {
	private TreenodeUrlDecoder() {
	}

	public static String decode(String parameter) {
		return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
	}
}

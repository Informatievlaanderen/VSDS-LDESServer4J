package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.exceptions;

public class BucketDescriptorParseException extends RuntimeException {
	private final String descriptor;

	public BucketDescriptorParseException(String descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String getMessage() {
		return "BucketDescriptor could not be created from string: " + descriptor;
	}

}

package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class DataConversionException extends RuntimeException {
	private final Class conversionClass;
	private final String operation;

	public DataConversionException(Class conversionClass, boolean serialising, Exception e) {
		super(e);
		this.conversionClass = conversionClass;
		this.operation = serialising ? "serializing" : "deserializing";
	}

	@Override
	public String getMessage() {
		return "Failed %s class %s failed.".formatted(operation, conversionClass);
	}
}

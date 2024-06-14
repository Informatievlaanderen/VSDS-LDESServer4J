package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

@SuppressWarnings("java:S3740")
public class DataConversionException extends RuntimeException {
	private final Class<?> conversionClass;
	private final String operation;

	public DataConversionException(Class<?> conversionClass, boolean serialising, Exception e) {
		super(e);
		this.conversionClass = conversionClass;
		this.operation = serialising ? "serializing" : "deserializing";
	}

	private DataConversionException(Class<?> conversionClass, String operation, Exception e) {
		super(e);
		this.conversionClass = conversionClass;
		this.operation = operation;
	}

	@Override
	public String getMessage() {
		return "Failed %s class %s failed.".formatted(operation, conversionClass);
	}

	public static DataConversionException serializationFailed(Class<?> conversionClass, Exception e) {
		return new DataConversionException(conversionClass, "serializing", e);
	}

	public static DataConversionException deserializationFailed(Class<?> conversionClass, Exception e) {
		return new DataConversionException(conversionClass, "deserializing", e);
	}
}

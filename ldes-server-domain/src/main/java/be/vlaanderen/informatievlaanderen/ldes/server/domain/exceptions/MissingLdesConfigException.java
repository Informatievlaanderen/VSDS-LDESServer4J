package be.vlaanderen.informatievlaanderen.ldes.server.domain.exceptions;

public class MissingLdesConfigException extends RuntimeException {
	private final String identifier;
	private final ObjectTypes objectType;

	public MissingLdesConfigException(String collectionName, String viewName) {
		super();
		this.objectType = ObjectTypes.VIEW;
		this.identifier = collectionName + "/" + viewName;
	}

	/**
	 * Constructs a new MissingLdesConfigException with default object type
	 * <i>stream</i>
	 *
	 * @param identifier
	 *            identifier of the missing ldes config object
	 */
	public MissingLdesConfigException(String identifier) {
		super();
		this.objectType = ObjectTypes.STREAM;
		this.identifier = identifier;
	}

	@Override
	public String getMessage() {
		return "No " + objectType.getValue() + " exists with identifier: " + identifier;
	}

	private enum ObjectTypes {
		STREAM("stream"), VIEW("view");

		private final String value;

		public String getValue() {
			return value;
		}

		ObjectTypes(String value) {
			this.value = value;
		}
	}
}

package be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects;

import static be.vlaanderen.informatievlaanderen.ldes.server.pagination.valueobjects.PageNumber.PAGE_NUMBER;

public class PartialUrl {
	private final String viewName;
	private final String bucketDescriptor;
	private final PageNumber pageNumber;

	public PartialUrl(String viewName, String bucketDescriptor, PageNumber pageNumber) {
		this.viewName = viewName;
		this.bucketDescriptor = bucketDescriptor;
		this.pageNumber = pageNumber;
	}

	public String asString() {
		if (bucketDescriptor.isEmpty() && pageNumber == null) {
			return "/" + viewName;
		}
		if (bucketDescriptor.isEmpty()) {
			return "/" + viewName + "?" + pageNumber.asString();
		}
		return "/" + viewName + "?" + bucketDescriptor + (pageNumber != null ? "&" + pageNumber.asString() : "");
	}

	public PartialUrl createChild() {
		return new PartialUrl(
				viewName,
				bucketDescriptor,
				pageNumber == null ? PageNumber.startPageNumber() : pageNumber.increment()
		);
	}

	public static PartialUrl fromUrl(String url) {
		final String[] parts = url.split("\\?");
		final String viewName = parts[0].substring(1);
		if (parts.length == 1) {
			return new PartialUrl(viewName, "", null);
		}
		if (isUrlNumberless(parts[1])) {
			return new PartialUrl(viewName, parts[1], null);
		}

		if (isUrlOnlyPaged(parts[1])) {
			return new PartialUrl(viewName, "", PageNumber.fromString(parts[1]));
		}

		return createCompletePartialUrl(viewName, parts[1]);
	}

	public boolean isNumberLess() {
		return pageNumber == null;
	}

	private static boolean isUrlNumberless(String extendedDescriptor) {
		return !extendedDescriptor.contains(PAGE_NUMBER);
	}

	private static boolean isUrlOnlyPaged(String extendedDescriptor) {
		return extendedDescriptor.startsWith(PAGE_NUMBER);
	}

	private static PartialUrl createCompletePartialUrl(String viewName, String extendedDescriptor) {
		final int lastIndex = extendedDescriptor.lastIndexOf("&");
		final String bucketDescriptor = extendedDescriptor.substring(0, lastIndex);
		final PageNumber pageNumber = PageNumber.fromString(extendedDescriptor.substring(lastIndex + 1));
		return new PartialUrl(viewName, bucketDescriptor, pageNumber);
	}
}

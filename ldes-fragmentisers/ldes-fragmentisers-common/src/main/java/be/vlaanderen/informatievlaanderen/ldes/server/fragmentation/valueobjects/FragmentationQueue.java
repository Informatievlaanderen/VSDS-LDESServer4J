package be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.valueobjects;

import java.util.LinkedHashSet;
import java.util.Set;

public class FragmentationQueue {
	private final Set<String> set;

	public FragmentationQueue() {
		set = new LinkedHashSet<>();
	}

	public synchronized void offer(String element) {
		set.add(element);
	}

	public synchronized String poll() {
		if (set.isEmpty()) {
			return null;
		}
		String element = set.iterator().next();
		set.remove(element);
		return element;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public Set<String> getAvailableCollections() {
		Set<String> collections = Set.copyOf(set);
		set.clear();
		return collections;
	}
}

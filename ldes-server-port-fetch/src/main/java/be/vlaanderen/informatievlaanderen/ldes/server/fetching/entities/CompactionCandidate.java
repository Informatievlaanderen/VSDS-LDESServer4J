package be.vlaanderen.informatievlaanderen.ldes.server.fetching.entities;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentation.entities.Fragment;

import java.util.Objects;

public class CompactionCandidate {
	private final String id;
	private final Integer size;
	private Fragment fragment;

	public CompactionCandidate(String id, Integer size) {
		this.id = id;
		this.size = size;
	}

	public Fragment getFragment() {
		if (fragment == null) {
			throw new IllegalStateException("Fragment has not yet been initialized");
		}
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

	public String getId() {
		return id;
	}

	public Integer getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "CompactionCandidate{ id='" + id +"'}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CompactionCandidate that = (CompactionCandidate) o;
		return Objects.equals(id, that.id) && Objects.equals(size, that.size) && Objects.equals(fragment, that.fragment);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, size, fragment);
	}
}

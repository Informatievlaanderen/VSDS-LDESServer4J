package be.vlaanderen.informatievlaanderen.ldes.server.domain.viewcreation.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewNameTest {

	// TODO: 17/04/2023 testing

	@Test
	void withCollectionName() {
	}

	@Test
	void getFullName() {
	}

	@Test
	void getCollectionName() {
	}

	@Test
	void testEqualsAndHashCode() {
		String colA = "colA";
		String nameA = "nameA";

		assertEquals(new ViewName(colA, nameA), new ViewName(colA, nameA));
		assertNotEquals(new ViewName("other", nameA), new ViewName(colA, nameA));
		assertNotEquals(new ViewName(colA, "other"), new ViewName(colA, nameA));
		assertNotEquals(new ViewName("other", "other"), new ViewName(colA, nameA));

		assertEquals(new ViewName(colA, nameA).hashCode(), new ViewName(colA, nameA).hashCode());
		assertNotEquals(new ViewName("other", nameA).hashCode(), new ViewName(colA, nameA).hashCode());
		assertNotEquals(new ViewName(colA, "other").hashCode(), new ViewName(colA, nameA).hashCode());
		assertNotEquals(new ViewName("other", "other").hashCode(), new ViewName(colA, nameA).hashCode());
	}
}
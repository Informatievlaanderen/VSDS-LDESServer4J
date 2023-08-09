package be.vlaanderen.informatievlaanderen.ldes.server;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

	ApplicationModules modules = ApplicationModules.of(Application.class);

	// @Test
	// void verifiesModularStructure() {
	// modules.verify();
	// }

	@Test
	void createModuleDocumentation() {
		new Documenter(modules)
				.writeDocumentation()
				.writeIndividualModulesAsPlantUml();
	}

}
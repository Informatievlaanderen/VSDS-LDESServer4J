package be.vlaanderen.informatievlaanderen.ldes.server;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ApplicationTest {

	ApplicationModules modules = ApplicationModules.of(Application.class);

	@Test
	void createModuleDocumentation() {
		new Documenter(modules)
				.writeDocumentation()
				.writeIndividualModulesAsPlantUml();
	}

}
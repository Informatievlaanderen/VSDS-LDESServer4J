package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongo.mongock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mongock")
public class MigrationScanPackages {

	private List<String> migrationScanPackage;

	public List<String> getMigrationScanPackage() {
		return migrationScanPackage;
	}

	public void setMigrationScanPackage(List<String> migrationScanPackage) {
		this.migrationScanPackage = migrationScanPackage;
	}

}

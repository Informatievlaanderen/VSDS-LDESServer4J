package be.vlaanderen.informatievlaanderen.ldes.server;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class IngestKafkaSteps extends LdesServerIntegrationTest {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Given("I have a Kafka Container running")
	public void iHaveAKafkaContainerRunning() {
		assertThat(kafka.isRunning()).isTrue();
	}

	@When("I add {int} members of template {string} to the Kafka topic {string}")
	public void iAddMembersOfTemplateToTheKafkaTopic(int numberOfMembers, String memberTemplate, String topic) throws IOException, URISyntaxException {
		final String memberContentTemplate = readMemberTemplate(memberTemplate);

		createTopicIfNotExists(topic, 1, (short) 1);

		for (int i = 0; i < numberOfMembers; i++) {
			String memberContent = memberContentTemplate
					.replace("ID", String.valueOf(i))
					.replace("DATETIME", getCurrentTimestamp());
			kafkaTemplate.send(topic, memberContent);
		}
	}

	private void createTopicIfNotExists(String topic, int partitions, short replicationFactor) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafka.getBootstrapServers());

		try (AdminClient adminClient = AdminClient.create(props)) {
			ListTopicsResult topics = adminClient.listTopics(new ListTopicsOptions().timeoutMs(10000));
			if (!topics.names().get().contains(topic)) {
				NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);
				adminClient.createTopics(Collections.singleton(newTopic)).all().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			if (!(e.getCause() instanceof TopicExistsException)) {
				throw new RuntimeException("Failed to create topic", e);
			}
		}
	}

	private String readMemberTemplate(String fileName) throws IOException, URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		Path path = Paths.get(Objects.requireNonNull(classLoader.getResource(fileName)).toURI());
		return Files.lines(path).collect(Collectors.joining());
	}

	private String getCurrentTimestamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.[SSS]'Z'"));
	}
}

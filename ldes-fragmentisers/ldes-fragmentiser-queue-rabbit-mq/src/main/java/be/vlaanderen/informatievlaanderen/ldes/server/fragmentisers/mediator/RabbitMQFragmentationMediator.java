package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.mediator;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationMediator;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.queue.FragmenterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import static be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.queue.FragmenterConstants.DEFAULT_LDES_MEMBER_FRAGMENTATION_QUEUE;

public class RabbitMQFragmentationMediator implements FragmentationMediator {
	private static final Logger LOGGER = LoggerFactory.getLogger(FragmentationMediator.class);

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private Queue queue;

	private final FragmentationExecutor fragmentationExecutor;

	public RabbitMQFragmentationMediator(FragmentationExecutor fragmentationExecutor) {
		LOGGER.info("Server has been configured to queue ldes members for fragmentation with RABBIT MQ");
		this.fragmentationExecutor = fragmentationExecutor;
	}

	@Override
	public void addMemberToFragment(String ldesMember) {
		this.template.convertAndSend(queue.getName(), ldesMember);
	}

	@RabbitListener(queues = DEFAULT_LDES_MEMBER_FRAGMENTATION_QUEUE)
	@Override
	public void processMember(String ldesMember) {
		fragmentationExecutor.executeFragmentation(ldesMember);
	}
}

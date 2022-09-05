package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.queue;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationExecutor;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentationQueueMediator;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitMQFragmentationMediator implements FragmentationQueueMediator {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private Queue queue;

	private final FragmentationExecutor fragmentationExecutor;

	public RabbitMQFragmentationMediator(FragmentationExecutor fragmentationExecutor) {
		this.fragmentationExecutor = fragmentationExecutor;
	}

	@Override
	public void addMemberToFragment(String ldesMember) {
		this.template.convertAndSend(queue.getName(), ldesMember);
	}

	@RabbitListener(queues = "hello")
	@Override
	public void processMember(String ldesMember) {
		fragmentationExecutor.executeFragmentation(ldesMember);
	}
}

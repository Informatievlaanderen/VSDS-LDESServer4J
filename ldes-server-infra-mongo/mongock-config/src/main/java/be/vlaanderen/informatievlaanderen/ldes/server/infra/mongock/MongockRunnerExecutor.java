package be.vlaanderen.informatievlaanderen.ldes.server.infra.mongock;

import io.mongock.runner.core.executor.MongockRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MongockRunnerExecutor {

    private final MongockRunner applicationRunner;

    public MongockRunnerExecutor(MongockRunner applicationRunner) {
        this.applicationRunner = applicationRunner;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void foo() {
        if (applicationRunner.isEnabled()) {
            applicationRunner.execute();
        }
    }
}

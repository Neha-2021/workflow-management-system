package orchestrator.workflow.service.publisher;

import static org.mockito.Mockito.verify;

import java.util.UUID;
import orchestrator.execution.dispatcher.StepExecutionDispatcher;
import org.junit.jupiter.api.Test;

class TransactionalExecutionPublisherTest {
  private final StepExecutionDispatcher stepExecutionDispatcher =
      org.mockito.Mockito.mock(StepExecutionDispatcher.class);

  private final TransactionalExecutionPublisher publisher =
      new TransactionalExecutionPublisher(stepExecutionDispatcher);

  @Test
  void shouldDispatchImmediatelyWhenNoTransactionSynchronizationIsActive() {
    UUID stepExecutionId = UUID.randomUUID();

    publisher.publish(stepExecutionId);

    verify(stepExecutionDispatcher).dispatch(stepExecutionId);
  }
}

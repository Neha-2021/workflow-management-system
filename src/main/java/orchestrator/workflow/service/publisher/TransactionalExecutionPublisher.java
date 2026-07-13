package orchestrator.workflow.service.publisher;

import java.util.UUID;
import orchestrator.execution.queue.ExecutionQueue;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionalExecutionPublisher implements ExecutionPublisher {

  private final ExecutionQueue executionQueue;

  public TransactionalExecutionPublisher(ExecutionQueue executionQueue) {
    this.executionQueue = executionQueue;
  }

  @Override
  public void publish(UUID stepExecutionId) {

    if (TransactionSynchronizationManager.isSynchronizationActive()) {

      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              executionQueue.submit(stepExecutionId);
            }
          });

    } else {
      executionQueue.submit(stepExecutionId);
    }
  }
}

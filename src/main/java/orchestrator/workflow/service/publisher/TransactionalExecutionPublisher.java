package orchestrator.workflow.service.publisher;

import java.util.UUID;
import orchestrator.execution.dispatcher.StepExecutionDispatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class TransactionalExecutionPublisher implements ExecutionPublisher {

  private final StepExecutionDispatcher stepExecutionDispatcher;

  public TransactionalExecutionPublisher(StepExecutionDispatcher stepExecutionDispatcher) {
    this.stepExecutionDispatcher = stepExecutionDispatcher;
  }

  @Override
  public void publish(UUID stepExecutionId) {

    if (TransactionSynchronizationManager.isSynchronizationActive()) {

      TransactionSynchronizationManager.registerSynchronization(
          new TransactionSynchronization() {
            @Override
            public void afterCommit() {
              stepExecutionDispatcher.dispatch(stepExecutionId);
            }
          });

    } else {
      stepExecutionDispatcher.dispatch(stepExecutionId);
    }
  }
}
